package repositories.impl;

import java.util.Date;
import java.util.List;
import mappers.ProviderExpenseMapper;
import models.ProviderInvoice;
import repositories.ProviderExpenseRepository;

public class ProviderExpenseRepositoryImpl implements ProviderExpenseRepository {

    private final ProviderExpenseMapper providerExpenseMapper;

    public ProviderExpenseRepositoryImpl(ProviderExpenseMapper providerExpenseMapper) {
        this.providerExpenseMapper = providerExpenseMapper;
    }

    @Override
    public List<String> findCategories() {
        return providerExpenseMapper.findCategories();
    }

    @Override
    public List<ProviderInvoice> findByDateRangeAndCategory(Date start, Date end, int category) {
        return providerExpenseMapper.findByDateRangeAndCategory(start, end, category);
    }
}

