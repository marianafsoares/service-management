package services;

import java.util.List;
import models.InvoiceType;
import repositories.InvoiceTypeRepository;

public class InvoiceTypeService {

    private final InvoiceTypeRepository invoiceTypeRepository;

    public InvoiceTypeService(InvoiceTypeRepository invoiceTypeRepository) {
        this.invoiceTypeRepository = invoiceTypeRepository;
    }

    public List<InvoiceType> findAll() {
        return invoiceTypeRepository.findAll();
    }
}
