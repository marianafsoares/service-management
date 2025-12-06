package utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility helpers for working with invoice types and categories.
 */
public final class InvoiceTypeUtils {

    private static final Set<String> CREDIT_DOCUMENT_TYPES = Stream.of(
            Constants.NOTA_CREDITO_A,
            Constants.NOTA_CREDITO_B,
            Constants.NOTA_CREDITO_C,
            Constants.NOTA_DEVOLUCION,
            Constants.NOTA_CREDITO_A_ABBR,
            Constants.NOTA_CREDITO_B_ABBR,
            Constants.NOTA_CREDITO_C_ABBR,
            Constants.NOTA_DEVOLUCION_ABBR
    ).map(InvoiceTypeUtils::normalize)
            .collect(Collectors.toSet());

    private static final Set<String> DEBIT_DOCUMENT_TYPES = Stream.of(
            Constants.NOTA_DEBITO_A,
            Constants.NOTA_DEBITO_B,
            Constants.NOTA_DEBITO_C,
            Constants.NOTA_DEBITO_A_ABBR,
            Constants.NOTA_DEBITO_B_ABBR,
            Constants.NOTA_DEBITO_C_ABBR
    ).map(InvoiceTypeUtils::normalize)
            .collect(Collectors.toSet());

    private static final Set<String> MANUAL_DOCUMENT_TYPES = Stream.of(
            Constants.PRESUPUESTO,
            Constants.NOTA_DEVOLUCION,
            Constants.PRESUPUESTO_ABBR,
            Constants.NOTA_DEVOLUCION_ABBR
    ).map(InvoiceTypeUtils::normalize)
            .collect(Collectors.toSet());

    private static final Set<String> INVOICE_DOCUMENT_TYPES = Stream.of(
            Constants.FACTURA_A,
            Constants.FACTURA_B,
            Constants.FACTURA_C,
            Constants.FACTURA_A_ABBR,
            Constants.FACTURA_B_ABBR,
            Constants.FACTURA_C_ABBR
    ).map(InvoiceTypeUtils::normalize)
            .collect(Collectors.toSet());

    private static final Map<String, Integer> AFIP_TYPE_CODES;
    private static final Map<String, String> STORAGE_VALUES;
    private static final Map<String, String> DISPLAY_VALUES;
    private static final Map<String, String> ABBREVIATION_VALUES;

