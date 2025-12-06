package repositories;

import java.util.List;
import models.receipts.ReceiptCheque;

public interface ReceiptChequeRepository {

    List<ReceiptCheque> findByReceiptIdAndType(int receiptId, String receiptType);

    List<ReceiptCheque> findByReceiptIdAndTypes(int receiptId, List<String> receiptTypes);

    ReceiptCheque findByNumberAndType(String checkNumber, String receiptType);

    void insert(ReceiptCheque cheque);

    void deleteByReceiptId(int receiptId);
}

