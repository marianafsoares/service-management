package services;

import java.util.Date;
import java.util.List;
import models.ProviderInvoice;
import repositories.ProviderExpenseRepository;
import services.reports.ReportExpenses;

/**
 * Business logic for provider expense reports.
 */
public class ProviderExpenseService {

    private final ProviderExpenseRepository providerExpenseRepository;

    public ProviderExpenseService(ProviderExpenseRepository providerExpenseRepository) {
        this.providerExpenseRepository = providerExpenseRepository;
    }

    public List<String> findCategories() {
        return providerExpenseRepository.findCategories();
    }

    public void process(Date start, Date end, int category) throws Exception {
        List<ProviderInvoice> invoices = providerExpenseRepository.findByDateRangeAndCategory(start, end, category);
        new ReportExpenses().print(invoices, String.valueOf(category));
    }
}

