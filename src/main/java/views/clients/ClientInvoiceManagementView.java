package views.clients;

import controllers.ClientController;
import controllers.ClientInvoiceController;
import controllers.InvoiceCategoryController;
import configs.MyBatisConfig;
import mappers.ClientMapper;
import mappers.ClientInvoiceMapper;
import mappers.InvoiceCategoryMapper;
import models.Client;
import models.ClientInvoice;
import models.InvoiceCategory;
import org.apache.ibatis.session.SqlSession;
import repositories.ClientRepository;
import repositories.ClientInvoiceRepository;
import repositories.InvoiceCategoryRepository;
import repositories.impl.ClientRepositoryImpl;
import repositories.impl.ClientInvoiceRepositoryImpl;
import repositories.impl.InvoiceCategoryRepositoryImpl;
import services.ClientService;
import services.ClientInvoiceService;
import services.InvoiceCategoryService;
import utils.Constants;
import utils.InvoiceTypeUtils;
import utils.TableUtils;
import views.MainView;
import views.clients.ClientInvoiceDetailView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;


public class ClientInvoiceManagementView extends javax.swing.JInternalFrame {

    private ClientInvoiceController invoiceController;
    private ClientController clientController;
    private InvoiceCategoryController invoiceCategoryController;
    private SqlSession sqlSession;
    private List<ClientInvoice> allInvoices = new ArrayList<>();
    private List<ClientInvoice> filteredInvoices = new ArrayList<>();

    private static final Set<String> FISCAL_TYPES = Set.of(
            Constants.FACTURA_A,
            Constants.FACTURA_B,
            Constants.FACTURA_C,
            Constants.NOTA_CREDITO_A,
            Constants.NOTA_CREDITO_B,
            Constants.NOTA_CREDITO_C,
            Constants.NOTA_DEBITO_A,
            Constants.NOTA_DEBITO_B,
            Constants.NOTA_DEBITO_C
    );

