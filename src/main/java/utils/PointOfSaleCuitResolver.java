package utils;

import configs.AppConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PointOfSaleCuitResolver {

    private static final Logger LOGGER = Logger.getLogger(PointOfSaleCuitResolver.class.getName());
    private static final String COMPANY_CUIT_PREFIX = "company.cuit.";
    private static final String POS_LIST_SUFFIX = ".pos.list";
    private static final Pattern POINT_OF_SALE_PATTERN = Pattern.compile("\\s*(\\d+)\\s*(?:\\(([^)]*)\\))?\\s*");
    private static final String DEFAULT_POINT = "0000";

    private PointOfSaleCuitResolver() {
    }

    public static PointOfSaleConfiguration loadConfiguration() {
        Map<String, LinkedHashMap<String, PointOfSaleOption>> optionsByCuit = new LinkedHashMap<>();

        initializeConfiguredCuits(optionsByCuit);

        Map<String, String> configured = AppConfig.getByPrefix(COMPANY_CUIT_PREFIX);
        configured.forEach((key, value) -> {
            if (!key.endsWith(POS_LIST_SUFFIX)) {
                return;
            }

            String cuitPart = key.substring(COMPANY_CUIT_PREFIX.length(), key.length() - POS_LIST_SUFFIX.length());
            String normalizedCuit = DocumentValidator.normalizeCuit(cuitPart);
            if (normalizedCuit == null || normalizedCuit.isBlank()) {
                LOGGER.log(Level.WARNING, "No se pudo interpretar el CUIT configurado en ''{0}''", key);
                return;
            }

            LinkedHashMap<String, PointOfSaleOption> options = ensureCuitEntry(optionsByCuit, normalizedCuit);
            if (value == null || value.isBlank()) {
                return;
            }

            for (String raw : value.split(",")) {
                if (raw == null || raw.isBlank()) {
                    continue;
                }

                Matcher matcher = POINT_OF_SALE_PATTERN.matcher(raw);
                if (!matcher.matches()) {
                    LOGGER.log(Level.WARNING,
                            "No se pudo interpretar el punto de venta ''{0}'' configurado en ''{1}''",
                            new Object[]{raw, key});
                    continue;
                }

                String pointOfSale = normalizePointOfSale(matcher.group(1));
                if (pointOfSale == null) {
                    LOGGER.log(Level.WARNING,
                            "Punto de venta inválido ''{0}'' configurado en ''{1}''",
                            new Object[]{raw, key});
                    continue;
                }

                String description = matcher.group(2);
                if (description != null) {
                    description = description.trim();
                }

                PointOfSaleOption existing = options.get(pointOfSale);
                if (existing == null) {
                    options.put(pointOfSale, new PointOfSaleOption(pointOfSale, description));
                } else if (description != null && !description.isBlank()
                        && (existing.getDescription() == null || existing.getDescription().isBlank())) {
                    options.put(pointOfSale, new PointOfSaleOption(pointOfSale, description));
                }
            }
        });

        Map<String, List<PointOfSaleOption>> pointsByCuit = new LinkedHashMap<>();
        optionsByCuit.forEach((cuit, options) -> pointsByCuit.put(cuit,
                Collections.unmodifiableList(new ArrayList<>(options.values()))));

        Map<String, String> pointToCuit = new LinkedHashMap<>();
        pointsByCuit.forEach((cuit, options) -> {
            for (PointOfSaleOption option : options) {
                if (DEFAULT_POINT.equals(option.getCode())) {
                    continue;
                }
                String previous = pointToCuit.putIfAbsent(option.getCode(), cuit);
                if (previous != null && !previous.equals(cuit)) {
                    LOGGER.log(Level.WARNING,
                            "El punto de venta {0} está configurado para más de un CUIT ({1} y {2})",
                            new Object[]{option.getCode(), previous, cuit});
                }
            }
        });

        return new PointOfSaleConfiguration(pointsByCuit,
                Collections.unmodifiableMap(pointToCuit),
                Collections.unmodifiableList(Collections.singletonList(new PointOfSaleOption(DEFAULT_POINT, null))));
    }

    public static Map<String, String> loadMappings() {
        return loadConfiguration().getPointToCuitMap();
    }

    public static List<PointOfSaleOption> loadPointsForCuit(String cuit) {
        return loadConfiguration().getPointsForCuit(cuit);
    }

    private static void initializeConfiguredCuits(Map<String, LinkedHashMap<String, PointOfSaleOption>> optionsByCuit) {
        String cuits = AppConfig.get("company.cuits", "");
        if (cuits == null || cuits.isBlank()) {
            return;
        }

        for (String raw : cuits.split(",")) {
            String normalized = DocumentValidator.normalizeCuit(raw);
            if (normalized != null && !normalized.isBlank()) {
                ensureCuitEntry(optionsByCuit, normalized);
            }
        }
    }

    private static LinkedHashMap<String, PointOfSaleOption> ensureCuitEntry(
            Map<String, LinkedHashMap<String, PointOfSaleOption>> optionsByCuit, String normalizedCuit) {
        LinkedHashMap<String, PointOfSaleOption> options = optionsByCuit.computeIfAbsent(normalizedCuit,
                key -> new LinkedHashMap<>());
        options.putIfAbsent(DEFAULT_POINT, new PointOfSaleOption(DEFAULT_POINT, null));
        return options;
    }

    private static String normalizePointOfSale(String raw) {
        if (raw == null) {
            return null;
        }

        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        try {
            int value = Integer.parseInt(trimmed);
            if (value < 0) {
                return null;
            }
            return String.format(Locale.ROOT, "%04d", value);
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "El punto de venta configurado no es numérico: {0}", raw);
            return null;
        }
    }

    public static final class PointOfSaleConfiguration {

        private final Map<String, List<PointOfSaleOption>> pointsByCuit;
        private final Map<String, String> pointToCuit;
        private final List<PointOfSaleOption> defaultOptions;

        private PointOfSaleConfiguration(Map<String, List<PointOfSaleOption>> pointsByCuit,
                Map<String, String> pointToCuit,
                List<PointOfSaleOption> defaultOptions) {
            this.pointsByCuit = pointsByCuit;
            this.pointToCuit = pointToCuit;
            this.defaultOptions = defaultOptions;
        }

        public Map<String, String> getPointToCuitMap() {
            return pointToCuit;
        }

        public List<PointOfSaleOption> getPointsForCuit(String cuit) {
            String normalized = DocumentValidator.normalizeCuit(cuit);
            if (normalized != null && !normalized.isBlank()) {
                List<PointOfSaleOption> configured = pointsByCuit.get(normalized);
                if (configured != null && !configured.isEmpty()) {
                    return configured;
                }
            }
            return defaultOptions;
        }
    }

    public static final class PointOfSaleOption {

        private final String code;
        private final String description;

        public PointOfSaleOption(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}
