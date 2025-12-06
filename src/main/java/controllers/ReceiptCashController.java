package controllers;

import java.util.List;
import models.receipts.ReceiptCash;
import services.ReceiptCashService;

public class ReceiptCashController {

    private final ReceiptCashService receiptCashService;

    public ReceiptCashController(ReceiptCashService receiptCashService) {
        this.receiptCashService = receiptCashService;
    }

    public List<ReceiptCash> findByReceipt(int receiptId, String receiptType) {
        return receiptCashService.findByReceipt(receiptId, receiptType);
    }

    public List<ReceiptCash> findByReceipt(int receiptId, List<String> receiptTypes) {
        return receiptCashService.findByReceipt(receiptId, receiptTypes);
    }

    public void save(ReceiptCash cash) {
        receiptCashService.save(cash);
    }

    public void deleteByReceipt(int receiptId) {
        receiptCashService.deleteByReceipt(receiptId);
    }
}

