package configs;

import org.apache.commons.dbcp2.BasicDataSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Diagnostics {
    private static final Logger LOGGER = Logger.getLogger(Diagnostics.class.getName());

    private Diagnostics() {}

    public static void logEnvironment(Logger target) {
        Logger logger = target != null ? target : LOGGER;
        logger.info(() -> "Java version: " + System.getProperty("java.version") +
                " (" + System.getProperty("java.vendor") + ")");
        logger.info(() -> "Java home: " + System.getProperty("java.home"));
        logger.info(() -> "Sistema operativo: " + System.getProperty("os.name") +
                " " + System.getProperty("os.version") +
                " (" + System.getProperty("os.arch") + ")");

        Path configFile = Path.of("config", "app.properties").toAbsolutePath().normalize();
        if (Files.isRegularFile(configFile)) {
            logger.info(() -> "Configuración externa detectada en " + configFile);
        } else {
            logger.warning(() -> "No se encontró config/app.properties en " + configFile);
        }

        Path logsDir = Path.of("logs").toAbsolutePath().normalize();
        if (Files.isDirectory(logsDir)) {
            logger.info(() -> "Los logs se guardan en " + logsDir);
        } else {
            logger.warning(() -> "La carpeta de logs aún no existe en " + logsDir);
        }
    }

    public static int runInteractive() {
        LOGGER.info("Iniciando diagnóstico interactivo de Bits&Bytes");
        logEnvironment(LOGGER);

        System.out.println();
        System.out.println("=== Diagnóstico de Bits&Bytes ===");
        System.out.println("Fecha y hora: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Path configFile = Path.of("config", "app.properties").toAbsolutePath().normalize();
        if (Files.isRegularFile(configFile)) {
            System.out.println("Configuración externa: " + configFile);
        } else {
            System.out.println("Configuración externa: NO ENCONTRADA en " + configFile);
        }

        String url = AppConfig.get("db.url", "");
        String user = AppConfig.get("db.user", "");
        String pass = AppConfig.get("db.pass", "");

        System.out.println("URL configurada: " + DatabaseMigration.maskJdbcUrl(url));
        System.out.println("Usuario configurado: " + (user == null || user.isBlank() ? "(sin usuario)" : user));

        if (pass == null || pass.isBlank()) {
            System.out.println("Contraseña configurada: (vacía)");
        } else {
            System.out.println("Contraseña configurada: **** (" + pass.length() + " caracteres)");
        }

        System.out.println();
        System.out.println("Intentando abrir conexión a la base de datos...");

        try (BasicDataSource ds = DatabaseMigration.newDataSource(url, user, pass);
             Connection connection = ds.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            System.out.println("Conexión exitosa.");
            System.out.println("Servidor: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            System.out.println("URL reportada por el driver: " + DatabaseMigration.maskJdbcUrl(meta.getURL()));
            LOGGER.info("Diagnóstico: conexión a la base realizada correctamente.");
            return 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Diagnóstico: error al conectar con la base", e);
            System.err.println();
            System.err.println("No se pudo conectar con la base de datos.");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState() + " | Código: " + e.getErrorCode());
            System.err.println();
            System.err.println("Revisá los permisos del usuario, la red y la configuración en config/app.properties.");
            return 2;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Diagnóstico: error inesperado", e);
            System.err.println();
            System.err.println("Ocurrió un error inesperado durante el diagnóstico: " + e.getMessage());
            return 3;
        }
    }
}
