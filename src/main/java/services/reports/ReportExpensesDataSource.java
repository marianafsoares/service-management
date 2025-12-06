package services.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import models.ProviderInvoice;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 * JasperReports data source for provider expenses.
 */
public class ReportExpensesDataSource implements JRDataSource {

    /**
     * Invoices to be used in the report.
     */
    public static List<ProviderInvoice> detailsList = new ArrayList<>();

    private int currentIndex = -1;

    @Override
    public Object getFieldValue(JRField field) throws JRException {
        ProviderInvoice invoice = detailsList.get(currentIndex);
        String name = field.getName();
        switch (name) {
            case "fecha":
                return invoice.getInvoiceDate();
            case "tc":
                return invoice.getCategory() != null ? invoice.getCategory().getDescription() : "";
            case "comprobante":
                return String.format("%s-%s", invoice.getPointOfSale(), invoice.getInvoiceNumber());
            case "razonSocial":
                return invoice.getProvider() != null ? invoice.getProvider().getName() : "";
            case "cuit":
                return invoice.getProvider() != null ? invoice.getProvider().getDocumentNumber() : "";
            case "neto21":
                return invoice.getVat21();
            case "neto105":
                return invoice.getVat105();
            case "neto27":
                return invoice.getVat27();
            case "iva":
                BigDecimal vatSum = Stream.of(invoice.getVat21(), invoice.getVat105(), invoice.getVat27())
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                return vatSum;
            case "ng":
                return invoice.getExemptAmount();
            case "op":
                return invoice.getGrossIncomePerception();
            case "total":
                return invoice.getTotal();
            default:
                return null;
        }
    }

    @Override
    public boolean next() throws JRException {
        return ++currentIndex < detailsList.size();
    }
}

