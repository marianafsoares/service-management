import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JOptionPane;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final Path LOGS_DIR = resolveLogsDir();

    public static void main(String[] args) {
        setupLogging();
        configs.Diagnostics.logEnvironment(LOGGER);

        boolean diagnosticMode = false;
        if (args != null) {
            for (String arg : args) {
                if ("--diagnostico".equalsIgnoreCase(arg) || "-d".equalsIgnoreCase(arg)) {
                    diagnosticMode = true;
                    break;
                }
            }
        }

        try {
            configs.ConfigBootstrap.ensureExternalConfig();

            if (diagnosticMode) {
                int exitCode = configs.Diagnostics.runInteractive();
                System.exit(exitCode);
                return;
            }

            configs.DatabaseMigration.migrate();
            configs.MyBatisConfig.getSqlSessionFactory();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error inicializando la aplicación", ex);

            StringBuilder message = new StringBuilder();
            message.append("No se pudo iniciar Bits&Bytes. Revisá la configuración y los permisos de la base de datos.");

            Throwable root = ex;
            while (root.getCause() != null) {
                root = root.getCause();
            }

            if (root.getMessage() != null && !root.getMessage().isBlank()) {
                message.append("\n\nDetalle: ").append(root.getMessage());
            }

            message.append("\n\nConsultá el archivo ")
                    .append(LOGS_DIR.resolve("errores.log").toAbsolutePath())
                    .append(" y la guía docs/troubleshooting.md para más opciones de diagnóstico.");

            JOptionPane.showMessageDialog(
                    null,
                    message.toString(),
                    "Bits&Bytes",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                new views.MainView().setVisible(true);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        });
    }

    private static void setupLogging() {
        System.setProperty("bitsandbytes.logs.dir", LOGS_DIR.toAbsolutePath().toString());
        try {
            Files.createDirectories(LOGS_DIR);
            Path logFile = LOGS_DIR.resolve("errores.log");
            if (Files.notExists(logFile)) {
                try {
                    Files.createFile(logFile);
                } catch (IOException ignored) {
                    // If two threads try to create the file at the same time we can safely ignore the failure
                }
            }

            Logger rootLogger = Logger.getLogger("");
            boolean hasFileHandler = false;
            for (Handler handler : rootLogger.getHandlers()) {
                if (handler instanceof FileHandler) {
                    hasFileHandler = true;
                    break;
                }
            }

            if (!hasFileHandler) {
                FileHandler handler = new FileHandler(LOGS_DIR.resolve("errores.log").toString(), 5 * 1024 * 1024, 5, true);
                handler.setFormatter(new SimpleFormatter());
                handler.setLevel(Level.ALL);
                rootLogger.addHandler(handler);
            }
        } catch (IOException e) {
            System.err.println("[ADVERTENCIA] No se pudo inicializar el archivo de logs en "
                    + LOGS_DIR.toAbsolutePath() + ": " + e.getMessage());
        }
    }

    private static Path resolveLogsDir() {
        return Optional.ofNullable(System.getenv("LOCALAPPDATA"))
                .map(Paths::get)
                .or(() -> Optional.ofNullable(System.getenv("APPDATA")).map(Paths::get))
                .or(() -> Optional.ofNullable(System.getProperty("user.home")).map(Paths::get))
                .orElse(Paths.get("."))
                .resolve("BitsAndBytes")
                .resolve("logs");
    }
}
