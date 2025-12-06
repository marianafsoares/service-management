package views;

import controllers.ClientController;
import controllers.ClientInvoiceController;
import controllers.ProviderController;
import controllers.ProviderInvoiceController;
import controllers.InvoiceCategoryController;
import configs.AppConfig;
import configs.MyBatisConfig;
import mappers.ClientMapper;
import mappers.ClientInvoiceMapper;
import mappers.receipts.ClientReceiptMapper;
import mappers.ProviderMapper;
import mappers.ProviderInvoiceMapper;
import mappers.receipts.ProviderReceiptMapper;
import mappers.InvoiceCategoryMapper;
import models.Client;
import models.ClientInvoice;
import models.InvoiceCategory;
import models.Provider;
import models.ProviderInvoice;
import org.apache.ibatis.session.SqlSession;
import repositories.ClientRepository;
import repositories.ClientInvoiceRepository;
import repositories.ClientReceiptRepository;
import repositories.ProviderRepository;
import repositories.ProviderInvoiceRepository;
import repositories.ProviderReceiptRepository;
import repositories.InvoiceCategoryRepository;
import repositories.impl.ClientRepositoryImpl;
import repositories.impl.ClientInvoiceRepositoryImpl;
import repositories.impl.ClientReceiptRepositoryImpl;
import repositories.impl.ProviderRepositoryImpl;
import repositories.impl.ProviderInvoiceRepositoryImpl;
import repositories.impl.ProviderReceiptRepositoryImpl;
import repositories.impl.InvoiceCategoryRepositoryImpl;
import services.ClientService;
import services.ClientInvoiceService;
import services.ProviderService;
import services.ProviderInvoiceService;
import utils.DocumentValidator;
import services.InvoiceCategoryService;
import utils.Constants;
import utils.CuitSelectorUtils;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import views.clients.ClientManagementView;
import views.providers.ProviderManagementView;
import views.providers.ProviderHistoryView;

