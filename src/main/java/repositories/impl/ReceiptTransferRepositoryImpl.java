package repositories.impl;

import java.util.List;
import mappers.receipts.ReceiptTransferMapper;
import models.receipts.ReceiptTransfer;
import repositories.ReceiptTransferRepository;

public class ReceiptTransferRepositoryImpl implements ReceiptTransferRepository {

    private final ReceiptTransferMapper receiptTransferMapper;

    public ReceiptTransferRepositoryImpl(ReceiptTransferMapper receiptTransferMapper) {
        this.receiptTransferMapper = receiptTransferMapper;
    }

    @Override
    public List<ReceiptTransfer> findByReceiptIdAndType(int receiptId, String receiptType) {
        return receiptTransferMapper.findByReceiptIdAndType(receiptId, receiptType);
    }

    @Override
    public List<ReceiptTransfer> findByReceiptIdAndTypes(int receiptId, List<String> receiptTypes) {
        return receiptTransferMapper.findByReceiptIdAndTypes(receiptId, receiptTypes);
    }

    @Override
    public void insert(ReceiptTransfer transfer) {
        receiptTransferMapper.insert(transfer);
    }

    @Override
    public void deleteByReceiptId(int receiptId) {
        receiptTransferMapper.deleteByReceiptId(receiptId);
    }
}
