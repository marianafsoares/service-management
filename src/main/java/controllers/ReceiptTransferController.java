package controllers;

import java.util.List;
import models.receipts.ReceiptTransfer;
import services.ReceiptTransferService;

public class ReceiptTransferController {

    private final ReceiptTransferService receiptTransferService;

    public ReceiptTransferController(ReceiptTransferService receiptTransferService) {
        this.receiptTransferService = receiptTransferService;
    }

    public List<ReceiptTransfer> findByReceipt(int receiptId, String receiptType) {
        return receiptTransferService.findByReceipt(receiptId, receiptType);
    }

    public List<ReceiptTransfer> findByReceipt(int receiptId, List<String> receiptTypes) {
        return receiptTransferService.findByReceipt(receiptId, receiptTypes);
    }

    public void save(ReceiptTransfer transfer) {
        receiptTransferService.save(transfer);
    }

    public void deleteByReceipt(int receiptId) {
        receiptTransferService.deleteByReceipt(receiptId);
    }
}
