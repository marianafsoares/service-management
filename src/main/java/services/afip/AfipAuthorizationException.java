package services.afip;

/**
 * Exception thrown when the AFIP authorization flow fails unexpectedly.
 */
public class AfipAuthorizationException extends RuntimeException {

    public AfipAuthorizationException(String message) {
        super(message);
    }

    public AfipAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
