package services;

import java.util.List;
import models.receipts.ProviderReceipt;
import repositories.ProviderReceiptRepository;

public class ProviderReceiptService {

    private final ProviderReceiptRepository providerReceiptRepository;

    public ProviderReceiptService(ProviderReceiptRepository providerReceiptRepository) {
        this.providerReceiptRepository = providerReceiptRepository;
    }

    public ProviderReceipt findById(int id) {
        return providerReceiptRepository.findById(id);
    }

    public List<ProviderReceipt> findAll() {
        return providerReceiptRepository.findAll();
    }

    public List<ProviderReceipt> findByProvider(int providerId) {
        return providerReceiptRepository.findByProviderId(providerId);
    }

    public void save(ProviderReceipt receipt) {
        if (receipt.getId() == null) {
            providerReceiptRepository.insert(receipt);
        } else {
            providerReceiptRepository.update(receipt);
        }
    }

    public void delete(int id) {
        providerReceiptRepository.delete(id);
    }
}