public class AdjustInsertView extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;

    private final ClientController clientController;
    private final ClientInvoiceController clientInvoiceController;
    private final ProviderController providerController;
    private final ProviderInvoiceController providerInvoiceController;
    private final InvoiceCategoryController invoiceCategoryController;
    private final Client client;
    private final Provider provider;
    private boolean updatingPointOfSale = false;
    private final SqlSession sqlSession;

    public AdjustInsertView(Client client) throws Exception {
        this.client = client;
        this.provider = null;
        sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
        ClientInvoiceMapper clientInvoiceMapper = sqlSession.getMapper(ClientInvoiceMapper.class);
        ClientReceiptMapper clientReceiptMapper = sqlSession.getMapper(ClientReceiptMapper.class);
        ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
        ProviderInvoiceMapper providerInvoiceMapper = sqlSession.getMapper(ProviderInvoiceMapper.class);
        ProviderReceiptMapper providerReceiptMapper = sqlSession.getMapper(ProviderReceiptMapper.class);
        InvoiceCategoryMapper invoiceCategoryMapper = sqlSession.getMapper(InvoiceCategoryMapper.class);
        ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
        ClientInvoiceRepository clientInvoiceRepository = new ClientInvoiceRepositoryImpl(clientInvoiceMapper);
        ClientReceiptRepository clientReceiptRepository = new ClientReceiptRepositoryImpl(clientReceiptMapper);
        ProviderRepository providerRepository = new ProviderRepositoryImpl(providerMapper);
        ProviderInvoiceRepository providerInvoiceRepository = new ProviderInvoiceRepositoryImpl(providerInvoiceMapper);
        ProviderReceiptRepository providerReceiptRepository = new ProviderReceiptRepositoryImpl(providerReceiptMapper);
        InvoiceCategoryRepository invoiceCategoryRepository = new InvoiceCategoryRepositoryImpl(invoiceCategoryMapper);
        ClientService clientService = new ClientService(clientRepository, clientInvoiceRepository, clientReceiptRepository);
        ClientInvoiceService clientInvoiceService = new ClientInvoiceService(clientInvoiceRepository);
        ProviderService providerService = new ProviderService(providerRepository, providerInvoiceRepository, providerReceiptRepository);
        ProviderInvoiceService providerInvoiceService = new ProviderInvoiceService(providerInvoiceRepository);
        InvoiceCategoryService invoiceCategoryService = new InvoiceCategoryService(invoiceCategoryRepository);
        clientController = new ClientController(clientService);
        clientInvoiceController = new ClientInvoiceController(clientInvoiceService);
        providerController = new ProviderController(providerService);
        providerInvoiceController = new ProviderInvoiceController(providerInvoiceService);
        invoiceCategoryController = new InvoiceCategoryController(invoiceCategoryService);
        initComponents();
        loadIssuerCuits();
        loadPointOfSales();
        initDefaultPointOfSale();
        isOpen = true;
        if (client != null) {
            jLabel1.setText(String.valueOf(client.getId()));
            jLabel2.setText(client.getFullName());
        }
    }

    public AdjustInsertView(Provider provider) throws Exception {
        this.client = null;
        this.provider = provider;
        sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
        ClientInvoiceMapper clientInvoiceMapper = sqlSession.getMapper(ClientInvoiceMapper.class);
        ClientReceiptMapper clientReceiptMapper = sqlSession.getMapper(ClientReceiptMapper.class);
        ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
        ProviderInvoiceMapper providerInvoiceMapper = sqlSession.getMapper(ProviderInvoiceMapper.class);
        ProviderReceiptMapper providerReceiptMapper = sqlSession.getMapper(ProviderReceiptMapper.class);
        InvoiceCategoryMapper invoiceCategoryMapper = sqlSession.getMapper(InvoiceCategoryMapper.class);
        ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
        ClientInvoiceRepository clientInvoiceRepository = new ClientInvoiceRepositoryImpl(clientInvoiceMapper);
        ClientReceiptRepository clientReceiptRepository = new ClientReceiptRepositoryImpl(clientReceiptMapper);
        ProviderRepository providerRepository = new ProviderRepositoryImpl(providerMapper);
        ProviderInvoiceRepository providerInvoiceRepository = new ProviderInvoiceRepositoryImpl(providerInvoiceMapper);
        ProviderReceiptRepository providerReceiptRepository = new ProviderReceiptRepositoryImpl(providerReceiptMapper);
        InvoiceCategoryRepository invoiceCategoryRepository = new InvoiceCategoryRepositoryImpl(invoiceCategoryMapper);
        ClientService clientService = new ClientService(clientRepository, clientInvoiceRepository, clientReceiptRepository);
        ClientInvoiceService clientInvoiceService = new ClientInvoiceService(clientInvoiceRepository);
        ProviderService providerService = new ProviderService(providerRepository, providerInvoiceRepository, providerReceiptRepository);
        ProviderInvoiceService providerInvoiceService = new ProviderInvoiceService(providerInvoiceRepository);
        InvoiceCategoryService invoiceCategoryService = new InvoiceCategoryService(invoiceCategoryRepository);
        clientController = new ClientController(clientService);
        clientInvoiceController = new ClientInvoiceController(clientInvoiceService);
        providerController = new ProviderController(providerService);
        providerInvoiceController = new ProviderInvoiceController(providerInvoiceService);
        invoiceCategoryController = new InvoiceCategoryController(invoiceCategoryService);
        initComponents();
        loadIssuerCuits();
        loadPointOfSales();
        initDefaultPointOfSale();
        isOpen = true;
        if (provider != null) {
            jLabel1.setText(String.valueOf(provider.getId()));
            jLabel2.setText(provider.getName());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jComboBoxIssuerCuit = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabelNumeroComprobante = new javax.swing.JLabel();
        jComboBoxPtoVenta = new javax.swing.JComboBox();

        setTitle("Ajuste");
        setMinimumSize(new java.awt.Dimension(460, 550));
        setPreferredSize(new java.awt.Dimension(460, 550));
        getContentPane().setLayout(null);

        jButton1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok.png"))); // NOI18N
        jButton1.setText("Guardar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(100, 410, 130, 27);

        jButton2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButton2.setText("Cancelar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(240, 410, 130, 27);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        getContentPane().add(jLabel1);
        jLabel1.setBounds(60, 90, 50, 20);

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        getContentPane().add(jLabel2);
        jLabel2.setBounds(120, 90, 250, 20);

        jLabel11.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Detalle");
        getContentPane().add(jLabel11);
        jLabel11.setBounds(10, 200, 98, 20);

        jTextField1.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        getContentPane().add(jTextField1);
        jTextField1.setBounds(120, 160, 160, 21);

        jLabel12.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Cuit emisor");
        getContentPane().add(jLabel12);
        jLabel12.setBounds(10, 120, 98, 20);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(120, 200, 290, 160);

        getContentPane().add(jComboBoxIssuerCuit);
        jComboBoxIssuerCuit.setBounds(120, 120, 160, 22);

        jLabel13.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Monto");
        getContentPane().add(jLabel13);
        jLabel13.setBounds(10, 160, 98, 20);

        jLabel3.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("-");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(310, 30, 20, 30);

        jLabelNumeroComprobante.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        getContentPane().add(jLabelNumeroComprobante);
        jLabelNumeroComprobante.setBounds(340, 30, 120, 30);

        jComboBoxPtoVenta.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jComboBoxPtoVenta.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxPtoVentaItemStateChanged(evt);
            }
        });
        getContentPane().add(jComboBoxPtoVenta);
        jComboBoxPtoVenta.setBounds(240, 30, 70, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private boolean updatingPos = false;

    private void initDefaultPointOfSale() {
        String pto = AppConfig.get("pos.default", "");
        try {
            pto = String.format("%05d", Integer.parseInt(pto));
        } catch (NumberFormatException ex) {
            if (jComboBoxPtoVenta.getItemCount() > 0) {
                pto = jComboBoxPtoVenta.getItemAt(0).toString();
            } else {
                pto = "00001";
            }
        }
        updatingPos = true;
        jComboBoxPtoVenta.setSelectedItem(pto);
        updatingPos = false;
        updateNextNumber(pto);
    }

    private void jComboBoxPtoVentaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxPtoVentaItemStateChanged
        if (evt.getStateChange() != java.awt.event.ItemEvent.SELECTED) {
            return;
        }
        if (updatingPointOfSale) {
            return;
        }

        Object sel = jComboBoxPtoVenta.getSelectedItem();
        String current = sel != null ? sel.toString() : "";
        String pto;

        try {
            pto = String.format("%05d", Integer.parseInt(current));
        } catch (NumberFormatException ex) {
            String def = AppConfig.get("pos.default", "1");
            try {
                pto = String.format("%05d", Integer.parseInt(def));
            } catch (NumberFormatException e2) {
                pto = "00001";
            }
        }

        // Solo reescribir si realmente cambia (evita loops innecesarios)
        if (!pto.equals(current)) {
            try {
                updatingPointOfSale = true;
                if (jComboBoxPtoVenta.isEditable()) {
                    // Si es editable, fijá el texto del editor en lugar de cambiar la selección
                    jComboBoxPtoVenta.getEditor().setItem(pto);
                } else {
                    jComboBoxPtoVenta.setSelectedItem(pto);
                }
            } finally {
                updatingPointOfSale = false;
            }
        }

        String cuit = provider != null ? provider.getDocumentNumber() : DocumentValidator.normalizeCuit((String) jComboBoxIssuerCuit.getSelectedItem());
        int ultFac = getLastInvoiceNumber(pto, cuit) + 1;
        jLabelNumeroComprobante.setText(String.format("%08d", ultFac));
    }//GEN-LAST:event_jComboBoxPtoVentaItemStateChanged

    private void jComboBoxIssuerCuitItemStateChanged(java.awt.event.ItemEvent evt) {
        String pto = jComboBoxPtoVenta.getSelectedItem() != null ? jComboBoxPtoVenta.getSelectedItem().toString() : "00001";
        updateNextNumber(pto);
    }

    private void updateNextNumber(String pointOfSale) {
        String cuit = provider != null ? provider.getDocumentNumber() : DocumentValidator.normalizeCuit((String) jComboBoxIssuerCuit.getSelectedItem());
        int ultFac = getLastInvoiceNumber(pointOfSale, cuit) + 1;
        jLabelNumeroComprobante.setText(String.format("%08d", ultFac));
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        isOpen = false;
        dispose();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        if (!isValidForm()) {
            return;
        }
        try {
            BigDecimal amount = new BigDecimal(jTextField1.getText());
            String description = jTextArea1.getText() == null ? "" : jTextArea1.getText().toUpperCase();
            String selectedCuit = provider != null ? provider.getDocumentNumber() : DocumentValidator.normalizeCuit((String) jComboBoxIssuerCuit.getSelectedItem());

            if (client != null) {
                ClientInvoice invoice = new ClientInvoice();
                invoice.setClient(client);
                invoice.setIssuerCuit(selectedCuit);
                invoice.setInvoiceType(Constants.AJUSTE_ABBR);
                invoice.setInvoiceNumber(jLabelNumeroComprobante.getText());
                invoice.setPointOfSale(jComboBoxPtoVenta.getSelectedItem().toString());
                invoice.setInvoiceDate(LocalDateTime.now());
                invoice.setSubtotal(amount);
                invoice.setTotal(amount);
                invoice.setDescription(description);
                clientInvoiceController.save(invoice);

            } else if (provider != null) {
                ProviderInvoice invoice = new ProviderInvoice();
                invoice.setProvider(provider);
                InvoiceCategory category = invoiceCategoryController.findAll().stream()
                        .filter(c -> Constants.AJUSTE.equalsIgnoreCase(c.getDescription()))
                        .filter(c -> "PROVIDER".equalsIgnoreCase(c.getType()))
                        .findFirst()
                        .orElse(null);
                if (category == null) {
                    category = new InvoiceCategory();
                    category.setDescription(Constants.AJUSTE);
                    category.setEnabled(true);
                    category.setType("PROVIDER");
                    invoiceCategoryController.create(category);
                }
                invoice.setCategory(category);
                invoice.setInvoiceNumber(jLabelNumeroComprobante.getText());
                invoice.setPointOfSale(jComboBoxPtoVenta.getSelectedItem().toString());
                invoice.setReceiverCuit(selectedCuit);
                invoice.setInvoiceDate(LocalDateTime.now());
                invoice.setTotal(amount);
                invoice.setSubtotal(amount);
                invoice.setDescription(description);
                invoice.setInvoiceType(Constants.AJUSTE_ABBR);
                providerInvoiceController.save(invoice);

            }

            if (client != null) {
                refreshClientTable();
            } else if (provider != null) {
                refreshProviderTable();
                ProviderHistoryView.refreshTableIfOpen();
            }
            JOptionPane.showMessageDialog(this, "El ajuste ha sido cargado!", "Guardar", JOptionPane.INFORMATION_MESSAGE);
            isOpen = false;
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error formato decimal. Use .", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(AdjustInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refreshClientTable() {
        try {
            BigDecimal totalBalance = clientController.fillTable(ClientManagementView.jTable1, null);
            int rows = ClientManagementView.jTable1.getRowCount();
            int clientId = client != null ? client.getId() : -1;
            for (int i = 0; i < rows; i++) {
                Object val = ClientManagementView.jTable1.getValueAt(i, 0);
                if (val != null && Integer.parseInt(val.toString()) == clientId) {
                    ClientManagementView.jTable1.setRowSelectionInterval(i, i);
                    break;
                }
            }
            ClientManagementView.jLabelsaldoDeudor.setText(clientController.formatCurrency(totalBalance));
        } catch (Exception ex) {
            Logger.getLogger(AdjustInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refreshProviderTable() {
        try {
            BigDecimal totalBalance = providerController.fillTable(ProviderManagementView.jTable1, null);
            int rows = ProviderManagementView.jTable1.getRowCount();
            int providerId = provider != null ? provider.getId() : -1;
            for (int i = 0; i < rows; i++) {
                Object val = ProviderManagementView.jTable1.getValueAt(i, 0);
                if (val != null && Integer.parseInt(val.toString()) == providerId) {
                    ProviderManagementView.jTable1.setRowSelectionInterval(i, i);
                    break;
                }
            }
            ProviderManagementView.jLabelTotalProviders.setText(providerController.formatCurrency(totalBalance));
        } catch (Exception ex) {
            Logger.getLogger(AdjustInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadIssuerCuits() {
        CuitSelectorUtils.populateCuits(jComboBoxIssuerCuit);
    }

    private void loadPointOfSales() {
        jComboBoxPtoVenta.removeAllItems();
        String points = AppConfig.get("pos.list", "");
        if (points != null && !points.trim().isEmpty()) {
            for (String p : points.split(",")) {
                String trimmed = p.trim();
                if (!trimmed.isEmpty()) {
                    try {
                        String formatted = String.format("%05d", Integer.parseInt(trimmed));
                        jComboBoxPtoVenta.addItem(formatted);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
    }

    private boolean isValidForm() {
        if (jTextField1.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar algún monto", "Atención", JOptionPane.WARNING_MESSAGE);
            jTextField1.requestFocus();
            return false;
        }
        if (jComboBoxIssuerCuit.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cuit emisor", "Atención", JOptionPane.WARNING_MESSAGE);
            jComboBoxIssuerCuit.requestFocus();
            return false;
        }
        return true;
    }

    private int getLastInvoiceNumber(String pointOfSale, String cuit) {
        if (client != null) {
            return clientInvoiceController.findAll().stream()
                    .filter(inv -> matchesPointOfSale(pointOfSale, inv.getPointOfSale()))
                    .filter(inv -> cuit != null && cuit.equals(inv.getIssuerCuit()))
                    .filter(inv -> Constants.AJUSTE_ABBR.equals(inv.getInvoiceType()))
                    .mapToInt(inv -> {
                        try {
                            return Integer.parseInt(inv.getInvoiceNumber());
                        } catch (NumberFormatException ex) {
                            return 0;
                        }
                    })
                    .max()
                    .orElse(0);
        } else if (provider != null) {
            return providerInvoiceController.findAll().stream()
                    .filter(inv -> matchesPointOfSale(pointOfSale, inv.getPointOfSale()))
                    .filter(inv -> cuit != null && cuit.equals(inv.getReceiverCuit()))
                    .filter(inv -> inv.getCategory() != null &&
                            Constants.AJUSTE.equalsIgnoreCase(inv.getCategory().getDescription()))
                    .mapToInt(inv -> {
                        try {
                            return Integer.parseInt(inv.getInvoiceNumber());
                        } catch (NumberFormatException ex) {
                            return 0;
                        }
                    })
                    .max()
                    .orElse(0);
        }
        return 0;
    }

    private boolean matchesPointOfSale(String expected, String actual) {
        String normalizedExpected = normalizePointOfSale(expected);
        String normalizedActual = normalizePointOfSale(actual);

        if (normalizedExpected != null && normalizedActual != null) {
            return normalizedExpected.equals(normalizedActual);
        }

        if (expected == null || actual == null) {
            return expected == null && actual == null;
        }

        return expected.trim().equals(actual.trim());
    }

    private String normalizePointOfSale(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        try {
            return Integer.toString(Integer.parseInt(trimmed));
        } catch (NumberFormatException ex) {
            return trimmed;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBoxIssuerCuit;
    public static javax.swing.JComboBox jComboBoxPtoVenta;
    public static javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    public static javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    public static javax.swing.JLabel jLabelNumeroComprobante;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    public static javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
