package services;

import java.util.List;
import models.receipts.ReceiptTransfer;
import repositories.ReceiptTransferRepository;

public class ReceiptTransferService {

    private final ReceiptTransferRepository receiptTransferRepository;

    public ReceiptTransferService(ReceiptTransferRepository receiptTransferRepository) {
        this.receiptTransferRepository = receiptTransferRepository;
    }

    public List<ReceiptTransfer> findByReceipt(int receiptId, String receiptType) {
        return receiptTransferRepository.findByReceiptIdAndType(receiptId, receiptType);
    }

    public List<ReceiptTransfer> findByReceipt(int receiptId, List<String> receiptTypes) {
        return receiptTransferRepository.findByReceiptIdAndTypes(receiptId, receiptTypes);
    }

    public void save(ReceiptTransfer transfer) {
        receiptTransferRepository.insert(transfer);
    }

    public void deleteByReceipt(int receiptId) {
        receiptTransferRepository.deleteByReceiptId(receiptId);
    }
}
