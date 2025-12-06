package models.receipts;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReceiptTransfer {
    private Integer id;
    private Integer receiptId;
    private String receiptType; // CLIENT o PROVIDER
    private String originAccount;
    private String destinationAccount;
    private Integer originBankId;
    private Integer destinationBankId;
    private String originBankName;
    private String destinationBankName;
    private String reference;
    private BigDecimal amount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Integer receiptId) {
        this.receiptId = receiptId;
    }

    public String getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(String receiptType) {
        this.receiptType = receiptType;
    }

    public String getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(String originAccount) {
        this.originAccount = originAccount;
    }

    public Integer getOriginBankId() {
        return originBankId;
    }

    public void setOriginBankId(Integer originBankId) {
        this.originBankId = originBankId;
    }

    public Integer getDestinationBankId() {
        return destinationBankId;
    }

    public void setDestinationBankId(Integer destinationBankId) {
        this.destinationBankId = destinationBankId;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public String getOriginBankName() {
        return originBankName;
    }

    public void setOriginBankName(String originBankName) {
        this.originBankName = originBankName;
    }

    public String getDestinationBankName() {
        return destinationBankName;
    }

    public void setDestinationBankName(String destinationBankName) {
        this.destinationBankName = destinationBankName;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
