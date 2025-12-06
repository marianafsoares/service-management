package services;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import models.ClientInvoice;
import models.ProviderInvoice;
import repositories.VatBookRepository;
import services.reports.ReportPurchasesVatBook;
import services.reports.ReportSalesVatBook;
import utils.Constants;
import utils.DocumentValidator;
import utils.InvoiceTypeUtils;
import utils.pyAfip.AfipManagement;

/**
 * Service in charge of processing VAT books for sales and purchases.
 */
public class VatBookService {

    private final VatBookRepository vatBookRepository;
    private final ReportSalesVatBook reportSales;
    private final ReportPurchasesVatBook reportPurchases;
    private final CitiFileExporter citiExporter;

    private static final Set<String> VAT_DOCUMENT_TYPES = createVatDocumentTypes();

    public VatBookService(VatBookRepository vatBookRepository,
                          ReportSalesVatBook reportSales,
                          ReportPurchasesVatBook reportPurchases) {
        this(vatBookRepository, reportSales, reportPurchases, new CitiFileExporter());
    }

    public VatBookService(VatBookRepository vatBookRepository,
                          ReportSalesVatBook reportSales,
                          ReportPurchasesVatBook reportPurchases,
                          CitiFileExporter citiExporter) {
        this.vatBookRepository = vatBookRepository;
        this.reportSales = reportSales;
        this.reportPurchases = reportPurchases;
        this.citiExporter = citiExporter == null ? new CitiFileExporter() : citiExporter;
    }

    /**
     * Generates the sales VAT book for the given period.
     *
     * @param start start date
     * @param end   end date
     * @return an {@link Optional} with the export directory or empty when no invoices were found
     * @throws Exception when an unexpected error occurs while generating the files
     */
    public Optional<VatBookGenerationResult> processSales(Date start, Date end, String issuerCuit) throws Exception {
        String normalizedCuit = normalizeIssuerCuit(issuerCuit);
        List<ClientInvoice> invoices = vatBookRepository.findSalesBetween(start, end, normalizedCuit, new ArrayList<>(VAT_DOCUMENT_TYPES));
        if (invoices == null || invoices.isEmpty()) {
            return Optional.empty();
        }
        try {
            Path citiDirectory = citiExporter.exportSales(invoices, start, end, normalizedCuit);
            Path vatBookDirectory = resolveVatBookDirectory(start, end);
            reportSales.print(invoices, start, end, vatBookDirectory, normalizedCuit);
            return Optional.of(new VatBookGenerationResult(vatBookDirectory, citiDirectory));
        } catch (IOException ex) {
            throw new Exception("No se pudieron generar los archivos CITI de ventas.", ex);
        }
    }

    /**
     * Generates the purchases VAT book for the given period.
     *
     * @param start start date
     * @param end   end date
     * @return an {@link Optional} with the export directory or empty when no invoices were found
     * @throws Exception when an unexpected error occurs while generating the files
     */
    public Optional<VatBookGenerationResult> processPurchases(Date start, Date end, String issuerCuit) throws Exception {
        String normalizedCuit = normalizeIssuerCuit(issuerCuit);
        List<ProviderInvoice> invoices = vatBookRepository.findPurchasesBetween(start, end, normalizedCuit, new ArrayList<>(VAT_DOCUMENT_TYPES));
        if (invoices == null || invoices.isEmpty()) {
            return Optional.empty();
        }
        try {
            Path citiDirectory = citiExporter.exportPurchases(invoices, start, end, normalizedCuit);
            Path vatBookDirectory = resolveVatBookDirectory(start, end);
            reportPurchases.print(invoices, start, end, vatBookDirectory, normalizedCuit);
            return Optional.of(new VatBookGenerationResult(vatBookDirectory, citiDirectory));
        } catch (IOException ex) {
            throw new Exception("No se pudieron generar los archivos CITI de compras.", ex);
        }
    }

    private Path resolveVatBookDirectory(Date start, Date end) {
        LocalDate reference = toLocalDate(start);
        if (reference == null) {
            reference = toLocalDate(end);
        }
        if (reference == null) {
            reference = LocalDate.now();
        }

        String year = Integer.toString(reference.getYear());
        String month = formatSpanishMonth(reference);

        return Path.of(AfipManagement.EXPORT_BASE_PATH)
                .resolve("Libros de Iva")
                .resolve(year)
                .resolve(month);
    }

    private String formatSpanishMonth(LocalDate date) {
        String month = date.format(DateTimeFormatter.ofPattern("MMMM", new Locale("es", "ES")));
        return Character.toUpperCase(month.charAt(0)) + month.substring(1);
    }

    private LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static Set<String> createVatDocumentTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(InvoiceTypeUtils.toStorageValue(Constants.FACTURA_A));
        types.add(InvoiceTypeUtils.toStorageValue(Constants.FACTURA_B));
        types.add(InvoiceTypeUtils.toStorageValue(Constants.FACTURA_C));
        types.add(InvoiceTypeUtils.toStorageValue(Constants.NOTA_CREDITO_A));
        types.add(InvoiceTypeUtils.toStorageValue(Constants.NOTA_CREDITO_B));
        types.add(InvoiceTypeUtils.toStorageValue(Constants.NOTA_CREDITO_C));
        types.add(InvoiceTypeUtils.toStorageValue(Constants.NOTA_DEBITO_A));
        types.add(InvoiceTypeUtils.toStorageValue(Constants.NOTA_DEBITO_B));
        types.add(InvoiceTypeUtils.toStorageValue(Constants.NOTA_DEBITO_C));
        types.removeIf(String::isBlank);
        return types;
    }

    private String normalizeIssuerCuit(String issuerCuit) {
        String normalized = DocumentValidator.normalizeCuit(issuerCuit);
        if (normalized == null || normalized.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar un CUIT emisor válido.");
        }
        return normalized;
    }
}

