package repositories;

import java.util.Date;
import java.util.List;
import models.ProviderInvoice;

public interface ProviderExpenseRepository {
    List<String> findCategories();
    List<ProviderInvoice> findByDateRangeAndCategory(Date start, Date end, int category);
}

