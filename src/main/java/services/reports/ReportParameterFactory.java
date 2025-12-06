package services.reports;

import configs.AppConfig;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import utils.DocumentValidator;

/**
 * Provides the default parameter set that should be injected into every
 * JasperReports template so that company information is configurable from
 * {@code app.properties}.
 */
public final class ReportParameterFactory {

    private ReportParameterFactory() {
    }

    public static Map<String, Object> createBaseParameters() {
        return createBaseParameters(null);
    }

    public static Map<String, Object> createBaseParameters(String issuerCuit) {
        Map<String, Object> parameters = new HashMap<>();

        String normalizedCuit = DocumentValidator.normalizeCuit(issuerCuit);

        InputStream logoStream = loadLogo(normalizedCuit);
        if (logoStream != null) {
            parameters.put("Logo", logoStream);
        }

        String name = resolveCompanyValue("name", normalizedCuit);
        String owner = resolveCompanyValue("owner", normalizedCuit);
        String taxId = resolveCompanyValue("tax_id", normalizedCuit);
        String address = resolveCompanyValue("address", normalizedCuit);
        String location = resolveCompanyValue("location", normalizedCuit);
        String phones = resolveCompanyValue("phones", normalizedCuit);
        String email = resolveCompanyValue("email", normalizedCuit);
        String website = resolveCompanyValue("website", normalizedCuit);
        String additional = resolveCompanyValue("additional_info", normalizedCuit);

        String contact1 = withDefault(resolveCompanyValue("contact.line1", normalizedCuit), address);
        String contact2 = withDefault(resolveCompanyValue("contact.line2", normalizedCuit), location);
        String contact3 = withDefault(resolveCompanyValue("contact.line3", normalizedCuit), phones);
        String contact4 = withDefault(resolveCompanyValue("contact.line4", normalizedCuit), email);

        String fullAddress;
        if (!location.isBlank() && !address.isBlank()) {
            fullAddress = location + " - " + address;
        } else if (!location.isBlank()) {
            fullAddress = location;
        } else {
            fullAddress = address;
        }

        putIfNotBlank(parameters, "CompanyName", name);
        putIfNotBlank(parameters, "CompanyOwner", owner);
        putIfNotBlank(parameters, "CompanyTaxId", taxId);
        putIfNotBlank(parameters, "CompanyAddress", address);
        putIfNotBlank(parameters, "CompanyLocation", location);
        putIfNotBlank(parameters, "CompanyPhones", phones);
        putIfNotBlank(parameters, "CompanyEmail", email);
        putIfNotBlank(parameters, "CompanyWebsite", website);
        putIfNotBlank(parameters, "CompanyAdditionalInfo", additional);
        putIfNotBlank(parameters, "CompanyFullAddress", fullAddress);

        putIfNotBlank(parameters, "CompanyContactLine1", contact1);
        putIfNotBlank(parameters, "CompanyContactLine2", contact2);
        putIfNotBlank(parameters, "CompanyContactLine3", contact3);
        putIfNotBlank(parameters, "CompanyContactLine4", contact4);

        return parameters;
    }

    private static InputStream loadLogo(String normalizedCuit) {
        String logoPath = resolveCompanyValue("logo", normalizedCuit);
        if (logoPath == null) {
            return null;
        }
        String trimmedPath = logoPath.trim();
        if (trimmedPath.isEmpty()) {
            return null;
        }
        return ReportParameterFactory.class.getResourceAsStream(trimmedPath);
    }

    private static void putIfNotBlank(Map<String, Object> parameters, String key, String value) {
        if (value != null && !value.isBlank()) {
            parameters.put(key, value);
        }
    }

    private static String withDefault(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    private static String resolveCompanyValue(String suffix, String normalizedCuit) {
        if (normalizedCuit != null && !normalizedCuit.isBlank()) {
            String specificKey = "company.cuit." + normalizedCuit + "." + suffix;
            String value = AppConfig.get(specificKey, null);
            if (value != null) {
                return value;
            }
        }
        return AppConfig.get("company." + suffix, "");
    }
}

