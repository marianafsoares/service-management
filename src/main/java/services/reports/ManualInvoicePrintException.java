package services.reports;

/**
 * Indicates that an error occurred while generating or printing a manual
 * client invoice (e.g. budgets).
 */
public class ManualInvoicePrintException extends Exception {

    public ManualInvoicePrintException(String message) {
        super(message);
    }

    public ManualInvoicePrintException(String message, Throwable cause) {
        super(message, cause);
    }
}

