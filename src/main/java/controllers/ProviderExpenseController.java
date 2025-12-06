package controllers;

import java.util.Date;
import java.util.List;
import services.ProviderExpenseService;

public class ProviderExpenseController {

    private final ProviderExpenseService providerExpenseService;

    public ProviderExpenseController(ProviderExpenseService providerExpenseService) {
        this.providerExpenseService = providerExpenseService;
    }

    public List<String> findCategories() {
        return providerExpenseService.findCategories();
    }

    public void process(Date start, Date end, int category) throws Exception {
        providerExpenseService.process(start, end, category);
    }
}

