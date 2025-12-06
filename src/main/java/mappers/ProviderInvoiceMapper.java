package mappers;

import models.ProviderInvoice;
import java.util.List;

public interface ProviderInvoiceMapper {
    ProviderInvoice findById(int id);
    List<ProviderInvoice> findAll();
    List<ProviderInvoice> findByProviderId(int providerId);
    void insert(ProviderInvoice invoice);
    void update(ProviderInvoice invoice);
    void delete(int id);
}
