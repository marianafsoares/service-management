package repositories;

import java.util.List;
import models.receipts.ReceiptCard;

public interface ReceiptCardRepository {
    List<ReceiptCard> findByReceiptIdAndType(int receiptId, String receiptType);
    List<ReceiptCard> findByReceiptIdAndTypes(int receiptId, List<String> receiptTypes);
    void insert(ReceiptCard card);
    void deleteByReceiptId(int receiptId);
}

