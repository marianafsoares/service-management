package views.providers;

import controllers.ProviderController;
import controllers.ProviderInvoiceController;
import controllers.ProviderReceiptController;
import configs.MyBatisConfig;
import mappers.ProviderMapper;
import mappers.ProviderInvoiceMapper;
import mappers.receipts.ProviderReceiptMapper;
import org.apache.ibatis.session.SqlSession;
import repositories.ProviderRepository;
import repositories.ProviderInvoiceRepository;
import repositories.ProviderReceiptRepository;
import repositories.impl.ProviderRepositoryImpl;
import repositories.impl.ProviderInvoiceRepositoryImpl;
import repositories.impl.ProviderReceiptRepositoryImpl;
import services.ProviderService;
import services.ProviderInvoiceService;
import services.ProviderReceiptService;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Provider;
import models.receipts.ProviderReceipt;
import models.receipts.ReceiptDetailData;
import models.receipts.ReceiptType;
import views.MainView;
import views.ReceiptDetailView;
import views.providers.ProviderInvoiceDetailView;
import views.providers.ProviderManagementView;
import views.utils.ReceiptDetailLoader;
import utils.Constants;
import utils.InvoiceTypeUtils;
import utils.TableUtils;

public class ProviderHistoryView extends javax.swing.JInternalFrame {

    private ProviderController providerController;
    private ProviderInvoiceController providerInvoiceController;
    private ProviderReceiptController providerReceiptController;
    private SqlSession sqlSession;

