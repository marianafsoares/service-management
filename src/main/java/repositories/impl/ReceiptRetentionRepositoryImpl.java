package repositories.impl;

import java.util.List;
import mappers.receipts.ReceiptRetentionMapper;
import models.receipts.ReceiptRetention;
import repositories.ReceiptRetentionRepository;

public class ReceiptRetentionRepositoryImpl implements ReceiptRetentionRepository {

    private final ReceiptRetentionMapper receiptRetentionMapper;

    public ReceiptRetentionRepositoryImpl(ReceiptRetentionMapper receiptRetentionMapper) {
        this.receiptRetentionMapper = receiptRetentionMapper;
    }

    @Override
    public List<ReceiptRetention> findByReceiptIdAndType(int receiptId, String receiptType) {
        return receiptRetentionMapper.findByReceiptIdAndType(receiptId, receiptType);
    }

    @Override
    public List<ReceiptRetention> findByReceiptIdAndTypes(int receiptId, List<String> receiptTypes) {
        return receiptRetentionMapper.findByReceiptIdAndTypes(receiptId, receiptTypes);
    }

    @Override
    public void insert(ReceiptRetention retention) {
        receiptRetentionMapper.insert(retention);
    }

    @Override
    public void deleteByReceiptId(int receiptId) {
        receiptRetentionMapper.deleteByReceiptId(receiptId);
    }
}
