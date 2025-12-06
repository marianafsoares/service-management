package repositories;

import java.util.List;
import models.receipts.ReceiptCash;

public interface ReceiptCashRepository {
    List<ReceiptCash> findByReceiptIdAndType(int receiptId, String receiptType);
    List<ReceiptCash> findByReceiptIdAndTypes(int receiptId, List<String> receiptTypes);
    void insert(ReceiptCash cash);
    void deleteByReceiptId(int receiptId);
}