    public static boolean isOpen = false;
    private static ProviderHistoryView instance;
    public int providerId;
    private Provider provider;
    public static List<models.ProviderInvoice> invoices = null;
    public static List<ProviderReceipt> receipts = null;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ProviderHistoryView() {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
            ProviderInvoiceMapper providerInvoiceMapper = sqlSession.getMapper(ProviderInvoiceMapper.class);
            ProviderReceiptMapper providerReceiptMapper = sqlSession.getMapper(ProviderReceiptMapper.class);
            ProviderRepository providerRepository = new ProviderRepositoryImpl(providerMapper);
            ProviderInvoiceRepository invoiceRepository = new ProviderInvoiceRepositoryImpl(providerInvoiceMapper);
            ProviderReceiptRepository receiptRepository = new ProviderReceiptRepositoryImpl(providerReceiptMapper);
            ProviderService providerService = new ProviderService(providerRepository, invoiceRepository, receiptRepository);
            ProviderInvoiceService invoiceService = new ProviderInvoiceService(invoiceRepository);
            ProviderReceiptService receiptService = new ProviderReceiptService(receiptRepository);
            this.providerController = new ProviderController(providerService);
            this.providerInvoiceController = new ProviderInvoiceController(invoiceService);
            this.providerReceiptController = new ProviderReceiptController(receiptService);

            initComponents();
            isOpen = true;
            instance = this;

            int i = ProviderManagementView.jTable1.getSelectedRow();
            int id = Integer.parseInt(ProviderManagementView.jTable1.getValueAt(i, 0).toString());

            Provider providerData = providerController.findById(id);
            providerId = id;
            provider = providerData;

            if (providerData != null) {
                jLabel1.setText(providerData.getName());
            }

            invoices = providerInvoiceController.findByProvider(id);
            receipts = providerReceiptController.findByProvider(id);
            jTable1.setModel(this.createHistoryModel(invoices, receipts));
            configureTableColumns();
            updateDeleteAdjustButtonState();

        } catch (Exception ex) {
            Logger.getLogger(ProviderHistoryView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void refreshTableIfOpen() {
        if (instance != null) {
            try {
                instance.refreshTable();
            } catch (Exception ex) {
                Logger.getLogger(ProviderHistoryView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void refreshTable() throws Exception {

        invoices = providerInvoiceController.findByProvider(providerId);
        receipts = providerReceiptController.findByProvider(providerId);
        jTable1.setModel(this.createHistoryModel(invoices, receipts));
        configureTableColumns();
        provider = providerController.findById(providerId);
        updateDeleteAdjustButtonState();
        refreshProviderTable();

    }

    private void refreshProviderTable() {
        try {
            BigDecimal totalBalance = providerController.fillTable(ProviderManagementView.jTable1, null);
            int rows = ProviderManagementView.jTable1.getRowCount();
            for (int i = 0; i < rows; i++) {
                Object val = ProviderManagementView.jTable1.getValueAt(i, 0);
                if (val != null && Integer.parseInt(val.toString()) == providerId) {
                    ProviderManagementView.jTable1.setRowSelectionInterval(i, i);
                    break;
                }
            }
            ProviderManagementView.jLabelTotalProviders.setText(providerController.formatCurrency(totalBalance));
        } catch (Exception ex) {
            Logger.getLogger(ProviderHistoryView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateDeleteAdjustButtonState() {
        boolean canDelete = provider != null && provider.isActive();
        jButtonDeleteAdjust.setEnabled(canDelete);
    }

    private models.ProviderInvoice findInvoice(int row) {
        if (row < 0) {
            return null;
        }
        Object value = jTable1.getValueAt(row, 2);
        if (value == null) {
            return null;
        }
        String numberStr = value.toString();
        String[] parts = numberStr.split("-");
        String pos = parts.length > 0 ? parts[0] : "";
        String num = parts.length > 1 ? parts[1] : "";
        if (invoices != null) {
            for (models.ProviderInvoice invoice : invoices) {
                if (pos.equals(invoice.getPointOfSale()) && num.equals(invoice.getInvoiceNumber())) {
                    return invoice;
                }
            }
        }
        return null;
    }

    private ProviderReceipt findReceipt(int row) {
        if (row < 0) {
            return null;
        }
        Object value = jTable1.getValueAt(row, 2);
        if (value == null) {
            return null;
        }
        String numberStr = value.toString();
        String[] parts = numberStr.split("-");
        String pos = parts.length > 0 ? parts[0] : "";
        String num = parts.length > 1 ? parts[1] : "";
        if (receipts != null) {
            for (ProviderReceipt receipt : receipts) {
                if (pos.equals(receipt.getPointOfSale()) && num.equals(receipt.getReceiptNumber())) {
                    return receipt;
                }
            }
        }
        return null;
    }

    public static models.ProviderInvoice getInvoiceAtRow(int row) {
        return instance != null ? instance.findInvoice(row) : null;
    }

    private DefaultTableModel createHistoryModel(List<models.ProviderInvoice> invoices,
                                                 List<ProviderReceipt> providerReceipts) {

        class HistoryItem {
            LocalDateTime date;
            String type;
            String number;
            BigDecimal total;
            boolean credit;
            BigDecimal partial;

            HistoryItem(LocalDateTime date, String type, String number, BigDecimal total, boolean credit) {
                this.date = date;
                this.type = type;
                this.number = number;
                this.total = total;
                this.credit = credit;
            }
        }

        DefaultTableModel tm = new DefaultTableModel(new String[]{"Fecha", "Tipo", "Numero", "Total", "Parcial"}, 0) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        List<HistoryItem> items = new ArrayList<>();

        if (invoices != null) {
            for (models.ProviderInvoice invoice : invoices) {
                LocalDateTime date = invoice.getInvoiceDate();
                String originalType = invoice.getCategory() != null ? invoice.getCategory().getDescription() : "";
                boolean isCredit = InvoiceTypeUtils.isCreditDocument(originalType)
                        || InvoiceTypeUtils.isCreditDocument(invoice.getInvoiceType());
                String type = originalType;
                if (Constants.AJUSTE.equalsIgnoreCase(type)) {
                    type = Constants.AJUSTE_ABBR;
                }
                String number = String.format("%s-%s", invoice.getPointOfSale(), invoice.getInvoiceNumber());
                items.add(new HistoryItem(date, type, number, invoice.getTotal(), isCredit));
            }
        }

        if (providerReceipts != null) {
            for (ProviderReceipt receipt : providerReceipts) {
                items.add(new HistoryItem(
                        receipt.getReceiptDate(),
                        Constants.RECIBO_ABBR,
                        String.format("%s-%s", receipt.getPointOfSale(), receipt.getReceiptNumber()),
                        receipt.getTotal(),
                        true
                ));
            }
        }

        items.sort(Comparator.comparing((HistoryItem item) -> item.date,
                Comparator.nullsLast(Comparator.naturalOrder())));

        BigDecimal currentBalance = BigDecimal.ZERO;
        for (HistoryItem item : items) {
            if (item.date == null || item.total == null) {
                continue;
            }
            if (item.credit) {
                currentBalance = currentBalance.subtract(item.total);
            } else {
                currentBalance = currentBalance.add(item.total);
            }
            item.partial = currentBalance.setScale(2, RoundingMode.HALF_EVEN);
        }

        for (int i = items.size() - 1; i >= 0; i--) {
            HistoryItem item = items.get(i);
            if (item.date == null || item.total == null || item.partial == null) {
                continue;
            }
            Vector row = new Vector();
            row.add(item.date.format(DATE_FORMATTER));
            row.add(item.type);
            row.add(item.number);
            row.add(item.total);
            row.add(item.partial);
            tm.addRow(row);
        }

        return tm;

    }

    private void configureTableColumns() {
        TableUtils.configureProviderHistoryViewTable(jTable1);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonDetail = new javax.swing.JButton();
        jButtonPrint = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jButtonReturn = new javax.swing.JButton();
        jButtonDeleteAdjust = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(573, 600));
        setPreferredSize(new java.awt.Dimension(573, 600));
        getContentPane().setLayout(null);

        jButtonDetail.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButtonDetail.setText("Ver");
        jButtonDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetailActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDetail);
        jButtonDetail.setBounds(40, 20, 120, 30);

        jButtonPrint.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/imprimir.png"))); // NOI18N
        jButtonPrint.setText("Imprimir");
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonPrint);
        jButtonPrint.setBounds(180, 20, 120, 30);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Fecha", "Tipo", "Numero", "Total", "Parcial"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(30, 120, 490, 360);

        jLabel1.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(40, 80, 200, 20);

        jButtonReturn.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonReturn.setText("Volver");
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReturn);
        jButtonReturn.setBounds(210, 510, 120, 30);

        jButtonDeleteAdjust.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDeleteAdjust.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/borrar item.png"))); // NOI18N
        jButtonDeleteAdjust.setText("Eliminar Ajuste");
        jButtonDeleteAdjust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteAdjustActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDeleteAdjust);
        jButtonDeleteAdjust.setBounds(320, 20, 180, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        isOpen = false;
        instance = null;
        dispose();
    }//GEN-LAST:event_jButtonReturnActionPerformed

    private void jButtonDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetailActionPerformed

        int i = ProviderHistoryView.jTable1.getSelectedRow();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningun comprobante", "Ver", JOptionPane.OK_OPTION);
            return;
        }

        String tipo = ProviderHistoryView.jTable1.getValueAt(i, 1).toString();
        models.ProviderInvoice invoice = findInvoice(i);

        if (Constants.AJUSTE_ABBR.equals(tipo) || Constants.AJUSTE.equalsIgnoreCase(tipo)) {
            if (invoice != null) {
                StringBuilder sb = new StringBuilder();
                if (invoice.getInvoiceDate() != null) {
                    sb.append("Fecha: ").append(invoice.getInvoiceDate().toLocalDate()).append("\n");
                }
                sb.append("Pto. Venta: ").append(invoice.getPointOfSale()).append("\n");
                sb.append("Número: ").append(invoice.getInvoiceNumber()).append("\n");
                sb.append("Monto: ").append(invoice.getTotal());
                if (invoice.getDescription() != null && !invoice.getDescription().isEmpty()) {
                    sb.append("\nDetalle: ").append(invoice.getDescription());
                }
                JOptionPane.showMessageDialog(this, sb.toString(), "Ajuste", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el ajuste seleccionado", "Ver", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }

        if (Constants.RECIBO_ABBR.equals(tipo)) {
            ProviderReceipt receipt = findReceipt(i);
            if (receipt != null) {
                if (!ReceiptDetailView.isOpen(ReceiptType.PROVIDER)) {
                    ReceiptDetailData detailData = ReceiptDetailLoader.loadProviderReceipt(sqlSession, receipt, providerController);
                    ReceiptDetailView view = new ReceiptDetailView(detailData);
                    MainView.jDesktopPane1.add(view);
                    view.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el recibo seleccionado", "Ver", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }

        if (invoice == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el comprobante seleccionado", "Ver", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ProviderInvoiceDetailView.isOpen) {
            try {
                ProviderInvoiceDetailView providerInvoiceDetailView = new ProviderInvoiceDetailView();
                MainView.jDesktopPane1.add(providerInvoiceDetailView);
                providerInvoiceDetailView.setVisible(true);
            } catch (Exception ex) {
                Logger.getLogger(ProviderHistoryView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButtonDetailActionPerformed

    private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed
        if (provider == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el proveedor", "Imprimir", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (ProviderHistorySearchByDateView.isOpen) {
            JOptionPane.showMessageDialog(this, "La ventana de impresión ya está abierta", "Imprimir", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            ProviderHistorySearchByDateView view = new ProviderHistorySearchByDateView(provider);
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
            try {
                view.setSelected(true);
            } catch (PropertyVetoException ignored) {
            }
        } catch (Exception ex) {
            Logger.getLogger(ProviderHistoryView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo abrir la ventana de impresión", "Imprimir", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButtonPrintActionPerformed

    private void jButtonDeleteAdjustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteAdjustActionPerformed
        if (provider == null || !provider.isActive()) {
            JOptionPane.showMessageDialog(this, "El proveedor está deshabilitado", "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningun comprobante", "Eliminar", JOptionPane.OK_OPTION);
            return;
        }
        String tipo = jTable1.getValueAt(row, 1).toString();
        if (!Constants.AJUSTE_ABBR.equals(tipo) && !Constants.AJUSTE.equalsIgnoreCase(tipo)) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un ajuste", "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        models.ProviderInvoice invoice = findInvoice(row);
        if (invoice == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el ajuste", "Eliminar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int option = JOptionPane.showConfirmDialog(this, "¿Desea eliminar el ajuste seleccionado?", "Eliminar", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                providerInvoiceController.delete(invoice.getId());
                refreshTable();
                JOptionPane.showMessageDialog(this, "Ajuste eliminado", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                Logger.getLogger(ProviderHistoryView.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error eliminando el ajuste", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButtonDeleteAdjustActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDeleteAdjust;
    private javax.swing.JButton jButtonDetail;
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonReturn;
    public static javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}
