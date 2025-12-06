package views.providers;

import controllers.InvoiceCategoryController;
import controllers.ProviderController;
import controllers.ProviderInvoiceController;
import configs.MyBatisConfig;
import mappers.InvoiceCategoryMapper;
import mappers.ProviderMapper;
import mappers.ProviderInvoiceMapper;
import models.InvoiceCategory;
import models.ProviderInvoice;
import org.apache.ibatis.session.SqlSession;
import repositories.InvoiceCategoryRepository;
import repositories.ProviderRepository;
import repositories.ProviderInvoiceRepository;
import repositories.impl.InvoiceCategoryRepositoryImpl;
import repositories.impl.ProviderRepositoryImpl;
import repositories.impl.ProviderInvoiceRepositoryImpl;
import services.InvoiceCategoryService;
import services.ProviderService;
import services.ProviderInvoiceService;
import views.MainView;
import views.providers.ProviderInvoiceDetailView;
import views.providers.ProviderInvoiceUpsertView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import utils.InvoiceTypeUtils;
import utils.TableUtils;

public class ProviderInvoiceManagementView extends javax.swing.JInternalFrame {

    private ProviderInvoiceController invoiceController;
    private ProviderController providerController;
    private InvoiceCategoryController categoryController;
    private SqlSession sqlSession;
    private static ProviderInvoiceManagementView instance;
    public static boolean isOpen = false;
    public static List<ProviderInvoice> invoices = new ArrayList<>();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private List<ProviderInvoice> displayedInvoices = new ArrayList<>();

    public ProviderInvoiceManagementView() {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
            ProviderInvoiceMapper invoiceMapper = sqlSession.getMapper(ProviderInvoiceMapper.class);
            InvoiceCategoryMapper categoryMapper = sqlSession.getMapper(InvoiceCategoryMapper.class);
            ProviderRepository providerRepository = new ProviderRepositoryImpl(providerMapper);
            ProviderInvoiceRepository invoiceRepository = new ProviderInvoiceRepositoryImpl(invoiceMapper);
            InvoiceCategoryRepository categoryRepository = new InvoiceCategoryRepositoryImpl(categoryMapper);
            ProviderService providerService = new ProviderService(providerRepository);
            ProviderInvoiceService invoiceService = new ProviderInvoiceService(invoiceRepository);
            InvoiceCategoryService categoryService = new InvoiceCategoryService(categoryRepository);
            providerController = new ProviderController(providerService);
            invoiceController = new ProviderInvoiceController(invoiceService);
            categoryController = new InvoiceCategoryController(categoryService);

            initComponents();
            isOpen = true;
            instance = this;

            addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    isOpen = false;
                    instance = null;
                }

