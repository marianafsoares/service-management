
package models.receipts;

import models.Client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import utils.DocumentValidator;

public class ClientReceipt {
    private Integer id;
    private String receiptNumber;
    private String pointOfSale;
    private String issuerCuit;
    private Client client;
    private LocalDateTime receiptDate;
    private BigDecimal total;
    private String notes;

    // Detalles opcionales, solo se cargan cuando se necesita
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

    public String getIssuerCuit() {
        return issuerCuit;
    }

    public void setIssuerCuit(String issuerCuit) {
        this.issuerCuit = DocumentValidator.normalizeCuit(issuerCuit);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

