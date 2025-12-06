package controllers;

import java.util.List;
import models.receipts.ReceiptCheque;
import services.ReceiptChequeService;

public class ReceiptChequeController {

    private final ReceiptChequeService receiptChequeService;

    public ReceiptChequeController(ReceiptChequeService receiptChequeService) {
        this.receiptChequeService = receiptChequeService;
    }

    public List<ReceiptCheque> findByReceipt(int receiptId, String receiptType) {
        return receiptChequeService.findByReceipt(receiptId, receiptType);
    }

    public List<ReceiptCheque> findByReceipt(int receiptId, List<String> receiptTypes) {
        return receiptChequeService.findByReceipt(receiptId, receiptTypes);
    }

    public ReceiptCheque findByNumberAndType(String number, String type) {
        return receiptChequeService.findByNumberAndType(number, type);
    }

    public void save(ReceiptCheque cheque) {
        receiptChequeService.save(cheque);
    }

    public void deleteByReceipt(int receiptId) {
        receiptChequeService.deleteByReceipt(receiptId);
    }
}

