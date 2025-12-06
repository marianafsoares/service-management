package repositories.impl;

import java.util.List;
import mappers.ProviderInvoiceMapper;
import models.ProviderInvoice;
import repositories.ProviderInvoiceRepository;

public class ProviderInvoiceRepositoryImpl implements ProviderInvoiceRepository {

    private final ProviderInvoiceMapper providerInvoiceMapper;

    public ProviderInvoiceRepositoryImpl(ProviderInvoiceMapper providerInvoiceMapper) {
        this.providerInvoiceMapper = providerInvoiceMapper;
    }

    @Override
    public ProviderInvoice findById(int id) {
        return providerInvoiceMapper.findById(id);
    }

    @Override
    public List<ProviderInvoice> findAll() {
        return providerInvoiceMapper.findAll();
    }

    @Override
    public List<ProviderInvoice> findByProviderId(int providerId) {
        return providerInvoiceMapper.findByProviderId(providerId);
    }

    @Override
    public void insert(ProviderInvoice invoice) {
        providerInvoiceMapper.insert(invoice);
    }

    @Override
    public void update(ProviderInvoice invoice) {
        providerInvoiceMapper.update(invoice);
    }

    @Override
    public void delete(int id) {
        providerInvoiceMapper.delete(id);
    }
}
