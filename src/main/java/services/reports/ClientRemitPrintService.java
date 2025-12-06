package services.reports;

import models.Client;
import models.ClientRemit;
import models.ClientRemitDetail;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import utils.DocumentValidator;
import utils.JasperViewerUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generates Jasper reports for client remits.
 */
public class ClientRemitPrintService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String REPORT_RESOURCE = "/reports/ImpresionRemito.jrxml";
    private static final String DOCUMENT_TYPE = "Remito";

    public void print(ClientRemit remit, List<ClientRemitDetail> details) throws RemitPrintException {
        if (remit == null) {
            throw new RemitPrintException("No se encontró el remito a imprimir");
        }

        List<Map<String, ?>> data = buildReportData(remit, details);
        if (data.isEmpty()) {
            throw new RemitPrintException("No hay información para imprimir");
        }

        try {
            JasperReport report = JasperReportFactory.loadReport(REPORT_RESOURCE);
            Map<String, Object> parameters = new HashMap<>(ReportParameterFactory.createBaseParameters());
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JRMapCollectionDataSource(data));
            JasperViewerUtils.showViewer(new JasperViewer(jasperPrint, false), DOCUMENT_TYPE);
        } catch (JRException ex) {
            throw new RemitPrintException("No se pudo generar el remito", ex);
        }
    }

    private List<Map<String, ?>> buildReportData(ClientRemit remit, List<ClientRemitDetail> details) {
        List<ClientRemitDetail> safeDetails = details != null ? details : Collections.emptyList();
        List<Map<String, ?>> rows = new ArrayList<>();

        Client client = remit.getClient();
        String clientName = client != null ? safeString(client.getFullName()) : "";
        String address = buildAddress(client);
        String locality = client != null && client.getCity() != null ? safeString(client.getCity().getName()) : "";
        String condition = client != null && client.getTaxCondition() != null ? safeString(client.getTaxCondition().getName()) : "";
        String clientNumber = client != null && client.getId() != null ? client.getId().toString() : "";
        String cuit = client != null ? DocumentValidator.formatCuit(client.getDocumentNumber()) : "";
        String formattedDate = remit.getRemitDate() != null ? DATE_FORMATTER.format(remit.getRemitDate()) : "";
        String formattedNumber = remit.getId() != null ? String.format(Locale.ROOT, "%08d", remit.getId()) : "";
        String remitDescription = safeString(remit.getDescription());
        String total = formatAmount(resolveTotal(remit, safeDetails));

        if (safeDetails.isEmpty()) {
            rows.add(buildDetailRow(null, remitDescription, total, formattedDate, formattedNumber,
                    clientName, address, locality, condition, clientNumber, cuit));
        } else {
            for (ClientRemitDetail detail : safeDetails) {
                rows.add(buildDetailRow(detail, remitDescription, total, formattedDate, formattedNumber,
                        clientName, address, locality, condition, clientNumber, cuit));
            }
        }

        return rows;
    }

    private Map<String, Object> buildDetailRow(ClientRemitDetail detail, String remitDescription, String total,
                                               String formattedDate, String formattedNumber, String clientName,
                                               String address, String locality, String condition,
                                               String clientNumber, String cuit) {
        Map<String, Object> row = new HashMap<>();
        row.put("tipoComprobante", DOCUMENT_TYPE);
        row.put("detalleRemito", remitDescription);
        row.put("cliente", clientName);
        row.put("direccion", address);
        row.put("localidad", locality);
        row.put("condicion", condition);
        row.put("nroCliente", clientNumber);
        row.put("numero", formattedNumber);
        row.put("fecha", formattedDate);
        row.put("cuit", cuit);
        row.put("total", total);

        if (detail != null) {
            row.put("codArticulo", safeString(detail.getProductCode()));
            row.put("detalle", safeString(detail.getDescription()));
            row.put("cantidad", formatQuantity(detail.getQuantity()));
            BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
            row.put("precio", formatAmount(price));
            BigDecimal quantity = detail.getQuantity() != null
                    ? new BigDecimal(Float.toString(detail.getQuantity())) : BigDecimal.ZERO;
            row.put("parcial", formatAmount(price.multiply(quantity)));
        } else {
            row.put("codArticulo", "");
            row.put("detalle", "");
            row.put("cantidad", "");
            row.put("precio", "");
            row.put("parcial", "");
        }

        return row;
    }

    private BigDecimal resolveTotal(ClientRemit remit, List<ClientRemitDetail> details) {
        if (remit != null && remit.getTotal() != null) {
            return remit.getTotal();
        }
        BigDecimal total = BigDecimal.ZERO;
        for (ClientRemitDetail detail : details) {
            if (detail == null) {
                continue;
            }
            BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
            BigDecimal quantity = detail.getQuantity() != null
                    ? new BigDecimal(Float.toString(detail.getQuantity())) : BigDecimal.ZERO;
            total = total.add(price.multiply(quantity));
        }
        return total;
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

    private String formatAmount(BigDecimal value) {
        BigDecimal safe = value != null ? value : BigDecimal.ZERO;
        return safe.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatQuantity(Float quantity) {
        if (quantity == null) {
            return "";
        }
        BigDecimal value = new BigDecimal(Float.toString(quantity));
        BigDecimal normalized = value.stripTrailingZeros();
        if (normalized.scale() < 0) {
            normalized = normalized.setScale(0);
        }
        return normalized.toPlainString();
    }

    private String safeString(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    public static class RemitPrintException extends Exception {
        public RemitPrintException(String message) {
            super(message);
        }

        public RemitPrintException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
