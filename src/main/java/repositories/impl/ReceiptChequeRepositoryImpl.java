package repositories.impl;

import java.util.List;
import mappers.receipts.ReceiptChequeMapper;
import models.receipts.ReceiptCheque;
import repositories.ReceiptChequeRepository;

public class ReceiptChequeRepositoryImpl implements ReceiptChequeRepository {

    private final ReceiptChequeMapper receiptChequeMapper;

    public ReceiptChequeRepositoryImpl(ReceiptChequeMapper receiptChequeMapper) {
        this.receiptChequeMapper = receiptChequeMapper;
    }

    @Override
    public List<ReceiptCheque> findByReceiptIdAndType(int receiptId, String receiptType) {
        return receiptChequeMapper.findByReceiptIdAndType(receiptId, receiptType);
    }

    @Override
    public List<ReceiptCheque> findByReceiptIdAndTypes(int receiptId, List<String> receiptTypes) {
        return receiptChequeMapper.findByReceiptIdAndTypes(receiptId, receiptTypes);
    }

    @Override
    public ReceiptCheque findByNumberAndType(String checkNumber, String receiptType) {
        return receiptChequeMapper.findByNumberAndType(checkNumber, receiptType);
    }

    @Override
    public void insert(ReceiptCheque cheque) {
        receiptChequeMapper.insert(cheque);
    }

    @Override
    public void deleteByReceiptId(int receiptId) {
        receiptChequeMapper.deleteByReceiptId(receiptId);
    }
}

