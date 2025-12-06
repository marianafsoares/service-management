package repositories;

import java.util.List;
import models.ProviderInvoice;

public interface ProviderInvoiceRepository {
    ProviderInvoice findById(int id);
    List<ProviderInvoice> findAll();
    List<ProviderInvoice> findByProviderId(int providerId);
    void insert(ProviderInvoice invoice);
    void update(ProviderInvoice invoice);
    void delete(int id);
}
