package views.clients;

import controllers.ClientController;
import controllers.ClientInvoiceController;
import controllers.ClientReceiptController;
import configs.MyBatisConfig;
import mappers.ClientMapper;
import mappers.ClientInvoiceMapper;
import mappers.receipts.ClientReceiptMapper;
import org.apache.ibatis.session.SqlSession;
import repositories.ClientRepository;
import repositories.ClientInvoiceRepository;
import repositories.ClientReceiptRepository;
import repositories.impl.ClientRepositoryImpl;
import repositories.impl.ClientInvoiceRepositoryImpl;
import repositories.impl.ClientReceiptRepositoryImpl;
import services.ClientService;
import services.ClientInvoiceService;
import services.ClientReceiptService;
import java.awt.print.PrinterException;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Client;
import models.ClientInvoice;
import models.receipts.ClientReceipt;
import models.receipts.ReceiptDetailData;
import models.receipts.ReceiptType;
import views.MainView;
import views.ReceiptDetailView;
import views.clients.ClientManagementView;
import views.utils.ReceiptDetailLoader;
import utils.Constants;
import utils.InvoiceTypeUtils;
import utils.TableUtils;
import services.reports.ClientReceiptPrintService;
import services.reports.ClientReceiptPrintService.ReceiptPrintException;

/**
 *
 * @author Mariana
 */
public class ClientHistoryView extends javax.swing.JInternalFrame {

    private ClientController clientController;
    private ClientInvoiceController clientInvoiceController;
    private ClientReceiptController clientReceiptController;
    private SqlSession sqlSession;
    private Client client;
    private final ClientReceiptPrintService receiptPrintService = new ClientReceiptPrintService();

    public static boolean isOpen = false;
    public static int clientId;
    public static List<ClientInvoice> invoices = null;
    public static List<ClientReceipt> receipts = null;

