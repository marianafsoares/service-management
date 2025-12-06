package services.afip;

import java.time.LocalDateTime;

/**
 * Value object representing the outcome of an authorization attempt against
 * AFIP's electronic billing service.
 */
public class AfipAuthorizationResult {

    private final boolean approved;
    private final String cae;
    private final LocalDateTime caeExpirationDate;
    private final String message;

    public AfipAuthorizationResult(boolean approved, String cae,
                                   LocalDateTime caeExpirationDate, String message) {
        this.approved = approved;
        this.cae = cae;
        this.caeExpirationDate = caeExpirationDate;
        this.message = message;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getCae() {
        return cae;
    }

    public LocalDateTime getCaeExpirationDate() {
        return caeExpirationDate;
    }

    public String getMessage() {
        return message;
    }
}
