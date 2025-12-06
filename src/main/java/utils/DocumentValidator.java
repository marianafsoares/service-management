package utils;

public class DocumentValidator {

    public static boolean isValidDni(String dni) {
        if (dni == null) {
            return false;
        }
        return dni.matches("\\d{7,8}");
    }

    public static boolean isValidCuit(String cuit) {
        if (cuit == null) {
            return false;
        }
        String digits = normalizeCuit(cuit);
        if (!digits.matches("\\d{11}")) {
            return false;
        }
        int[] multipliers = {5,4,3,2,7,6,5,4,3,2};
        int sum = 0;
        for (int i = 0; i < multipliers.length; i++) {
            sum += Character.getNumericValue(digits.charAt(i)) * multipliers[i];
        }
        int mod = 11 - (sum % 11);
        int checkDigit = mod == 11 ? 0 : (mod == 10 ? 9 : mod);
        int lastDigit = Character.getNumericValue(digits.charAt(10));
        return checkDigit == lastDigit;
    }

    public static String normalizeCuit(String cuit) {
        return cuit == null ? null : cuit.replaceAll("-", "");
    }

    public static String formatCuit(String cuit) {
        String digits = normalizeCuit(cuit);
        if (digits == null || digits.length() != 11) {
            return cuit;
        }
        return String.format("%s-%s-%s",
                digits.substring(0, 2),
                digits.substring(2, 10),
                digits.substring(10));
    }
}
