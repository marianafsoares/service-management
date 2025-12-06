package controllers;

import java.math.BigDecimal;
import java.util.List;
import javax.swing.JTable;
import models.Provider;
import services.ProviderService;

public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    public BigDecimal fillTable(JTable table, String value) {
        return providerService.fillTable(table, value);
    }

    public Provider findById(int id) {
        return providerService.findById(id);
    }

    public Provider findByDocument(String documentNumber) {
        return providerService.findByDocument(documentNumber);
    }

    public void update(Provider provider) {
        providerService.update(provider);
    }

    public void save(Provider provider) {
        providerService.save(provider);
    }

    public List<Provider> findAll() {
        return providerService.findAll();
    }

    public void delete(int id) {
        providerService.delete(id);
    }

    public boolean hasAssociations(int id) {
        return providerService.hasAssociations(id);
    }

    public String formatCurrency(Object value) {
        return providerService.formatCurrency(value);
    }
}

