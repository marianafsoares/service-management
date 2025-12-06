package controllers;

import java.util.List;
import models.receipts.ProviderReceipt;
import services.ProviderReceiptService;

public class ProviderReceiptController {

    private final ProviderReceiptService providerReceiptService;

    public ProviderReceiptController(ProviderReceiptService providerReceiptService) {
        this.providerReceiptService = providerReceiptService;
    }

    public ProviderReceipt findById(int id) {
        return providerReceiptService.findById(id);
    }

    public List<ProviderReceipt> findAll() {
        return providerReceiptService.findAll();
    }

    public List<ProviderReceipt> findByProvider(int providerId) {
        return providerReceiptService.findByProvider(providerId);
    }

    public void save(ProviderReceipt receipt) {
        providerReceiptService.save(receipt);
    }

    public void delete(int id) {
        providerReceiptService.delete(id);
    }
}
