package services.afip;

import configs.AppConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import utils.DocumentValidator;
import utils.pyAfip.AfipManagement;

/**
 * Resolves the command line invocations used to interact with PyAfipWs. The
 * resolver supports configuring specific commands per CUIT and falls back to
 * the defaults detected in the working directory when no customization is
 * provided.
 */
public class AfipCommandResolver {

    private static final String AUTHORIZATION_COMMAND_PROPERTY = "afip.ws.command";
    private static final String PDF_COMMAND_PROPERTY = "afip.pdf.command";
    private static final String PYTHON_COMMAND_PROPERTY = "afip.pdf.pythonCommand";

    private static final List<String> DEFAULT_AUTHORIZATION_COMMAND = Arrays.asList(
            "pyafipws.exe",
            "wsfev1",
            "--archivo",
            "entrada.txt"
    );

    private static final String RECE_EXECUTABLE = "rece1.exe";
    private static final String PDF_EXECUTABLE = "pyfepdf_cli.exe";
    private static final String PDF_SCRIPT = "pyfepdf_cli.py";

    public List<String> resolveAuthorizationCommand(String issuerCuit) {
        List<String> configured = findConfiguredCommand(issuerCuit, AUTHORIZATION_COMMAND_PROPERTY);
        if (configured != null) {
            return configured;
        }
        File workingDirectory = AfipManagement.getAfipWorkingDirectory();
        File rece1 = new File(workingDirectory, RECE_EXECUTABLE);
        if (rece1.exists()) {
            List<String> command = new ArrayList<>();
            command.add(rece1.getAbsolutePath());
            command.addAll(resolveReceIniArguments(issuerCuit, workingDirectory));
            return command;
        }
        return new ArrayList<>(DEFAULT_AUTHORIZATION_COMMAND);
    }

    public List<String> resolvePdfCommand(String issuerCuit) {
        List<String> configured = findConfiguredCommand(issuerCuit, PDF_COMMAND_PROPERTY);
        if (configured != null) {
            return configured;
        }
        List<String> discovered = discoverPdfCommand();
        if (!discovered.isEmpty()) {
            return discovered;
        }
        return new ArrayList<>(Arrays.asList(PDF_EXECUTABLE, "--cargar"));
    }

    private List<String> discoverPdfCommand() {
        File workingDirectory = AfipManagement.getAfipWorkingDirectory();

        File executable = new File(workingDirectory, PDF_EXECUTABLE);
        if (executable.exists()) {
            return new ArrayList<>(Arrays.asList(executable.getAbsolutePath(), "--cargar"));
        }

        File script = new File(workingDirectory, PDF_SCRIPT);
        if (script.exists()) {
            String pythonCommand = resolvePythonCommand();
            return new ArrayList<>(Arrays.asList(pythonCommand, script.getAbsolutePath(), "--cargar"));
        }

        return new ArrayList<>();
    }

    private String resolvePythonCommand() {
        String configured = getProperty(PYTHON_COMMAND_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            return configured.trim();
        }
        return "python";
    }

    private List<String> findConfiguredCommand(String issuerCuit, String baseProperty) {
        String normalized = DocumentValidator.normalizeCuit(issuerCuit);
        if (normalized != null && !normalized.isBlank()) {
            List<String> perCuit = parseCommandProperty(baseProperty + "." + normalized);
            if (perCuit != null) {
                return perCuit;
            }
        }
        return parseCommandProperty(baseProperty);
    }

    private List<String> parseCommandProperty(String property) {
        if (property == null || property.isBlank()) {
            return null;
        }
        String value = getProperty(property);
        if (value != null && !value.isBlank()) {
            return CommandLineTokenizer.tokenize(value.trim());
        }
        return null;
    }

    private String getProperty(String key) {
        String value = System.getProperty(key);
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        return AppConfig.get(key, null);
    }

    private List<String> resolveReceIniArguments(String issuerCuit, File workingDirectory) {
        String normalized = DocumentValidator.normalizeCuit(issuerCuit);
        if (normalized == null || normalized.isBlank()) {
            return List.of();
        }
        String iniName = "rece" + normalized + ".ini";
        File iniFile = new File(workingDirectory, iniName);
        if (!iniFile.isFile()) {
            throw new AfipAuthorizationException(String.format(
                    "No se encontró el archivo de configuración %s en %s", iniName,
                    workingDirectory.getAbsolutePath()));
        }
        return List.of(iniFile.getAbsolutePath());
    }
}
