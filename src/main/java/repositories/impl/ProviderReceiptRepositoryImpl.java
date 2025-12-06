package repositories.impl;

import java.util.List;
import mappers.receipts.ProviderReceiptMapper;
import models.receipts.ProviderReceipt;
import repositories.ProviderReceiptRepository;

public class ProviderReceiptRepositoryImpl implements ProviderReceiptRepository {

    private final ProviderReceiptMapper providerReceiptMapper;

    public ProviderReceiptRepositoryImpl(ProviderReceiptMapper providerReceiptMapper) {
        this.providerReceiptMapper = providerReceiptMapper;
    }

    @Override
    public ProviderReceipt findById(int id) {
        return providerReceiptMapper.findById(id);
    }

    @Override
    public List<ProviderReceipt> findAll() {
        return providerReceiptMapper.findAll();
    }

    @Override
    public List<ProviderReceipt> findByProviderId(int providerId) {
        return providerReceiptMapper.findByProviderId(providerId);
    }

    @Override
    public void insert(ProviderReceipt receipt) {
        providerReceiptMapper.insert(receipt);
    }

    @Override
    public void update(ProviderReceipt receipt) {
        providerReceiptMapper.update(receipt);
    }

    @Override
    public void delete(int id) {
        providerReceiptMapper.delete(id);
    }
}
