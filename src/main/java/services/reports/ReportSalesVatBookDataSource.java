package services.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import models.ClientInvoice;
import utils.InvoiceTypeUtils;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 * JasperReports data source for sales VAT book.
 */
public class ReportSalesVatBookDataSource implements JRDataSource {

    public static List<ClientInvoice> detailsList = new ArrayList<>();
    private static final Locale REPORT_LOCALE = new Locale("es", "AR");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static String periodMonth = "";
    private static Integer periodYear = null;
    private int currentIndex = -1;

    @Override
    public Object getFieldValue(JRField field) throws JRException {
        ClientInvoice invoice = detailsList.get(currentIndex);
        String name = field.getName();

        int sign = resolveSign(invoice);
        switch (name) {
            case "mes":
                return periodMonth;
            case "año":
                return periodYear;
            case "fecha":
                return formatDate(invoice.getInvoiceDate());
            case "tc":
                return InvoiceTypeUtils.toAbbreviation(invoice.getInvoiceType());
            case "comprobante":
                return String.format("%s-%s", invoice.getPointOfSale(), invoice.getInvoiceNumber());
            case "razonSocial":
                return invoice.getClient() != null ? invoice.getClient().getFullName() : "";
            case "r":
                return (invoice.getClient() != null && invoice.getClient().getTaxCondition() != null)
                        ? invoice.getClient().getTaxCondition().getAbbreviation() : "";
            case "cuit":
                return invoice.getClient() != null ? invoice.getClient().getDocumentNumber() : "";
            case "neto21":
                return applySign(computeNet(invoice.getVat21(), new BigDecimal("0.21")), sign);
            case "neto105":
                return applySign(computeNet(invoice.getVat105(), new BigDecimal("0.105")), sign);
            case "neto27":
                return applySign(computeNet(invoice.getVat27(), new BigDecimal("0.27")), sign);
            case "iva21":
                return applySign(valueOrZero(invoice.getVat21()), sign);
            case "iva":
                return applySign(sum(invoice.getVat21(), invoice.getVat105(), invoice.getVat27()), sign);
            case "op":
                return BigDecimal.ZERO;
            case "total":
                return applySign(valueOrZero(invoice.getTotal()), sign);
            default:
                return null;
        }
    }

    @Override
    public boolean next() throws JRException {
        return ++currentIndex < detailsList.size();
    }

    public static void configurePeriod(Date start, Date end) {
        LocalDate startDate = toLocalDate(start);
        LocalDate endDate = toLocalDate(end);
        if (startDate != null && endDate != null
                && (startDate.getMonth() != endDate.getMonth() || startDate.getYear() != endDate.getYear())) {
            periodMonth = formatRange(startDate, endDate);
            periodYear = null;
            return;
        }
        if (startDate != null) {
            periodMonth = formatMonth(startDate);
            periodYear = startDate.getYear();
        } else {
            periodMonth = "";
            periodYear = null;
        }
    }

    private BigDecimal computeNet(BigDecimal vatAmount, BigDecimal rate) {
        if (vatAmount == null) {
            return BigDecimal.ZERO;
        }
        return vatAmount.divide(rate, 2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal sum(BigDecimal... values) {
        BigDecimal result = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            if (value != null) {
                result = result.add(value);
            }
        }
        return result;
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private int resolveSign(ClientInvoice invoice) {
        return InvoiceTypeUtils.isDebitDocument(invoice.getInvoiceType()) ? -1 : 1;
    }

    private BigDecimal applySign(BigDecimal value, int sign) {
        return value.multiply(BigDecimal.valueOf(sign));
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_FORMAT);
    }

    private static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static String formatRange(LocalDate start, LocalDate end) {
        return formatMonth(start) + " " + start.getYear() + " - "
                + formatMonth(end) + " " + end.getYear();
    }

    private static String formatMonth(LocalDate date) {
        if (date == null) {
            return "";
        }
        String monthName = date.getMonth().getDisplayName(TextStyle.FULL, REPORT_LOCALE);
        if (monthName.isEmpty()) {
            return "";
        }
        return monthName.substring(0, 1).toUpperCase(REPORT_LOCALE) + monthName.substring(1);
    }
}
