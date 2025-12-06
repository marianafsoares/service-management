package services;

import java.util.List;
import java.util.stream.Collectors;
import models.InvoiceCategory;
import repositories.InvoiceCategoryRepository;

public class InvoiceCategoryService {

    private final InvoiceCategoryRepository invoiceCategoryRepository;

    public InvoiceCategoryService(InvoiceCategoryRepository invoiceCategoryRepository) {
        this.invoiceCategoryRepository = invoiceCategoryRepository;
    }

    public List<InvoiceCategory> findAll() {
        return invoiceCategoryRepository.findAll();
    }

    public InvoiceCategory findById(int id) {
        return invoiceCategoryRepository.findById(id);
    }

    public void create(InvoiceCategory category) {
        validateUnique(category);
        invoiceCategoryRepository.insert(category);
    }

    public void update(InvoiceCategory category) {
        validateUnique(category);
        invoiceCategoryRepository.update(category);
    }

    public void delete(int id) {
        invoiceCategoryRepository.delete(id);
    }

    public List<InvoiceCategory> findProviderCategories() {
        return findAll().stream()
                .filter(this::isEnabled)
                .filter(c -> appliesToType(c.getType(), "PROVIDER"))
                .collect(Collectors.toList());
    }

    public List<InvoiceCategory> findClientCategories() {
        return findAll().stream()
                .filter(this::isEnabled)
                .filter(c -> appliesToType(c.getType(), "CLIENT"))
                .collect(Collectors.toList());
    }

    private boolean isEnabled(InvoiceCategory category) {
        return category != null && (category.getEnabled() == null || Boolean.TRUE.equals(category.getEnabled()));
    }

    private boolean appliesToType(String categoryType, String expectedType) {
        if (categoryType == null || categoryType.isBlank()) {
            return true;
        }
        return expectedType.equalsIgnoreCase(categoryType);
    }

    private void validateUnique(InvoiceCategory category) {
        InvoiceCategory existing = invoiceCategoryRepository.findByDescriptionAndType(
                category.getDescription(), category.getType());
        if (existing != null && (category.getId() == null || existing.getId() != category.getId())) {
            throw new IllegalArgumentException("Ya existe una categoría de facturas con ese nombre para el tipo seleccionado.");
        }
    }
}
