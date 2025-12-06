package configs;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import utils.DocumentValidator;

/**
 * Provides access to configuration that defines which invoice document letters
 * each issuer CUIT is allowed to emit.
 */
public final class InvoiceEmissionConfig {

    private static final String DEFAULT_PROPERTY = "invoice.emission.allowed.default";
    private static final String ISSUER_PROPERTY_PREFIX = "invoice.emission.allowed.";
    private static final Set<String> ALL_DOCUMENT_LETTERS = Set.of("A", "B", "C");

    private InvoiceEmissionConfig() {
    }

    /**
     * Returns the set of document letters that can be emitted by the provided
     * issuer CUIT. When the CUIT has no specific configuration the global
     * default is used, and if neither is present all letters (A, B and C) are
     * enabled.
     *
     * @param issuerCuit CUIT of the issuer, formatted or not
     * @return immutable set of allowed document letters
     */
    public static Set<String> getAllowedLetters(String issuerCuit) {
        Set<String> allowed = Collections.emptySet();

        String normalizedCuit = DocumentValidator.normalizeCuit(issuerCuit);
        if (normalizedCuit != null && !normalizedCuit.isBlank()) {
            String issuerValue = AppConfig.get(ISSUER_PROPERTY_PREFIX + normalizedCuit, null);
            allowed = parseAllowedLetters(issuerValue);
        }

        if (allowed.isEmpty()) {
            String defaultValue = AppConfig.get(DEFAULT_PROPERTY, null);
            allowed = parseAllowedLetters(defaultValue);
        }

        if (allowed.isEmpty()) {
            allowed = ALL_DOCUMENT_LETTERS;
        }

        return Collections.unmodifiableSet(new LinkedHashSet<>(allowed));
    }

    private static Set<String> parseAllowedLetters(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Collections.emptySet();
        }

        Set<String> result = new LinkedHashSet<>();
        String[] parts = rawValue.split(",");
        for (String part : parts) {
            if (part == null) {
                continue;
            }
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String upper = trimmed.toUpperCase(Locale.ROOT);
            if ("*".equals(upper) || "ALL".equals(upper)) {
                return ALL_DOCUMENT_LETTERS;
            }

            String letter = upper.substring(0, 1);
            if (ALL_DOCUMENT_LETTERS.contains(letter)) {
                result.add(letter);
            }
        }

        return result;
    }
}
