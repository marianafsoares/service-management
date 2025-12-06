package configs;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public final class AppConfig {
    private static final Properties P = new Properties();

    static {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (in != null) {
                try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    P.load(reader);
                }
            }
        } catch (Exception ignored) {}

        try {
            Path ext = Path.of("config", "app.properties");
            if (Files.isRegularFile(ext)) {
                Properties extProps = new Properties();
                try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(ext), StandardCharsets.UTF_8)) {
                    extProps.load(reader);
                }
                P.putAll(extProps);
            }
        } catch (Exception ignored) {}
    }

    private AppConfig() {}

    public static String get(String key, String def) {
        String v = System.getProperty(key);
        if (v != null && !v.isEmpty()) return v;
        v = P.getProperty(key);
        return v != null ? v : def;
    }

    public static Map<String, String> getByPrefix(String prefix) {
        Map<String, String> result = new LinkedHashMap<>();
        if (prefix == null || prefix.isEmpty()) {
            return result;
        }

        Properties systemProperties = System.getProperties();
        for (String name : systemProperties.stringPropertyNames()) {
            if (name.startsWith(prefix)) {
                String value = systemProperties.getProperty(name);
                if (value != null) {
                    result.put(name, value);
                }
            }
        }

        for (String name : P.stringPropertyNames()) {
            if (name.startsWith(prefix) && !result.containsKey(name)) {
                String value = P.getProperty(name);
                if (value != null) {
                    result.put(name, value);
                }
            }
        }

        return result;
    }
}
