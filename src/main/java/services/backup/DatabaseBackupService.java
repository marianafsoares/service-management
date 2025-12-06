package services.backup;

import configs.AppConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import java.util.stream.Collectors;

public class DatabaseBackupService {
    private static final Logger LOGGER = Logger.getLogger(DatabaseBackupService.class.getName());
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
    private static final AtomicReference<Path> MYSQLDUMP_CACHE = new AtomicReference<>();

    private enum Strategy {
        FULL,
        INCREMENTAL;

        static Strategy fromConfig(String value) {
            if (value == null || value.isBlank()) {
                return FULL;
            }
            try {
                return Strategy.valueOf(value.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.WARNING, "Unknown backup strategy ''{0}'', defaulting to FULL", value);
                return FULL;
            }
        }
    }

    public Path backupNow() throws BackupException {
        Path finalFile = null;
        Path tempFile = null;
        try {
            if (!isEnabled()) {
                LOGGER.fine("Automatic backup is disabled by configuration.");
                return null;
            }

            MysqlConnectionInfo info = MysqlConnectionInfo.from(AppConfig.get("db.url", ""));
            if (info.database().isBlank()) {
                throw new BackupException("No se pudo determinar el nombre de la base de datos desde la URL de conexión");
            }

            Strategy strategy = Strategy.fromConfig(AppConfig.get("backup.strategy", "full"));
            boolean compress = Boolean.parseBoolean(AppConfig.get("backup.compress", "true"));
            String clientName = resolveClientName();
            String clientSlug = slugifyClientName(clientName);
            Path outputDir = ensureOutputDirectory(clientSlug);
            Path absoluteOutputDir = outputDir.toAbsolutePath().normalize();
            LOGGER.log(Level.INFO, "Los respaldos locales se guardan en {0}", absoluteOutputDir);
            String clientRemoteSuffix = clientSlug.isBlank() ? "" : "/" + clientSlug;

            String timestampPattern = AppConfig.get("backup.timestamp.pattern", "yyyyMMdd-HHmmss");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampPattern);
            String timestamp = formatter.format(LocalDateTime.now());

            String filePrefix = AppConfig.get("backup.filename.prefix", "backup");
            String extension = compress ? ".sql.gz" : ".sql";
            String strategySuffix = strategy == Strategy.FULL ? "full" : "incremental";
            StringJoiner joiner = new StringJoiner("-");
            joiner.add(filePrefix);
            if (!clientSlug.isBlank()) {
                joiner.add(clientSlug);
            }
            joiner.add(strategySuffix);
            joiner.add(timestamp);
            String fileName = joiner.toString() + extension;
            finalFile = outputDir.resolve(fileName);
            tempFile = outputDir.resolve(fileName + ".part");

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("host", info.host());
            placeholders.put("port", Integer.toString(info.port()));
            placeholders.put("database", info.database());
            String user = AppConfig.get("db.user", "");
            String password = AppConfig.get("db.pass", "");
            placeholders.put("user", user);
            placeholders.put("password", password);
            placeholders.put("strategy", strategySuffix);
            placeholders.put("timestamp", timestamp);
            placeholders.put("file", finalFile.toAbsolutePath().toString());
            placeholders.put("filename", finalFile.getFileName().toString());
            placeholders.put("directory", outputDir.toAbsolutePath().toString());
            placeholders.put("clientName", clientName);
            placeholders.put("clientSlug", clientSlug);
            placeholders.put("clientDirectory", clientSlug);
            placeholders.put("clientRemoteSuffix", clientRemoteSuffix);

            List<String> command = buildCommand(strategy, placeholders);
            if (command.isEmpty()) {
                throw new BackupException("El comando de backup está vacío. Revise la configuración 'backup.command." + strategySuffix + "'");
            }

            ensurePasswordArgument(command, password);
            ensureExecutable(command);

            String commandForLog = command.stream()
                    .map(arg -> maskPassword(arg, password))
                    .collect(Collectors.joining(" "));
            LOGGER.log(Level.INFO, "Ejecutando backup {0}: {1}", new Object[]{strategySuffix, commandForLog});

            ProcessBuilder builder = new ProcessBuilder(command);
            if (password != null && !password.isBlank()) {
                builder.environment().put("MYSQL_PWD", password);
            }
            builder.redirectErrorStream(false);

            Process process;
            try {
                process = builder.start();
            } catch (IOException ex) {
                String message = ex.getMessage();
                if (message != null && message.contains("Cannot run program")) {
                    String executable = command.isEmpty() ? "" : command.get(0);
                    if (executable != null && !executable.isBlank()) {
                        throw new BackupException(
                                "No se pudo ejecutar '" + executable + "'. Verifique que esté instalado y disponible en el PATH "
                                        + "o configure 'backup.command." + (strategy == Strategy.FULL ? "full" : "incremental")
                                        + "' con la ruta completa al ejecutable",
                                ex);
                    }
                }
                throw ex;
            }

            AtomicReference<IOException> streamException = new AtomicReference<>();
            StringBuilder errorBuilder = new StringBuilder();
            Thread errorReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorBuilder.append(line).append(System.lineSeparator());
                    }
                } catch (IOException ex) {
                    streamException.set(ex);
                }
            }, "backup-stderr");
            errorReader.setDaemon(true);
            errorReader.start();

            try (InputStream stdout = new BufferedInputStream(process.getInputStream());
                 OutputStream fileOut = new BufferedOutputStream(Files.newOutputStream(tempFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
                 OutputStream targetOut = compress ? new GZIPOutputStream(fileOut) : fileOut) {
                stdout.transferTo(targetOut);
                if (targetOut instanceof GZIPOutputStream gz) {
                    gz.finish();
                }
            }

            int exitCode = process.waitFor();
            errorReader.join();
            IOException streamError = streamException.get();
            if (streamError != null) {
                throw streamError;
            }

            if (exitCode != 0) {
                Files.deleteIfExists(tempFile);
                String message = errorBuilder.length() > 0 ? errorBuilder.toString() : ("El comando de backup finalizó con código " + exitCode);
                throw new BackupException(message);
            }

            try {
                Files.move(tempFile, finalFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(tempFile, finalFile, StandardCopyOption.REPLACE_EXISTING);
            }
            enforceRetention(outputDir, filePrefix, extension);

            runPostCommand(placeholders, strategySuffix, finalFile);

            LOGGER.log(Level.INFO, "Backup generado en {0}", finalFile.toAbsolutePath());
            return finalFile;
        } catch (BackupException ex) {
            deleteQuietly(tempFile);
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            deleteQuietly(tempFile);
            throw new BackupException("La operación de backup fue interrumpida", ex);
        } catch (IOException ex) {
            deleteQuietly(tempFile);
            throw new BackupException("Error generando el backup: " + ex.getMessage(), ex);
        }
    }

    private boolean isEnabled() {
        return Boolean.parseBoolean(AppConfig.get("backup.enabled", "true"));
    }

    private Path ensureOutputDirectory(String clientSlug) throws IOException {
        String dir = AppConfig.get("backup.output.dir", "backups");
        Path configured;
        try {
            configured = Path.of(dir);
        } catch (InvalidPathException ex) {
            LOGGER.log(Level.WARNING,
                    "Ruta inválida para 'backup.output.dir': " + dir + ". Se usará la ubicación predeterminada.",
                    ex);
            Path fallback = appendClientSubdirectory(defaultBackupDirectory(), clientSlug);
            createDirectoriesIfNeeded(fallback);
            return fallback;
        }

        Path configuredWithClient = appendClientSubdirectory(configured, clientSlug);
        try {
            createDirectoriesIfNeeded(configuredWithClient);
            return configuredWithClient;
        } catch (AccessDeniedException | SecurityException ex) {
            Path fallback = appendClientSubdirectory(defaultBackupDirectory(), clientSlug);
            Path normalizedConfigured = normalize(configuredWithClient);
            Path normalizedFallback = normalize(fallback);
            if (normalizedConfigured.equals(normalizedFallback)) {
                if (ex instanceof AccessDeniedException accessDeniedException) {
                    throw accessDeniedException;
                }
                throw new IOException("Permisos insuficientes para crear el directorio de backups", ex);
            }
            LOGGER.log(Level.WARNING,
                    "No se pudo crear el directorio de backups " + normalizedConfigured
                            + ". Se utilizará " + normalizedFallback + " en su lugar.",
                    ex);
            createDirectoriesIfNeeded(fallback);
            return fallback;
        }
    }

    private Path appendClientSubdirectory(Path base, String clientSlug) {
        if (base == null || clientSlug == null || clientSlug.isBlank()) {
            return base;
        }
        return base.resolve(clientSlug);
    }

    public Path previewOutputDirectory() throws BackupException {
        String clientName = resolveClientName();
        String clientSlug = slugifyClientName(clientName);
        try {
            Path directory = ensureOutputDirectory(clientSlug);
            return directory.toAbsolutePath().normalize();
        } catch (IOException ex) {
            throw new BackupException("No se pudo preparar el directorio de backups", ex);
        }
    }

    private String resolveClientName() {
        String configured = AppConfig.get("backup.client.name", "");
        if (configured != null) {
            configured = configured.trim();
        }
        if (configured != null && !configured.isEmpty()) {
            return configured;
        }
        String fallback = AppConfig.get("company.name", "");
        if (fallback != null) {
            fallback = fallback.trim();
        }
        return fallback != null ? fallback : "";
    }

    private String slugifyClientName(String name) {
        if (name == null) {
            return "";
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        String normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFD);
        String withoutMarks = normalized.replaceAll("\\p{M}+", "");
        String alphanumeric = withoutMarks.replaceAll("[^A-Za-z0-9]", "");
        return alphanumeric;
    }

    private void createDirectoriesIfNeeded(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    private Path defaultBackupDirectory() {
        Path base = envPath("LOCALAPPDATA");
        if (base == null) {
            base = envPath("APPDATA");
        }
        if (base == null) {
            String userHome = System.getProperty("user.home", "");
            if (userHome != null && !userHome.isBlank()) {
                try {
                    base = Path.of(userHome);
                } catch (InvalidPathException ex) {
                    base = Path.of(".");
                }
            } else {
                base = Path.of(".");
            }
        }
        return base.resolve("BitsAndBytes").resolve("backups");
    }

    private Path normalize(Path path) {
        return path.toAbsolutePath().normalize();
    }

    private List<String> buildCommand(Strategy strategy, Map<String, String> placeholders) {
        String key = "backup.command." + (strategy == Strategy.FULL ? "full" : "incremental");
        String template = AppConfig.get(key, defaultCommand(strategy));
        String resolved = substitutePlaceholders(template, placeholders);
        return parseArguments(resolved);
    }

    private void ensurePasswordArgument(List<String> command, String password) {
        if (command == null || command.isEmpty()) {
            return;
        }

        boolean passwordConfigured = password != null && !password.isBlank();
        boolean found = false;

        for (int i = 0; i < command.size(); i++) {
            String arg = command.get(i);
            if (arg == null) {
                continue;
            }

            if (arg.equals("--password") || arg.equals("-p")) {
                found = true;
                int nextIndex = i + 1;
                if (passwordConfigured) {
                    command.set(i, "--password=" + password);
                    if (nextIndex < command.size()) {
                        String next = command.get(nextIndex);
                        if (Objects.equals(next, password) || (next != null && next.isBlank())) {
                            command.remove(nextIndex);
                        }
                    }
                } else {
                    command.remove(i);
                    if (i < command.size()) {
                        String next = command.get(i);
                        if (Objects.equals(next, password) || (next != null && next.isBlank())) {
                            command.remove(i);
                        }
                    }
                    i--;
                }
                continue;
            }

            if (arg.startsWith("--password=")) {
                found = true;
                if (passwordConfigured) {
                    command.set(i, "--password=" + password);
                } else {
                    command.remove(i);
                    i--;
                }
                continue;
            }

            if (arg.startsWith("-p") && arg.length() > 2) {
                found = true;
                if (passwordConfigured) {
                    command.set(i, "--password=" + password);
                } else {
                    command.remove(i);
                    i--;
                }
            }
        }

        if (!found && passwordConfigured) {
            String passwordArgument = "--password=" + password;
            int userIndex = findUserOptionIndex(command);
            if (userIndex >= 0 && userIndex < command.size()) {
                command.add(userIndex + 1, passwordArgument);
            } else {
                command.add(passwordArgument);
            }
        }
    }

    private void ensureExecutable(List<String> command) {
        if (command == null || command.isEmpty()) {
            return;
        }
        String executable = command.get(0);
        if (executable == null || executable.isBlank()) {
            return;
        }
        if (hasPathSeparator(executable)) {
            return;
        }
        Path resolved = locateMysqldump(executable);
        if (resolved != null) {
            command.set(0, resolved.toString());
        }
    }

    private boolean hasPathSeparator(String value) {
        return value.indexOf('/') >= 0 || value.indexOf('\\') >= 0;
    }

    private Path locateMysqldump(String executable) {
        String lower = executable.toLowerCase(Locale.ROOT);
        if (!lower.equals("mysqldump") && !lower.equals("mysqldump.exe")) {
            return null;
        }

        Path cached = MYSQLDUMP_CACHE.get();
        if (cached != null && Files.isRegularFile(cached)) {
            return cached;
        }

        Path found = findOnPath(executable);
        if (found == null && isWindows()) {
            String exeName = lower.endsWith(".exe") ? executable : executable + ".exe";
            found = findOnPath(exeName);
            if (found == null) {
                found = searchWindowsInstallations(exeName);
            }
        }

        if (found != null) {
            MYSQLDUMP_CACHE.set(found);
            LOGGER.log(Level.INFO, "Se encontró el ejecutable mysqldump en {0}", found);
        }
        return found;
    }

    private Path findOnPath(String executable) {
        if (executable == null || executable.isBlank()) {
            return null;
        }
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null || pathEnv.isBlank()) {
            return null;
        }
        String separator = System.getProperty("path.separator", ":");
        String[] entries = pathEnv.split(Pattern.quote(separator));
        boolean windows = isWindows();
        for (String entry : entries) {
            if (entry == null || entry.isBlank()) {
                continue;
            }
            Path dir;
            try {
                dir = Path.of(entry.trim());
            } catch (InvalidPathException ex) {
                continue;
            }
            Path candidate = dir.resolve(executable);
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
            if (windows && !executable.toLowerCase(Locale.ROOT).endsWith(".exe")) {
                Path withExtension = dir.resolve(executable + ".exe");
                if (Files.isRegularFile(withExtension)) {
                    return withExtension;
                }
            }
        }
        return null;
    }

    private Path searchWindowsInstallations(String exeName) {
        List<Path> bases = new ArrayList<>();
        addIfPresent(bases, envPath("MYSQL_HOME"));
        addIfPresent(bases, envPath("MYSQL_PATH"));
        addIfPresent(bases, envPath("ProgramFiles"));
        addIfPresent(bases, envPath("ProgramFiles(x86)"));
        addIfPresent(bases, envPath("ProgramW6432"));
        Path localAppData = envPath("LOCALAPPDATA");
        if (localAppData != null) {
            addIfPresent(bases, localAppData.resolve("Programs"));
        }

        for (Path base : bases) {
            if (base == null) {
                continue;
            }
            Path direct = checkMysqldumpAt(base, exeName);
            if (direct != null) {
                return direct;
            }
            if (!Files.isDirectory(base)) {
                continue;
            }
            Path candidate = scanVendorDirectory(base.resolve("MySQL"), exeName, 2);
            if (candidate != null) {
                return candidate;
            }
            candidate = scanVendorDirectory(base.resolve("MariaDB"), exeName, 2);
            if (candidate != null) {
                return candidate;
            }
            candidate = scanVendorDirectory(base, exeName, 1);
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    private void addIfPresent(List<Path> bases, Path candidate) {
        if (candidate == null) {
            return;
        }
        if (Files.isRegularFile(candidate) || Files.isDirectory(candidate)) {
            bases.add(candidate);
        }
    }

    private Path envPath(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Path.of(value);
        } catch (InvalidPathException ex) {
            LOGGER.log(Level.FINE, "Valor inválido para la variable de entorno {0}: {1}", new Object[]{key, value});
            return null;
        }
    }

    private Path scanVendorDirectory(Path root, String exeName, int depth) {
        if (root == null || depth < 0 || !Files.isDirectory(root)) {
            return null;
        }
        Path candidate = checkMysqldumpAt(root, exeName);
        if (candidate != null) {
            return candidate;
        }
        if (depth == 0) {
            return null;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for (Path child : stream) {
                if (!Files.isDirectory(child)) {
                    continue;
                }
                if (!containsVendorName(child.getFileName())) {
                    continue;
                }
                candidate = checkMysqldumpAt(child, exeName);
                if (candidate != null) {
                    return candidate;
                }
                candidate = scanVendorDirectory(child, exeName, depth - 1);
                if (candidate != null) {
                    return candidate;
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.FINE, "No se pudo explorar {0} buscando mysqldump", root);
        }
        return null;
    }

    private Path checkMysqldumpAt(Path base, String exeName) {
        if (base == null) {
            return null;
        }
        if (Files.isRegularFile(base) && base.getFileName().toString().equalsIgnoreCase(exeName)) {
            return base;
        }
        Path candidate = base.resolve(exeName);
        if (Files.isRegularFile(candidate)) {
            return candidate;
        }
        Path binCandidate = base.resolve("bin").resolve(exeName);
        if (Files.isRegularFile(binCandidate)) {
            return binCandidate;
        }
        return null;
    }

    private boolean containsVendorName(Path name) {
        if (name == null) {
            return false;
        }
        String lower = name.toString().toLowerCase(Locale.ROOT);
        return lower.contains("mysql") || lower.contains("mariadb") || lower.contains("maria");
    }

    private boolean isWindows() {
        String os = System.getProperty("os.name", "");
        return os.toLowerCase(Locale.ROOT).contains("win");
    }

    private int findUserOptionIndex(List<String> command) {
        for (int i = 0; i < command.size(); i++) {
            String arg = command.get(i);
            if (arg == null) {
                continue;
            }
            if (arg.startsWith("--user")) {
                return i;
            }
            if (arg.equals("-u") || arg.startsWith("-u")) {
                return i;
            }
        }
        return -1;
    }

    private String maskPassword(String arg, String password) {
        if (arg == null || password == null || password.isBlank()) {
            return arg;
        }
        if (arg.startsWith("--password")) {
            int equals = arg.indexOf('=');
            if (equals >= 0) {
                return arg.substring(0, equals + 1) + "******";
            }
            return "--password=******";
        }
        if (arg.equals("-p")) {
            return "-p ******";
        }
        if (arg.startsWith("-p")) {
            return "-p******";
        }
        if (arg.equals(password)) {
            return "******";
        }
        return arg.replace(password, "******");
    }

    private void enforceRetention(Path dir, String prefix, String extension) {
        int retention = parseInt(AppConfig.get("backup.retention.count", "10"), 10);
        if (retention <= 0) {
            return;
        }
        try (var stream = Files.list(dir)) {
            List<Path> backups = stream
                    .filter(p -> Files.isRegularFile(p))
                    .filter(p -> {
                        String name = p.getFileName().toString();
                        return name.startsWith(prefix + "-") && name.endsWith(extension);
                    })
                    .sorted(Comparator.comparing((Path p) -> getLastModifiedSafe(p)).reversed())
                    .collect(Collectors.toList());

            for (int i = retention; i < backups.size(); i++) {
                Files.deleteIfExists(backups.get(i));
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "No se pudieron limpiar backups antiguos", ex);
        }
    }

    private FileTime getLastModifiedSafe(Path p) {
        try {
            return Files.getLastModifiedTime(p);
        } catch (IOException ex) {
            return FileTime.fromMillis(0L);
        }
    }

    private void runPostCommand(Map<String, String> placeholders, String strategy, Path file) throws BackupException {
        String commandTemplate = AppConfig.get("backup.post.command", "");
        if (commandTemplate == null || commandTemplate.isBlank()) {
            return;
        }

        Map<String, String> context = new HashMap<>(placeholders);
        context.put("file", file.toAbsolutePath().toString());
        context.put("filename", file.getFileName().toString());
        context.put("directory", file.getParent().toAbsolutePath().toString());
        context.put("strategy", strategy);

        String resolved = substitutePlaceholders(commandTemplate, context);
        List<String> command = parseArguments(resolved);
        if (command.isEmpty()) {
            LOGGER.warning("El comando posterior al backup está vacío y será ignorado");
            return;
        }

        boolean failOnError = Boolean.parseBoolean(AppConfig.get("backup.post.failOnError", "false"));
        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }
            int exit = process.waitFor();
            if (exit != 0) {
                String message = "El comando posterior al backup finalizó con código " + exit + ": " + output;
                if (failOnError) {
                    throw new BackupException(message);
                }
                LOGGER.warning(message);
            } else {
                LOGGER.fine("Comando posterior al backup ejecutado correctamente: " + output);
            }
        } catch (IOException ex) {
            if (failOnError) {
                throw new BackupException("Error ejecutando el comando posterior al backup", ex);
            }
            LOGGER.log(Level.WARNING, "Error ejecutando el comando posterior al backup", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            if (failOnError) {
                throw new BackupException("El comando posterior al backup fue interrumpido", ex);
            }
            LOGGER.log(Level.WARNING, "El comando posterior al backup fue interrumpido", ex);
        }
    }

    private String substitutePlaceholders(String template, Map<String, String> placeholders) {
        String result = template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String value = Objects.toString(entry.getValue(), "");
            result = result.replace("{" + entry.getKey() + "}", value);
        }
        return result;
    }

    private List<String> parseArguments(String input) {
        List<String> args = new ArrayList<>();
        if (input == null) {
            return args;
        }
        Matcher matcher = ARGUMENT_PATTERN.matcher(input);
        while (matcher.find()) {
            String token = matcher.group(1);
            if (token == null) {
                continue;
            }
            if (token.startsWith("\"") && token.endsWith("\"") && token.length() >= 2) {
                token = token.substring(1, token.length() - 1);
            }
            if (!token.isBlank()) {
                args.add(token);
            }
        }
        return args;
    }

    private String defaultCommand(Strategy strategy) {
        if (strategy == Strategy.FULL) {
            return "mysqldump --host={host} --port={port} --user={user} --routines --events --triggers --single-transaction --quick {database}";
        }
        return "mysqldump --host={host} --port={port} --user={user} --single-transaction --quick {database}";
    }

    private int parseInt(String value, int def) {
        if (value == null || value.isBlank()) {
            return def;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    private void deleteQuietly(Path file) {
        if (file == null) {
            return;
        }
        try {
            Files.deleteIfExists(file);
        } catch (IOException ex) {
            LOGGER.log(Level.FINE, "No se pudo eliminar el archivo temporal de backup {0}", new Object[]{file});
            LOGGER.log(Level.FINER, "Detalle del error al eliminar archivo temporal", ex);
        }
    }

    private record MysqlConnectionInfo(String host, int port, String database) {
        static MysqlConnectionInfo from(String jdbcUrl) throws BackupException {
            if (jdbcUrl == null || jdbcUrl.isBlank()) {
                throw new BackupException("La URL de conexión a la base de datos no está configurada");
            }
            String url = jdbcUrl.startsWith("jdbc:") ? jdbcUrl.substring("jdbc:".length()) : jdbcUrl;
            URI uri = URI.create(url);
            String host = uri.getHost();
            if (host == null || host.isBlank()) {
                host = "localhost";
            }
            int port = uri.getPort();
            if (port <= 0) {
                port = 3306;
            }
            String path = uri.getPath();
            String database = path != null && path.length() > 1 ? path.substring(1) : "";
            if (database.contains("/")) {
                database = database.substring(database.lastIndexOf('/') + 1);
            }
            int queryIndex = database.indexOf('?');
            if (queryIndex >= 0) {
                database = database.substring(0, queryIndex);
            }
            return new MysqlConnectionInfo(host, port, database);
        }
    }
}
