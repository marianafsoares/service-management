package repositories.impl;

import java.util.Date;
import java.util.List;
import mappers.VatBookMapper;
import models.ClientInvoice;
import models.ProviderInvoice;
import repositories.VatBookRepository;

public class VatBookRepositoryImpl implements VatBookRepository {

    private final VatBookMapper vatBookMapper;

    public VatBookRepositoryImpl(VatBookMapper vatBookMapper) {
        this.vatBookMapper = vatBookMapper;
    }

    @Override
    public List<ClientInvoice> findSalesBetween(Date start, Date end, String issuerCuit, List<String> types) {
        return vatBookMapper.findSalesBetween(start, end, issuerCuit, types);
    }

    @Override
    public List<ProviderInvoice> findPurchasesBetween(Date start, Date end, String issuerCuit, List<String> types) {
        return vatBookMapper.findPurchasesBetween(start, end, issuerCuit, types);
    }
}

