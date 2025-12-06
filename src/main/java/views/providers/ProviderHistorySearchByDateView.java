package views.providers;

import controllers.ProviderInvoiceController;
import controllers.ProviderReceiptController;
import configs.MyBatisConfig;
import mappers.ProviderInvoiceMapper;
import mappers.receipts.ProviderReceiptMapper;
import models.Provider;
import models.ProviderInvoice;
import models.receipts.ProviderReceipt;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.ibatis.session.SqlSession;
import repositories.ProviderInvoiceRepository;
import repositories.ProviderReceiptRepository;
import repositories.impl.ProviderInvoiceRepositoryImpl;
import repositories.impl.ProviderReceiptRepositoryImpl;
import services.ProviderInvoiceService;
import services.ProviderReceiptService;
import services.reports.JasperReportFactory;
import services.reports.ReportParameterFactory;
import utils.Constants;
import utils.InvoiceTypeUtils;
import utils.JasperViewerUtils;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
 * Dialog used to filter provider history by date range and trigger the Jasper
 * report printing.
 */
public class ProviderHistorySearchByDateView extends javax.swing.JInternalFrame {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String REPORT_RESOURCE = "/reports/ImpresionHistorialCliente.jrxml";

    public static boolean isOpen = false;

    private final ProviderInvoiceController providerInvoiceController;
    private final ProviderReceiptController providerReceiptController;
    private final Provider provider;
    private final SqlSession sqlSession;

    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonSearch;
    private com.toedter.calendar.JDateChooser jDateChooserFrom;
    private com.toedter.calendar.JDateChooser jDateChooserTo;
    private javax.swing.JLabel jLabelFrom;
    private javax.swing.JLabel jLabelTo;

    public ProviderHistorySearchByDateView(Provider provider) throws Exception {
        this.provider = provider;
        sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ProviderInvoiceMapper invoiceMapper = sqlSession.getMapper(ProviderInvoiceMapper.class);
        ProviderReceiptMapper receiptMapper = sqlSession.getMapper(ProviderReceiptMapper.class);
        ProviderInvoiceRepository invoiceRepository = new ProviderInvoiceRepositoryImpl(invoiceMapper);
        ProviderReceiptRepository receiptRepository = new ProviderReceiptRepositoryImpl(receiptMapper);
        ProviderInvoiceService invoiceService = new ProviderInvoiceService(invoiceRepository);
        ProviderReceiptService receiptService = new ProviderReceiptService(receiptRepository);
        this.providerInvoiceController = new ProviderInvoiceController(invoiceService);
        this.providerReceiptController = new ProviderReceiptController(receiptService);

        initComponents();
        isOpen = true;
        jDateChooserTo.setDate(new Date());
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabelFrom = new javax.swing.JLabel();
        jLabelTo = new javax.swing.JLabel();
        jDateChooserFrom = new com.toedter.calendar.JDateChooser();
        jDateChooserTo = new com.toedter.calendar.JDateChooser();
        jButtonSearch = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();

        setClosable(true);
        setMinimumSize(new java.awt.Dimension(320, 220));
        setPreferredSize(new java.awt.Dimension(320, 220));
        getContentPane().setLayout(null);

        jLabelFrom.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabelFrom.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelFrom.setText("Desde");
        getContentPane().add(jLabelFrom);
        jLabelFrom.setBounds(20, 20, 70, 20);

        getContentPane().add(jDateChooserFrom);
        jDateChooserFrom.setBounds(100, 20, 150, 20);

        jLabelTo.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabelTo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTo.setText("Hasta");
        getContentPane().add(jLabelTo);
        jLabelTo.setBounds(20, 60, 70, 20);

        getContentPane().add(jDateChooserTo);
        jDateChooserTo.setBounds(100, 60, 150, 20);

        jButtonSearch.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButtonSearch.setText("Buscar");
        jButtonSearch.addActionListener(this::jButtonSearchActionPerformed);
        getContentPane().add(jButtonSearch);
        jButtonSearch.setBounds(40, 120, 100, 30);

        jButtonClose.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonClose.setText("Volver");
        jButtonClose.addActionListener(this::jButtonCloseActionPerformed);
        getContentPane().add(jButtonClose);
        jButtonClose.setBounds(150, 120, 110, 30);

        pack();
    }