    static {
        Map<String, Integer> codes = new HashMap<>();
        codes.put(normalize(Constants.FACTURA_A), 8);
        codes.put(normalize(Constants.NOTA_CREDITO_A), 9);
        codes.put(normalize(Constants.NOTA_DEBITO_A), 10);
        codes.put(normalize(Constants.FACTURA_B), 1);
        codes.put(normalize(Constants.NOTA_CREDITO_B), 2);
        codes.put(normalize(Constants.NOTA_DEBITO_B), 3);
        codes.put(normalize(Constants.FACTURA_C), 11);
        codes.put(normalize(Constants.NOTA_CREDITO_C), 12);
        codes.put(normalize(Constants.NOTA_DEBITO_C), 13);
        codes.put(normalize(Constants.FACTURA_A_ABBR), 8);
        codes.put(normalize(Constants.NOTA_CREDITO_A_ABBR), 9);
        codes.put(normalize(Constants.NOTA_DEBITO_A_ABBR), 10);
        codes.put(normalize(Constants.FACTURA_B_ABBR), 1);
        codes.put(normalize(Constants.NOTA_CREDITO_B_ABBR), 2);
        codes.put(normalize(Constants.NOTA_DEBITO_B_ABBR), 3);
        codes.put(normalize(Constants.FACTURA_C_ABBR), 11);
        codes.put(normalize(Constants.NOTA_CREDITO_C_ABBR), 12);
        codes.put(normalize(Constants.NOTA_DEBITO_C_ABBR), 13);
        AFIP_TYPE_CODES = Collections.unmodifiableMap(codes);

        Map<String, String> storageValues = new HashMap<>();
        addStorageMapping(storageValues, Constants.PRESUPUESTO, Constants.PRESUPUESTO_ABBR);
        addStorageMapping(storageValues, Constants.NOTA_DEVOLUCION, Constants.NOTA_DEVOLUCION_ABBR);
        addStorageMapping(storageValues, Constants.FACTURA_A, Constants.FACTURA_A_ABBR);
        addStorageMapping(storageValues, Constants.FACTURA_B, Constants.FACTURA_B_ABBR);
        addStorageMapping(storageValues, Constants.FACTURA_C, Constants.FACTURA_C_ABBR);
        addStorageMapping(storageValues, Constants.NOTA_CREDITO_A, Constants.NOTA_CREDITO_A_ABBR);
        addStorageMapping(storageValues, Constants.NOTA_CREDITO_B, Constants.NOTA_CREDITO_B_ABBR);
        addStorageMapping(storageValues, Constants.NOTA_CREDITO_C, Constants.NOTA_CREDITO_C_ABBR);
        addStorageMapping(storageValues, Constants.NOTA_DEBITO_A, Constants.NOTA_DEBITO_A_ABBR);
        addStorageMapping(storageValues, Constants.NOTA_DEBITO_B, Constants.NOTA_DEBITO_B_ABBR);
        addStorageMapping(storageValues, Constants.NOTA_DEBITO_C, Constants.NOTA_DEBITO_C_ABBR);
        addStorageMapping(storageValues, Constants.AJUSTE, Constants.AJUSTE_ABBR);
        addStorageMapping(storageValues, Constants.RECIBO_COBRO, Constants.RECIBO_ABBR);
        STORAGE_VALUES = Collections.unmodifiableMap(storageValues);

        Map<String, String> displayValues = new HashMap<>();
        addDisplayMapping(displayValues, Constants.PRESUPUESTO, Constants.PRESUPUESTO);
        addDisplayMapping(displayValues, Constants.PRESUPUESTO_ABBR, Constants.PRESUPUESTO);
        addDisplayMapping(displayValues, Constants.NOTA_DEVOLUCION, Constants.NOTA_DEVOLUCION);
        addDisplayMapping(displayValues, Constants.NOTA_DEVOLUCION_ABBR, Constants.NOTA_DEVOLUCION);
        addDisplayMapping(displayValues, Constants.FACTURA_A, Constants.FACTURA_A);
        addDisplayMapping(displayValues, Constants.FACTURA_A_ABBR, Constants.FACTURA_A);
        addDisplayMapping(displayValues, Constants.FACTURA_B, Constants.FACTURA_B);
        addDisplayMapping(displayValues, Constants.FACTURA_B_ABBR, Constants.FACTURA_B);
        addDisplayMapping(displayValues, Constants.FACTURA_C, Constants.FACTURA_C);
        addDisplayMapping(displayValues, Constants.FACTURA_C_ABBR, Constants.FACTURA_C);
        addDisplayMapping(displayValues, Constants.NOTA_CREDITO_A, Constants.NOTA_CREDITO_A);
        addDisplayMapping(displayValues, Constants.NOTA_CREDITO_A_ABBR, Constants.NOTA_CREDITO_A);
        addDisplayMapping(displayValues, Constants.NOTA_CREDITO_B, Constants.NOTA_CREDITO_B);
        addDisplayMapping(displayValues, Constants.NOTA_CREDITO_B_ABBR, Constants.NOTA_CREDITO_B);
        addDisplayMapping(displayValues, Constants.NOTA_CREDITO_C, Constants.NOTA_CREDITO_C);
        addDisplayMapping(displayValues, Constants.NOTA_CREDITO_C_ABBR, Constants.NOTA_CREDITO_C);
        addDisplayMapping(displayValues, Constants.NOTA_DEBITO_A, Constants.NOTA_DEBITO_A);
        addDisplayMapping(displayValues, Constants.NOTA_DEBITO_A_ABBR, Constants.NOTA_DEBITO_A);
        addDisplayMapping(displayValues, Constants.NOTA_DEBITO_B, Constants.NOTA_DEBITO_B);
        addDisplayMapping(displayValues, Constants.NOTA_DEBITO_B_ABBR, Constants.NOTA_DEBITO_B);
        addDisplayMapping(displayValues, Constants.NOTA_DEBITO_C, Constants.NOTA_DEBITO_C);
        addDisplayMapping(displayValues, Constants.NOTA_DEBITO_C_ABBR, Constants.NOTA_DEBITO_C);
        addDisplayMapping(displayValues, Constants.AJUSTE, Constants.AJUSTE);
        addDisplayMapping(displayValues, Constants.AJUSTE_ABBR, Constants.AJUSTE);
        addDisplayMapping(displayValues, Constants.RECIBO_COBRO, Constants.RECIBO_COBRO);
        addDisplayMapping(displayValues, Constants.RECIBO_ABBR, Constants.RECIBO_COBRO);
        DISPLAY_VALUES = Collections.unmodifiableMap(displayValues);

        Map<String, String> abbreviationValues = new HashMap<>();
        addAbbreviationMapping(abbreviationValues, Constants.FACTURA_A, Constants.FACTURA_A_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.FACTURA_A_ABBR, Constants.FACTURA_A_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.FACTURA_B, Constants.FACTURA_B_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.FACTURA_B_ABBR, Constants.FACTURA_B_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.FACTURA_C, Constants.FACTURA_C_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.FACTURA_C_ABBR, Constants.FACTURA_C_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.NOTA_CREDITO_A, Constants.NOTA_CREDITO_A_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.NOTA_CREDITO_A_ABBR, Constants.NOTA_CREDITO_A_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.NOTA_CREDITO_B, Constants.NOTA_CREDITO_B_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.NOTA_CREDITO_B_ABBR, Constants.NOTA_CREDITO_B_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.NOTA_CREDITO_C, Constants.NOTA_CREDITO_C_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.NOTA_CREDITO_C_ABBR, Constants.NOTA_CREDITO_C_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.NOTA_DEBITO_A, Constants.NOTA_DEBITO_A_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.NOTA_DEBITO_A_ABBR, Constants.NOTA_DEBITO_A_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.NOTA_DEBITO_B, Constants.NOTA_DEBITO_B_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.NOTA_DEBITO_B_ABBR, Constants.NOTA_DEBITO_B_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.NOTA_DEBITO_C, Constants.NOTA_DEBITO_C_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.NOTA_DEBITO_C_ABBR, Constants.NOTA_DEBITO_C_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.AJUSTE, Constants.AJUSTE_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.AJUSTE_ABBR, Constants.AJUSTE_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.RECIBO_COBRO, Constants.RECIBO_ABBR);
        addAbbreviationMapping(abbreviationValues, Constants.RECIBO_ABBR, Constants.RECIBO_ABBR);
        ABBREVIATION_VALUES = Collections.unmodifiableMap(abbreviationValues);
    }

