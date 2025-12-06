package mappers.receipts;

import java.util.List;
import models.receipts.ReceiptRetention;
import org.apache.ibatis.annotations.Param;

public interface ReceiptRetentionMapper {

    List<ReceiptRetention> findByReceiptIdAndType(@Param("receiptId") int receiptId,
                                                  @Param("receiptType") String receiptType);

    List<ReceiptRetention> findByReceiptIdAndTypes(@Param("receiptId") int receiptId,
                                                   @Param("receiptTypes") List<String> receiptTypes);

    void insert(ReceiptRetention retention);

    void deleteByReceiptId(@Param("receiptId") int receiptId);
}
