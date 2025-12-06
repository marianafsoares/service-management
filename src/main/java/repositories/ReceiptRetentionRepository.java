package repositories;

import java.util.List;
import models.receipts.ReceiptRetention;

public interface ReceiptRetentionRepository {

    List<ReceiptRetention> findByReceiptIdAndType(int receiptId, String receiptType);

    List<ReceiptRetention> findByReceiptIdAndTypes(int receiptId, List<String> receiptTypes);

    void insert(ReceiptRetention retention);

    void deleteByReceiptId(int receiptId);
}