    private static final Set<String> EXTRA_TYPES = Set.of(
            Constants.PRESUPUESTO,
            Constants.NOTA_DEVOLUCION
    );

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault());

    public static boolean isOpen = false;
    public static List<ClientInvoice> invoices = null;

    public ClientInvoiceManagementView() {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
            ClientInvoiceMapper invoiceMapper = sqlSession.getMapper(ClientInvoiceMapper.class);
            InvoiceCategoryMapper invoiceCategoryMapper = sqlSession.getMapper(InvoiceCategoryMapper.class);
            ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
            ClientInvoiceRepository invoiceRepository = new ClientInvoiceRepositoryImpl(invoiceMapper);
            InvoiceCategoryRepository invoiceCategoryRepository = new InvoiceCategoryRepositoryImpl(invoiceCategoryMapper);
            ClientService clientService = new ClientService(clientRepository);
            ClientInvoiceService invoiceService = new ClientInvoiceService(invoiceRepository);
            InvoiceCategoryService invoiceCategoryService = new InvoiceCategoryService(invoiceCategoryRepository);
            clientController = new ClientController(clientService);
            invoiceController = new ClientInvoiceController(invoiceService);
            invoiceCategoryController = new InvoiceCategoryController(invoiceCategoryService);

            initComponents();
            isOpen = true;

            invoiceCategoryController.loadClientCategories(jComboBoxCategory);
            if (jComboBoxCategory.getItemCount() > 0) {
                jComboBoxCategory.setSelectedIndex(0);
            }

            configureFrameListener();
            configureTableInteractions();
            configureDatePickers();

            loadInvoices();
            applyFilters(false);
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void configureFrameListener() {
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                isOpen = false;
            }

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                isOpen = false;
            }
        });
    }

    private void configureTableInteractions() {
        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    openSelectedInvoiceDetail();
                }
            }
        });

        jCheckBoxSeeAll.addActionListener(evt -> applyFilters(false));
    }

    private void configureDatePickers() {
        jDateChooser1.setDateFormatString("dd-MM-yyyy");
        jDateChooser2.setDateFormatString("dd-MM-yyyy");

        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());

        jDateChooser1.setDate(Date.from(firstDay.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        jDateChooser2.setDate(Date.from(lastDay.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private void loadInvoices() {
        invoices = invoiceController.findAll();
        if (invoices == null) {
            invoices = new ArrayList<>();
        }
        allInvoices = new ArrayList<>(invoices);
        enrichClients(allInvoices);
    }

    private void enrichClients(List<ClientInvoice> invoiceList) {
        Map<Integer, Client> clientCache = new HashMap<>();
        for (ClientInvoice invoice : invoiceList) {
            if (invoice == null) {
                continue;
            }
            Client client = invoice.getClient();
            if (client == null) {
                continue;
            }
            if (client.getFullName() != null && !client.getFullName().isBlank()) {
                continue;
            }
            Integer clientId = client.getId();
            if (clientId == null) {
                continue;
            }
            Client fullClient = clientCache.computeIfAbsent(clientId, clientController::findById);
            if (fullClient != null) {
                invoice.setClient(fullClient);
            }
        }
    }

    private void setModelTable() {
        TableUtils.configureClientInvoiceManagementViewTable(jTable1);
    }

    private void updateTable(List<ClientInvoice> list) {
        List<ClientInvoice> safeList = list == null ? new ArrayList<>() : list;
        BigDecimal tot = BigDecimal.ZERO;
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal vat21 = BigDecimal.ZERO;
        BigDecimal vat105 = BigDecimal.ZERO;

        jTable1.setModel(createInvoiceTableModel(safeList));
        jTable1.setRowSelectionAllowed(true);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setModelTable();

        for (ClientInvoice c : safeList) {
            BigDecimal sign = getSign(c);
            if (c.getTotal() != null) {
                tot = tot.add(c.getTotal().multiply(sign));
            }
            if (c.getSubtotal() != null) {
                subtotal = subtotal.add(c.getSubtotal().multiply(sign));
            }
            if (c.getVat21() != null) {
                vat21 = vat21.add(c.getVat21().multiply(sign));
            }
            if (c.getVat105() != null) {
                vat105 = vat105.add(c.getVat105().multiply(sign));
            }
        }

        jLabelTotal.setText(formatAmount(tot));
        jLabelSubTotal.setText(formatAmount(subtotal));
        jLabelIva21.setText(formatAmount(vat21));
        jLabelIva105.setText(formatAmount(vat105));
    }

    private DefaultTableModel createInvoiceTableModel(List<ClientInvoice> list) {
        DefaultTableModel tm = new DefaultTableModel(new String[]{"Fecha", "Tipo", "Numero", "Codigo Cliente", "Nombre Cliente", "Categoría", "Total"}, 0) {
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        for (ClientInvoice r : list) {
            tm.addRow(new Object[]{
                r.getInvoiceDate() != null ? r.getInvoiceDate().format(DATE_FORMATTER) : "",
                InvoiceTypeUtils.toDisplayValue(r.getInvoiceType()),
                formatInvoiceNumber(r.getPointOfSale(), r.getInvoiceNumber()),
                r.getClient() != null ? r.getClient().getId() : "",
                r.getClient() != null ? Objects.toString(r.getClient().getFullName(), "") : "",
                getCategoryDescription(r.getCategory()),
                r.getTotal()
            });
        }
        return tm;
    }

    private BigDecimal getSign(ClientInvoice invoice) {
        if (invoice == null) {
            return BigDecimal.ONE;
        }
        String type = invoice.getInvoiceType();
        if (type == null) {
            return BigDecimal.ONE;
        }
        if (InvoiceTypeUtils.isCreditDocument(type)) {
            return BigDecimal.valueOf(-1);
        }
        return BigDecimal.ONE;
    }

    private String formatAmount(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN).toString();
        }
        return value.setScale(2, RoundingMode.HALF_EVEN).toString();
    }

    private String getCategoryDescription(InvoiceCategory category) {
        return category != null ? Objects.toString(category.getDescription(), "") : "";
    }

    private void applyFilters(boolean validateDates) {
        if (!validateDates && !isDateRangeValid()) {
            return;
        }
        if (validateDates && !validateSearch()) {
            return;
        }

        Date from = jDateChooser1.getDate();
        Date to = jDateChooser2.getDate();

        final LocalDate startDate = from != null
                ? from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;
        final LocalDate endDate = to != null
                ? to.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;

        final String search = jTextField1.getText();
        final boolean seeAll = jCheckBoxSeeAll.isSelected();
        Object categorySelection = jComboBoxCategory.getSelectedItem();
        final Integer selectedCategoryId = categorySelection instanceof InvoiceCategory
                ? ((InvoiceCategory) categorySelection).getId()
                : null;

        Comparator<ClientInvoice> comparator = Comparator.comparing(
                ClientInvoice::getInvoiceDate,
                Comparator.nullsLast(Comparator.naturalOrder())
        ).reversed();

        filteredInvoices = allInvoices.stream()
                .filter(inv -> includeInvoice(inv, seeAll))
                .filter(inv -> filterByDate(inv, startDate, endDate))
                .filter(inv -> filterBySearch(inv, search))
                .filter(inv -> filterByCategory(inv, selectedCategoryId))
                .sorted(comparator)
                .collect(Collectors.toCollection(ArrayList::new));

        invoices = filteredInvoices;
        updateTable(filteredInvoices);
    }

    private boolean filterByDate(ClientInvoice invoice, LocalDate startDate, LocalDate endDate) {
        if (invoice == null || invoice.getInvoiceDate() == null) {
            return false;
        }
        LocalDate invoiceDate = invoice.getInvoiceDate().toLocalDate();
        if (startDate != null && invoiceDate.isBefore(startDate)) {
            return false;
        }
        if (endDate != null && invoiceDate.isAfter(endDate)) {
            return false;
        }
        return true;
    }

    private boolean filterBySearch(ClientInvoice invoice, String search) {
        if (invoice == null || search == null || search.isBlank()) {
            return true;
        }

        String normalized = search.trim().toLowerCase(Locale.getDefault());

        boolean matchesName = false;
        Client client = invoice.getClient();
        if (client != null && client.getFullName() != null) {
            matchesName = client.getFullName()
                    .toLowerCase(Locale.getDefault())
                    .contains(normalized);
        }

        String pointOfSale = Objects.toString(invoice.getPointOfSale(), "");
        String invoiceNumber = Objects.toString(invoice.getInvoiceNumber(), "");
        String formattedNumber = formatInvoiceNumber(pointOfSale, invoiceNumber);

        boolean matchesNumber = false;
        if (!pointOfSale.isBlank() || !invoiceNumber.isBlank()) {
            String normalizedCombined = formattedNumber.toLowerCase(Locale.getDefault());

            if (normalizedCombined.contains(normalized)) {
                matchesNumber = true;
            } else {
                String normalizedDigits = normalized.replaceAll("[^0-9]", "");
                if (!normalizedDigits.isBlank()) {
                    String combinedDigits = normalizedCombined.replaceAll("[^0-9]", "");
                    matchesNumber = combinedDigits.contains(normalizedDigits);
                }
            }
        }

        return matchesName || matchesNumber;
    }

    private String formatInvoiceNumber(String pointOfSale, String number) {
        String pos = leftPadDigits(pointOfSale, 4);
        String invoiceNumber = leftPadDigits(number, 8);
        return String.format("%s-%s", pos, invoiceNumber);
    }

    private String leftPadDigits(String value, int size) {
        if (value == null) {
            value = "";
        }
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.length() > size) {
            digits = digits.substring(digits.length() - size);
        }
        return String.format(Locale.ROOT, "%" + size + "s", digits).replace(' ', '0');
    }

    private boolean filterByCategory(ClientInvoice invoice, Integer categoryId) {
        if (categoryId == null) {
            return true;
        }
        if (invoice == null || invoice.getCategory() == null) {
            return false;
        }
        return Objects.equals(invoice.getCategory().getId(), categoryId);
    }

    private boolean includeInvoice(ClientInvoice invoice, boolean seeAll) {
        if (invoice == null) {
            return false;
        }
        String type = InvoiceTypeUtils.toDisplayValue(invoice.getInvoiceType());
        if (type == null) {
            return false;
        }
        if (FISCAL_TYPES.contains(type)) {
            return true;
        }
        return seeAll && EXTRA_TYPES.contains(type);
    }

    private boolean isDateRangeValid() {
        Date from = jDateChooser1.getDate();
        Date to = jDateChooser2.getDate();
        if (from == null && to == null) {
            return true;
        }
        if (from == null || to == null) {
            return false;
        }
        return !to.before(from);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jButton12 = new javax.swing.JButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabelCategory = new javax.swing.JLabel();
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
        jCheckBoxSeeAll = new javax.swing.JCheckBox();

        setMinimumSize(new java.awt.Dimension(1050, 700));
        setPreferredSize(new java.awt.Dimension(1050, 700));
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setText("Buscar");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(31, 136, 83, 20);

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField1);
        jTextField1.setBounds(90, 130, 495, 22);

        jLabelCategory.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabelCategory.setText("Categoría");
        getContentPane().add(jLabelCategory);
        jLabelCategory.setBounds(600, 130, 80, 20);

        jComboBoxCategory.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jComboBoxCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCategoryActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxCategory);
        jComboBoxCategory.setBounds(680, 130, 180, 22);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Fecha", "Tipo", "Numero ", "Codigo Cliente", "Nombre Cliente", "Categoría", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(31, 174, 950, 302);

        jButton8.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButton8.setText("Volver");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton8);
        jButton8.setBounds(460, 567, 115, 30);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Buscar Comprobantes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 16))); // NOI18N
        jPanel1.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jPanel1.setLayout(null);

        jButton12.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton12);
        jButton12.setBounds(480, 40, 25, 25);
        jPanel1.add(jDateChooser1);
        jDateChooser1.setBounds(100, 40, 150, 22);
        jPanel1.add(jDateChooser2);
        jDateChooser2.setBounds(320, 40, 150, 22);

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Desde");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(20, 40, 70, 20);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Hasta");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(250, 40, 60, 20);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(240, 10, 540, 90);

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
        jLabel20.setBounds(240, 10, 60, 20);

        jLabel24.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Total:");
        jPanel2.add(jLabel24);
        jLabel24.setBounds(700, 10, 60, 20);

        jLabelSubTotal.setFont(new java.awt.Font("Calibri", 3, 16)); // NOI18N
        jPanel2.add(jLabelSubTotal);
        jLabelSubTotal.setBounds(80, 10, 150, 20);

        jLabel25.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Iva 10.5:");
        jPanel2.add(jLabel25);
        jLabel25.setBounds(480, 10, 70, 20);

        jLabelTotal.setFont(new java.awt.Font("Calibri", 3, 16)); // NOI18N
        jPanel2.add(jLabelTotal);
        jLabelTotal.setBounds(780, 10, 150, 20);

        jLabelIva105.setFont(new java.awt.Font("Calibri", 3, 16)); // NOI18N
        jPanel2.add(jLabelIva105);
        jLabelIva105.setBounds(560, 10, 150, 20);

        jLabelIva21.setFont(new java.awt.Font("Calibri", 3, 16)); // NOI18N
        jPanel2.add(jLabelIva21);
        jLabelIva21.setBounds(310, 10, 150, 20);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(31, 485, 960, 39);

        jCheckBoxSeeAll.setText("Ver Todos");
        getContentPane().add(jCheckBoxSeeAll);
        jCheckBoxSeeAll.setBounds(600, 130, 150, 20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        applyFilters(false);
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jComboBoxCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCategoryActionPerformed
        applyFilters(false);
    }//GEN-LAST:event_jComboBoxCategoryActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        isOpen = false;
        dispose();
}//GEN-LAST:event_jButton8ActionPerformed

    public boolean validateSearch() {
        boolean ok = true;
        if (jDateChooser1.getDate() != null && jDateChooser2.getDate() != null) {
            if (jDateChooser2.getDate().before(jDateChooser1.getDate())) {
                JOptionPane.showMessageDialog(this, "La fecha desde debe ser anterior a la fecha hasta", "Atención", JOptionPane.WARNING_MESSAGE);
                ok = false;
            }
        } else if (jDateChooser1.getDate() == null && jDateChooser2.getDate() != null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar fecha desde", "Atención", JOptionPane.WARNING_MESSAGE);
            ok = false;
        } else if (jDateChooser1.getDate() != null && jDateChooser2.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar fecha hasta", "Atención", JOptionPane.WARNING_MESSAGE);
            ok = false;
        }
        return ok;
    }

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed

        applyFilters(true);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void openSelectedInvoiceDetail() {
        int row = ClientInvoiceManagementView.jTable1.getSelectedRow();
        if (row < 0 || filteredInvoices == null || row >= filteredInvoices.size()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una factura", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ClientInvoiceDetailView.isOpen) {
            try {
                ClientInvoiceDetailView clientInvoiceDetailVent = new ClientInvoiceDetailView();
                MainView.jDesktopPane1.add(clientInvoiceDetailVent);
                clientInvoiceDetailVent.setVisible(true);
            } catch (Exception ex) {
                Logger.getLogger(ClientInvoiceManagementView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton8;
    private javax.swing.JCheckBox jCheckBoxSeeAll;
    private javax.swing.JComboBox jComboBoxCategory;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabelCategory;
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
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

}
