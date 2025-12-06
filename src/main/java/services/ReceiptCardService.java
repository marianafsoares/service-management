package services;

import models.receipts.ReceiptCard;
import repositories.ReceiptCardRepository;

import java.util.List;

public class ReceiptCardService {

    private final ReceiptCardRepository receiptCardRepository;

    public ReceiptCardService(ReceiptCardRepository receiptCardRepository) {
        this.receiptCardRepository = receiptCardRepository;
    }

    public List<ReceiptCard> findByReceipt(int receiptId, String receiptType) {
        return receiptCardRepository.findByReceiptIdAndType(receiptId, receiptType);
    }

    public List<ReceiptCard> findByReceipt(int receiptId, List<String> receiptTypes) {
        return receiptCardRepository.findByReceiptIdAndTypes(receiptId, receiptTypes);
    }

    public void save(ReceiptCard card) {
        receiptCardRepository.insert(card);
    }

    public void deleteByReceipt(int receiptId) {
        receiptCardRepository.deleteByReceiptId(receiptId);
    }
}

