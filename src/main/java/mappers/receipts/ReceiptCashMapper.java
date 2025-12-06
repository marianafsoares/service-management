package mappers.receipts;

import java.util.List;
import models.receipts.ReceiptCash;
import org.apache.ibatis.annotations.Param;

public interface ReceiptCashMapper {

    List<ReceiptCash> findByReceiptIdAndType(@Param("receiptId") int receiptId,
                                             @Param("receiptType") String receiptType);

    List<ReceiptCash> findByReceiptIdAndTypes(@Param("receiptId") int receiptId,
                                              @Param("receiptTypes") List<String> receiptTypes);

    void insert(ReceiptCash receiptCash);

    void deleteByReceiptId(@Param("receiptId") int receiptId);
}
