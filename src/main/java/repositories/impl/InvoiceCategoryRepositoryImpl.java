package repositories.impl;

import java.util.List;
import mappers.InvoiceCategoryMapper;
import models.InvoiceCategory;
import repositories.InvoiceCategoryRepository;

public class InvoiceCategoryRepositoryImpl implements InvoiceCategoryRepository {

    private final InvoiceCategoryMapper invoiceCategoryMapper;

    public InvoiceCategoryRepositoryImpl(InvoiceCategoryMapper invoiceCategoryMapper) {
        this.invoiceCategoryMapper = invoiceCategoryMapper;
    }

    @Override
    public InvoiceCategory findById(int id) {
        return invoiceCategoryMapper.findById(id);
    }

    @Override
    public InvoiceCategory findByDescriptionAndType(String description, String type) {
        return invoiceCategoryMapper.findByDescriptionAndType(description, type);
    }

    @Override
    public List<InvoiceCategory> findAll() {
        return invoiceCategoryMapper.findAll();
    }

    @Override
    public void insert(InvoiceCategory category) {
        invoiceCategoryMapper.insert(category);
    }

    @Override
    public void update(InvoiceCategory category) {
        invoiceCategoryMapper.update(category);
    }

    @Override
    public void delete(int id) {
        invoiceCategoryMapper.delete(id);
    }
}
