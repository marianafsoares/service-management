package repositories;

import java.util.Date;
import java.util.List;
import models.ClientInvoice;
import models.ProviderInvoice;

public interface VatBookRepository {
    List<ClientInvoice> findSalesBetween(Date start, Date end, String issuerCuit, List<String> types);
    List<ProviderInvoice> findPurchasesBetween(Date start, Date end, String issuerCuit, List<String> types);
}

