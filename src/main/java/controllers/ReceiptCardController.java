package controllers;

import java.util.List;
import models.receipts.ReceiptCard;
import services.ReceiptCardService;

public class ReceiptCardController {

    private final ReceiptCardService receiptCardService;

    public ReceiptCardController(ReceiptCardService receiptCardService) {
        this.receiptCardService = receiptCardService;
    }

    public List<ReceiptCard> findByReceipt(int receiptId, String receiptType) {
        return receiptCardService.findByReceipt(receiptId, receiptType);
    }

    public List<ReceiptCard> findByReceipt(int receiptId, List<String> receiptTypes) {
        return receiptCardService.findByReceipt(receiptId, receiptTypes);
    }

    public void save(ReceiptCard card) {
        receiptCardService.save(card);
    }

    public void deleteByReceipt(int receiptId) {
        receiptCardService.deleteByReceipt(receiptId);
    }
}