                @Override
                public void internalFrameClosing(InternalFrameEvent e) {
                    isOpen = false;
                    instance = null;
                }
            });

            loadCategories();
            setDefaultDates();
            applyFilters();
            setModelTable();
        } catch (Exception ex) {
            Logger.getLogger(ProviderInvoiceManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<ProviderInvoice> safeList(List<ProviderInvoice> source) {
        return source != null ? new ArrayList<>(source) : new ArrayList<>();
    }

    private void loadCategories() {
        categoryController.loadProviderCategories(jComboBoxCategory);
    }

    private void setModelTable() {
        TableUtils.configureProviderInvoiceManagementViewTable(jTable1);
    }

    private void updateTable(List<ProviderInvoice> list) {
        BigDecimal tot = BigDecimal.ZERO;
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal vat21 = BigDecimal.ZERO;
        BigDecimal vat105 = BigDecimal.ZERO;

        List<ProviderInvoice> data = safeList(list);
        displayedInvoices = data;
        jTable1.setModel(createInvoiceTableModel(data));
        TableUtils.configureProviderInvoiceManagementViewTable(jTable1);
        applySearchFilter();
        jTable1.setRowSelectionAllowed(true);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        for (ProviderInvoice c : data) {
            if (c.getTotal() != null) tot = tot.add(c.getTotal());
            if (c.getSubtotal() != null) subtotal = subtotal.add(c.getSubtotal());
            if (c.getVat21() != null) vat21 = vat21.add(c.getVat21());
            if (c.getVat105() != null) vat105 = vat105.add(c.getVat105());
        }

        jLabelTotal.setText(tot.toString());
        jLabelSubTotal.setText(subtotal.toString());
        jLabelIva21.setText(vat21.toString());
        jLabelIva105.setText(vat105.toString());
    }

    private DefaultTableModel createInvoiceTableModel(List<ProviderInvoice> list) {
        DefaultTableModel tm = new DefaultTableModel(new String[]{"Codigo", "Razon Social", "Categoría", "Tipo", "Numero", "Fecha", "Total"}, 0) {
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        Vector row;
        for (ProviderInvoice r : list) {
            row = new Vector();
            row.add(r.getId());
            String providerName = "";
            if (r.getProvider() != null) {
                providerName = providerController.findById(r.getProvider().getId()).getName();
            }
            row.add(providerName);
            row.add(getCategoryDescription(r.getCategory()));
            row.add(InvoiceTypeUtils.toAbbreviation(r.getInvoiceType()));
            row.add(formatInvoiceCode(r.getPointOfSale(), r.getInvoiceNumber()));
            row.add(r.getInvoiceDate() != null ? r.getInvoiceDate().format(DATE_FORMATTER) : "");
            row.add(r.getTotal());
            tm.addRow(row);
        }
        return tm;
    }

    private String formatInvoiceCode(String pointOfSale, String number) {
        String pos = padNumeric(pointOfSale, 4);
        String num = padNumeric(number, 8);
        if (pos.isEmpty() && num.isEmpty()) {
            return "";
        }
        if (pos.isEmpty()) {
            return num;
        }
        if (num.isEmpty()) {
            return pos;
        }
        return pos + "-" + num;
    }

    private String padNumeric(String value, int length) {
        if (value == null) {
            return "";
        }
        String digits = value.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return "";
        }
        if (digits.length() >= length) {
            return digits;
        }
        return String.format("%" + length + "s", digits).replace(' ', '0');
    }

    private void setDefaultDates() {
        LocalDate now = LocalDate.now();
        LocalDate first = now.withDayOfMonth(1);
        LocalDate last = now.withDayOfMonth(now.lengthOfMonth());
        jDateChooserFrom.setDate(Date.from(first.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        jDateChooserTo.setDate(Date.from(last.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private void reloadInvoices() {
        if (invoiceController == null) {
            invoices = new ArrayList<>();
            return;
        }
        if (sqlSession != null) {
            sqlSession.clearCache();
        }
        invoices = safeList(invoiceController.findAll());
    }

    private void applyFilters() {
        reloadInvoices();
        if (!validateSearch()) {
            return;
        }
        updateTable(filterInvoices(safeList(invoices)));
    }

    private List<ProviderInvoice> filterInvoices(List<ProviderInvoice> source) {
        List<ProviderInvoice> filtered = safeList(source);
        Date d1 = jDateChooserFrom.getDate();
        Date d2 = jDateChooserTo.getDate();
        if (d1 != null && d2 != null) {
            LocalDate start = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            filtered = filtered.stream()
                    .filter(inv -> {
                        LocalDateTime invoiceDate = inv.getInvoiceDate();
                        if (invoiceDate == null) {
                            return false;
                        }
                        LocalDate date = invoiceDate.toLocalDate();
                        return (date.isEqual(start) || date.isAfter(start)) && (date.isEqual(end) || date.isBefore(end));
                    })
                    .collect(Collectors.toList());
        }
        Object selObj = jComboBoxCategory.getSelectedItem();
        if (selObj instanceof InvoiceCategory) {
            InvoiceCategory selectedCategory = (InvoiceCategory) selObj;
            Integer catId = selectedCategory.getId();
            filtered = filtered.stream()
                    .filter(inv -> {
                        InvoiceCategory category = inv.getCategory();
                        return category != null && Objects.equals(category.getId(), catId);
                    })
                    .collect(Collectors.toList());
        } else if (selObj instanceof String) {
            String selectedDescription = ((String) selObj).trim();
            if (!selectedDescription.isEmpty() && !"Seleccione...".equalsIgnoreCase(selectedDescription)) {
                filtered = filtered.stream()
                        .filter(inv -> {
                            InvoiceCategory category = inv.getCategory();
                            return category != null && category.getDescription() != null
                                    && category.getDescription().equalsIgnoreCase(selectedDescription);
                        })
                        .collect(Collectors.toList());
            }
        }
        return filtered;
    }

    public static void refreshTable() {
        if (isOpen && instance != null) {
            Runnable task = instance::refreshSilently;
            if (SwingUtilities.isEventDispatchThread()) {
                task.run();
            } else {
                SwingUtilities.invokeLater(task);
            }
        }
    }

    private void refreshSilently() {
        reloadInvoices();
        if (!hasValidDateRangeSelected()) {
            return;
        }
        updateTable(filterInvoices(safeList(invoices)));
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextFieldSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonReturn = new javax.swing.JButton();
        jButtonDetail = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jButtonSearch = new javax.swing.JButton();
        jDateChooserFrom = new com.toedter.calendar.JDateChooser();
        jDateChooserTo = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jComboBoxCategory = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabelSubTotal = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabelTotal = new javax.swing.JLabel();
        jLabelIva105 = new javax.swing.JLabel();
        jLabelIva21 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(1000, 620));
        setPreferredSize(new java.awt.Dimension(1000, 620));
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setText("Buscar");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(70, 180, 60, 20);

        jTextFieldSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldSearchKeyReleased(evt);
            }
        });
        getContentPane().add(jTextFieldSearch);
        jTextFieldSearch.setBounds(130, 180, 380, 22);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Razon Social", "Tipo Compr.", "Factura", "Fecha", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(50, 220, 890, 260);

        jButtonReturn.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonReturn.setText("Volver");
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReturn);
        jButtonReturn.setBounds(460, 540, 130, 27);

        jButtonDetail.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButtonDetail.setText("Ver");
        jButtonDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetailActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDetail);
        jButtonDetail.setBounds(580, 120, 140, 27);

        jButtonDelete.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButtonDelete.setText("Borrar");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDelete);
        jButtonDelete.setBounds(580, 80, 140, 27);

        jButtonAdd.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/agregar.png"))); // NOI18N
        jButtonAdd.setText("Cargar Fact.");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonAdd);
        jButtonAdd.setBounds(580, 40, 140, 27);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Buscar Comprobantes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 16))); // NOI18N
        jPanel1.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jPanel1.setLayout(null);

        jButtonSearch.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonSearch);
        jButtonSearch.setBounds(480, 60, 40, 30);
        jPanel1.add(jDateChooserFrom);
        jDateChooserFrom.setBounds(100, 40, 150, 25);
        jPanel1.add(jDateChooserTo);
        jDateChooserTo.setBounds(320, 40, 150, 25);

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Desde");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(20, 40, 70, 25);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Hasta");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(250, 40, 60, 25);

        jLabel12.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Categoría");
        jPanel1.add(jLabel12);
        jLabel12.setBounds(140, 80, 80, 20);

        jComboBoxCategory.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jPanel1.add(jComboBoxCategory);
        jComboBoxCategory.setBounds(230, 80, 180, 21);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(20, 20, 550, 140);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(null);

        jLabel18.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Subtotal:");
        jPanel2.add(jLabel18);
        jLabel18.setBounds(0, 10, 70, 20);

        jLabel20.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Iva 21:");
        jPanel2.add(jLabel20);
        jLabel20.setBounds(200, 10, 70, 20);

        jLabel24.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Total:");
        jPanel2.add(jLabel24);
        jLabel24.setBounds(670, 10, 70, 20);

        jLabelSubTotal.setFont(new java.awt.Font("Calibri", 3, 16)); // NOI18N
        jPanel2.add(jLabelSubTotal);
        jLabelSubTotal.setBounds(80, 10, 120, 20);

        jLabel25.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Iva 10.5:");
        jPanel2.add(jLabel25);
        jLabel25.setBounds(440, 10, 70, 20);

        jLabelTotal.setFont(new java.awt.Font("Calibri", 3, 16)); // NOI18N
        jPanel2.add(jLabelTotal);
        jLabelTotal.setBounds(750, 10, 120, 20);

        jLabelIva105.setFont(new java.awt.Font("Calibri", 3, 16)); // NOI18N
        jPanel2.add(jLabelIva105);
        jLabelIva105.setBounds(520, 10, 110, 20);

        jLabelIva21.setFont(new java.awt.Font("Calibri", 3, 16)); // NOI18N
        jPanel2.add(jLabelIva21);
        jLabelIva21.setBounds(280, 10, 100, 20);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(50, 490, 890, 40);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldSearchKeyReleased
        applySearchFilter();
}//GEN-LAST:event_jTextFieldSearchKeyReleased

    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        isOpen = false;
        dispose();
}//GEN-LAST:event_jButtonReturnActionPerformed

    private void jButtonDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetailActionPerformed

        ProviderInvoice invoice = getSelectedInvoice();
        if (invoice == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una factura", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ProviderInvoiceDetailView.isOpen) {
            try {
                ProviderInvoiceDetailView providerInvoiceDetailVent = new ProviderInvoiceDetailView();
                MainView.jDesktopPane1.add(providerInvoiceDetailVent);
                providerInvoiceDetailVent.setVisible(true);
            } catch (Exception ex) {
                Logger.getLogger(ProviderInvoiceManagementView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButtonDetailActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed

        ProviderInvoice invoice = getSelectedInvoice();
        if (invoice == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una factura", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar el comprobante " + invoice.getInvoiceNumber() + "?", "Atención", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            invoiceController.delete(invoice.getId());
            refreshTable();
        }

    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        if (!ProviderInvoiceUpsertView.abierta) {
            ProviderInvoiceUpsertView view = new ProviderInvoiceUpsertView();
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        }
    }//GEN-LAST:event_jButtonAddActionPerformed

    public boolean validateSearch() {
        Date from = jDateChooserFrom.getDate();
        Date to = jDateChooserTo.getDate();
        if (from != null && to != null) {
            if (to.before(from)) {
                JOptionPane.showMessageDialog(this, "La fecha desde debe ser anterior a la fecha hasta", "Atención", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } else if (from == null && to != null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar fecha desde", "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (from != null && to == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar fecha hasta", "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean hasValidDateRangeSelected() {
        Date from = jDateChooserFrom.getDate();
        Date to = jDateChooserTo.getDate();
        if (from == null && to == null) {
            return true;
        }
        if (from == null || to == null) {
            return false;
        }
        return !to.before(from);
    }

    private ProviderInvoice getSelectedInvoice() {
        if (displayedInvoices == null || displayedInvoices.isEmpty()) {
            return null;
        }
        int viewIndex = jTable1.getSelectedRow();
        if (viewIndex < 0) {
            return null;
        }
        int modelIndex = jTable1.convertRowIndexToModel(viewIndex);
        if (modelIndex < 0 || modelIndex >= displayedInvoices.size()) {
            return null;
        }
        return displayedInvoices.get(modelIndex);
    }

    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed

        applyFilters();

    }//GEN-LAST:event_jButtonSearchActionPerformed

    private void applySearchFilter() {
        String value = jTextFieldSearch.getText();
        if (value == null || value.isBlank()) {
            jTable1.setRowSorter(null);
            return;
        }
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        try {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(value)));
            jTable1.setRowSorter(sorter);
        } catch (java.util.regex.PatternSyntaxException ex) {
            jTable1.setRowSorter(sorter);
            sorter.setRowFilter(null);
        }
    }

    private String getCategoryDescription(InvoiceCategory category) {
        if (category == null || category.getDescription() == null) {
            return "";
        }
        return category.getDescription();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDetail;
    private javax.swing.JButton jButtonReturn;
    private javax.swing.JButton jButtonSearch;
    public static javax.swing.JComboBox jComboBoxCategory;
    private com.toedter.calendar.JDateChooser jDateChooserFrom;
    private com.toedter.calendar.JDateChooser jDateChooserTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    public static javax.swing.JLabel jLabelIva105;
    public static javax.swing.JLabel jLabelIva21;
    public static javax.swing.JLabel jLabelSubTotal;
    public static javax.swing.JLabel jLabelTotal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextFieldSearch;
    // End of variables declaration//GEN-END:variables

}
