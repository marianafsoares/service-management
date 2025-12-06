package mappers.receipts;

import java.util.List;
import models.receipts.ReceiptTransfer;
import org.apache.ibatis.annotations.Param;

public interface ReceiptTransferMapper {

    List<ReceiptTransfer> findByReceiptIdAndType(@Param("receiptId") int receiptId,
                                                 @Param("receiptType") String receiptType);

    List<ReceiptTransfer> findByReceiptIdAndTypes(@Param("receiptId") int receiptId,
                                                  @Param("receiptTypes") List<String> receiptTypes);

    void insert(ReceiptTransfer receiptTransfer);

    void deleteByReceiptId(@Param("receiptId") int receiptId);
}
