package views.clients;

import controllers.ClientBudgetController;
import controllers.ClientBudgetDetailController;
import controllers.ClientController;
import controllers.ProductController;
import configs.MyBatisConfig;
import mappers.ClientBudgetDetailMapper;
import mappers.ClientBudgetMapper;
import mappers.ClientMapper;
import mappers.ProductMapper;
import models.Client;
import models.ClientBudget;
import models.ClientBudgetDetail;
import models.Product;
import org.apache.ibatis.session.SqlSession;
import repositories.ClientBudgetDetailRepository;
import repositories.ClientBudgetRepository;
import repositories.ClientRepository;
import repositories.ProductRepository;
import repositories.impl.ClientBudgetDetailRepositoryImpl;
import repositories.impl.ClientBudgetRepositoryImpl;
import repositories.impl.ClientRepositoryImpl;
import repositories.impl.ProductRepositoryImpl;
import services.ClientBudgetDetailService;
import services.ClientBudgetService;
import services.ClientService;
import services.ProductService;
import services.reports.ClientBudgetReportService;
import utils.DocumentValidator;
import views.MainView;
import views.clients.ClientManagementView;
import views.products.ProductSearchView;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.math.BigDecimal;
import java.util.HashSet;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientBudgetUpsertView extends javax.swing.JInternalFrame {

    private ClientBudgetController budgetController;
    private ClientBudgetDetailController detailController;
    private ClientController clientController;
    private ProductController productController;
    private final ClientBudgetReportService budgetReportService;
    private SqlSession sqlSession;
    public static boolean open = false;
    private Integer budgetId;
    private final Set<String> loadedProductCodes = new HashSet<>();
    private TableModelListener budgetTableModelListener;
    private TableModel budgetTableModel;
    private boolean updatingBudgetTotals;

    public ClientBudgetUpsertView() {
        this(null);
    }

    public ClientBudgetUpsertView(Integer budgetId) {
        budgetReportService = new ClientBudgetReportService();
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            ClientBudgetMapper budgetMapper = sqlSession.getMapper(ClientBudgetMapper.class);
            ClientBudgetDetailMapper detailMapper = sqlSession.getMapper(ClientBudgetDetailMapper.class);
            ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
            ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);
            ClientBudgetRepository budgetRepository = new ClientBudgetRepositoryImpl(budgetMapper);
            ClientBudgetDetailRepository detailRepository = new ClientBudgetDetailRepositoryImpl(detailMapper);
            ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
            ProductRepository productRepository = new ProductRepositoryImpl(productMapper);
            ClientBudgetService budgetService = new ClientBudgetService(budgetRepository);
            ClientBudgetDetailService detailService = new ClientBudgetDetailService(detailRepository);
            ClientService clientService = new ClientService(clientRepository);
            ProductService productService = new ProductService(productRepository);
            budgetController = new ClientBudgetController(budgetService);
            detailController = new ClientBudgetDetailController(detailService);
            clientController = new ClientController(clientService);
            productController = new ProductController(productService);

            initComponents();
            open = true;
            this.budgetId = budgetId;

            addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosing(InternalFrameEvent e) {
                    open = false;
                }

                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    open = false;
                }
            });

            if (budgetId != null) {
                loadBudgetData(budgetId);
            } else {
                jTable2.setModel(createEmptyBudgetModel());
                configureBudgetTable();
                jDateChooser1.setDate(new java.util.Date());
                jLabelTotal.setText("0");
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientBudgetUpsertView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadClient(int id) {
        loadClient(id, true);
    }

    private void loadClient(int id, boolean showWarnings) {
        try {
            Client client = clientController.findById(id);
            if (client == null) {
                if (showWarnings) {
                    JOptionPane.showMessageDialog(this, "No se ha encontrado cliente!", "Cliente", JOptionPane.WARNING_MESSAGE);
                }
                clearClientDetails();
                return;
            }
            if (showWarnings && !client.isActive()) {
                JOptionPane.showMessageDialog(this, "El cliente está deshabilitado", "Cliente", JOptionPane.WARNING_MESSAGE);
            }
            jTextFieldClientId.setText(client.getId().toString());
            jLabelClientName.setText(client.getFullName());
            jLabelAddress.setText(client.getAddress() != null ? client.getAddress().getName() + " " + client.getAddressNumber() : "");
            jLabelCity.setText(client.getCity() != null ? client.getCity().getName() : "");
            jLabelVatCondition.setText(client.getTaxCondition() != null ? client.getTaxCondition().getName() : "");
            jLabelDniCuit.setText(DocumentValidator.formatCuit(client.getDocumentNumber()));
        } catch (Exception ex) {
            Logger.getLogger(ClientBudgetUpsertView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearClientDetails() {
        jTextFieldClientId.setText("");
        jLabelClientName.setText("");
        jLabelAddress.setText("");
        jLabelCity.setText("");
        jLabelVatCondition.setText("");
        jLabelDniCuit.setText("");
    }

    private void loadBudgetData(int id) throws Exception {
        ClientBudget budget = budgetController.findById(id);
        if (budget != null && budget.getClient() != null) {
            loadClient(budget.getClient().getId(), false);
            if (budget.getBudgetDate() != null) {
                jDateChooser1.setDate(java.util.Date.from(budget.getBudgetDate().atZone(ZoneId.systemDefault()).toInstant()));
            }
            jTextAreaObs.setText(budget.getDescription());
            jLabelTotal.setText(budget.getTotal() != null ? budget.getTotal().toString() : "0");
            loadedProductCodes.clear();
        }
        List<ClientBudgetDetail> details = detailController.findByBudget(id);
        jTable2.setModel(createBudgetModel(details));
        configureBudgetTable();
        for (ClientBudgetDetail detail : details) {
            if (detail != null && detail.getProductCode() != null) {
                loadedProductCodes.add(detail.getProductCode().trim());
            }
        }
    }

    private DefaultTableModel createEmptyBudgetModel() {
        return new DefaultTableModel(new String[]{"Codigo", "Descripcion", "Cantidad", "Precio", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 1) {
                    Object codeValue = getValueAt(row, 0);
                    return codeValue != null && "99".equals(codeValue.toString().trim());
                }
                return column == 2 || column == 3;
            }
        };
    }

    private DefaultTableModel createBudgetModel(List<ClientBudgetDetail> details) {
        DefaultTableModel tm = createEmptyBudgetModel();
        for (ClientBudgetDetail d : details) {
            Vector<Object> row = new Vector<>();
            row.add(d.getProductCode());
            row.add(d.getDescription());
            row.add(d.getQuantity());
            row.add(d.getPrice());
            BigDecimal line = BigDecimal.ZERO;
            if (d.getQuantity() != null && d.getPrice() != null) {
                line = new BigDecimal(d.getQuantity().toString()).multiply(d.getPrice());
            }
            row.add(line);
            tm.addRow(row);
        }
        return tm;
    }

    private void handleClientSelectedFromSearch(Client client) {
        if (client == null || client.getId() == null) {
            return;
        }
        jTextFieldClientId.setText(client.getId().toString());
        loadClient(client.getId(), true);
    }

    private boolean addProductToBudget(Product product) {
        if (product == null || product.getCode() == null) {
            return false;
        }

        String code = product.getCode().trim();
        if (!code.isEmpty() && loadedProductCodes.contains(code)) {
            JOptionPane.showMessageDialog(this,
                    "El artículo seleccionado ya está cargado en el presupuesto. Modifique la cantidad si es necesario.",
                    "Producto", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        Vector<Object> row = new Vector<>();
        row.add(code);
        row.add(product.getDescription());
        row.add(Float.valueOf(1f));
        BigDecimal price = product.getCashPrice() != null ? product.getCashPrice() : BigDecimal.ZERO;
        row.add(price);
        row.add(price);
        model.addRow(row);
        if (!code.isEmpty()) {
            loadedProductCodes.add(code);
        }
        updateTotals();
        int newRow = model.getRowCount() - 1;
        if (newRow >= 0) {
            jTable2.changeSelection(newRow, 2, false, false);
            jTable2.requestFocusInWindow();
        }
        return true;
    }

    private void addEmptyRow() {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        Vector<Object> row = new Vector<>();
        row.add("99");
        row.add("");
        row.add(Float.valueOf(1f));
        row.add(BigDecimal.ZERO);
        row.add(BigDecimal.ZERO);
        model.addRow(row);
        int newRow = model.getRowCount() - 1;
        if (newRow >= 0) {
            jTable2.changeSelection(newRow, 1, false, false);
            jTable2.requestFocusInWindow();
        }
    }

    private void handleProductSelectedFromSearch(Product product) {
        if (addProductToBudget(product)) {
            jTextFieldSearchProduct.setText("");
            jTextFieldSearchProduct.requestFocusInWindow();
        }
    }

    private void searchProductByCode(String code) {
        if (code == null) {
            return;
        }
        String trimmed = code.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        if ("99".equals(trimmed)) {
            addEmptyRow();
            jTextFieldSearchProduct.setText("");
            return;
        }
        try {
            Product product = productController.findByCode(trimmed);
            if (product == null) {
                JOptionPane.showMessageDialog(this, "El artículo no existe", "Producto", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            handleProductSelectedFromSearch(product);
        } catch (Exception ex) {
            Logger.getLogger(ClientBudgetUpsertView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo buscar el artículo", "Producto", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTotals() {
        if (jTable2 == null) {
            return;
        }
        DefaultTableModel tm = (DefaultTableModel) jTable2.getModel();
        BigDecimal total = BigDecimal.ZERO;
        updatingBudgetTotals = true;
        try {
            for (int i = 0; i < tm.getRowCount(); i++) {
                Object qtyObj = tm.getValueAt(i, 2);
                Object priceObj = tm.getValueAt(i, 3);
                if (qtyObj != null && priceObj != null) {
                    try {
                        BigDecimal qty = new BigDecimal(qtyObj.toString());
                        BigDecimal price = new BigDecimal(priceObj.toString());
                        BigDecimal line = qty.multiply(price);
                        tm.setValueAt(line, i, 4);
                        total = total.add(line);
                    } catch (NumberFormatException ignored) {
                        // ignore invalid numbers
                    }
                }
            }
        } finally {
            updatingBudgetTotals = false;
        }
        jLabelTotal.setText(total.toString());
    }

    private void configureBudgetTable() {
        if (jTable2 == null) {
            return;
        }
        jTable2.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        jTable2.setSurrendersFocusOnKeystroke(true);
        installNumericEditor(jTable2, 2);
        installNumericEditor(jTable2, 3);
        attachBudgetTableListener();
    }

    private void installNumericEditor(JTable table, int columnIndex) {
        if (table.getColumnModel().getColumnCount() <= columnIndex) {
            return;
        }
        JTextField field = new JTextField();
        field.setHorizontalAlignment(JTextField.RIGHT);
        DefaultCellEditor editor = new DefaultCellEditor(field);
        editor.setClickCountToStart(1);
        editor.addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                SwingUtilities.invokeLater(() -> updateTotals());
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
                // no-op
            }
        });
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        column.setCellEditor(editor);
    }

    private void attachBudgetTableListener() {
        if (jTable2 == null) {
            return;
        }
        if (budgetTableModelListener == null) {
            budgetTableModelListener = event -> {
                if (updatingBudgetTotals) {
                    return;
                }
                int type = event.getType();
                if (type == TableModelEvent.UPDATE) {
                    int column = event.getColumn();
                    if (column == TableModelEvent.ALL_COLUMNS || column == 2 || column == 3) {
                        updateTotals();
                    }
                } else if (type == TableModelEvent.INSERT || type == TableModelEvent.DELETE) {
                    updateTotals();
                }
            };
        }
        TableModel model = jTable2.getModel();
        if (budgetTableModel == model) {
            return;
        }
        if (budgetTableModel != null) {
            budgetTableModel.removeTableModelListener(budgetTableModelListener);
        }
        budgetTableModel = model;
        budgetTableModel.addTableModelListener(budgetTableModelListener);
    }

    private void commitBudgetTableEdits() {
        if (jTable2 != null && jTable2.isEditing()) {
            TableCellEditor editor = jTable2.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }
    }

    private boolean validateBudgetRows() {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            Object codeObj = model.getValueAt(i, 0);
            String code = codeObj != null ? codeObj.toString().trim() : "";
            if (code.isEmpty()) {
                continue;
            }
            Object qtyObj = model.getValueAt(i, 2);
            Object priceObj = model.getValueAt(i, 3);
            Object subtotalObj = model.getValueAt(i, 4);
            if (qtyObj == null || priceObj == null || subtotalObj == null) {
                JOptionPane.showMessageDialog(this,
                        "Complete cantidad, precio y subtotal en la fila " + (i + 1),
                        "Guardar",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
            try {
                BigDecimal quantity = new BigDecimal(qtyObj.toString());
                BigDecimal price = new BigDecimal(priceObj.toString());
                BigDecimal subtotal = new BigDecimal(subtotalObj.toString());
                if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "La cantidad debe ser mayor a cero en la fila " + (i + 1),
                            "Guardar",
                            JOptionPane.WARNING_MESSAGE);
                    return false;
                }
                BigDecimal expected = price.multiply(quantity);
                if (!normalize(expected).equals(normalize(subtotal))) {
                    JOptionPane.showMessageDialog(this,
                            "El subtotal no coincide con Cantidad x Precio en la fila " + (i + 1),
                            "Guardar",
                            JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Valores numéricos inválidos en la fila " + (i + 1),
                        "Guardar",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private BigDecimal normalize(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal normalized = value.stripTrailingZeros();
        return normalized.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : normalized;
    }

    private void saveBudget() {
        try {
            commitBudgetTableEdits();
            if (jTextFieldClientId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente", "Guardar", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int clientId = Integer.parseInt(jTextFieldClientId.getText());
            Client client = clientController.findById(clientId);
            if (client == null) {
                JOptionPane.showMessageDialog(this, "El cliente seleccionado ya no existe", "Guardar", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!client.isActive()) {
                JOptionPane.showMessageDialog(this, "El cliente está deshabilitado", "Guardar", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ClientBudget budget = new ClientBudget();
            budget.setClient(client);
            budget.setBudgetDate(jDateChooser1.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            budget.setDescription(jTextAreaObs.getText());
            budget.setTotal(new BigDecimal(jLabelTotal.getText().isEmpty() ? "0" : jLabelTotal.getText()));
            budget.setClosed(false);

            if (!validateBudgetRows()) {
                return;
            }

            DefaultTableModel tm = (DefaultTableModel) jTable2.getModel();
            List<ClientBudgetDetail> details = new ArrayList<>();
            for (int i = 0; i < tm.getRowCount(); i++) {
                Object codeObj = tm.getValueAt(i, 0);
                Object descObj = tm.getValueAt(i, 1);
                Object qtyObj = tm.getValueAt(i, 2);
                Object priceObj = tm.getValueAt(i, 3);
                Object subtotalObj = tm.getValueAt(i, 4);
                if (descObj == null || qtyObj == null || priceObj == null || subtotalObj == null) {
                    continue;
                }
                ClientBudgetDetail d = new ClientBudgetDetail();
                d.setBudget(budget);

                if (codeObj != null) {
                    String code = codeObj.toString().trim();
                    if (!code.isEmpty()) {
                        d.setProductCode("99".equals(code) ? "99" : code);
                    }
                }

                d.setDescription(descObj.toString());
                BigDecimal quantity = new BigDecimal(qtyObj.toString());
                d.setQuantity(quantity.floatValue());
                BigDecimal price = new BigDecimal(priceObj.toString());
                d.setPrice(price);
                details.add(d);
            }
            budget.setDetails(details);

            if (budgetId == null) {
                budgetController.save(budget);
                for (ClientBudgetDetail d : details) detailController.save(d);
            } else {
                budget.setId(budgetId);
                budgetController.update(budget);
                detailController.deleteByBudget(budgetId);
                for (ClientBudgetDetail d : details) {
                    d.setBudget(budget);
                    detailController.save(d);
                }
            }

            printBudgetReport(budget, details);

            if (ClientBudgetManagementView.isOpen) {
                ClientBudgetManagementView.refreshTable();
            }
            open = false;
            dispose();
        } catch (Exception ex) {
            Logger.getLogger(ClientBudgetUpsertView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void printBudgetReport(ClientBudget budget, List<ClientBudgetDetail> details) {
        try {
            budgetReportService.printBudget(budget, details);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Imprimir", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(ClientBudgetUpsertView.class.getName()).log(Level.SEVERE, "Error al imprimir el presupuesto", ex);
            JOptionPane.showMessageDialog(this, "No se pudo generar el informe del presupuesto", "Imprimir", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldClientId = new javax.swing.JTextField();
        jLabelClientName = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabelAddress = new javax.swing.JLabel();
        jLabelCity = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jLabelVatCondition = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabelTotal = new javax.swing.JLabel();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jButtonDeleteItem = new javax.swing.JButton();
        jButtonDeleteAll = new javax.swing.JButton();
        jLabelDniCuit = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jTextFieldSearchProduct = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jButtonSearchProduct = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaObs = new javax.swing.JTextArea();

        setPreferredSize(new java.awt.Dimension(850, 630));
        getContentPane().setLayout(null);
        getContentPane().add(jDateChooser1);
        jDateChooser1.setBounds(610, 10, 120, 20);

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Cod Cliente");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(40, 10, 125, 20);

        jTextFieldClientId.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldClientId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldClientIdActionPerformed(evt);
            }
        });
        jTextFieldClientId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldClientIdKeyPressed(evt);
            }
        });
        getContentPane().add(jTextFieldClientId);
        jTextFieldClientId.setBounds(190, 10, 40, 20);
        getContentPane().add(jLabelClientName);
        jLabelClientName.setBounds(280, 10, 130, 20);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Domicilio");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(40, 70, 125, 20);

        jLabelAddress.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelAddress);
        jLabelAddress.setBounds(190, 70, 150, 20);

        jLabelCity.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelCity);
        jLabelCity.setBounds(190, 40, 150, 20);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Localidad");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(40, 40, 125, 20);

        jLabel8.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Dni/Cuit");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(40, 100, 125, 20);

        jTable2.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Descripcion", "Cantidad", "Precio", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable2KeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(30, 220, 760, 180);

        jLabel11.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Condicion");
        getContentPane().add(jLabel11);
        jLabel11.setBounds(40, 130, 125, 20);

        jLabelVatCondition.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelVatCondition);
        jLabelVatCondition.setBounds(190, 130, 150, 20);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(null);

        jLabel19.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Total:");
        jPanel2.add(jLabel19);
        jLabel19.setBounds(40, 20, 70, 20);

        jLabelTotal.setFont(new java.awt.Font("Calibri", 3, 18)); // NOI18N
        jLabelTotal.setForeground(new java.awt.Color(0, 153, 51));
        jPanel2.add(jLabelTotal);
        jLabelTotal.setBounds(120, 20, 100, 20);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(530, 420, 250, 60);

        jButtonSave.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok.png"))); // NOI18N
        jButtonSave.setText("Guardar");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSave);
        jButtonSave.setBounds(520, 540, 130, 30);

        jButtonCancel.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButtonCancel.setText("Cancelar");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonCancel);
        jButtonCancel.setBounds(660, 540, 130, 30);

        jButtonDeleteItem.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDeleteItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/borrar item.png"))); // NOI18N
        jButtonDeleteItem.setText("Borrar Item");
        jButtonDeleteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteItemActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDeleteItem);
        jButtonDeleteItem.setBounds(520, 500, 130, 30);

        jButtonDeleteAll.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDeleteAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/borrar items.png"))); // NOI18N
        jButtonDeleteAll.setText("Borrar Todo");
        jButtonDeleteAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteAllActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDeleteAll);
        jButtonDeleteAll.setBounds(660, 500, 130, 30);

        jLabelDniCuit.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelDniCuit);
        jLabelDniCuit.setBounds(190, 100, 150, 20);

        jLabel21.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Fecha");
        getContentPane().add(jLabel21);
        jLabel21.setBounds(470, 10, 125, 20);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6);
        jButton6.setBounds(240, 10, 25, 25);

        jTextFieldSearchProduct.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextFieldSearchProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSearchProductActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldSearchProduct);
        jTextFieldSearchProduct.setBounds(170, 180, 340, 22);

        jLabel13.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Buscar Articulo");
        getContentPane().add(jLabel13);
        jLabel13.setBounds(30, 180, 125, 20);

        jButtonSearchProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButtonSearchProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchProductActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSearchProduct);
        jButtonSearchProduct.setBounds(520, 180, 25, 25);

        jTextAreaObs.setColumns(20);
        jTextAreaObs.setRows(5);
        jScrollPane1.setViewportView(jTextAreaObs);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(30, 440, 460, 100);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldClientIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldClientIdActionPerformed
        if (!jTextFieldClientId.getText().isEmpty()) {
            loadClient(Integer.parseInt(jTextFieldClientId.getText()));
        }
    }//GEN-LAST:event_jTextFieldClientIdActionPerformed

    private void jTextFieldClientIdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldClientIdKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            jTextFieldClientIdActionPerformed(null);
        }
    }//GEN-LAST:event_jTextFieldClientIdKeyPressed

    private void jTable2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable2KeyReleased
        updateTotals();
    }//GEN-LAST:event_jTable2KeyReleased

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        saveBudget();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        open = false;
        dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonDeleteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteItemActionPerformed
        int row = jTable2.getSelectedRow();
        if (row != -1) {
            Object codeValue = jTable2.getValueAt(row, 0);
            if (codeValue != null) {
                loadedProductCodes.remove(codeValue.toString().trim());
            }
            ((DefaultTableModel) jTable2.getModel()).removeRow(row);
            updateTotals();
        }
    }//GEN-LAST:event_jButtonDeleteItemActionPerformed

    private void jButtonDeleteAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteAllActionPerformed
        ((DefaultTableModel) jTable2.getModel()).setRowCount(0);
        loadedProductCodes.clear();
        updateTotals();
    }//GEN-LAST:event_jButtonDeleteAllActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (!ClientManagementView.isOpen) {
            ClientManagementView view = new ClientManagementView();
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        }
        ClientManagementView.setClientSelectionListener(this::handleClientSelectedFromSearch, true);
        ClientManagementView.bringToFront();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jTextFieldSearchProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSearchProductActionPerformed
        searchProductByCode(jTextFieldSearchProduct.getText());
    }//GEN-LAST:event_jTextFieldSearchProductActionPerformed

    private void jButtonSearchProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchProductActionPerformed
        if (productController == null) {
            JOptionPane.showMessageDialog(this, "No se pudo inicializar la búsqueda de artículos", "Producto", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!ProductSearchView.isOpen) {
            ProductSearchView view = new ProductSearchView(productController, this::handleProductSelectedFromSearch);
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        } else {
            ProductSearchView.updateSelectionListener(this::handleProductSelectedFromSearch);
            ProductSearchView.bringToFront();
        }
    }//GEN-LAST:event_jButtonSearchProductActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDeleteAll;
    private javax.swing.JButton jButtonDeleteItem;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSearchProduct;
    public static com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelAddress;
    private javax.swing.JLabel jLabelCity;
    private javax.swing.JLabel jLabelClientName;
    private javax.swing.JLabel jLabelDniCuit;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JLabel jLabelVatCondition;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextAreaObs;
    private javax.swing.JTextField jTextFieldClientId;
    private javax.swing.JTextField jTextFieldSearchProduct;
    // End of variables declaration//GEN-END:variables
}

