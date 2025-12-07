package utils.pyAfip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Date;

import configs.AppConfig;

import models.Client;
import models.ClientInvoice;
import models.ClientInvoiceDetail;
import models.TaxCondition;
import utils.DocumentValidator;
import utils.InvoiceTypeUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper utilities for interacting with the PyAfipWs service. The original
 * project exposed a very large helper with many dependencies on legacy entity
 * classes. This version keeps the same public API but adapts the internals to
 * the current domain models.
 */
public final class AfipManagement {

    private static final Logger LOGGER = Logger.getLogger(AfipManagement.class.getName());

    /** Path where the PyAfipWs executable reads and writes files. */
    public static final String DEFAULT_AFIP_WORKING_DIRECTORY = "C:\\Program Files\\PyAfipWs\\";
    private static final String WORKING_DIRECTORY_PROPERTY = "afip.pdf.workingDir";

    private static final String PDF_CONFIG_FILE_NAME = "product-management-pyfepdf.ini";
    private static final String PDF_COMPANY_NAME_PROPERTY = "afip.pdf.companyName";
    private static final String PDF_HEADER_LINE1_PROPERTY = "afip.pdf.headerLine1";
    private static final String PDF_HEADER_LINE2_PROPERTY = "afip.pdf.headerLine2";
    private static final String PDF_IIBB_PROPERTY = "afip.pdf.headerIibb";
    private static final String PDF_IVA_PROPERTY = "afip.pdf.headerIva";
    private static final String PDF_START_DATE_PROPERTY = "afip.pdf.headerStartDate";
    private static final String PDF_LOGO_PROPERTY = "afip.pdf.logoPath";
    private static final String PDF_WATERMARK_PROPERTY = "afip.pdf.watermark";

    /** Path used by the desktop application to export generated reports. */
    public static final String EXPORT_BASE_PATH = resolveExportBasePath();
    private static final String EXPORT_DIRECTORY_PROPERTY = "afip.pdf.exportDir";

    private static final DateTimeFormatter YEAR_FORMAT = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MM");

    /** Default point of sale used for standard invoices. */
    public static final String DEFAULT_POINT_OF_SALE = "0001";

    private static final BigDecimal VAT_RATE_21 = new BigDecimal("0.21");
    private static final BigDecimal VAT_RATE_105 = new BigDecimal("0.105");
    private static final RoundingMode AMOUNT_ROUNDING_MODE = RoundingMode.DOWN;
    private static final DateTimeFormatter CAE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static int resolveAfipTypeCode(String invoiceType) {
        if (invoiceType == null) {
            return 0;
        }
        try {
            return Integer.parseInt(invoiceType);
        } catch (NumberFormatException ex) {
            Integer mapped = InvoiceTypeUtils.findAfipTypeCode(invoiceType);
            if (mapped != null) {
                return mapped;
            }
            throw new IllegalArgumentException("Unknown invoice type: " + invoiceType, ex);
        }
    }

    private AfipManagement() {
        // utility class
    }

    // ---------------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------------

