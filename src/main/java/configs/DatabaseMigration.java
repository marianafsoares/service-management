package configs;

import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executes database migrations.
 */
public final class DatabaseMigration {
    private static final Logger LOGGER = Logger.getLogger(DatabaseMigration.class.getName());

    private DatabaseMigration() {}

    private static String extractDbName(String url) {
        int scheme = url.indexOf("jdbc:mysql://");
        if (scheme != 0) {
            int lastSlash = url.lastIndexOf('/');
            if (lastSlash < 0) return null;
            int q = url.indexOf('?', lastSlash);
            return q < 0 ? url.substring(lastSlash + 1) : url.substring(lastSlash + 1, q);
        }

        int start = url.indexOf('/', "jdbc:mysql://".length());
        if (start < 0) return null;
        int q = url.indexOf('?', start);
        int end = q < 0 ? url.length() : q;
        return url.substring(start + 1, end);
    }

    private static String serverUrlFrom(String url) {
        int scheme = url.indexOf("jdbc:mysql://");
        if (scheme != 0) return url;
        int firstSlashAfterHost = url.indexOf('/', "jdbc:mysql://".length());
        if (firstSlashAfterHost < 0) return url + "/";
        int q = url.indexOf('?', firstSlashAfterHost);
        String params = q < 0 ? "" : url.substring(q);
        return url.substring(0, firstSlashAfterHost + 1) + params;
    }

    static String ensureAllowPublicKeyRetrieval(String url) {
        if (url == null || url.isBlank()) return url;
        if (!url.startsWith("jdbc:mysql:")) return url;

        String parameter = "allowPublicKeyRetrieval=";
        int queryStart = url.indexOf('?');
        if (queryStart >= 0) {
            String query = url.substring(queryStart + 1);
            if (query.contains(parameter)) return url;
            return url + "&allowPublicKeyRetrieval=true";
        }

        return url + "?allowPublicKeyRetrieval=true";
    }

    static BasicDataSource newDataSource(String url, String user, String pass) {
        url = ensureAllowPublicKeyRetrieval(url);
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.addConnectionProperty("allowPublicKeyRetrieval", "true");
        return ds;
    }

    static String maskJdbcUrl(String url) {
        if (url == null || url.isBlank()) {
            return "(no configurada)";
        }

        return url.replaceAll("(?i)(password=)[^&;]+", "$1****");
    }

    private static String describeUser(String user) {
        if (user == null || user.isBlank()) {
            return "(sin usuario)";
        }
        return user;
    }

    private static boolean databaseExists(String url, String user, String pass) throws Exception {
        BasicDataSource ds = newDataSource(url, user, pass);

        try (Connection ignored = ds.getConnection()) {
            return true;
        } catch (SQLException e) {
            String state = e.getSQLState();
            int code = e.getErrorCode();
            String message = e.getMessage();

            boolean unknownDatabase =
                    code == 1049 /* ER_BAD_DB_ERROR */ ||
                    "3D000".equals(state) ||
                    (message != null && message.toLowerCase().contains("unknown database"));

            if (unknownDatabase) {
                LOGGER.fine(() -> "La base configurada no existe aún en " + maskJdbcUrl(url));
                return false;
            }

            LOGGER.log(Level.SEVERE, "Falló la verificación de la base en " + maskJdbcUrl(url), e);
            throw e;
        } finally {
            ds.close();
        }
    }

    private static void ensureDatabaseExists(String url, String user, String pass) throws Exception {
        String db = extractDbName(url);
        if (db == null || db.isBlank()) return;
        LOGGER.info(() -> "Verificando la existencia de la base '" + db + "' para " + maskJdbcUrl(url));
        if (databaseExists(url, user, pass)) return;

        String serverUrl = serverUrlFrom(url);

        LOGGER.info(() -> "No se encontró la base '" + db + "'. Intentando crearla en " + maskJdbcUrl(serverUrl));

        BasicDataSource ds = newDataSource(serverUrl, user, pass);

        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.execute("CREATE DATABASE IF NOT EXISTS `" + db + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci");
            LOGGER.info(() -> "Base '" + db + "' creada o verificada correctamente.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "No se pudo crear la base '" + db + "' usando " + maskJdbcUrl(serverUrl), e);
            throw new SQLException("No se pudo crear la base de datos '" + db + "'. Verificá que el usuario tenga permisos para crear bases de datos.", e);
        } finally {
            ds.close();
        }
    }

    private static String toFilesystemLocation(Path path) {
        Path absolute = path.toAbsolutePath().normalize();
        String location = absolute.toString();
        if (location.indexOf('\\') >= 0) {
            location = location.replace('\\', '/');
        }
        return "filesystem:" + location;
    }

    private static List<String> flywayLocations() {
        Set<String> locations = new LinkedHashSet<>();

        Path externalMigrations = Path.of("config", "db", "migration");
        if (Files.isDirectory(externalMigrations)) {
            locations.add(toFilesystemLocation(externalMigrations));
        }

        String configured = AppConfig.get("flyway.locations", "");
        if (configured != null && !configured.isBlank()) {
            for (String rawLocation : configured.split(",")) {
                String location = rawLocation.trim();
                if (location.isEmpty()) {
                    continue;
                }

                if (!location.contains(":") && Files.isDirectory(Path.of(location))) {
                    locations.add(toFilesystemLocation(Path.of(location)));
                } else {
                    locations.add(location);
                }
            }
        }

        locations.add("filesystem:src/main/resources/db/migration");
        locations.add("classpath:db/migration");

        return new ArrayList<>(locations);
    }

    public static void migrate() {
        String url = ensureAllowPublicKeyRetrieval(AppConfig.get("db.url", ""));
        String user = AppConfig.get("db.user", "");
        String pass = AppConfig.get("db.pass", "");

        try {
            ensureDatabaseExists(url, user, pass);
            String[] locations = flywayLocations().toArray(new String[0]);
            LOGGER.info(() -> "Ejecutando migraciones en " + maskJdbcUrl(url) + " con el usuario '" + describeUser(user) + "'.");
            LOGGER.info(() -> "Ubicaciones de migraciones: " + String.join(", ", locations));
            try (BasicDataSource flywayDs = newDataSource(url, user, pass)) {
                Flyway.configure()
                        .dataSource(flywayDs)
                        .locations(locations)
                        .load()
                        .migrate();
            }
            LOGGER.info("Migraciones ejecutadas correctamente.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al ejecutar migraciones en " + maskJdbcUrl(url), e);
            throw new RuntimeException("Error running database migrations", e);
        }
    }

    public static void main(String[] args) {
        migrate();
    }
}
