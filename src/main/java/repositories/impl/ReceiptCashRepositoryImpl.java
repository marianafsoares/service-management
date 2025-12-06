package repositories.impl;

import java.util.List;
import mappers.receipts.ReceiptCashMapper;
import models.receipts.ReceiptCash;
import repositories.ReceiptCashRepository;

public class ReceiptCashRepositoryImpl implements ReceiptCashRepository {

    private final ReceiptCashMapper receiptCashMapper;

    public ReceiptCashRepositoryImpl(ReceiptCashMapper receiptCashMapper) {
        this.receiptCashMapper = receiptCashMapper;
    }

    @Override
    public List<ReceiptCash> findByReceiptIdAndType(int receiptId, String receiptType) {
        return receiptCashMapper.findByReceiptIdAndType(receiptId, receiptType);
    }

    @Override
    public List<ReceiptCash> findByReceiptIdAndTypes(int receiptId, List<String> receiptTypes) {
        return receiptCashMapper.findByReceiptIdAndTypes(receiptId, receiptTypes);
    }

    @Override
    public void insert(ReceiptCash cash) {
        receiptCashMapper.insert(cash);
    }

    @Override
    public void deleteByReceiptId(int receiptId) {
        receiptCashMapper.deleteByReceiptId(receiptId);
    }
}

