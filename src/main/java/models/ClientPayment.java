package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import utils.DocumentValidator;

public class ClientPayment {
    private Integer id;
    private String receiptNumber;
    private String pointOfSale;
    private String issuerCuit;
    private Integer clientId;
    private String clientName;
    private LocalDateTime receiptDate;
    private BigDecimal total;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getPointOfSale() {
        return pointOfSale;
    }

    public void setPointOfSale(String pointOfSale) {
        this.pointOfSale = pointOfSale;
    }

    public String getIssuerCuit() {
        return issuerCuit;
    }

    public void setIssuerCuit(String issuerCuit) {
        this.issuerCuit = DocumentValidator.normalizeCuit(issuerCuit);
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public LocalDateTime getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(LocalDateTime receiptDate) {
        this.receiptDate = receiptDate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}

