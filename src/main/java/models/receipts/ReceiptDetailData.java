package models.receipts;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReceiptDetailData {
    private ReceiptType type;
    private String pointOfSale;
    private String receiptNumber;
    private LocalDateTime receiptDate;
    private BigDecimal total;
    private String entityName;
    private String notes;
    private BigDecimal balance;
    private ReceiptCash cashPayment;
    private final List<ReceiptCard> cardPayments = new ArrayList<>();
    private final List<ReceiptCheque> chequePayments = new ArrayList<>();
    private final List<ReceiptTransfer> transferPayments = new ArrayList<>();
    private final List<ReceiptRetention> retentionPayments = new ArrayList<>();

    public ReceiptType getType() {
        return type;
    }

    public void setType(ReceiptType type) {
        this.type = type;
    }

    public String getPointOfSale() {
        return pointOfSale;
    }

    public void setPointOfSale(String pointOfSale) {
        this.pointOfSale = pointOfSale;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
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

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public ReceiptCash getCashPayment() {
        return cashPayment;
    }

    public void setCashPayment(ReceiptCash cashPayment) {
        this.cashPayment = cashPayment;
    }

    public List<ReceiptCard> getCardPayments() {
        return Collections.unmodifiableList(cardPayments);
    }

    public void setCardPayments(List<ReceiptCard> cards) {
        cardPayments.clear();
        if (cards != null) {
            cardPayments.addAll(cards);
        }
    }

    public List<ReceiptCheque> getChequePayments() {
        return Collections.unmodifiableList(chequePayments);
    }

    public void setChequePayments(List<ReceiptCheque> cheques) {
        chequePayments.clear();
        if (cheques != null) {
            chequePayments.addAll(cheques);
        }
    }

    public List<ReceiptTransfer> getTransferPayments() {
        return Collections.unmodifiableList(transferPayments);
    }

    public void setTransferPayments(List<ReceiptTransfer> transfers) {
        transferPayments.clear();
        if (transfers != null) {
            transferPayments.addAll(transfers);
        }
    }

    public List<ReceiptRetention> getRetentionPayments() {
        return Collections.unmodifiableList(retentionPayments);
    }

    public void setRetentionPayments(List<ReceiptRetention> retentions) {
        retentionPayments.clear();
        if (retentions != null) {
            retentionPayments.addAll(retentions);
        }
    }
}