    private InvoiceTypeUtils() {
        // utility class
    }

    /**
     * Returns {@code true} when the provided description represents a credit
     * document (credit note or return).
     *
     * @param description invoice type or category description
     * @return {@code true} if the description should be treated as a credit
     *         document
     */
    public static boolean isCreditDocument(String description) {
        String normalized = normalize(description);
        return !normalized.isEmpty() && CREDIT_DOCUMENT_TYPES.contains(normalized);
    }

    public static boolean isDebitDocument(String description) {
        String normalized = normalize(description);
        return !normalized.isEmpty() && DEBIT_DOCUMENT_TYPES.contains(normalized);
    }

    public static boolean isInvoiceDocument(String description) {
        String normalized = normalize(description);
        return !normalized.isEmpty() && INVOICE_DOCUMENT_TYPES.contains(normalized);
    }

    public static boolean requiresAfipAuthorization(String description) {
        String normalized = normalize(description);
        if (normalized.isEmpty()) {
            return false;
        }
        if (MANUAL_DOCUMENT_TYPES.contains(normalized)) {
            return false;
        }
        return INVOICE_DOCUMENT_TYPES.contains(normalized)
                || CREDIT_DOCUMENT_TYPES.contains(normalized)
                || DEBIT_DOCUMENT_TYPES.contains(normalized);
    }

    public static Integer findAfipTypeCode(String description) {
        String normalized = normalize(description);
        if (normalized.isEmpty()) {
            return null;
        }
        return AFIP_TYPE_CODES.get(normalized);
    }

