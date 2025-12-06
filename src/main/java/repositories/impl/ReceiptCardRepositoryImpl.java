package repositories.impl;

import java.util.List;
import mappers.receipts.ReceiptCardMapper;
import models.receipts.ReceiptCard;
import repositories.ReceiptCardRepository;

public class ReceiptCardRepositoryImpl implements ReceiptCardRepository {

    private final ReceiptCardMapper receiptCardMapper;

    public ReceiptCardRepositoryImpl(ReceiptCardMapper receiptCardMapper) {
        this.receiptCardMapper = receiptCardMapper;
    }

    @Override
    public List<ReceiptCard> findByReceiptIdAndType(int receiptId, String receiptType) {
        return receiptCardMapper.findByReceiptIdAndType(receiptId, receiptType);
    }

    @Override
    public List<ReceiptCard> findByReceiptIdAndTypes(int receiptId, List<String> receiptTypes) {
        return receiptCardMapper.findByReceiptIdAndTypes(receiptId, receiptTypes);
    }

    @Override
    public void insert(ReceiptCard card) {
        receiptCardMapper.insert(card);
    }

    @Override
    public void deleteByReceiptId(int receiptId) {
        receiptCardMapper.deleteByReceiptId(receiptId);
    }
}

