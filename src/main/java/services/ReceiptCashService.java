package services;

import models.receipts.ReceiptCash;
import repositories.ReceiptCashRepository;

import java.util.List;

public class ReceiptCashService {

    private final ReceiptCashRepository receiptCashRepository;

    public ReceiptCashService(ReceiptCashRepository receiptCashRepository) {
        this.receiptCashRepository = receiptCashRepository;
    }

    public List<ReceiptCash> findByReceipt(int receiptId, String receiptType) {
        return receiptCashRepository.findByReceiptIdAndType(receiptId, receiptType);
    }

    public List<ReceiptCash> findByReceipt(int receiptId, List<String> receiptTypes) {
        return receiptCashRepository.findByReceiptIdAndTypes(receiptId, receiptTypes);
    }

    public void save(ReceiptCash cash) {
        receiptCashRepository.insert(cash);
    }

    public void deleteByReceipt(int receiptId) {
        receiptCashRepository.deleteByReceiptId(receiptId);
    }
}

