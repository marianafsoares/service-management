package mappers.receipts;

import java.util.List;
import models.receipts.ReceiptCheque;
import org.apache.ibatis.annotations.Param;

public interface ReceiptChequeMapper {

    ReceiptCheque findByNumberAndType(@Param("checkNumber") String checkNumber,
                                      @Param("receiptType") String receiptType);

    List<ReceiptCheque> findByReceiptIdAndType(@Param("receiptId") int receiptId,
                                               @Param("receiptType") String receiptType);

    List<ReceiptCheque> findByReceiptIdAndTypes(@Param("receiptId") int receiptId,
                                                @Param("receiptTypes") List<String> receiptTypes);

    void insert(ReceiptCheque cheque);

    void deleteByReceiptId(@Param("receiptId") int receiptId);
}