    /**
     * Generates the {@code entrada.txt} file consumed by PyAfipWs in order to
     * obtain the CAE of an electronic invoice.
     */
    public static void generateElectronicVoucherEntry(ClientInvoice invoice,
                                              List<ClientInvoiceDetail> details,
                                              ClientInvoice associated) {
        File workingDirectory = getAfipWorkingDirectory();
        try (PrintWriter pw = new PrintWriter(new FileWriter(new File(workingDirectory, "entrada.txt")))) {
            Client client = invoice.getClient();

            writeEntryHeader(pw, invoice, client);
            pw.println();

            writeVatEntryDetails(pw, invoice);

            if (associated != null && shouldIncludeAssociatedReceipt(invoice)) {
                pw.println();
                writeAssociatedReceipt(pw, invoice, associated);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a {@code factura.txt} file used by PyAfipWs to render the final
     * PDF invoice. The file is generated using ISO-8859-1 encoding as expected
     * by the external service.
     */
    public static void generateElectronicVoucherPdf(ClientInvoice invoice,
                                                   ClientInvoice associated) {
        File workingDirectory = getAfipWorkingDirectory();
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(new File(workingDirectory, "factura.txt")), "8859_1")))) {
            Client client = invoice.getClient();
            writePdfHeader(pw, invoice, client);
            writePdfItems(pw, invoice);
            writePdfVatSummary(pw, invoice);
            writeAssociatedReceipt(pw, invoice, associated);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File preparePdfConfiguration(ClientInvoice invoice) throws IOException {
        File workingDirectory = getAfipWorkingDirectory();
        File configFile = new File(workingDirectory, PDF_CONFIG_FILE_NAME);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(configFile), StandardCharsets.ISO_8859_1))) {
            writeIniFactSection(writer);
            writer.newLine();
            Map<String, String> pdfDefaults = loadRecePdfDefaults(invoice == null ? null : invoice.getIssuerCuit());
            writeIniPdfSection(writer, invoice, pdfDefaults);
        }
        return configFile;
    }

    private static void writeIniFactSection(BufferedWriter writer) throws IOException {
        writer.write("[FACTURA]");
        writer.newLine();
        writeIniLine(writer, "ENTRADA", "factura.txt");
        writeIniLine(writer, "SALIDA", "factura.pdf");
        writeIniLine(writer, "FORMATO", "factura.csv");
        writeIniLine(writer, "DIRECTORIO", ".");
        writeIniLine(writer, "SUBDIRECTORIO", "");
        writeIniLine(writer, "ARCHIVO", "numero");
        writeIniLine(writer, "PAPEL", "legal");
        writeIniLine(writer, "ORIENTACION", "portrait");
        writeIniLine(writer, "FMT_CANTIDAD", "0.2");
        writeIniLine(writer, "FMT_PRECIO", "0.2");
        writeIniLine(writer, "CANT_POS", "izq");
    }

    private static void writeIniPdfSection(BufferedWriter writer,
                                          ClientInvoice invoice,
                                          Map<String, String> defaultValues) throws IOException {
        writer.write("[PDF]");
        writer.newLine();
        String logoPath = resolveLogoValue(getAfipWorkingDirectory(), defaultValues.get("LOGO"));
        if (logoPath != null) {
            writeIniLine(writer, "LOGO", logoPath);
        }
        writeIniLine(writer, "EMPRESA", resolveConfiguredValueAllowEmpty(PDF_COMPANY_NAME_PROPERTY,
                defaultValues.get("EMPRESA"), resolveIssuerCuitLabel(invoice)));
        writeIniLine(writer, "MEMBRETE1", resolveConfiguredValue(PDF_HEADER_LINE1_PROPERTY,
                defaultValues.get("MEMBRETE1"), ""));
        writeIniLine(writer, "MEMBRETE2", resolveConfiguredValue(PDF_HEADER_LINE2_PROPERTY,
                defaultValues.get("MEMBRETE2"), ""));
        writeIniLine(writer, "CUIT", Optional.ofNullable(defaultValues.get("CUIT"))
                .filter(value -> !value.isBlank())
                .orElse(resolveIssuerCuitLabel(invoice)));
        writeIniLine(writer, "IIBB", resolveConfiguredValue(PDF_IIBB_PROPERTY,
                defaultValues.get("IIBB"), ""));
        writeIniLine(writer, "IVA", resolveConfiguredValue(PDF_IVA_PROPERTY,
                defaultValues.get("IVA"), ""));
        writeIniLine(writer, "INICIO", resolveConfiguredValue(PDF_START_DATE_PROPERTY,
                defaultValues.get("INICIO"), ""));
        writeIniLine(writer, "BORRADOR", resolveConfiguredValue(PDF_WATERMARK_PROPERTY,
                defaultValues.get("BORRADOR"), ""));
    }

    private static String resolveLogoValue(File workingDirectory, String defaultValue) {
        String configuredLogo = resolveConfiguredValueAllowEmpty(PDF_LOGO_PROPERTY, defaultValue, "");
        if (configuredLogo == null || configuredLogo.trim().isEmpty()) {
            return null;
        }

        String candidatePath = configuredLogo.trim();
        File logoFile = new File(candidatePath);
        if (!logoFile.isAbsolute()) {
            logoFile = new File(workingDirectory, candidatePath);
        }

        if (!logoFile.isFile()) {
            LOGGER.log(Level.WARNING, "No se encontró el logo configurado para PyAfip en {0}. Se usará el logo por defecto de la plantilla.", logoFile.getAbsolutePath());
            return null;
        }

        return logoFile.getAbsolutePath();
    }

    private static void writeIniLine(BufferedWriter writer, String key, String value) throws IOException {
        writer.write(key);
        writer.write('=');
        writer.write(sanitizeIniValue(value));
        writer.newLine();
    }

    private static Map<String, String> loadRecePdfDefaults(String issuerCuit) {
        Map<String, String> values = new HashMap<>();
        String normalized = DocumentValidator.normalizeCuit(issuerCuit);
        if (normalized == null || normalized.isBlank()) {
            return values;
        }

        File workingDirectory = getAfipWorkingDirectory();
        File iniFile = new File(workingDirectory, "rece" + normalized + ".ini");
        if (!iniFile.isFile()) {
            return values;
        }

        try (BufferedReader reader = Files.newBufferedReader(iniFile.toPath(), StandardCharsets.ISO_8859_1)) {
            String currentSection = null;
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith(";")) {
                    continue;
                }
                if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                    currentSection = trimmed.substring(1, trimmed.length() - 1).trim().toUpperCase(Locale.ROOT);
                    continue;
                }

                if (!"PDF".equals(currentSection)) {
                    continue;
                }

                int separatorIndex = trimmed.indexOf('=');
                if (separatorIndex <= 0) {
                    continue;
                }

                String key = trimmed.substring(0, separatorIndex).trim().toUpperCase(Locale.ROOT);
                String value = trimmed.substring(separatorIndex + 1).trim();
                if (!key.isEmpty()) {
                    values.put(key, value);
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "No se pudo leer la configuración PDF de AFIP desde {0}", iniFile.getAbsolutePath());
            LOGGER.log(Level.FINE, "Error leyendo archivo rece", ex);
        }

        return values;
    }

    private static String sanitizeIniValue(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('\r', ' ').replace('\n', ' ').trim();
    }

    private static String resolveConfiguredValue(String key, String iniValue, String defaultValue) {
        String configured = System.getProperty(key);
        if (configured != null && !configured.trim().isEmpty()) {
            return configured.trim();
        }
        String valueFromConfig = AppConfig.get(key, null);
        if (valueFromConfig != null && !valueFromConfig.trim().isEmpty()) {
            return valueFromConfig.trim();
        }
        if (iniValue != null && !iniValue.trim().isEmpty()) {
            return iniValue.trim();
        }
        return defaultValue;
    }

    private static String resolveConfiguredValueAllowEmpty(String key, String iniValue, String defaultValue) {
        String configured = System.getProperty(key);
        if (configured != null) {
            return configured.trim();
        }

        String valueFromConfig = AppConfig.get(key, null);
        if (valueFromConfig != null) {
            return valueFromConfig.trim();
        }

        if (iniValue != null) {
            return iniValue.trim();
        }
        return defaultValue;
    }

    private static String resolveIssuerCuitLabel(ClientInvoice invoice) {
        if (invoice == null || invoice.getIssuerCuit() == null || invoice.getIssuerCuit().trim().isEmpty()) {
            return "";
        }
        return "CUIT " + Optional.ofNullable(DocumentValidator.formatCuit(invoice.getIssuerCuit()))
                .orElse(invoice.getIssuerCuit());
    }

    /**
     * Reads a substring from the {@code salida.txt} file generated by
     * PyAfipWs. It is typically used to obtain the CAE and other metadata of
     * the invoiced document.
     */
    public static String readAfipOutputData(int desde, int hasta) {
        try {
            File workingDirectory = getAfipWorkingDirectory();
            byte[] bytes = Files.readAllBytes(Paths.get(workingDirectory.getAbsolutePath(), "salida.txt"));
            String datos = new String(bytes, StandardCharsets.ISO_8859_1);
            return datos.length() >= hasta ? datos.substring(desde, hasta) : "";
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * Resolves the directory where the PyAfipWs utilities should read and
     * write their files. The value can be customized through the system
     * property {@code afip.pdf.workingDir}. If the property is not defined, the
     * legacy default location is used.
     *
     * @return directory configured for PyAfipWs execution.
     */
    public static File getAfipWorkingDirectory() {
        String configured = System.getProperty(WORKING_DIRECTORY_PROPERTY);
        String path = (configured == null || configured.trim().isEmpty())
                ? DEFAULT_AFIP_WORKING_DIRECTORY
                : configured.trim();

        File directory = new File(path);
        if (!directory.isAbsolute()) {
            directory = directory.getAbsoluteFile();
        }
        return directory;
    }

    private static String resolveExportBasePath() {
        String configured = System.getProperty(EXPORT_DIRECTORY_PROPERTY);
        String path;
        if (configured != null && !configured.trim().isEmpty()) {
            path = configured.trim();
        } else {
            String userHome = System.getProperty("user.home");
            if (userHome != null && !userHome.trim().isEmpty()) {
                path = userHome + File.separator + "Desktop" + File.separator;
            } else {
                path = DEFAULT_AFIP_WORKING_DIRECTORY;
            }
        }

        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        return path;
    }

    private static String buildInvoiceExportPath(ClientInvoice invoice) {
        LocalDateTime invoiceDate = invoice.getInvoiceDate() != null ? invoice.getInvoiceDate() : LocalDateTime.now();
        String year = invoiceDate.format(YEAR_FORMAT);
        String month = invoiceDate.format(MONTH_FORMAT);

        String invoiceAbbreviation = InvoiceTypeUtils.toAbbreviation(invoice.getInvoiceType());
        if (invoiceAbbreviation == null || invoiceAbbreviation.isBlank()) {
            invoiceAbbreviation = "Factura";
        }

        String formattedPointOfSale = padLeftWithZeros(Optional.ofNullable(invoice.getPointOfSale()).orElse(""), 4);
        String formattedInvoiceNumber = padLeftWithZeros(Optional.ofNullable(invoice.getInvoiceNumber()).orElse(""), 8);

        String clientName = Optional.ofNullable(invoice.getClient()).map(Client::getFullName).orElse("").trim();
        String clientDocument = buildClientDocumentLabel(invoice.getClient());

        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(invoiceAbbreviation.trim())
                .append(' ')
                .append(formattedPointOfSale)
                .append('-')
                .append(formattedInvoiceNumber);

        if (!clientName.isEmpty()) {
            fileNameBuilder.append(' ').append(clientName);
        }

        if (!clientDocument.isEmpty()) {
            fileNameBuilder.append(' ').append(clientDocument);
        }

        String sanitizedName = fileNameBuilder.toString()
                .replaceAll("[\\\\/:*?\\\"<>|]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        Path exportDirectory = Paths.get(EXPORT_BASE_PATH, "Facturas", year, month);
        try {
            Files.createDirectories(exportDirectory);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "No se pudo crear la carpeta de exportación de facturas {0}",
                    exportDirectory.toAbsolutePath());
        }

        return exportDirectory.resolve(sanitizedName + ".pdf").toString();
    }

    private static String buildClientDocumentLabel(Client client) {
        if (client == null) {
            return "";
        }

        String documentType = Optional.ofNullable(client.getDocumentType()).orElse("").trim();
        String documentNumber = Optional.ofNullable(DocumentValidator.normalizeCuit(client.getDocumentNumber()))
                .orElse("").trim();

        if (documentType.isEmpty() && documentNumber.isEmpty()) {
            return "";
        }

        if (documentType.isEmpty()) {
            return documentNumber;
        }

        if (documentNumber.isEmpty()) {
            return documentType;
        }

        return documentType + " " + documentNumber;
    }

    // ---------------------------------------------------------------------
    // Text generation helpers
    // ---------------------------------------------------------------------

    private static void writeEntryHeader(PrintWriter pw,
                                                   ClientInvoice invoice,
                                                   Client client) {
        int afipTypeCode = resolveAfipTypeCode(invoice.getInvoiceType());
        boolean isInvoiceTypeC = isTypeCInvoice(afipTypeCode);

        pw.print("0");
        pw.print(formatDate(invoice.getInvoiceDate(), 1));
        pw.print(mapToPyAfipReceiptCode(afipTypeCode));
        pw.print(padLeftWithZeros(invoice.getPointOfSale(), 4));
        pw.print(padLeftWithZeros(invoice.getInvoiceNumber(), 8));
        pw.print(padLeftWithZeros(invoice.getInvoiceNumber(), 8));
        pw.print("1");

        String docType = Optional.ofNullable(client.getDocumentType()).orElse("");
        String tipoDoc = docType.equalsIgnoreCase("cuit") ? "80"
                : docType.equalsIgnoreCase("dni") ? "96" : "00";
        pw.print(tipoDoc);
        pw.print(padLeftWithZeros(client.getDocumentNumber(), 11));

        BigDecimal impNeto = truncateAmount(Optional.ofNullable(invoice.getSubtotal()).orElse(BigDecimal.ZERO), 2);
        BigDecimal impIva = isInvoiceTypeC ? BigDecimal.ZERO
                : truncateAmount(Optional.ofNullable(invoice.getVat105()).orElse(BigDecimal.ZERO)
                        .add(Optional.ofNullable(invoice.getVat21()).orElse(BigDecimal.ZERO)), 2);
        BigDecimal impTrib = BigDecimal.ZERO;

        BigDecimal totalAmount = truncateAmount(Optional.ofNullable(invoice.getTotal()).orElse(impNeto.add(impIva)), 2);
        BigDecimal impTotal = totalAmount;
        if (isInvoiceTypeC) {
            // Para comprobantes tipo C AFIP exige que ImpTotal sea igual a ImpNeto + ImpTrib
            BigDecimal finalAmount = totalAmount.compareTo(BigDecimal.ZERO) > 0 ? totalAmount : impNeto;
            impNeto = finalAmount;
            impTotal = finalAmount.add(impTrib);
        }

        pw.print(formatAmount(impTotal, 13, 2));
        pw.print(formatAmount(impTrib, 13, 2));
        pw.print(formatAmount(BigDecimal.ZERO, 13, 2));
        pw.print(formatAmount(impNeto, 13, 2));
        pw.print(formatAmount(impIva, 13, 2));
        pw.print(formatAmount(BigDecimal.ZERO, 13, 2));
        pw.print(formatAmount(BigDecimal.ZERO, 13, 2));

        pw.print("PES");
        pw.print("0001000000");
        pw.print(padRightWithSpaces("", 8));

        // Campos de respuesta del servicio (se completan luego de la llamada)
        pw.print(padRightWithSpaces("", 14));
        pw.print(padRightWithSpaces("", 8));
        pw.print(padRightWithSpaces("", 1));
        pw.print(padRightWithSpaces("", 1000));
        pw.print(padRightWithSpaces("", 6));
        pw.print(padRightWithSpaces("", 1000));
        pw.print(padRightWithSpaces("", 1));

        // Campos nuevos del formato WSFEv1
        pw.print(padRightWithSpaces("", 4));
        pw.print(padRightWithSpaces("", 8));
        pw.print(padRightWithSpaces("", 8));
        pw.print(mapToAfipReceiptCode(afipTypeCode));
        pw.print(padLeftWithZeros(invoice.getPointOfSale(), 5));
        pw.print(padRightWithSpaces("", 14));
        pw.print(padRightWithSpaces("", 1));

        int condId = Optional.ofNullable(client.getTaxCondition())
                .map(TaxCondition::getId).orElse(0);
        pw.print(mapVatConditionCode(condId));
    }

    private static void writeVatEntryDetails(PrintWriter pw,
                                                   ClientInvoice invoice) {
        if (isTypeCInvoice(resolveAfipTypeCode(invoice.getInvoiceType()))) {
            return;
        }

        BigDecimal iva105 = truncateAmount(Optional.ofNullable(invoice.getVat105()).orElse(BigDecimal.ZERO), 2);
        BigDecimal iva21 = truncateAmount(Optional.ofNullable(invoice.getVat21()).orElse(BigDecimal.ZERO), 2);

        BigDecimal base105 = iva105.compareTo(BigDecimal.ZERO) > 0
                ? truncateAmount(calculateVat105Base(iva105), 2)
                : BigDecimal.ZERO;
        BigDecimal base21 = iva21.compareTo(BigDecimal.ZERO) > 0
                ? truncateAmount(calculateVat21Base(iva21), 2)
                : BigDecimal.ZERO;

        BigDecimal impNeto = truncateAmount(Optional.ofNullable(invoice.getSubtotal()).orElse(BigDecimal.ZERO), 2);
        BigDecimal adjustment = impNeto.subtract(base105.add(base21));
        if (adjustment.compareTo(BigDecimal.ZERO) != 0) {
            boolean applied = false;

            if (iva21.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal candidate = base21.add(adjustment);
                if (candidate.compareTo(BigDecimal.ZERO) >= 0) {
                    base21 = candidate;
                    applied = true;
                }
            }

            if (!applied && iva105.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal candidate = base105.add(adjustment);
                if (candidate.compareTo(BigDecimal.ZERO) >= 0) {
                    base105 = candidate;
                    applied = true;
                }
            }

            if (!applied && iva21.compareTo(BigDecimal.ZERO) > 0) {
                base21 = base21.add(adjustment);
                applied = true;
            } else if (!applied && iva105.compareTo(BigDecimal.ZERO) > 0) {
                base105 = base105.add(adjustment);
                applied = true;
            }

            if (applied) {
                base21 = base21.setScale(2, RoundingMode.HALF_UP);
                base105 = base105.setScale(2, RoundingMode.HALF_UP);
            }
        }

        if (iva105.compareTo(BigDecimal.ZERO) > 0) {
            pw.print("2");
            pw.print(padLeftWithZeros("4", 16));
            pw.print(formatAmount(base105, 13, 2));
            pw.print(formatAmount(iva105, 13, 2));
            pw.println();
        }

        if (iva21.compareTo(BigDecimal.ZERO) > 0) {
            pw.print("2");
            pw.print(padLeftWithZeros("5", 16));
            pw.print(formatAmount(base21, 13, 2));
            pw.print(formatAmount(iva21, 13, 2));
            pw.println();
        }
    }

    private static void writeAssociatedReceipt(PrintWriter pw,
                                                    ClientInvoice invoice,
                                                    ClientInvoice associated) {
        if (associated == null) {
            return;
        }

        if (!shouldIncludeAssociatedReceipt(invoice)) {
            return;
        }

        pw.print("3");
        pw.print(mapToAfipReceiptCode(resolveAfipTypeCode(associated.getInvoiceType())));
        pw.print(padLeftWithZeros(associated.getPointOfSale(), 4));
        pw.print(padLeftWithZeros(associated.getInvoiceNumber(), 8));
        pw.print(formatDate(associated.getInvoiceDate(), 1));
        String issuerCuit = Optional.ofNullable(associated.getIssuerCuit()).orElse(invoice.getIssuerCuit());
        String normalized = DocumentValidator.normalizeCuit(issuerCuit);
        pw.print(padLeftWithZeros(normalized, 11));
    }

    private static boolean shouldIncludeAssociatedReceipt(ClientInvoice invoice) {
        int tipo = resolveAfipTypeCode(invoice.getInvoiceType());
        List<Integer> tipos = Arrays.asList(202, 203, 207, 208, 9, 10, 2, 3, 12, 13);
        return tipos.contains(tipo);
    }

    private static void writePdfHeader(PrintWriter pw,
                                              ClientInvoice invoice,
                                              Client client) {
        pw.print("0");
        pw.print("WSFEv1");
        pw.print(formatDate(invoice.getInvoiceDate(), 1));

        int tipo = resolveAfipTypeCode(invoice.getInvoiceType());
        boolean esFCE = Arrays.asList(201, 202, 203, 206, 207, 208).contains(tipo);
        if (esFCE) {
            pw.print(padRightWithSpaces("", 2));
            pw.print(padRightWithSpaces("", 4));
        } else {
            pw.print(mapToPyAfipReceiptCode(tipo));
            pw.print(padLeftWithZeros(invoice.getPointOfSale(), 4));
        }

        pw.print(padLeftWithZeros(invoice.getInvoiceNumber(), 8));
        pw.print("1");
        pw.print(" ");
        pw.print("200");
        pw.print(padRightWithSpaces(client.getFullName(), 200));

        String docType = Optional.ofNullable(client.getDocumentType()).orElse("");
        String tipoDoc = docType.equalsIgnoreCase("cuit") ? "80"
                : docType.equalsIgnoreCase("dni") ? "96" : "00";
        pw.print(tipoDoc);
        pw.print(padLeftWithZeros(client.getDocumentNumber(), 11));

        String direccion = Optional.ofNullable(client.getAddress()).map(a -> a.getName()).orElse("")
                + " " + Optional.ofNullable(client.getAddressNumber()).orElse("");
        pw.print(padRightWithSpaces(direccion, 300));

        int condId = Optional.ofNullable(client.getTaxCondition())
                .map(TaxCondition::getId).orElse(0);
        pw.print(padRightWithSpaces(describeTaxCondition(condId), 50));

        pw.print(formatAmount(truncateAmount(invoice.getTotal(), 3), 12, 3));
        pw.print(padRightWithSpaces("", 15));

        BigDecimal subtotalFinal = Optional.ofNullable(invoice.getSubtotal()).orElse(BigDecimal.ZERO);
        if (isTypeBInvoice(tipo)) {
            subtotalFinal = subtotalFinal
                    .add(Optional.ofNullable(invoice.getVat21()).orElse(BigDecimal.ZERO))
                    .add(Optional.ofNullable(invoice.getVat105()).orElse(BigDecimal.ZERO));
        } else if (isTypeCInvoice(tipo)) {
            subtotalFinal = Optional.ofNullable(invoice.getTotal()).orElse(subtotalFinal);
        }
        pw.print(formatAmount(truncateAmount(subtotalFinal, 3), 12, 3));

        BigDecimal ivaSum = isTypeCInvoice(tipo)
                ? BigDecimal.ZERO
                : Optional.ofNullable(invoice.getVat105()).orElse(BigDecimal.ZERO)
                        .add(Optional.ofNullable(invoice.getVat21()).orElse(BigDecimal.ZERO));
        pw.print(formatAmount(truncateAmount(ivaSum, 3), 12, 3));

        for (int i = 0; i < 7; i++) {
            pw.print(padRightWithSpaces("", 15));
        }
        pw.print("PES");
        pw.print("0001000000");
        pw.print(padRightWithSpaces("", 1000));
        pw.print(padRightWithSpaces("", 1000));

        String forma = Optional.ofNullable(invoice.getPaymentMethod()).orElse("");
        pw.print(padRightWithSpaces(forma, 50));
        pw.print(padRightWithSpaces("", 3));
        pw.print(padRightWithSpaces("", 20));
        pw.print("1");
        pw.print(padRightWithSpaces("", 5));
        pw.print(padRightWithSpaces("", 8));
        pw.print("0");
        pw.print(padRightWithSpaces("", 8));
        pw.print(padRightWithSpaces("", 8));

        String cae = resolveCaeForPdf(invoice);
        String caeExpiration = resolveCaeExpirationForPdf(invoice);
        String authorizationStatus = resolveAuthorizationStatusForPdf(invoice);
        String resultCode = resolveResultCodeForPdf(invoice);

        pw.print(padRightWithSpaces(cae, 14));
        pw.print(padRightWithSpaces(caeExpiration, 8));
        pw.print(authorizationStatus);
        pw.print(resultCode);
        pw.print(padRightWithSpaces(readAfipOutputData(194, 1194), 1000));

        String invoiceId = invoice.getId() != null ? invoice.getId().toString() : "0";
        pw.print(padLeftWithZeros(invoiceId, 15));
        pw.print(padRightWithSpaces(Optional.ofNullable(client.getMobile()).orElse(""), 50));
        String ciudad = Optional.ofNullable(client.getCity()).map(c -> c.getName()).orElse("");
        pw.print(padRightWithSpaces(ciudad, 50));
        pw.print(padRightWithSpaces("", 50));
        pw.print(padLeftWithZeros("0", 10));
        pw.print(padRightWithSpaces("", 100));

        String nomPdf = buildInvoiceExportPath(invoice);
        pw.print(padRightWithSpaces(nomPdf, 100));
        
        pw.print(padRightWithSpaces(readAfipOutputData(1194, 1200), 6));
        pw.print(padRightWithSpaces(readAfipOutputData(1200, 2200), 1000));
        pw.print(padRightWithSpaces("", 203));
        pw.print(mapToAfipReceiptCode(resolveAfipTypeCode(invoice.getInvoiceType())));
        pw.print(padLeftWithZeros(invoice.getPointOfSale(), 5));
        pw.println();
    }

    private static void writePdfItems(PrintWriter pw, ClientInvoice invoice) {
        List<ClientInvoiceDetail> detalles = invoice.getDetails();
        int tipoComprobante = resolveAfipTypeCode(invoice.getInvoiceType());
        boolean esFacturaB = isTypeBInvoice(tipoComprobante);
        boolean omitirDetalleIVA = esFacturaB || isTypeCInvoice(tipoComprobante);

        if (detalles == null) {
            return;
        }

        for (ClientInvoiceDetail d : detalles) {
            pw.print("1");
            pw.print(padRightWithSpaces(d.getArticleCode(), 30));

            BigDecimal cantidad = Optional.ofNullable(d.getQuantity()).orElse(BigDecimal.ZERO);
            pw.print(formatIntegerPart(cantidad, 10));
            pw.print(formatDecimalPart(cantidad, 2));
            pw.print("07");

            BigDecimal precio = Optional.ofNullable(d.getUnitPrice()).orElse(BigDecimal.ZERO);
            BigDecimal ivaMonto = Optional.ofNullable(d.getVatAmount()).orElse(BigDecimal.ZERO);
            BigDecimal tasa = BigDecimal.ZERO;
            if (precio.compareTo(BigDecimal.ZERO) > 0 && ivaMonto.compareTo(BigDecimal.ZERO) > 0) {
                tasa = ivaMonto.divide(precio.multiply(cantidad), 4, AMOUNT_ROUNDING_MODE);
            }

            BigDecimal precioConIVA = precio;
            BigDecimal subtotal = precio.multiply(cantidad).setScale(2, AMOUNT_ROUNDING_MODE);
            if (esFacturaB) {
                precioConIVA = precio.multiply(BigDecimal.ONE.add(tasa)).setScale(2, AMOUNT_ROUNDING_MODE);
                subtotal = precioConIVA.multiply(cantidad).setScale(2, AMOUNT_ROUNDING_MODE);
            }

            pw.print(formatAmount(truncateAmount(precioConIVA, 3), 12, 3));
            pw.print(formatAmount(truncateAmount(subtotal, 3), 12, 3));

            String codigoIVA;
            if (omitirDetalleIVA) {
                codigoIVA = padRightWithSpaces("", 5);
            } else if (tasa.compareTo(VAT_RATE_21) == 0) {
                codigoIVA = "00005";
            } else if (tasa.compareTo(VAT_RATE_105) == 0) {
                codigoIVA = "00004";
            } else {
                codigoIVA = "00003";
            }
            pw.print(codigoIVA);

            pw.print(padRightWithSpaces(d.getArticleDescription(), 4000));
            pw.print(padRightWithSpaces("", 15));
            pw.print(padRightWithSpaces("", 15));

            BigDecimal bonificacion = precio.multiply(
                    Optional.ofNullable(d.getDiscountPercent()).orElse(BigDecimal.ZERO)
                            .divide(BigDecimal.valueOf(100), 4, AMOUNT_ROUNDING_MODE));
            pw.print(formatAmount(truncateAmount(bonificacion, 2), 13, 2));

            BigDecimal impIVA = omitirDetalleIVA ? BigDecimal.ZERO : truncateAmount(ivaMonto, 2);
            pw.println(formatAmount(impIVA, 13, 2));
        }
    }

    private static boolean hasStoredCae(ClientInvoice invoice) {
        return invoice != null
                && invoice.getCae() != null
                && !invoice.getCae().trim().isEmpty();
    }

    private static String resolveCaeForPdf(ClientInvoice invoice) {
        if (hasStoredCae(invoice)) {
            return invoice.getCae().trim();
        }
        String cae = readAfipOutputData(171, 185);
        return cae != null ? cae.trim() : "";
    }

    private static String resolveCaeExpirationForPdf(ClientInvoice invoice) {
        if (invoice != null && invoice.getCaeExpirationDate() != null) {
            return CAE_DATE_FORMATTER.format(invoice.getCaeExpirationDate());
        }
        String expiration = readAfipOutputData(185, 193);
        return expiration != null ? expiration.trim() : "";
    }

    private static String resolveAuthorizationStatusForPdf(ClientInvoice invoice) {
        if (hasStoredCae(invoice)) {
            return "A";
        }
        String status = readAfipOutputData(193, 194);
        return status != null && !status.trim().isEmpty() ? status.trim() : " ";
    }

    private static String resolveResultCodeForPdf(ClientInvoice invoice) {
        if (hasStoredCae(invoice)) {
            return " ";
        }
        String result = readAfipOutputData(2200, 2201);
        return result != null && !result.trim().isEmpty() ? result.trim() : " ";
    }

    private static void writePdfVatSummary(PrintWriter pw, ClientInvoice invoice) {
        if (isTypeCInvoice(resolveAfipTypeCode(invoice.getInvoiceType()))) {
            pw.print("4");
            pw.print("00003");
            pw.print(formatAmount(BigDecimal.ZERO, 12, 3));
            pw.print(formatAmount(BigDecimal.ZERO, 12, 3));
            pw.println(padRightWithSpaces("IVA 0%", 200));
            return;
        }

        BigDecimal iva105 = truncateAmount(Optional.ofNullable(invoice.getVat105()).orElse(BigDecimal.ZERO), 3);
        BigDecimal iva21 = truncateAmount(Optional.ofNullable(invoice.getVat21()).orElse(BigDecimal.ZERO), 3);

        if (iva105.compareTo(BigDecimal.ZERO) == 0 && iva21.compareTo(BigDecimal.ZERO) == 0) {
            pw.print("4");
            pw.print("00003");
            pw.print(formatAmount(BigDecimal.ZERO, 12, 3));
            pw.print(formatAmount(BigDecimal.ZERO, 12, 3));
            pw.println(padRightWithSpaces("IVA 0%", 200));
        } else {
            if (iva105.compareTo(BigDecimal.ZERO) > 0) {
                pw.print("4");
                pw.print("00004");
                pw.print(formatAmount(truncateAmount(calculateVat105Base(iva105), 3), 12, 3));
                pw.print(formatAmount(iva105, 12, 3));
                pw.println(padRightWithSpaces("IVA 10.5%", 200));
            }
            if (iva21.compareTo(BigDecimal.ZERO) > 0) {
                pw.print("4");
                pw.print("00005");
                pw.print(formatAmount(truncateAmount(calculateVat21Base(iva21), 3), 12, 3));
                pw.print(formatAmount(iva21, 12, 3));
                pw.println(padRightWithSpaces("IVA 21%", 200));
            }
        }
    }

    // ---------------------------------------------------------------------
    // Utility helpers reused across the application
    // ---------------------------------------------------------------------

    public static String extractPointOfSale(String num) {
        if (num == null || !num.contains("-")) {
            return "";
        }
        return num.substring(0, num.indexOf("-"));
    }

    public static String extractReceiptNumber(String num) {
        if (num == null || !num.contains("-")) {
            return "";
        }
        return num.substring(num.indexOf("-") + 1);
    }

    private static String padValue(String valor, int longitud, char relleno, boolean izquierda) {
        if (valor == null) {
            valor = "";
        }
        if (valor.length() > longitud) {
            return valor.substring(0, longitud);
        }
        return izquierda ? String.format("%" + longitud + "s", valor).replace(' ', relleno)
                : String.format("%-" + longitud + "s", valor).replace(' ', relleno);
    }

    public static String padLeftWithZeros(String valor, int longitud) {
        return padValue(valor, longitud, '0', true);
    }

    public static String padRightWithSpaces(String valor, int longitud) {
        return padValue(valor, longitud, ' ', false);
    }

    public static String formatIntegerPart(BigDecimal valor, int longitud) {
        BigDecimal safe = valor == null ? BigDecimal.ZERO : valor;
        String entera = safe.setScale(0, AMOUNT_ROUNDING_MODE).toPlainString();
        return padLeftWithZeros(entera, longitud);
    }

    public static String formatDecimalPart(BigDecimal valor, int decimales) {
        if (decimales <= 0) {
            return "";
        }
        BigDecimal safe = valor == null ? BigDecimal.ZERO : valor;
        BigDecimal truncated = safe.setScale(decimales, AMOUNT_ROUNDING_MODE);
        String[] partes = truncated.toPlainString().split("\\.");
        String dec = partes.length > 1 ? partes[1] : "";
        return padLeftWithZeros(dec, decimales);
    }

    public static String formatAmount(BigDecimal valor, int enteros, int decimales) {
        return formatIntegerPart(valor, enteros) + formatDecimalPart(valor, decimales);
    }

    private static BigDecimal truncateAmount(BigDecimal value, int scale) {
        BigDecimal safe = value == null ? BigDecimal.ZERO : value;
        return safe.setScale(scale, AMOUNT_ROUNDING_MODE);
    }

    public static BigDecimal calculateTaxableBase(BigDecimal iva, BigDecimal porcentaje) {
        if (iva == null || porcentaje == null || porcentaje.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return iva.divide(porcentaje, 3, AMOUNT_ROUNDING_MODE);
    }

    public static BigDecimal calculateVat21Base(BigDecimal iva21) {
        return calculateTaxableBase(iva21, VAT_RATE_21);
    }

    public static BigDecimal calculateVat105Base(BigDecimal iva105) {
        return calculateTaxableBase(iva105, VAT_RATE_105);
    }

    public static String formatDate(LocalDateTime fecha, int tipo) {
        if (fecha == null) {
            return "";
        }
        SimpleDateFormat formatter;
        if (tipo == 1) {
            formatter = new SimpleDateFormat("yyyyMMdd");
        } else if (tipo == 2) {
            formatter = new SimpleDateFormat("MMyy");
        } else {
            return "";
        }
        return formatter.format(Date.from(fecha.atZone(ZoneId.systemDefault()).toInstant()));
    }

    public static String mapToAfipReceiptCode(int tipo) {
        switch (tipo) {
            case 1:
                return "006";
            case 2:
                return "008";
            case 3:
                return "007";
            case 8:
                return "001";
            case 9:
                return "003";
            case 10:
                return "002";
            case 11:
                return "011";
            case 12:
                return "013";
            case 13:
                return "012";
            default:
                return "Invalid cod";
        }
    }

    public static String mapToPyAfipReceiptCode(int tipo) {
        switch (tipo) {
            case 1:
                return "06";
            case 2:
                return "08";
            case 3:
                return "07";
            case 8:
                return "01";
            case 9:
                return "03";
            case 10:
                return "02";
            case 11:
                return "11";
            case 12:
                return "13";
            case 13:
                return "12";
            default:
                return "Invalid cod";
        }
    }

    public static String describeTaxCondition(int tipo) {
        switch (tipo) {
            case TaxCondition.RESPONSABLE_INSCRIPTO:
                return "RESPONSABLE INSCRIPTO";
            case TaxCondition.EXENTO:
                return "EXENTO";
            case TaxCondition.CONSUMIDOR_FINAL:
                return "CONSUMIDOR FINAL";
            case TaxCondition.MONOTRIBUTO:
                return "MONOTRIBUTO";
            default:
                return "Invalid cod";
        }
    }

    public static boolean isTypeBInvoice(int tipoComprobante) {
        return tipoComprobante == 1 || tipoComprobante == 2 || tipoComprobante == 3;
    }

    public static boolean isTypeCInvoice(int tipoComprobante) {
        return tipoComprobante == 11 || tipoComprobante == 12 || tipoComprobante == 13;
    }

    public static String mapUnitToAfipCode(String medida) {
        if (medida == null) {
            return "07"; // por defecto: unidades
        }
        switch (medida.trim().toLowerCase()) {
            case "kilogramos":
                return "01";
            case "metros":
                return "02";
            case "metros cuadrados":
                return "03";
            case "metros cúbicos":
                return "04";
            case "litros":
                return "05";
            case "1000 kwh":
                return "06";
            case "unidades":
                return "07";
            case "pares":
                return "08";
            case "docenas":
                return "09";
            case "quilates":
                return "10";
            case "millares":
                return "11";
            case "gramos":
                return "14";
            case "milimetros":
                return "15";
            case "mm cúbicos":
                return "16";
            case "kilómetros":
                return "17";
            case "hectolitros":
                return "18";
            case "centímetros":
                return "20";
            case "jgo. pqt. mazo naipes":
                return "25";
            case "cm cúbicos":
                return "27";
            case "toneladas":
                return "29";
            case "dam cúbicos":
                return "30";
            case "hm cúbicos":
                return "31";
            case "km cúbicos":
                return "32";
            case "microgramos":
                return "33";
            case "nanogramos":
                return "34";
            case "picogramos":
                return "35";
            case "miligramos":
                return "41";
            case "mililitros":
                return "47";
            case "curie":
                return "48";
            case "milicurie":
                return "49";
            case "microcurie":
                return "50";
            case "uiacthor":
                return "51";
            case "muiacthor":
                return "52";
            case "kg base":
                return "53";
            case "gruesa":
                return "54";
            case "kg bruto":
                return "61";
            case "uiactant":
                return "62";
            case "muiactant":
                return "63";
            case "uiactig":
                return "64";
            case "muiactig":
                return "65";
            case "kg activo":
                return "66";
            case "gramo activo":
                return "67";
            case "gramo base":
                return "68";
            case "packs":
                return "96";
            case "seña/anticipo":
                return "97";
            case "otras unidades":
                return "98";
            case "bonificación":
                return "99";
            default:
                return "07"; // por defecto: unidades
        }
    }

    public static String mapVatConditionCode(int idCondicion) {
        switch (idCondicion) {
            case TaxCondition.RESPONSABLE_INSCRIPTO:
                return "0001"; // RESPONSABLE INSCRIPTO
            case TaxCondition.EXENTO:
                return "0004"; // EXENTO
            case TaxCondition.CONSUMIDOR_FINAL:
                return "0005"; // CONSUMIDOR FINAL
            case TaxCondition.MONOTRIBUTO:
                return "0006"; // MONOTRIBUTO
            default:
                return "0000"; // Desconocido o sin categorizar
        }
    }
}

