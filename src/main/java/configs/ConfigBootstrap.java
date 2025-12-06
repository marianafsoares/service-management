package configs;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class ConfigBootstrap {
    private ConfigBootstrap() {}

    public static void ensureExternalConfig() {
        try {
            Path dir = Path.of("config");
            Path file = dir.resolve("app.properties");
            if (Files.exists(file)) return;
            Files.createDirectories(dir);
            try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("app.properties")) {
                if (in != null) {
                    Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (Exception ignored) {}
    }
}
