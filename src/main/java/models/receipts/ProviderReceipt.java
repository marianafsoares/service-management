package models.receipts;

import models.Provider;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import utils.DocumentValidator;

public class ProviderReceipt {
    private Integer id;
    private String receiptNumber;
    private String pointOfSale;
    private String receiverCuit;
    private Provider provider;
    private LocalDateTime receiptDate;
    private BigDecimal total;
    private String notes;

    private ReceiptCash cashPayment;
    private List<ReceiptCard> cardPayments;
    private List<ReceiptCheque> chequePayments;
    private List<ReceiptTransfer> transferPayments;
    private List<ReceiptRetention> retentionPayments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ReceiptCash getCashPayment() {
        return cashPayment;
    }

    public void setCashPayment(ReceiptCash cashPayment) {
        this.cashPayment = cashPayment;
    }

    public List<ReceiptCard> getCardPayments() {
        return cardPayments;
    }

    public void setCardPayments(List<ReceiptCard> cardPayments) {
        this.cardPayments = cardPayments;
    }

    public List<ReceiptCheque> getChequePayments() {
        return chequePayments;
    }

    public void setChequePayments(List<ReceiptCheque> chequePayments) {
        this.chequePayments = chequePayments;
    }

    public List<ReceiptTransfer> getTransferPayments() {
        return transferPayments;
    }

    public void setTransferPayments(List<ReceiptTransfer> transferPayments) {
        this.transferPayments = transferPayments;
    }

    public List<ReceiptRetention> getRetentionPayments() {
        return retentionPayments;
    }

    public void setRetentionPayments(List<ReceiptRetention> retentionPayments) {
        this.retentionPayments = retentionPayments;
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
