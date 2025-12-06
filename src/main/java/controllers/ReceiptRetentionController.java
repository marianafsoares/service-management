package controllers;

import java.util.List;
import models.receipts.ReceiptRetention;
import services.ReceiptRetentionService;

public class ReceiptRetentionController {

    private final ReceiptRetentionService receiptRetentionService;

    public ReceiptRetentionController(ReceiptRetentionService receiptRetentionService) {
        this.receiptRetentionService = receiptRetentionService;
    }

    public List<ReceiptRetention> findByReceipt(int receiptId, String receiptType) {
        return receiptRetentionService.findByReceipt(receiptId, receiptType);
    }

    public List<ReceiptRetention> findByReceipt(int receiptId, List<String> receiptTypes) {
        return receiptRetentionService.findByReceipt(receiptId, receiptTypes);
    }

    public void save(ReceiptRetention retention) {
        receiptRetentionService.save(retention);
    }

    public void deleteByReceipt(int receiptId) {
        receiptRetentionService.deleteByReceipt(receiptId);
    }
}
