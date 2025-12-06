package services;

import java.util.List;
import models.receipts.ReceiptCheque;
import repositories.ReceiptChequeRepository;

public class ReceiptChequeService {

    private final ReceiptChequeRepository receiptChequeRepository;

    public ReceiptChequeService(ReceiptChequeRepository receiptChequeRepository) {
        this.receiptChequeRepository = receiptChequeRepository;
    }

    public List<ReceiptCheque> findByReceipt(int receiptId, String receiptType) {
        return receiptChequeRepository.findByReceiptIdAndType(receiptId, receiptType);
    }

    public List<ReceiptCheque> findByReceipt(int receiptId, List<String> receiptTypes) {
        return receiptChequeRepository.findByReceiptIdAndTypes(receiptId, receiptTypes);
    }

    public ReceiptCheque findByNumberAndType(String checkNumber, String receiptType) {
        return receiptChequeRepository.findByNumberAndType(checkNumber, receiptType);
    }

    public void save(ReceiptCheque cheque) {
        receiptChequeRepository.insert(cheque);
    }

    public void deleteByReceipt(int receiptId) {
        receiptChequeRepository.deleteByReceiptId(receiptId);
    }
}

