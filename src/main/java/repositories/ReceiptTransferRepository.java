package repositories;

import java.util.List;
import models.receipts.ReceiptTransfer;

public interface ReceiptTransferRepository {

    List<ReceiptTransfer> findByReceiptIdAndType(int receiptId, String receiptType);

    List<ReceiptTransfer> findByReceiptIdAndTypes(int receiptId, List<String> receiptTypes);

    void insert(ReceiptTransfer transfer);

    void deleteByReceiptId(int receiptId);
}