    private boolean validateSearch() {
        if (jDateChooserFrom.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar fecha desde", "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (jDateChooserTo.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar fecha hasta", "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (jDateChooserTo.getDate().before(jDateChooserFrom.getDate())) {
            JOptionPane.showMessageDialog(this, "La fecha desde debe ser anterior a la fecha hasta", "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {
        if (!validateSearch()) {
            return;
        }
        if (provider == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el proveedor", "Imprimir", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate fromDate = toLocalDate(jDateChooserFrom.getDate());
            LocalDate toDate = toLocalDate(jDateChooserTo.getDate());

            List<ProviderInvoice> invoices = providerInvoiceController.findByProvider(provider.getId());
            List<ProviderReceipt> receipts = providerReceiptController.findByProvider(provider.getId());

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

            String providerName = safeString(provider.getName());
            String address = buildAddress(provider);

            List<Map<String, ?>> data = buildReportData(filteredItems, initialBalance, providerName, address);
            Map<String, Object> parameters = ReportParameterFactory.createBaseParameters();

            JasperReport report = JasperReportFactory.loadReport(REPORT_RESOURCE);
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(data);
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
            displayReport(jasperPrint, "Historial de " + providerName);
            SwingUtilities.invokeLater(this::dispose);
        } catch (JRException ex) {
            Logger.getLogger(ProviderHistorySearchByDateView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo generar el reporte", "Imprimir", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(ProviderHistorySearchByDateView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Ocurrió un error al generar el reporte", "Imprimir", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {
        isOpen = false;
        dispose();
    }

    @Override
    public void dispose() {
        isOpen = false;
        super.dispose();
    }

    private List<HistoryItem> buildHistoryItems(List<ProviderInvoice> invoices, List<ProviderReceipt> receipts) {
        List<HistoryItem> items = new ArrayList<>();
        if (invoices != null) {
            for (ProviderInvoice invoice : invoices) {
                if (invoice == null || invoice.getInvoiceDate() == null || invoice.getTotal() == null) {
                    continue;
                }
                String originalType = invoice.getCategory() != null
                        ? safeString(invoice.getCategory().getDescription())
                        : safeString(invoice.getInvoiceType());
                boolean subtract = InvoiceTypeUtils.isCreditDocument(originalType)
                        || InvoiceTypeUtils.isCreditDocument(invoice.getInvoiceType());
                String type = originalType;
                if (Constants.AJUSTE.equalsIgnoreCase(type)) {
                    type = Constants.AJUSTE_ABBR;
                }
                String number = formatNumber(invoice.getPointOfSale(), invoice.getInvoiceNumber());
                items.add(new HistoryItem(invoice.getInvoiceDate(), type, number, invoice.getTotal(), subtract));
            }
        }
        if (receipts != null) {
            for (ProviderReceipt receipt : receipts) {
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

    private void displayReport(JasperPrint jasperPrint, String title) {
        JasperViewerUtils.showViewer(new JasperViewer(jasperPrint, false), title);
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
                                                 String providerName, String address) {
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
            row.put("cliente", safeString(providerName));
            row.put("direccion", safeString(address));
            row.put("saldoInicial", initialValue);
            rows.add(row);
        }
        return rows;
    }

    private String buildAddress(Provider provider) {
        if (provider == null) {
            return "";
        }
        String street = provider.getAddress() != null ? safeString(provider.getAddress().getName()) : "";
        String number = safeString(provider.getAddressNumber());
        String city = provider.getCity() != null ? safeString(provider.getCity().getName()) : "";
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
}

