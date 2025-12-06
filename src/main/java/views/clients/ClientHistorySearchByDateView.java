

package views.clients;

import controllers.ClientController;
import controllers.ClientInvoiceController;
import controllers.ClientReceiptController;
import configs.MyBatisConfig;
import mappers.ClientMapper;
import mappers.ClientInvoiceMapper;
import mappers.receipts.ClientReceiptMapper;
import models.Client;
import models.ClientInvoice;
import models.receipts.ClientReceipt;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
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
import utils.Constants;
import utils.InvoiceTypeUtils;
import services.reports.JasperReportFactory;
import services.reports.ReportParameterFactory;
import utils.JasperViewerUtils;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mariana
 */
public class ClientHistorySearchByDateView extends javax.swing.JInternalFrame {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String REPORT_RESOURCE = "/reports/ImpresionHistorialCliente.jrxml";

    public static boolean isOpen = false;
    private final ClientController clientController;
    private final ClientInvoiceController clientInvoiceController;
    private final ClientReceiptController clientReceiptController;
    private final Client client;
    private final SqlSession sqlSession;

    public ClientHistorySearchByDateView(Client client) throws Exception {
        this.client = client;
        sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
        ClientInvoiceMapper invoiceMapper = sqlSession.getMapper(ClientInvoiceMapper.class);
        ClientReceiptMapper receiptMapper = sqlSession.getMapper(ClientReceiptMapper.class);
        ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
        ClientInvoiceRepository invoiceRepository = new ClientInvoiceRepositoryImpl(invoiceMapper);
        ClientReceiptRepository receiptRepository = new ClientReceiptRepositoryImpl(receiptMapper);
        ClientService clientService = new ClientService(clientRepository, invoiceRepository, receiptRepository);
        ClientInvoiceService invoiceService = new ClientInvoiceService(invoiceRepository);
        ClientReceiptService receiptService = new ClientReceiptService(receiptRepository);
        this.clientController = new ClientController(clientService);
        this.clientInvoiceController = new ClientInvoiceController(invoiceService);
        this.clientReceiptController = new ClientReceiptController(receiptService);

        initComponents();
        isOpen = true;
        jDateChooser2.setDate(new Date());
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(320, 220));
        setPreferredSize(new java.awt.Dimension(320, 220));
        getContentPane().setLayout(null);

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Desde");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(20, 20, 70, 20);
        getContentPane().add(jDateChooser1);
        jDateChooser1.setBounds(100, 20, 150, 20);
        getContentPane().add(jDateChooser2);
        jDateChooser2.setBounds(100, 60, 150, 20);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Hasta");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(20, 60, 70, 20);

