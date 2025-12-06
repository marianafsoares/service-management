package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import utils.DocumentValidator;

public class ProviderPayment {
    private Integer id;
    private String receiptNumber;
    private String pointOfSale;
    private String receiverCuit;
    private Integer providerId;
    private String providerName;
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

    public String getReceiverCuit() {
        return receiverCuit;
    }

    public void setReceiverCuit(String receiverCuit) {
        this.receiverCuit = DocumentValidator.normalizeCuit(receiverCuit);
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
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

