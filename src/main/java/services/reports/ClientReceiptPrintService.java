package services.reports;

import models.Client;
import models.receipts.ReceiptCard;
import models.receipts.ReceiptCash;
import models.receipts.ReceiptCheque;
import models.receipts.ReceiptDetailData;
import models.receipts.ReceiptRetention;
import models.receipts.ReceiptTransfer;
import models.receipts.ClientReceipt;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import utils.DocumentValidator;
import utils.JasperViewerUtils;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Generates Jasper reports for client receipts.
 */
public class ClientReceiptPrintService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter DUE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String REPORT_RESOURCE = "/reports/ImpresionRecibo.jrxml";
    private static final String DOCUMENT_TITLE = "Recibo de Cobro";

    public void print(ClientReceipt receipt, Client client, ReceiptDetailData detailData) throws ReceiptPrintException {
        JasperPrint jasperPrint = buildReport(receipt, client, detailData);
        JasperViewerUtils.showViewer(new JasperViewer(jasperPrint, false), DOCUMENT_TITLE);
    }

    public File exportPdf(ClientReceipt receipt, Client client, ReceiptDetailData detailData) throws ReceiptPrintException {
        return exportPdf(receipt, client, detailData, null);
    }

    public File exportPdf(ClientReceipt receipt, Client client, ReceiptDetailData detailData, Path outputPath)
            throws ReceiptPrintException {
        JasperPrint jasperPrint = buildReport(receipt, client, detailData);
        try {
            Path target = outputPath != null ? outputPath : Files.createTempFile("recibo-", ".pdf");
            JasperExportManager.exportReportToPdfFile(jasperPrint, target.toString());
            return target.toFile();
        } catch (Exception ex) {
            throw new ReceiptPrintException("No se pudo exportar el recibo", ex);
        }
    }

    private JasperPrint buildReport(ClientReceipt receipt, Client client, ReceiptDetailData detailData)
            throws ReceiptPrintException {
        if (receipt == null || receipt.getId() == null) {
            throw new ReceiptPrintException("No se encontró el recibo a imprimir");
        }
        if (detailData == null) {
            throw new ReceiptPrintException("No se pudo obtener el detalle del recibo");
        }

        List<Map<String, ?>> data = buildReportData(receipt, client, detailData);
        if (data.isEmpty()) {
            throw new ReceiptPrintException("No hay información para imprimir");
        }

        try {
            JasperReport report = JasperReportFactory.loadReport(REPORT_RESOURCE);
            Map<String, Object> parameters = new HashMap<>(ReportParameterFactory.createBaseParameters(receipt.getIssuerCuit()));
            return JasperFillManager.fillReport(report, parameters, new JRMapCollectionDataSource(data));
        } catch (JRException ex) {
            throw new ReceiptPrintException("No se pudo generar el recibo", ex);
        }
    }

    private List<Map<String, ?>> buildReportData(ClientReceipt receipt, Client client, ReceiptDetailData detailData) {
        List<String> payments = buildPaymentDescriptions(detailData);
        if (payments.isEmpty()) {
            payments.add("Sin detalle de pagos");
        }

        String receiptNumber = formatReceiptNumber(detailData.getPointOfSale(), detailData.getReceiptNumber());
        String formattedDate = detailData.getReceiptDate() != null
                ? DATE_FORMATTER.format(detailData.getReceiptDate())
                : "";
        BigDecimal total = resolveTotal(receipt, detailData);
        String notes = safeString(detailData.getNotes());
        String clientName = safeString(detailData.getEntityName());
        String address = buildAddress(client);
        String city = client != null && client.getCity() != null ? safeString(client.getCity().getName()) : "";
        String condition = client != null && client.getTaxCondition() != null ? safeString(client.getTaxCondition().getName()) : "";
        String cuit = client != null ? DocumentValidator.formatCuit(client.getDocumentNumber()) : "";

        List<Map<String, ?>> rows = new ArrayList<>();
        for (String payment : payments) {
            Map<String, Object> row = new HashMap<>();
            row.put("tipoPago", payment);
            row.put("total", total.floatValue());
            BigDecimal balance = resolveBalance(detailData);
            row.put("saldo", balance.floatValue());
            row.put("numero", receiptNumber);
            row.put("cliente", clientName);
            row.put("direccion", address);
            row.put("fecha", formattedDate);
            row.put("ciudad", city);
            row.put("condicion", condition);
            row.put("cuit", cuit);
            row.put("detalle", notes);
            rows.add(row);
        }

        return rows;
    }

    private BigDecimal resolveBalance(ReceiptDetailData detailData) {
        if (detailData != null && detailData.getBalance() != null) {
            return detailData.getBalance();
        }
        return BigDecimal.ZERO;
    }

    private List<String> buildPaymentDescriptions(ReceiptDetailData detailData) {
        List<String> lines = new ArrayList<>();

        ReceiptCash cash = detailData.getCashPayment();
        if (cash != null && cash.getAmount() != null && isPositive(cash.getAmount())) {
            lines.add(String.format(Locale.ROOT, "Efectivo: $%s", formatAmount(cash.getAmount())));
        }

        for (ReceiptCard card : detailData.getCardPayments()) {
            if (card == null || card.getAmount() == null || !isPositive(card.getAmount())) {
                continue;
            }
            String lastDigits = safeString(card.getLastFourDigits());
            if (!lastDigits.isEmpty()) {
                lastDigits = " ****" + lastDigits;
            }
            String cardName = safeString(card.getCardName());
            String cardType = translateCardType(card.getCardType());
            lines.add(String.format(Locale.ROOT, "Tarjeta %s %s%s: $%s",
                    cardType,
                    cardName,
                    lastDigits,
                    formatAmount(card.getAmount())));
        }

        for (ReceiptCheque cheque : detailData.getChequePayments()) {
            if (cheque == null || cheque.getAmount() == null || !isPositive(cheque.getAmount())) {
                continue;
            }
            String dueDate = cheque.getDueDate() != null ? DUE_DATE_FORMATTER.format(cheque.getDueDate()) : "";
            lines.add(String.format(Locale.ROOT, "Cheque %s %s %s %s: $%s",
                    safeString(cheque.getCheckNumber()),
                    safeString(cheque.getHolderName()),
                    safeString(cheque.getBankName()),
                    dueDate.isEmpty() ? "" : "(Vto " + dueDate + ")",
                    formatAmount(cheque.getAmount())));
        }

        for (ReceiptTransfer transfer : detailData.getTransferPayments()) {
            if (transfer == null || transfer.getAmount() == null || !isPositive(transfer.getAmount())) {
                continue;
            }
            String destinationBank = safeString(transfer.getDestinationBankName());
            String label = destinationBank.isEmpty()
                    ? "Transferencia"
                    : "Transferencia a banco " + destinationBank;
            lines.add(String.format(Locale.ROOT, "%s: $%s", label, formatAmount(transfer.getAmount())));
        }

        for (ReceiptRetention retention : detailData.getRetentionPayments()) {
            if (retention == null || retention.getAmount() == null || !isPositive(retention.getAmount())) {
                continue;
            }
            String description = safeString(retention.getDescription());
            if (description.isEmpty()) {
                description = "Retención";
            } else {
                description = "Retención " + description;
            }
            lines.add(String.format(Locale.ROOT, "%s: $%s", description, formatAmount(retention.getAmount())));
        }

        return lines;
    }

    private BigDecimal resolveTotal(ClientReceipt receipt, ReceiptDetailData detailData) {
        if (detailData.getTotal() != null) {
            return detailData.getTotal();
        }
        if (receipt != null && receipt.getTotal() != null) {
            return receipt.getTotal();
        }
        return BigDecimal.ZERO;
    }

    private String combineBankAndAccount(String bank, String account) {
        String safeBank = safeString(bank);
        String safeAccount = safeString(account);
        if (safeBank.isEmpty()) {
            return safeAccount;
        }
        if (safeAccount.isEmpty()) {
            return safeBank;
        }
        return safeBank + " " + safeAccount;
    }

    private String buildAddress(Client client) {
        if (client == null || client.getAddress() == null) {
            return "";
        }
        String street = safeString(client.getAddress().getName());
        String number = safeString(client.getAddressNumber());
        if (street.isEmpty()) {
            return number;
        }
        if (number.isEmpty()) {
            return street;
        }
        return street + " " + number;
    }

    private String formatReceiptNumber(String pointOfSale, String number) {
        String pos = padNumeric(pointOfSale, 4);
        String num = padNumeric(number, 8);
        if (pos.isEmpty()) {
            return num;
        }
        if (num.isEmpty()) {
            return pos;
        }
        return pos + "-" + num;
    }

    private String padNumeric(String value, int length) {
        String trimmed = safeString(value);
        if (trimmed.isEmpty()) {
            return "";
        }
        try {
            int numeric = Integer.parseInt(trimmed);
            return String.format(Locale.ROOT, "%0" + length + "d", numeric);
        } catch (NumberFormatException ex) {
            return trimmed;
        }
    }

    private boolean isPositive(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private String formatAmount(BigDecimal value) {
        BigDecimal safe = value != null ? value : BigDecimal.ZERO;
        return safe.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String translateCardType(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (Objects.equals(normalized, "CREDIT")) {
            return "Crédito";
        }
        if (Objects.equals(normalized, "DEBIT")) {
            return "Débito";
        }
        return value;
    }

    private String safeString(String value) {
        return value == null ? "" : value.trim();
    }

    public static class ReceiptPrintException extends Exception {
        public ReceiptPrintException(String message) {
            super(message);
        }

        public ReceiptPrintException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