        jButton12.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButton12.setText("Buscar");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton12);
        jButton12.setBounds(40, 120, 100, 30);

        jButton4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButton4.setText("Volver");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4);
        jButton4.setBounds(150, 120, 110, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private boolean validateSearch() {
        if (jDateChooser1.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar fecha desde", "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (jDateChooser2.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar fecha hasta", "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (jDateChooser2.getDate().before(jDateChooser1.getDate())) {
            JOptionPane.showMessageDialog(this, "La fecha desde debe ser anterior a la fecha hasta", "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }


    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed

        if (!validateSearch()) {
            return;
        }

        try {
            Date from = jDateChooser1.getDate();
            Date to = jDateChooser2.getDate();

            LocalDate fromDate = toLocalDate(from);
            LocalDate toDate = toLocalDate(to);

            List<ClientInvoice> invoices = clientInvoiceController.findByClient(client.getId());
            List<ClientReceipt> receipts = clientReceiptController.findByClient(client.getId());

            List<HistoryItem> historyItems = buildHistoryItems(invoices, receipts);
            if (historyItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay información para imprimir", "Imprimir", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            BigDecimal initialBalance = calculateInitialBalance(historyItems, fromDate);
            List<HistoryItem> filteredItems = filterHistory(historyItems, fromDate, toDate);
            if (filteredItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay información para imprimir en el rango seleccionado", "Imprimir", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String clientName = client != null ? safeString(client.getFullName()) : "";
            String address = buildAddress(client);

            List<Map<String, ?>> data = buildReportData(filteredItems, initialBalance, clientName, address);
            Map<String, Object> parameters = ReportParameterFactory.createBaseParameters();

            JasperReport report = JasperReportFactory.loadReport(REPORT_RESOURCE);
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(data);
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
            displayReport(jasperPrint, "Historial de " + clientName);
            SwingUtilities.invokeLater(this::dispose);
        } catch (JRException ex) {
            Logger.getLogger(ClientHistorySearchByDateView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo generar el reporte", "Imprimir", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(ClientHistorySearchByDateView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Ocurrió un error al generar el reporte", "Imprimir", JOptionPane.ERROR_MESSAGE);
        }
}//GEN-LAST:event_jButton12ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        isOpen = false;
        dispose();
}//GEN-LAST:event_jButton4ActionPerformed

    @Override
    public void dispose() {
        isOpen = false;
        super.dispose();
    }


    private List<HistoryItem> buildHistoryItems(List<ClientInvoice> invoices, List<ClientReceipt> receipts) {
        List<HistoryItem> items = new ArrayList<>();
        if (invoices != null) {
            for (ClientInvoice invoice : invoices) {
                if (invoice == null || invoice.getInvoiceDate() == null || invoice.getTotal() == null) {
                    continue;
                }
                boolean subtract = InvoiceTypeUtils.isCreditDocument(invoice.getInvoiceType());
                String number = formatNumber(invoice.getPointOfSale(), invoice.getInvoiceNumber());
                items.add(new HistoryItem(invoice.getInvoiceDate(), invoice.getInvoiceType(), number, invoice.getTotal(), subtract));
            }
        }
        if (receipts != null) {
            for (ClientReceipt receipt : receipts) {
                if (receipt == null || receipt.getReceiptDate() == null || receipt.getTotal() == null) {
                    continue;
                }
                String number = formatNumber(receipt.getPointOfSale(), receipt.getReceiptNumber());
                items.add(new HistoryItem(receipt.getReceiptDate(), Constants.RECIBO_ABBR, number, receipt.getTotal(), true));
            }
        }
        items.sort(Comparator.comparing((HistoryItem item) -> item.date, Comparator.nullsLast(Comparator.naturalOrder())));
        return items;
    }

    private BigDecimal calculateInitialBalance(List<HistoryItem> items, LocalDate fromDate) {
        if (items == null || fromDate == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal balance = BigDecimal.ZERO;
        for (HistoryItem item : items) {
            if (item.date == null || item.amount == null) {
                continue;
            }
            LocalDate itemDate = item.date.toLocalDate();
            if (itemDate.isBefore(fromDate)) {
                balance = balance.add(item.getSignedAmount());
            }
        }
        return balance;
    }

    private List<HistoryItem> filterHistory(List<HistoryItem> items, LocalDate fromDate, LocalDate toDate) {
        List<HistoryItem> filtered = new ArrayList<>();
        if (items == null || fromDate == null || toDate == null) {
            return filtered;
        }
        for (HistoryItem item : items) {
            if (item.date == null || item.amount == null) {
                continue;
            }
            LocalDate itemDate = item.date.toLocalDate();
            if (!itemDate.isBefore(fromDate) && !itemDate.isAfter(toDate)) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    private List<Map<String, ?>> buildReportData(List<HistoryItem> items, BigDecimal initialBalance,
                                                 String clientName, String address) {
        List<Map<String, ?>> rows = new ArrayList<>();
        BigDecimal initial = initialBalance != null ? initialBalance : BigDecimal.ZERO;
        float initialValue = initial.setScale(2, RoundingMode.HALF_EVEN).floatValue();
        for (HistoryItem item : items) {
            if (item.date == null || item.amount == null) {
                continue;
            }
            Map<String, Object> row = new HashMap<>();
            row.put("fecha", DATE_FORMATTER.format(item.date));
            row.put("tc", InvoiceTypeUtils.toStorageValue(item.type));
            row.put("comprobante", safeString(item.number));
            BigDecimal signed = item.getSignedAmount();
            row.put("total", signed.setScale(2, RoundingMode.HALF_EVEN).floatValue());
            row.put("cliente", safeString(clientName));
            row.put("direccion", safeString(address));
            row.put("saldoInicial", initialValue);
            rows.add(row);
        }
        return rows;
    }

    private String buildAddress(Client client) {
        if (client == null) {
            return "";
        }
        String street = client.getAddress() != null ? safeString(client.getAddress().getName()) : "";
        String number = safeString(client.getAddressNumber());
        String city = client.getCity() != null ? safeString(client.getCity().getName()) : "";
        StringBuilder sb = new StringBuilder();
        if (!street.isEmpty()) {
            sb.append(street);
        }
        if (!number.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(number);
        }
        if (!city.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" - ");
            }
            sb.append(city);
        }
        return sb.toString();
    }

    private String safeString(String value) {
        return value == null ? "" : value.trim();
    }

    private String formatNumber(String pos, String number) {
        String p = safeString(pos);
        String n = safeString(number);
        if (p.isEmpty()) {
            return n;
        }
        if (n.isEmpty()) {
            return p;
        }
        return p + "-" + n;
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static class HistoryItem {
        private final LocalDateTime date;
        private final String type;
        private final String number;
        private final BigDecimal amount;
        private final boolean subtract;

        HistoryItem(LocalDateTime date, String type, String number, BigDecimal amount, boolean subtract) {
            this.date = date;
            this.type = type;
            this.number = number;
            this.amount = amount;
            this.subtract = subtract;
        }

        BigDecimal getSignedAmount() {
            if (amount == null) {
                return BigDecimal.ZERO;
            }
            return subtract ? amount.negate() : amount;
        }
    }

    private void displayReport(JasperPrint jasperPrint, String title) {
        JasperViewerUtils.showViewer(new JasperViewer(jasperPrint, false), title);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton4;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    // End of variables declaration//GEN-END:variables

}
