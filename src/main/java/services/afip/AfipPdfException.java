package services.afip;

/**
 * Exception thrown when the PDF generation or printing process executed by
 * PyAfipWs fails.
 */
public class AfipPdfException extends RuntimeException {

    public AfipPdfException(String message) {
        super(message);
    }

    public AfipPdfException(String message, Throwable cause) {
        super(message, cause);
    }
}
