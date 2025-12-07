package services.afip;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import models.ClientInvoice;
import utils.DocumentValidator;
import utils.InvoiceTypeUtils;
import utils.pyAfip.AfipManagement;
import services.reports.ClientInvoiceManualPrintService;
import services.reports.ManualInvoicePrintException;

/**
 * Handles the generation of the factura.txt file consumed by PyAfipWs and the
 * execution of the PDF generation command line utility.
 */
public class AfipPdfService {

    private static final String INVOICE_ROOT_FOLDER = "facturas";
    private static final DateTimeFormatter INVOICE_PERIOD_FORMAT = DateTimeFormatter.ofPattern("yyyyMM");

    private final List<String> overrideCommand;
    private final AfipCommandResolver commandResolver;
    private final ClientInvoiceManualPrintService manualPrintService;

    public AfipPdfService() {
        this(null, new AfipCommandResolver());
    }

    public AfipPdfService(List<String> command) {
        this(command, new AfipCommandResolver());
    }

    AfipPdfService(List<String> command, AfipCommandResolver resolver) {
        this.overrideCommand = command == null || command.isEmpty()
                ? null
                : new ArrayList<>(command);
        this.commandResolver = resolver == null ? new AfipCommandResolver() : resolver;
        this.manualPrintService = new ClientInvoiceManualPrintService();
    }

    public void generateAndPrint(ClientInvoice invoice, ClientInvoice associatedInvoice) {
        generatePdfFile(invoice, associatedInvoice, true);
    }

    public File generatePdfFile(ClientInvoice invoice, ClientInvoice associatedInvoice, boolean openPdf) {
        Objects.requireNonNull(invoice, "invoice must not be null");
        try {
            if (!InvoiceTypeUtils.requiresAfipAuthorization(invoice.getInvoiceType())) {
                File exportedPdf = generateManualBudgetPdf(invoice);
                if (openPdf) {
                    openGeneratedPdf(exportedPdf);
                }
                return exportedPdf;
            }
            AfipManagement.generateElectronicVoucherPdf(invoice, associatedInvoice);
            File configFile = AfipManagement.preparePdfConfiguration(invoice);
            List<String> commandToUse = resolveCommand(invoice.getIssuerCuit());
            List<String> commandWithConfig = injectConfigFile(commandToUse, configFile);
            runPdfProcess(commandWithConfig);
            File exportedPdf = copyGeneratedPdf(invoice);
            if (openPdf) {
                openGeneratedPdf(exportedPdf);
            }
            return exportedPdf;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AfipPdfException("El proceso de generación de la factura de AFIP fue interrumpido", ex);
        } catch (IOException ex) {
            throw new AfipPdfException("No se pudo ejecutar el comando de generación de AFIP", ex);
        } catch (ManualInvoicePrintException ex) {
            throw new AfipPdfException("No se pudo generar el PDF del presupuesto", ex);
        }
    }

    private List<String> injectConfigFile(List<String> command, File configFile) {
        if (command == null) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>(command.size() + 1);
        if (command.isEmpty()) {
            return result;
        }

        result.add(command.get(0));
        int startIndex = 1;
        if (command.size() > 1 && isPythonInterpreter(command.get(0))) {
            result.add(command.get(1));
            startIndex = 2;
        }

        if (configFile != null) {
            result.add(configFile.getAbsolutePath());
        }

        for (int i = startIndex; i < command.size(); i++) {
            result.add(command.get(i));
        }

        return result;
    }

    private boolean isPythonInterpreter(String token) {
        if (token == null) {
            return false;
        }
        String normalized = token.trim().toLowerCase(Locale.ROOT).replace("\\", "/");
        return normalized.equals("python")
                || normalized.equals("python3")
                || normalized.equals("py")
                || normalized.endsWith("/python")
                || normalized.endsWith("/python3")
                || normalized.endsWith("/python.exe")
                || normalized.endsWith("/python3.exe")
                || normalized.endsWith("/py.exe");
    }

    private List<String> resolveCommand(String issuerCuit) {
        if (overrideCommand != null) {
            return new ArrayList<>(overrideCommand);
        }
        List<String> resolved = commandResolver.resolvePdfCommand(issuerCuit);
        if (resolved == null || resolved.isEmpty()) {
            throw new AfipPdfException(
                    "No se configuró el comando de generación de AFIP. Defina la propiedad 'afip.pdf.command'.");
        }
        return resolved;
    }

    private void runPdfProcess(List<String> command) throws IOException, InterruptedException {
        if (command == null || command.isEmpty()) {
            throw new AfipPdfException(
                    "No se configuró el comando de generación de AFIP. Defina la propiedad 'afip.pdf.command'.");
        }

        File workingDirectory = AfipManagement.getAfipWorkingDirectory();
        ensureExecutableExists(workingDirectory, command);

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(workingDirectory);
        builder.redirectErrorStream(true);

        Process process = builder.start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (output.length() > 0) {
                    output.append(System.lineSeparator());
                }
                output.append(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            StringBuilder message = new StringBuilder(
                    "El proceso de generación de AFIP finalizó con código " + exitCode);
            message.append(". Comando ejecutado: ").append(String.join(" ", command));

            if (output.length() > 0) {
                message.append(". Salida del proceso: ").append(output);
            }

            throw new AfipPdfException(message.toString());
        }
    }

