package services.reports;

import models.Client;
import models.ClientBudget;
import models.ClientBudgetDetail;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import utils.Constants;
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
 * Generates Jasper reports for {@link ClientBudget} instances so that
 * newly created or updated budgets can be printed immediately.
 */
public class ClientBudgetReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String REPORT_RESOURCE = "/reports/ImpresionPresupuesto.jrxml";

    /**
     * Prints the given budget using the default JasperReports template.
     *
     * @param budget  the budget to render
     * @param details the detail rows associated with the budget
     * @throws JRException if the report cannot be generated
     */
    public void printBudget(ClientBudget budget, List<ClientBudgetDetail> details) throws JRException {
        if (budget == null) {
            throw new IllegalArgumentException("No se encontró el presupuesto a imprimir");
        }

        List<Map<String, ?>> data = buildBudgetReportData(budget, details);
        if (data.isEmpty()) {
            throw new IllegalStateException("No hay información para imprimir");
        }

        JasperReport report = JasperReportFactory.loadReport(REPORT_RESOURCE);
        Map<String, Object> parameters = new HashMap<>(ReportParameterFactory.createBaseParameters());
        JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(data);
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
        JasperViewerUtils.showViewer(new JasperViewer(jasperPrint, false), Constants.PRESUPUESTO);
    }

    private List<Map<String, ?>> buildBudgetReportData(ClientBudget budget, List<ClientBudgetDetail> details) {
        List<ClientBudgetDetail> safeDetails = details != null ? details : Collections.emptyList();
        List<Map<String, ?>> rows = new ArrayList<>();

        Client client = budget.getClient();
        String clientName = client != null ? safeString(client.getFullName()) : "";
        String address = buildAddress(budget);
        String locality = client != null && client.getCity() != null
                ? safeString(client.getCity().getName()) : "";
        String condition = client != null && client.getTaxCondition() != null
                ? safeString(client.getTaxCondition().getName()) : "";
        String clientNumber = client != null && client.getId() != null
                ? client.getId().toString() : "";
        String cuit = client != null ? DocumentValidator.formatCuit(client.getDocumentNumber()) : "";
        String formattedDate = budget.getBudgetDate() != null
                ? DATE_FORMATTER.format(budget.getBudgetDate()) : "";
        String formattedNumber = budget.getId() != null ? String.format(Locale.ROOT, "%08d", budget.getId()) : "";
        String total = formatAmount(budget.getTotal());

        String observations = safeString(budget.getDescription());

        if (safeDetails.isEmpty()) {
            rows.add(buildBudgetRow(null, total, formattedDate, formattedNumber,
                    clientName, address, locality, condition, clientNumber, cuit, observations));
        } else {
            for (ClientBudgetDetail detail : safeDetails) {
                rows.add(buildBudgetRow(detail, total, formattedDate, formattedNumber,
                        clientName, address, locality, condition, clientNumber, cuit, observations));
            }
        }
        return rows;
    }

    private Map<String, Object> buildBudgetRow(ClientBudgetDetail detail, String total, String formattedDate,
                                               String formattedNumber, String clientName, String address, String locality,
                                               String condition, String clientNumber, String cuit, String observations) {
        Map<String, Object> row = new HashMap<>();
        row.put("tipoComprobante", Constants.PRESUPUESTO);
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
            row.put("codArticulo", safeString(detail.getProductCode()));
            row.put("detalle", safeString(detail.getDescription()));
            row.put("cantidad", formatQuantity(detail.getQuantity()));
            BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
            row.put("precio", formatAmount(price));
            BigDecimal quantity = detail.getQuantity() != null
                    ? new BigDecimal(Float.toString(detail.getQuantity()))
                    : BigDecimal.ZERO;
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

    private String buildAddress(ClientBudget budget) {
        if (budget == null || budget.getClient() == null || budget.getClient().getAddress() == null) {
            return "";
        }
        String street = safeString(budget.getClient().getAddress().getName());
        String number = safeString(budget.getClient().getAddressNumber());
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
        BigDecimal bd = new BigDecimal(Float.toString(quantity));
        BigDecimal normalized = bd.stripTrailingZeros();
        if (normalized.scale() < 0) {
            normalized = normalized.setScale(0);
        }
        return normalized.toPlainString();
    }

    private String safeString(Object value) {
        return value == null ? "" : value.toString().trim();
    }
}
