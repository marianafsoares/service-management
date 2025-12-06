package repositories;

import java.util.List;
import models.receipts.ProviderReceipt;

public interface ProviderReceiptRepository {
    ProviderReceipt findById(int id);
    List<ProviderReceipt> findAll();
    List<ProviderReceipt> findByProviderId(int providerId);
    void insert(ProviderReceipt receipt);
    void update(ProviderReceipt receipt);
    void delete(int id);
}