    public static String toStorageValue(String description) {
        String normalized = normalize(description);
        if (normalized.isEmpty()) {
            return "";
        }
        return STORAGE_VALUES.getOrDefault(normalized, description);
    }

    public static String toDisplayValue(String value) {
        String normalized = normalize(value);
        if (normalized.isEmpty()) {
            return "";
        }
        return DISPLAY_VALUES.getOrDefault(normalized, value);
    }

    public static String toAbbreviation(String value) {
        String normalized = normalize(value);
        if (normalized.isEmpty()) {
            return "";
        }
        return ABBREVIATION_VALUES.getOrDefault(normalized, value);
    }

    /**
     * Resolves the invoice type that should be used when looking up an
     * associated document (for example, when a credit note references the
     * original invoice).
     *
     * @param invoiceType invoice type or abbreviation of the current document
     * @return the expected invoice type of the associated document, or
     *         {@code null} if it cannot be determined
     */
    public static String findAssociatedInvoiceType(String invoiceType) {
        String normalized = toStorageValue(invoiceType);
        if (normalized.isEmpty()) {
            return null;
        }

        if (equalsIgnoreCase(normalized, Constants.FACTURA_A_ABBR)
                || equalsIgnoreCase(normalized, Constants.NOTA_CREDITO_A_ABBR)
                || equalsIgnoreCase(normalized, Constants.NOTA_DEBITO_A_ABBR)) {
            return Constants.FACTURA_A_ABBR;
        }
        if (equalsIgnoreCase(normalized, Constants.FACTURA_B_ABBR)
                || equalsIgnoreCase(normalized, Constants.NOTA_CREDITO_B_ABBR)
                || equalsIgnoreCase(normalized, Constants.NOTA_DEBITO_B_ABBR)) {
            return Constants.FACTURA_B_ABBR;
        }
        if (equalsIgnoreCase(normalized, Constants.FACTURA_C_ABBR)
                || equalsIgnoreCase(normalized, Constants.NOTA_CREDITO_C_ABBR)
                || equalsIgnoreCase(normalized, Constants.NOTA_DEBITO_C_ABBR)) {
            return Constants.FACTURA_C_ABBR;
        }
        return null;
    }

    /**
     * Determines the document letter (A, B or C) associated with the provided
     * invoice or note description.
     *
     * @param description invoice type description or abbreviation
     * @return the document letter, or an empty string when it cannot be
     *         determined
     */
    public static String findDocumentLetter(String description) {
        String normalized = toStorageValue(description);
        if (normalized.isEmpty()) {
            return "";
        }

        if (equalsIgnoreCase(normalized, Constants.FACTURA_A_ABBR)) {
            return "A";
        }
        if (equalsIgnoreCase(normalized, Constants.FACTURA_B_ABBR)) {
            return "B";
        }
        if (equalsIgnoreCase(normalized, Constants.FACTURA_C_ABBR)) {
            return "C";
        }

        String associated = findAssociatedInvoiceType(description);
        if (associated == null || associated.isEmpty()) {
            return "";
        }

        if (equalsIgnoreCase(associated, Constants.FACTURA_A_ABBR)) {
            return "A";
        }
        if (equalsIgnoreCase(associated, Constants.FACTURA_B_ABBR)) {
            return "B";
        }
        if (equalsIgnoreCase(associated, Constants.FACTURA_C_ABBR)) {
            return "C";
        }

        return "";
    }

    private static boolean equalsIgnoreCase(String value, String other) {
        return value != null && other != null && value.equalsIgnoreCase(other);
    }

    private static String normalize(String description) {
        if (description == null) {
            return "";
        }
        String normalized = description.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? "" : normalized;
    }

    private static void addStorageMapping(Map<String, String> target, String key, String value) {
        if (key != null && value != null) {
            target.put(normalize(key), value);
        }
    }

    private static void addDisplayMapping(Map<String, String> target, String key, String value) {
        if (key != null && value != null) {
            target.put(normalize(key), value);
        }
    }

    private static void addAbbreviationMapping(Map<String, String> target, String key, String value) {
        if (key != null && value != null) {
            target.put(normalize(key), value);
        }
    }
}

