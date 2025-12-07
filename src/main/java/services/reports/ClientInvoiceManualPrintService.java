package services.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import models.Client;
import models.ClientInvoice;
import models.ClientInvoiceDetail;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import utils.Constants;
import utils.DocumentValidator;
import utils.InvoiceTypeUtils;
import utils.JasperViewerUtils;

/**
 * Generates Jasper reports for manually managed client invoices (such as
 * budgets).
 */
public class ClientInvoiceManualPrintService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String REPORT_RESOURCE = "/reports/ImpresionComprobanteCliente.jrxml";

    /**
     * Generates and displays the manual client document (FX) for the provided
     * invoice using the standard comprobante template.
     *
     * @param invoice the invoice to render
     * @throws ManualInvoicePrintException if the report cannot be generated or
     *                                     displayed
     */
    public void printBudget(ClientInvoice invoice) throws ManualInvoicePrintException {
        if (invoice == null) {
            throw new ManualInvoicePrintException("No se encontró el presupuesto a imprimir");
        }

        try {
            JasperPrint jasperPrint = createBudgetReport(invoice);
            JasperViewerUtils.showViewer(new JasperViewer(jasperPrint, false), Constants.PRESUPUESTO);
        } catch (JRException ex) {
            throw new ManualInvoicePrintException("Error al generar el presupuesto", ex);
        }
    }

    public JasperPrint createBudgetReport(ClientInvoice invoice) throws JRException {
        JasperReport report = loadReport();
        Map<String, Object> parameters = createReportParameters(invoice);
        JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(buildReportData(invoice));
        return JasperFillManager.fillReport(report, parameters, dataSource);
    }

    public void exportBudgetPdf(ClientInvoice invoice, String outputPath) throws ManualInvoicePrintException {
        if (outputPath == null || outputPath.isBlank()) {
            throw new ManualInvoicePrintException("No se configuró la ruta de exportación del presupuesto");
        }
        try {
            JasperPrint jasperPrint = createBudgetReport(invoice);
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
        } catch (JRException ex) {
            throw new ManualInvoicePrintException("Error al exportar el presupuesto a PDF", ex);
        }
    }

    private JasperReport loadReport() throws JRException {
        return JasperReportFactory.loadReport(REPORT_RESOURCE);
    }

    private Map<String, Object> createReportParameters(ClientInvoice invoice) {
        String issuerCuit = invoice != null ? invoice.getIssuerCuit() : null;
        return new HashMap<>(ReportParameterFactory.createBaseParameters(issuerCuit));
    }

    private List<Map<String, ?>> buildReportData(ClientInvoice invoice) {
        List<ClientInvoiceDetail> details = invoice.getDetails() != null
                ? invoice.getDetails() : Collections.emptyList();
        List<Map<String, ?>> rows = new ArrayList<>();

        Client client = invoice.getClient();
        String clientName = client != null ? safeString(client.getFullName()) : "";
        String address = buildAddress(client);
        String locality = client != null && client.getCity() != null
                ? safeString(client.getCity().getName()) : "";
        String condition = client != null && client.getTaxCondition() != null
                ? safeString(client.getTaxCondition().getName()) : "";
        String clientNumber = client != null && client.getId() != null
                ? client.getId().toString() : "";
        String cuit = client != null ? DocumentValidator.formatCuit(client.getDocumentNumber()) : "";
        String formattedDate = invoice.getInvoiceDate() != null ? DATE_FORMATTER.format(invoice.getInvoiceDate()) : "";
        String formattedNumber = formatInvoiceNumber(invoice.getPointOfSale(), invoice.getInvoiceNumber());
        String total = formatAmount(invoice.getTotal());
        String observations = safeString(invoice.getDescription());
        String documentType = resolveDocumentType(invoice);

        if (details.isEmpty()) {
            rows.add(buildDetailRow(null, total, formattedDate, formattedNumber, clientName, address,
                    locality, condition, clientNumber, cuit, observations, documentType));
        } else {
            for (ClientInvoiceDetail detail : details) {
                rows.add(buildDetailRow(detail, total, formattedDate, formattedNumber, clientName, address,
                        locality, condition, clientNumber, cuit, observations, documentType));
            }
        }

        return rows;
    }

    private Map<String, Object> buildDetailRow(ClientInvoiceDetail detail, String total, String formattedDate,
            String formattedNumber, String clientName, String address, String locality,
            String condition, String clientNumber, String cuit, String observations, String documentType) {

        Map<String, Object> row = new HashMap<>();
        row.put("tipoComprobante", documentType);
        row.put("cliente", clientName);
        row.put("direccion", address);
        row.put("localidad", locality);
        row.put("condicion", condition);
        row.put("nroCliente", clientNumber);
        row.put("numero", formattedNumber);
        row.put("fecha", formattedDate);
        row.put("cuit", cuit);
        row.put("total", total);
        row.put("observaciones", observations);

        if (detail != null) {
            String code = safeString(detail.getArticleCode());
            row.put("codArticulo", code);
            row.put("detalle", safeString(detail.getArticleDescription()));
            row.put("cantidad", formatQuantity(detail.getQuantity()));
            BigDecimal unitPrice = detail.getUnitPrice() != null ? detail.getUnitPrice() : BigDecimal.ZERO;
            row.put("precio", formatAmount(unitPrice));
            BigDecimal subtotal = detail.getSubtotal() != null ? detail.getSubtotal()
                    : unitPrice.multiply(detail.getQuantity() != null ? detail.getQuantity() : BigDecimal.ZERO);
            row.put("parcial", formatAmount(subtotal));
            row.put("bonificacion", formatBonification(detail.getDiscountPercent()));
        } else {
            row.put("codArticulo", "");
            row.put("detalle", "");
            row.put("cantidad", "");
            row.put("precio", "");
            row.put("parcial", "");
            row.put("bonificacion", "");
        }

        return row;
    }

    private String resolveDocumentType(ClientInvoice invoice) {
        if (invoice == null) {
            return Constants.PRESUPUESTO;
        }
        String type = InvoiceTypeUtils.toDisplayValue(invoice.getInvoiceType());
        if (type == null || type.trim().isEmpty()) {
            return Constants.PRESUPUESTO;
        }
        return type;
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

    private String formatInvoiceNumber(String pointOfSale, String number) {
        String safePos = normalizeDigits(pointOfSale);
        String safeNumber = normalizeDigits(number);
        if (safePos.isEmpty() && safeNumber.isEmpty()) {
            return "";
        }
        return String.format(Locale.ROOT, "%s-%s", leftPadDigits(safePos, 4), leftPadDigits(safeNumber, 8));
    }

    private String formatAmount(BigDecimal value) {
        BigDecimal safe = value != null ? value : BigDecimal.ZERO;
        return safe.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatQuantity(BigDecimal quantity) {
        if (quantity == null) {
            return "";
        }
        BigDecimal normalized = quantity.stripTrailingZeros();
        if (normalized.scale() < 0) {
            normalized = normalized.setScale(0);
        }
        return normalized.toPlainString();
    }

    private String formatBonification(BigDecimal discountPercent) {
        if (discountPercent == null) {
            return "";
        }
        BigDecimal normalized = discountPercent.stripTrailingZeros();
        if (normalized.scale() < 0) {
            normalized = normalized.setScale(0);
        }
        return normalized.toPlainString();
    }

    private String safeString(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private String normalizeDigits(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^0-9]", "");
    }

    private String leftPadDigits(String value, int size) {
        String normalized = normalizeDigits(value);
        if (normalized.length() > size) {
            normalized = normalized.substring(normalized.length() - size);
        }
        if (normalized.isEmpty()) {
            normalized = "0";
        }
        return String.format(Locale.ROOT, "%" + size + "s", normalized).replace(' ', '0');
    }
}

