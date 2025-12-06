package services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import models.Client;
import models.ClientInvoice;
import models.Provider;
import models.ProviderInvoice;
import utils.DocumentValidator;
import utils.InvoiceTypeUtils;
import utils.pyAfip.AfipManagement;

/**
 * Helper responsible for exporting the CITI purchases and sales files required
 * by AFIP ("Régimen de Información de Compras y Ventas").
 */
public class CitiFileExporter {

    private static final String SALES_CBTE_FILE = "REGINFO_CV_VENTAS_CBTE.txt";
    private static final String SALES_ALIC_FILE = "REGINFO_CV_VENTAS_ALICUOTAS.txt";
    private static final String PURCHASES_CBTE_FILE = "REGINFO_CV_COMPRAS_CBTE.txt";
    private static final String PURCHASES_ALIC_FILE = "REGINFO_CV_COMPRAS_ALICUOTAS.txt";
    private static final String VAT_ROOT_FOLDER = "iva";
    private static final String SALES_FOLDER = "ventas";
    private static final String PURCHASES_FOLDER = "compras";

    private static final BigDecimal VAT_RATE_21 = new BigDecimal("0.21");
    private static final BigDecimal VAT_RATE_105 = new BigDecimal("0.105");
    private static final BigDecimal VAT_RATE_27 = new BigDecimal("0.27");

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter PERIOD_FORMAT = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter RANGE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final BigDecimal ROUNDING_TOLERANCE = new BigDecimal("0.01");

    /**
     * Generates the CITI sales files for the provided invoices.
     *
     * @param invoices   invoices to export
     * @param start      start date used for folder naming
     * @param end        end date used for folder naming
     * @param issuerCuit CUIT of the issuer (used to create the output folder)
     * @return directory where the files were generated
     * @throws IOException when the files cannot be created
     */
    public Path exportSales(List<ClientInvoice> invoices, Date start, Date end, String issuerCuit)
            throws IOException {
        Path directory = resolveOutputDirectory(start, end, issuerCuit, SALES_FOLDER);
        writeLines(directory.resolve(SALES_CBTE_FILE), buildSalesCbteLines(invoices));
        writeLines(directory.resolve(SALES_ALIC_FILE), buildSalesAlicLines(invoices));
        return directory;
    }

    /**
     * Generates the CITI purchases files for the provided invoices.
     *
     * @param invoices   invoices to export
     * @param start      start date used for folder naming
     * @param end        end date used for folder naming
     * @param issuerCuit CUIT of the issuer (used to create the output folder)
     * @return directory where the files were generated
     * @throws IOException when the files cannot be created
     */
    public Path exportPurchases(List<ProviderInvoice> invoices, Date start, Date end, String issuerCuit)
            throws IOException {
        Path directory = resolveOutputDirectory(start, end, issuerCuit, PURCHASES_FOLDER);
        writeLines(directory.resolve(PURCHASES_CBTE_FILE), buildPurchasesCbteLines(invoices));
        writeLines(directory.resolve(PURCHASES_ALIC_FILE), buildPurchasesAlicLines(invoices));
        return directory;
    }

    private Path resolveOutputDirectory(Date start, Date end, String issuerCuit, String vatTypeFolder) throws IOException {
        String normalizedCuit = DocumentValidator.normalizeCuit(issuerCuit);
        if (normalizedCuit == null || normalizedCuit.isBlank()) {
            normalizedCuit = "sin-cuit";
        }
        Path base = Paths.get(AfipManagement.EXPORT_BASE_PATH)
                .resolve(VAT_ROOT_FOLDER)
                .resolve(normalizedCuit);
        if (vatTypeFolder != null && !vatTypeFolder.isBlank()) {
            base = base.resolve(vatTypeFolder);
        }
        String period = resolvePeriodFolder(start, end);
        if (!period.isBlank()) {
            base = base.resolve(period);
        }
        Files.createDirectories(base);
        return base;
    }

    private String resolvePeriodFolder(Date start, Date end) {
        LocalDate from = toLocalDate(start);
        LocalDate to = toLocalDate(end);
        if (from == null && to == null) {
            return LocalDate.now().format(PERIOD_FORMAT);
        }
        if (from == null) {
            from = to;
        }
        if (to == null) {
            to = from;
        }
        if (from == null || to == null) {
            return LocalDate.now().format(PERIOD_FORMAT);
        }
        if (from.getYear() == to.getYear() && from.getMonthValue() == to.getMonthValue()) {
            return from.format(PERIOD_FORMAT);
        }
        return from.format(RANGE_FORMAT) + "-" + to.format(RANGE_FORMAT);
    }

