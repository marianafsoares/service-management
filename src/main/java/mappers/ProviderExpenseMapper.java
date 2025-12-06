package mappers;

import java.util.Date;
import java.util.List;
import models.ProviderInvoice;
import org.apache.ibatis.annotations.Param;

public interface ProviderExpenseMapper {
    List<String> findCategories();
    List<ProviderInvoice> findByDateRangeAndCategory(@Param("start") Date start,
                                                     @Param("end") Date end,
                                                     @Param("category") int category);
}