    private void ensureExecutableExists(File workingDirectory, List<String> command) {
        if (!workingDirectory.exists() || !workingDirectory.isDirectory()) {
            throw new AfipPdfException(String.format(
                    "El directorio configurado para PyAfipWs '%s' no existe o no es un directorio válido."
                            + " Ajuste la propiedad 'afip.pdf.workingDir'.",
                    workingDirectory.getAbsolutePath()));
        }

        String executable = findExecutableCandidate(command);
        if (executable == null) {
            return;
        }

        File executableFile = new File(executable);
        if (!executableFile.isAbsolute()) {
            executableFile = new File(workingDirectory, executable);
        }

        if (!executableFile.exists()) {
            throw new AfipPdfException(String.format(
                    "El ejecutable de PyAfipWs '%s' no existe. "
                    + "Verifique la instalación o configure la propiedad 'afip.pdf.command'.",
                    executableFile.getAbsolutePath()));
        }
    }

    private String findExecutableCandidate(List<String> command) {
        for (String token : command) {
            if (token == null || token.trim().isEmpty()) {
                continue;
            }
            String normalized = token.trim();
            String lower = normalized.toLowerCase();
            if (lower.equals("python") || lower.equals("python3") || lower.equals("py")) {
                continue;
            }
            if (normalized.startsWith("-")) {
                continue;
            }
            if (normalized.contains(".") || normalized.contains(File.separator)) {
                return normalized;
            }
        }
        return command.isEmpty() ? null : command.get(0);
    }

    private File copyGeneratedPdf(ClientInvoice invoice) {
        String fileName = buildPdfFileName(invoice);

        File workingDirectory = AfipManagement.getAfipWorkingDirectory();
        File generatedPdf = new File(workingDirectory, "factura.pdf");
        if (!generatedPdf.exists()) {
            throw new AfipPdfException(String.format("No se encontró el archivo PDF generado en '%s'.",
                    generatedPdf.getAbsolutePath()));
        }

        Path exportDirectory = resolveExportDirectory(invoice);

        Path source = generatedPdf.toPath();
        Path target = exportDirectory.resolve(fileName);
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new AfipPdfException("No se pudo copiar la factura generada a la carpeta de exportación.", ex);
        }

        return target.toFile();
    }

    public File findExistingPdf(ClientInvoice invoice) {
        if (invoice == null) {
            return null;
        }
        Path exportDirectory = resolveExportDirectory(invoice);
        Path pdfPath = exportDirectory.resolve(buildPdfFileName(invoice));
        if (Files.exists(pdfPath)) {
            return pdfPath.toFile();
        }
        return null;
    }

    private void openGeneratedPdf(File pdfFile) {
        if (!Desktop.isDesktopSupported()) {
            throw new AfipPdfException("La visualización de facturas no está soportada en el entorno actual.");
        }

        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            throw new AfipPdfException("La acción de abrir archivos PDF no está soportada en este equipo.");
        }

        if (!pdfFile.exists()) {
            throw new AfipPdfException(String.format("No se encontró el archivo PDF generado en '%s'.",
                    pdfFile.getAbsolutePath()));
        }

        try {
            desktop.open(pdfFile);
        } catch (IOException ex) {
            throw new AfipPdfException("No se pudo abrir la factura generada para su visualización.", ex);
        }
    }

    private String buildPdfFileName(ClientInvoice invoice) {
        return "factura" + Optional.ofNullable(invoice.getPointOfSale()).orElse("")
                + Optional.ofNullable(invoice.getInvoiceNumber()).orElse("") + ".pdf";
    }

    private String resolveInvoicePeriod(LocalDateTime invoiceDate) {
        LocalDateTime dateTime = invoiceDate != null ? invoiceDate : LocalDateTime.now();
        return dateTime.format(INVOICE_PERIOD_FORMAT);
    }

    private Path resolveExportDirectory(ClientInvoice invoice) {
        String normalizedCuit = DocumentValidator.normalizeCuit(invoice.getIssuerCuit());
        if (normalizedCuit == null || normalizedCuit.isBlank()) {
            normalizedCuit = "sin-cuit";
        }

        String period = resolveInvoicePeriod(invoice.getInvoiceDate());

        Path exportDirectory = Paths.get(AfipManagement.EXPORT_BASE_PATH)
                .resolve(INVOICE_ROOT_FOLDER)
                .resolve(normalizedCuit);
        if (!period.isBlank()) {
            exportDirectory = exportDirectory.resolve(period);
        }

        try {
            Files.createDirectories(exportDirectory);
        } catch (IOException ex) {
            throw new AfipPdfException(String.format(
                    "No se pudo crear la carpeta de exportación de facturas '%s'.",
                    exportDirectory.toAbsolutePath()), ex);
        }

        return exportDirectory;
    }

    private File generateManualBudgetPdf(ClientInvoice invoice) throws ManualInvoicePrintException {
        Path exportDirectory = resolveExportDirectory(invoice);
        Path target = exportDirectory.resolve(buildPdfFileName(invoice));
        manualPrintService.exportBudgetPdf(invoice, target.toString());
        return target.toFile();
    }
}