    private LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void writeLines(Path file, List<String> lines) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.ISO_8859_1)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private List<String> buildSalesCbteLines(List<ClientInvoice> invoices) {
        List<String> lines = new ArrayList<>();
        if (invoices == null) {
            return lines;
        }
        for (ClientInvoice invoice : invoices) {
            StringJoiner joiner = new StringJoiner(";");
            Client client = invoice.getClient();
            BigDecimal vat21 = safe(invoice.getVat21());
            BigDecimal vat105 = safe(invoice.getVat105());
            BigDecimal vat27 = safe(invoice.getVat27());
            BigDecimal subtotal = safe(invoice.getSubtotal());
            BigDecimal totalVat = vat21.add(vat105).add(vat27);
            BigDecimal total = safe(invoice.getTotal());
            BigDecimal nonTaxed = total.subtract(subtotal).subtract(totalVat);
            nonTaxed = normalizeAmount(nonTaxed);

            int vatCount = countVatEntries(vat21, vat105, vat27);

            String receiptNumber = formatReceiptNumber(invoice.getInvoiceNumber());

            joiner.add(formatInvoiceDate(invoice.getInvoiceDate()));
            joiner.add(resolveReceiptCode(invoice.getInvoiceType()));
            joiner.add(formatPointOfSale(invoice.getPointOfSale()));
            joiner.add(receiptNumber);
            joiner.add(receiptNumber);
            joiner.add(resolveDocumentTypeCode(client));
            joiner.add(formatDocumentNumber(client));
            joiner.add(sanitizeName(client != null ? client.getFullName() : ""));
            joiner.add(formatAmount(total));
            joiner.add(formatAmount(nonTaxed));
            joiner.add(formatAmount(BigDecimal.ZERO)); // Exento
            joiner.add(formatAmount(BigDecimal.ZERO)); // Perc. IVA
            joiner.add(formatAmount(BigDecimal.ZERO)); // Perc. IIBB
            joiner.add(formatAmount(BigDecimal.ZERO)); // Perc. municipales
            joiner.add(formatAmount(BigDecimal.ZERO)); // Impuestos internos
            joiner.add("PES");
            joiner.add("0001000000");
            joiner.add(Integer.toString(vatCount));
            joiner.add(resolveSalesOperationCode(subtotal, totalVat, total));
            joiner.add(formatAmount(BigDecimal.ZERO)); // Otros tributos
            joiner.add(""); // Fecha de vencimiento de pago (no disponible)
            lines.add(joiner.toString());
        }
        return lines;
    }

    private List<String> buildSalesAlicLines(List<ClientInvoice> invoices) {
        List<String> lines = new ArrayList<>();
        if (invoices == null) {
            return lines;
        }
        for (ClientInvoice invoice : invoices) {
            String typeCode = resolveReceiptCode(invoice.getInvoiceType());
            String pointOfSale = formatPointOfSale(invoice.getPointOfSale());
            String receiptNumber = formatReceiptNumber(invoice.getInvoiceNumber());
            appendVatLine(lines, typeCode, pointOfSale, receiptNumber, invoice.getVat105(), VAT_RATE_105);
            appendVatLine(lines, typeCode, pointOfSale, receiptNumber, invoice.getVat21(), VAT_RATE_21);
            appendVatLine(lines, typeCode, pointOfSale, receiptNumber, invoice.getVat27(), VAT_RATE_27);
        }
        return lines;
    }

    private List<String> buildPurchasesCbteLines(List<ProviderInvoice> invoices) {
        List<String> lines = new ArrayList<>();
        if (invoices == null) {
            return lines;
        }
        for (ProviderInvoice invoice : invoices) {
            Provider provider = invoice.getProvider();
            BigDecimal vat21 = safe(invoice.getVat21());
            BigDecimal vat105 = safe(invoice.getVat105());
            BigDecimal vat27 = safe(invoice.getVat27());
            BigDecimal totalVat = vat21.add(vat105).add(vat27);
            BigDecimal exempt = safe(invoice.getExemptAmount());
            BigDecimal vatPerception = safe(invoice.getVatPerception());
            BigDecimal grossIncomePerception = safe(invoice.getGrossIncomePerception());
            BigDecimal incomeTaxPerception = safe(invoice.getIncomeTaxPerception());
            BigDecimal stampTax = safe(invoice.getStampTax());
            BigDecimal subtotal = safe(invoice.getSubtotal());
            BigDecimal total = safe(invoice.getTotal());

            BigDecimal otherTributes = incomeTaxPerception.add(stampTax);
            BigDecimal nonTaxed = total.subtract(subtotal)
                    .subtract(totalVat)
                    .subtract(exempt)
                    .subtract(vatPerception)
                    .subtract(grossIncomePerception)
                    .subtract(otherTributes);
            nonTaxed = normalizeAmount(nonTaxed);

            BigDecimal netGravado = subtotal.compareTo(BigDecimal.ZERO) != 0
                    ? subtotal
                    : calculateNetFromVat(vat21, VAT_RATE_21)
                        .add(calculateNetFromVat(vat105, VAT_RATE_105))
                        .add(calculateNetFromVat(vat27, VAT_RATE_27));

            int vatCount = countVatEntries(vat21, vat105, vat27);
            BigDecimal creditoFiscal = totalVat;

            String receiptNumber = formatReceiptNumber(invoice.getInvoiceNumber());

            StringJoiner joiner = new StringJoiner(";");
            joiner.add(formatInvoiceDate(invoice.getInvoiceDate()));
            joiner.add(resolveReceiptCode(invoice.getInvoiceType()));
            joiner.add(formatPointOfSale(invoice.getPointOfSale()));
            joiner.add(receiptNumber);
            joiner.add(receiptNumber);
            joiner.add(resolveDocumentTypeCode(provider));
            joiner.add(formatDocumentNumber(provider));
            joiner.add(sanitizeName(provider != null ? provider.getName() : ""));
            joiner.add(formatAmount(total));
            joiner.add(formatAmount(nonTaxed));
            joiner.add(formatAmount(netGravado));
            joiner.add(formatAmount(exempt));
            joiner.add(formatAmount(BigDecimal.ZERO)); // IVA comisión (no disponible)
            joiner.add(formatAmount(BigDecimal.ZERO)); // IVA no categorizado
            joiner.add(formatAmount(BigDecimal.ZERO)); // IVA perc a no inscriptos
            joiner.add(formatAmount(vatPerception));
            joiner.add(formatAmount(grossIncomePerception));
            joiner.add(formatAmount(BigDecimal.ZERO)); // Perc. impuestos municipales
            joiner.add(formatAmount(stampTax)); // Impuestos internos / sellos
            joiner.add("PES");
            joiner.add("0001000000");
            joiner.add(Integer.toString(vatCount));
            joiner.add(resolvePurchasesOperationCode(creditoFiscal, netGravado, exempt));
            joiner.add(formatAmount(creditoFiscal));
            joiner.add(formatAmount(otherTributes));
            joiner.add(""); // Fecha de vencimiento de pago
            lines.add(joiner.toString());
        }
        return lines;
    }

    private List<String> buildPurchasesAlicLines(List<ProviderInvoice> invoices) {
        List<String> lines = new ArrayList<>();
        if (invoices == null) {
            return lines;
        }
        for (ProviderInvoice invoice : invoices) {
            String typeCode = resolveReceiptCode(invoice.getInvoiceType());
            String pointOfSale = formatPointOfSale(invoice.getPointOfSale());
            String receiptNumber = formatReceiptNumber(invoice.getInvoiceNumber());
            appendVatLine(lines, typeCode, pointOfSale, receiptNumber, invoice.getVat105(), VAT_RATE_105);
            appendVatLine(lines, typeCode, pointOfSale, receiptNumber, invoice.getVat21(), VAT_RATE_21);
            appendVatLine(lines, typeCode, pointOfSale, receiptNumber, invoice.getVat27(), VAT_RATE_27);
        }
        return lines;
    }

    private void appendVatLine(List<String> lines, String typeCode, String pointOfSale, String receiptNumber,
                               BigDecimal vatAmount, BigDecimal rate) {
        if (vatAmount == null) {
            return;
        }
        BigDecimal normalizedVat = normalizeAmount(vatAmount);
        if (normalizedVat.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        BigDecimal base = calculateNetFromVat(normalizedVat, rate);
        StringJoiner joiner = new StringJoiner(";");
        joiner.add(typeCode);
        joiner.add(pointOfSale);
        joiner.add(receiptNumber);
        joiner.add(formatAmount(base));
        joiner.add(formatRate(rate));
        joiner.add(formatAmount(normalizedVat));
        lines.add(joiner.toString());
    }

    private BigDecimal calculateNetFromVat(BigDecimal vatAmount, BigDecimal rate) {
        if (vatAmount == null || vatAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return vatAmount.divide(rate, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal normalizeAmount(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
        if (scaled.abs().compareTo(ROUNDING_TOLERANCE) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return scaled;
    }

    private int countVatEntries(BigDecimal... vats) {
        int count = 0;
        for (BigDecimal vat : vats) {
            if (vat != null && vat.setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) != 0) {
                count++;
            }
        }
        return count;
    }

    private String resolveReceiptCode(String invoiceType) {
        if (invoiceType == null || invoiceType.isBlank()) {
            return "000";
        }
        try {
            int parsed = Integer.parseInt(invoiceType.trim());
            return String.format(Locale.ROOT, "%03d", parsed);
        } catch (NumberFormatException ex) {
            Integer mapped = InvoiceTypeUtils.findAfipTypeCode(invoiceType);
            if (mapped != null) {
                return String.format(Locale.ROOT, "%03d", mapped);
            }
        }
        return "000";
    }

    private String formatInvoiceDate(LocalDateTime date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date.toLocalDate());
    }

    private String formatPointOfSale(String pointOfSale) {
        String digits = sanitizeDigits(pointOfSale);
        if (digits.length() > 5) {
            digits = digits.substring(digits.length() - 5);
        }
        return leftPadWithZeros(digits, 5);
    }

    private String formatReceiptNumber(String invoiceNumber) {
        String digits = sanitizeDigits(invoiceNumber);
        if (digits.length() > 20) {
            digits = digits.substring(digits.length() - 20);
        }
        return leftPadWithZeros(digits, 20);
    }

    private String resolveDocumentTypeCode(Client client) {
        if (client == null) {
            return "99";
        }
        return resolveDocumentTypeCode(client.getDocumentType(), client.getDocumentNumber());
    }

    private String resolveDocumentTypeCode(Provider provider) {
        if (provider == null) {
            return "99";
        }
        return resolveDocumentTypeCode(provider.getDocumentType(), provider.getDocumentNumber());
    }

    private String resolveDocumentTypeCode(String documentType, String documentNumber) {
        String normalizedType = documentType == null ? "" : documentType.trim().toUpperCase(Locale.ROOT);
        switch (normalizedType) {
            case "CUIT":
            case "CUIL":
                return "80";
            case "DNI":
                return "96";
            case "LE":
                return "89";
            case "LC":
                return "90";
            default:
                String digits = sanitizeDigits(documentNumber);
                if (digits.length() == 11) {
                    return "80";
                }
                if (digits.length() == 8) {
                    return "96";
                }
                return "99";
        }
    }

    private String formatDocumentNumber(Client client) {
        if (client == null) {
            return "0";
        }
        return formatDocumentNumber(client.getDocumentNumber());
    }

    private String formatDocumentNumber(Provider provider) {
        if (provider == null) {
            return "0";
        }
        return formatDocumentNumber(provider.getDocumentNumber());
    }

    private String formatDocumentNumber(String documentNumber) {
        String digits = sanitizeDigits(documentNumber);
        if (digits.length() > 20) {
            digits = digits.substring(digits.length() - 20);
        }
        return leftPadWithZeros(digits, 20);
    }

    private String sanitizeDigits(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("\\D", "");
    }

    private String formatAmount(BigDecimal value) {
        return normalizeAmount(value).toPlainString();
    }

    private String formatRate(BigDecimal rate) {
        if (rate == null) {
            return "0";
        }
        return rate.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String resolveSalesOperationCode(BigDecimal neto, BigDecimal iva, BigDecimal total) {
        if (total == null) {
            return "";
        }
        BigDecimal safeNet = safe(neto);
        BigDecimal safeVat = safe(iva);
        if (safeNet.compareTo(BigDecimal.ZERO) == 0 && safeVat.compareTo(BigDecimal.ZERO) == 0) {
            return "E"; // Operación exenta o no gravada
        }
        return "";
    }

    private String resolvePurchasesOperationCode(BigDecimal creditoFiscal, BigDecimal neto, BigDecimal exento) {
        if (creditoFiscal == null) {
            return "";
        }
        if (creditoFiscal.compareTo(BigDecimal.ZERO) > 0 && neto.compareTo(BigDecimal.ZERO) > 0) {
            return "A";
        }
        if (safe(exento).compareTo(BigDecimal.ZERO) > 0) {
            return "E";
        }
        return "";
    }

    private String sanitizeName(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        normalized = normalized.replace(';', ' ').replace('\r', ' ').replace('\n', ' ').trim();
        if (normalized.length() > 30) {
            normalized = normalized.substring(0, 30);
        }
        return normalized;
    }

    private String leftPadWithZeros(String value, int length) {
        if (value == null) {
            value = "";
        }
        if (value.length() >= length) {
            return value;
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = value.length(); i < length; i++) {
            sb.append('0');
        }
        sb.append(value);
        return sb.toString();
    }
}