    public ClientHistoryView() {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
            ClientInvoiceMapper clientInvoiceMapper = sqlSession.getMapper(ClientInvoiceMapper.class);
            ClientReceiptMapper clientReceiptMapper = sqlSession.getMapper(ClientReceiptMapper.class);
            ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
            ClientInvoiceRepository invoiceRepository = new ClientInvoiceRepositoryImpl(clientInvoiceMapper);
            ClientReceiptRepository receiptRepository = new ClientReceiptRepositoryImpl(clientReceiptMapper);
            ClientService clientService = new ClientService(clientRepository, invoiceRepository, receiptRepository);
            ClientInvoiceService invoiceService = new ClientInvoiceService(invoiceRepository);
            ClientReceiptService receiptService = new ClientReceiptService(receiptRepository);
            this.clientController = new ClientController(clientService);
            this.clientInvoiceController = new ClientInvoiceController(invoiceService);
            this.clientReceiptController = new ClientReceiptController(receiptService);

            initComponents();
            isOpen = true;

            int i = ClientManagementView.jTable1.getSelectedRow();
            int id = Integer.parseInt(ClientManagementView.jTable1.getValueAt(i, 0).toString());

            client = clientController.findById(id);
            clientId = id;

            jLabelCliente.setText(client != null ? client.getFullName() : "");
            jLabelCodigo.setText(String.valueOf(id));

            invoices = clientInvoiceController.findByClient(id);
            receipts = clientReceiptController.findByClient(id);
            jTable1.setModel(createHistoryTableModel(invoices, receipts));
            configureTableColumns();
            updateDeleteAdjustButtonState();

        } catch (Exception ex) {
            Logger.getLogger(ClientHistoryView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void refreshTable() throws Exception {

        invoices = clientInvoiceController.findByClient(clientId);
        receipts = clientReceiptController.findByClient(clientId);
        jTable1.setModel(createHistoryTableModel(invoices, receipts));
        configureTableColumns();
        client = clientController.findById(clientId);
        updateDeleteAdjustButtonState();
        refreshClientTable();

    }

    private void refreshClientTable() {
        try {
            BigDecimal totalBalance = clientController.fillTable(ClientManagementView.jTable1, null);
            int rows = ClientManagementView.jTable1.getRowCount();
            for (int i = 0; i < rows; i++) {
                Object val = ClientManagementView.jTable1.getValueAt(i, 0);
                if (val != null && Integer.parseInt(val.toString()) == clientId) {
                    ClientManagementView.jTable1.setRowSelectionInterval(i, i);
                    break;
                }
            }
            ClientManagementView.jLabelsaldoDeudor.setText(clientController.formatCurrency(totalBalance));
        } catch (Exception ex) {
            Logger.getLogger(ClientHistoryView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateDeleteAdjustButtonState() {
        boolean canDelete = client != null && client.isActive();
        jButtonDeleteAdjust.setEnabled(canDelete);
    }

    private DefaultTableModel createHistoryTableModel(List<models.ClientInvoice> invoices, List<models.receipts.ClientReceipt> receipts) {

        class HistoryItem {
            LocalDateTime date;
            String type;
            String number;
            BigDecimal total;
            BigDecimal partial;

            HistoryItem(LocalDateTime date, String type, String number, BigDecimal total) {
                this.date = date;
                this.type = type;
                this.number = number;
                this.total = total;
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
            for (models.ClientInvoice invoice : invoices) {
                LocalDateTime date = invoice.getInvoiceDate();
                String type = invoice.getInvoiceType();
                String number = formatNumber(invoice.getPointOfSale(), invoice.getInvoiceNumber());
                BigDecimal total = invoice.getTotal();
                items.add(new HistoryItem(date, type, number, total));
            }
        }

        if (receipts != null) {
            for (models.receipts.ClientReceipt receipt : receipts) {
                LocalDateTime date = receipt.getReceiptDate();
                String number = formatNumber(receipt.getPointOfSale(), receipt.getReceiptNumber());
                BigDecimal total = receipt.getTotal();
                items.add(new HistoryItem(date, Constants.RECIBO_ABBR, number, total));
            }
        }

        Comparator<HistoryItem> comparator = Comparator
                .comparing((HistoryItem i) -> i.date, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(i -> buildSortableNumber(i.number), Comparator.nullsLast(Comparator.naturalOrder()));
        items.sort(comparator.reversed());

        BigDecimal currentBalance = BigDecimal.ZERO;
        for (int idx = items.size() - 1; idx >= 0; idx--) {
            HistoryItem item = items.get(idx);
            if (item.date == null || item.total == null) {
                continue;
            }
            if (Constants.NOTA_CREDITO_A_ABBR.equals(item.type)
                    || Constants.NOTA_CREDITO_B_ABBR.equals(item.type)
                    || Constants.NOTA_DEVOLUCION_ABBR.equals(item.type)
                    || Constants.RECIBO_ABBR.equals(item.type)) {
                currentBalance = currentBalance.subtract(item.total);
            } else {
                currentBalance = currentBalance.add(item.total);
            }
            item.partial = currentBalance.setScale(2, RoundingMode.HALF_EVEN);
        }

        for (HistoryItem item : items) {
            if (item.date == null) continue;
            Vector row = new Vector();
            row.add(item.date.toLocalDate());
            row.add(InvoiceTypeUtils.toStorageValue(item.type));
            row.add(item.number);
            row.add(item.total);
            row.add(item.partial);
            tm.addRow(row);
        }

        return tm;

    }

    private String formatNumber(String pointOfSale, String number) {
        return String.format("%s-%s", leftPadDigits(pointOfSale, 4), leftPadDigits(number, 8));
    }

    private String normalizeNumberPart(String value) {
        if (value == null) {
            return "";
        }
        String digits = value.replaceAll("[^0-9]", "");
        return digits.replaceFirst("^0+", "");
    }

    private String buildSortableNumber(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^0-9]", "");
    }

    private String leftPadDigits(String value, int size) {
        String normalized = value == null ? "" : value.replaceAll("[^0-9]", "");
        if (normalized.length() > size) {
            normalized = normalized.substring(normalized.length() - size);
        }
        normalized = normalized.isEmpty() ? "0" : normalized;
        return String.format(Locale.getDefault(), "%" + size + "s", normalized).replace(' ', '0');
    }

    private void configureTableColumns() {
        TableUtils.configureClientHistoryViewTable(jTable1);
    }

    private ClientInvoice findInvoice(int row) {
        String numberStr = jTable1.getValueAt(row, 2).toString();
        String[] parts = numberStr.split("-");
        String pos = normalizeNumberPart(parts.length > 0 ? parts[0] : "");
        String num = normalizeNumberPart(parts.length > 1 ? parts[1] : "");
        if (invoices != null) {
            for (ClientInvoice inv : invoices) {
                if (pos.equals(normalizeNumberPart(inv.getPointOfSale()))
                        && num.equals(normalizeNumberPart(inv.getInvoiceNumber()))) {
                    return inv;
                }
            }
        }
        return null;
    }

    private ClientReceipt findReceipt(int row) {
        String numberStr = jTable1.getValueAt(row, 2).toString();
        String[] parts = numberStr.split("-");
        String pos = normalizeNumberPart(parts.length > 0 ? parts[0] : "");
        String num = normalizeNumberPart(parts.length > 1 ? parts[1] : "");
        if (receipts != null) {
            for (ClientReceipt r : receipts) {
                if (pos.equals(normalizeNumberPart(r.getPointOfSale()))
                        && num.equals(normalizeNumberPart(r.getReceiptNumber()))) {
                    return r;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonDetail = new javax.swing.JButton();
        jButtonPrint = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabelCliente = new javax.swing.JLabel();
        jButtonReturn = new javax.swing.JButton();
        jButtonDeleteAdjust = new javax.swing.JButton();
        jLabelCodigo = new javax.swing.JLabel();

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
        jButtonDetail.setBounds(40, 30, 150, 30);

        jButtonPrint.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/imprimir.png"))); // NOI18N
        jButtonPrint.setText("Imprimir");
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonPrint);
        jButtonPrint.setBounds(200, 30, 150, 30);

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

        jLabelCliente.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        getContentPane().add(jLabelCliente);
        jLabelCliente.setBounds(110, 80, 200, 20);

        jButtonReturn.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonReturn.setText("Volver");
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReturn);
        jButtonReturn.setBounds(210, 500, 120, 30);

        jButtonDeleteAdjust.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDeleteAdjust.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/borrar item.png"))); // NOI18N
        jButtonDeleteAdjust.setText("Elminar Aj");
        jButtonDeleteAdjust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteAdjustActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDeleteAdjust);
        jButtonDeleteAdjust.setBounds(360, 30, 150, 30);

        jLabelCodigo.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        getContentPane().add(jLabelCodigo);
        jLabelCodigo.setBounds(30, 80, 60, 20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        isOpen = false;
        dispose();
    }//GEN-LAST:event_jButtonReturnActionPerformed

    private void jButtonDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetailActionPerformed

        int i = ClientHistoryView.jTable1.getSelectedRow();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningun comprobante", "Ver", JOptionPane.OK_OPTION);
            return;
        }

        String tipo = ClientHistoryView.jTable1.getValueAt(i, 1).toString();
        String normalizedTipo = InvoiceTypeUtils.toStorageValue(tipo);

        if (Constants.AJUSTE_ABBR.equalsIgnoreCase(normalizedTipo)) {
            ClientInvoice invoice = findInvoice(i);
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

        if (Constants.RECIBO_ABBR.equalsIgnoreCase(normalizedTipo)) {
            ClientReceipt receipt = findReceipt(i);
            if (receipt != null && !ReceiptDetailView.isOpen(ReceiptType.CLIENT)) {
                ReceiptDetailData detailData = ReceiptDetailLoader.loadClientReceipt(sqlSession, receipt, clientController);
                ReceiptDetailView.PrintAction printAction = data -> {
                    try {
                        Client resolvedClient = client;
                        if (resolvedClient == null && receipt.getClient() != null && receipt.getClient().getId() != null) {
                            resolvedClient = clientController.findById(receipt.getClient().getId());
                        }
                        receiptPrintService.print(receipt, resolvedClient, data);
                    } catch (ReceiptPrintException ex) {
                        throw ex;
                    } catch (Exception ex) {
                        throw new ReceiptPrintException("No se pudo generar el recibo", ex);
                    }
                };
                ReceiptDetailView view = new ReceiptDetailView(detailData, printAction);
                MainView.jDesktopPane1.add(view);
                view.setVisible(true);
            } else if (receipt == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el recibo seleccionado", "Ver", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }

        if (!ClientInvoiceDetailView.isOpen) {
            try {
                ClientInvoiceDetailView clientInvoiceDetailVent = new ClientInvoiceDetailView();
                MainView.jDesktopPane1.add(clientInvoiceDetailVent);
                clientInvoiceDetailVent.setVisible(true);
            } catch (Exception ex) {
                Logger.getLogger(ClientHistoryView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jButtonDetailActionPerformed

    private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed
        if (client == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el cliente", "Imprimir", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (ClientHistorySearchByDateView.isOpen) {
            JOptionPane.showMessageDialog(this, "La ventana de impresión ya está abierta", "Imprimir", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            ClientHistorySearchByDateView view = new ClientHistorySearchByDateView(client);
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
            try {
                view.setSelected(true);
            } catch (PropertyVetoException ignored) {
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientHistoryView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo abrir la ventana de impresión", "Imprimir", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonPrintActionPerformed

    private void jButtonDeleteAdjustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteAdjustActionPerformed
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningun comprobante", "Eliminar", JOptionPane.OK_OPTION);
            return;
        }
        String tipo = jTable1.getValueAt(row, 1).toString();
        if (!Constants.AJUSTE_ABBR.equals(tipo)) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un ajuste", "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ClientInvoice invoice = findInvoice(row);
        if (invoice == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el ajuste", "Eliminar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int option = JOptionPane.showConfirmDialog(this, "¿Desea eliminar el ajuste seleccionado?", "Eliminar", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                clientInvoiceController.delete(invoice.getId());
                refreshTable();
                JOptionPane.showMessageDialog(this, "Ajuste eliminado", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                Logger.getLogger(ClientHistoryView.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error eliminando el ajuste", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButtonDeleteAdjustActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDeleteAdjust;
    private javax.swing.JButton jButtonDetail;
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonReturn;
    public static javax.swing.JLabel jLabelCliente;
    public static javax.swing.JLabel jLabelCodigo;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}
