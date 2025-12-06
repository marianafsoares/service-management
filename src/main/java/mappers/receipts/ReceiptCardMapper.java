package mappers.receipts;

import java.util.List;
import models.receipts.ReceiptCard;
import org.apache.ibatis.annotations.Param;

public interface ReceiptCardMapper {

    List<ReceiptCard> findByReceiptIdAndType(@Param("receiptId") int receiptId,
                                             @Param("receiptType") String receiptType);

    List<ReceiptCard> findByReceiptIdAndTypes(@Param("receiptId") int receiptId,
                                              @Param("receiptTypes") List<String> receiptTypes);

    void insert(ReceiptCard receiptCard);

    void deleteByReceiptId(@Param("receiptId") int receiptId);
}
