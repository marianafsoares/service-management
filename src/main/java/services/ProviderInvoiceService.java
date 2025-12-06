package services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import utils.Constants;
import utils.InvoiceTypeUtils;
import models.ProviderInvoice;
import repositories.ProviderInvoiceRepository;

public class ProviderInvoiceService {

    private final ProviderInvoiceRepository providerInvoiceRepository;

    private static final Set<String> NON_ACCOUNTING_TYPES = Set.of(
            Constants.RECIBO_COBRO,
            Constants.AJUSTE,
            Constants.PRESUPUESTO,
            Constants.NOTA_DEVOLUCION
    );

    public ProviderInvoiceService(ProviderInvoiceRepository providerInvoiceRepository) {
        this.providerInvoiceRepository = providerInvoiceRepository;
    }

    public ProviderInvoice findById(int id) {
        return providerInvoiceRepository.findById(id);
    }

    public List<ProviderInvoice> findAll() {
        return providerInvoiceRepository.findAll().stream()
                .filter(inv -> !NON_ACCOUNTING_TYPES.contains(InvoiceTypeUtils.toDisplayValue(inv.getInvoiceType())))
                .collect(Collectors.toList());
    }

    public List<ProviderInvoice> findByProvider(int providerId) {
        return providerInvoiceRepository.findByProviderId(providerId).stream()
                .filter(inv -> !NON_ACCOUNTING_TYPES.contains(InvoiceTypeUtils.toDisplayValue(inv.getInvoiceType())))
                .collect(Collectors.toList());
    }

    public void save(ProviderInvoice invoice) {
        providerInvoiceRepository.insert(invoice);
    }

    public void update(ProviderInvoice invoice) {
        providerInvoiceRepository.update(invoice);
    }

    public void delete(int id) {
        providerInvoiceRepository.delete(id);
    }
}
