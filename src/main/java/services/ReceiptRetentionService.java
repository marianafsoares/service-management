package services;

import java.util.List;
import models.receipts.ReceiptRetention;
import repositories.ReceiptRetentionRepository;

public class ReceiptRetentionService {

    private final ReceiptRetentionRepository receiptRetentionRepository;

    public ReceiptRetentionService(ReceiptRetentionRepository receiptRetentionRepository) {
        this.receiptRetentionRepository = receiptRetentionRepository;
    }

    public List<ReceiptRetention> findByReceipt(int receiptId, String receiptType) {
        return receiptRetentionRepository.findByReceiptIdAndType(receiptId, receiptType);
    }

    public List<ReceiptRetention> findByReceipt(int receiptId, List<String> receiptTypes) {
        return receiptRetentionRepository.findByReceiptIdAndTypes(receiptId, receiptTypes);
    }

    public void save(ReceiptRetention retention) {
        receiptRetentionRepository.insert(retention);
    }

    public void deleteByReceipt(int receiptId) {
        receiptRetentionRepository.deleteByReceiptId(receiptId);
    }
}
