package services.afip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

import models.ClientInvoice;
import models.ClientInvoiceDetail;
import utils.pyAfip.AfipManagement;

/**
 * Coordinates the interaction with the PyAfipWs command line utilities to
 * authorize invoices before persisting them in the system.
 */
public class AfipAuthorizationService {

    private static final DateTimeFormatter CAE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final List<String> overrideCommand;
    private final AfipCommandResolver commandResolver;

    public AfipAuthorizationService() {
        this(null, new AfipCommandResolver());
    }

    public AfipAuthorizationService(List<String> command) {
        this(command, new AfipCommandResolver());
    }

    AfipAuthorizationService(List<String> command, AfipCommandResolver resolver) {
        this.overrideCommand = command == null || command.isEmpty()
                ? null
                : new ArrayList<>(command);
        this.commandResolver = resolver == null ? new AfipCommandResolver() : resolver;
    }

    public AfipAuthorizationResult authorize(ClientInvoice invoice,
                                             List<ClientInvoiceDetail> details,
                                             ClientInvoice associatedInvoice) {
        try {
            AfipManagement.generateElectronicVoucherEntry(invoice, details, associatedInvoice);
            List<String> commandToUse = resolveCommand(invoice != null ? invoice.getIssuerCuit() : null);
            runAuthorizationProcess(commandToUse);
            return parseAuthorizationResult();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AfipAuthorizationException("AFIP authorization process was interrupted", ex);
        } catch (IOException ex) {
            throw new AfipAuthorizationException("Failed to execute AFIP authorization command", ex);
        }
    }

    private List<String> resolveCommand(String issuerCuit) {
        if (overrideCommand != null) {
            return new ArrayList<>(overrideCommand);
        }
        List<String> resolved = commandResolver.resolveAuthorizationCommand(issuerCuit);
        if (resolved == null || resolved.isEmpty()) {
            throw new AfipAuthorizationException(
                    "AFIP authorization command is not configured. Set system property 'afip.ws.command'.");
        }
        return resolved;
    }

    private void runAuthorizationProcess(List<String> command) throws IOException, InterruptedException {
        if (command == null || command.isEmpty()) {
            throw new AfipAuthorizationException(
                    "AFIP authorization command is not configured. Set system property 'afip.ws.command'.");
        }

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(AfipManagement.getAfipWorkingDirectory());
        builder.redirectErrorStream(true);

        Process process = builder.start();
        List<String> output = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            StringBuilder message = new StringBuilder("AFIP authorization process failed with exit code ")
                    .append(exitCode);
            if (!output.isEmpty()) {
                message.append(". Output: ")
                        .append(String.join(" ", output));
            }
            throw new AfipAuthorizationException(message.toString());
        }
    }

    private AfipAuthorizationResult parseAuthorizationResult() {
        String status = clean(AfipManagement.readAfipOutputData(193, 194));
        boolean approved = "A".equalsIgnoreCase(status);

        String cae = clean(AfipManagement.readAfipOutputData(171, 185));
        LocalDateTime caeExpiration = parseCaeExpiration(clean(AfipManagement.readAfipOutputData(185, 193)));

        StringBuilder message = new StringBuilder();
        appendMessage(message, AfipManagement.readAfipOutputData(194, 1194));
        appendMessage(message, AfipManagement.readAfipOutputData(1194, 1200));
        appendMessage(message, AfipManagement.readAfipOutputData(1200, 2200));
        appendMessage(message, AfipManagement.readAfipOutputData(2200, 3200));

        return new AfipAuthorizationResult(approved, cae, caeExpiration, message.toString().trim());
    }

    private static void appendMessage(StringBuilder builder, String value) {
        String cleaned = clean(value);
        if (!cleaned.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(cleaned);
        }
    }

    private static String clean(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.replace('\r', ' ').replace('\n', ' ').trim();
        return trimmed.replaceAll("\\s+", " ").trim();
    }

    private static LocalDateTime parseCaeExpiration(String raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(raw, CAE_DATE_FORMAT);
            return date.atTime(LocalTime.MAX);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

}
