package controllers;

import java.util.List;
import models.ProviderInvoice;
import services.ProviderInvoiceService;

public class ProviderInvoiceController {

    private final ProviderInvoiceService providerInvoiceService;

    public ProviderInvoiceController(ProviderInvoiceService providerInvoiceService) {
        this.providerInvoiceService = providerInvoiceService;
    }

    public ProviderInvoice findById(int id) {
        return providerInvoiceService.findById(id);
    }

    public List<ProviderInvoice> findByProvider(int providerId) {
        return providerInvoiceService.findByProvider(providerId);
    }

    public List<ProviderInvoice> findAll() {
        return providerInvoiceService.findAll();
    }

    public void save(ProviderInvoice invoice) {
        providerInvoiceService.save(invoice);
    }

    public void update(ProviderInvoice invoice) {
        providerInvoiceService.update(invoice);
    }

    public void delete(int id) {
        providerInvoiceService.delete(id);
    }
}
