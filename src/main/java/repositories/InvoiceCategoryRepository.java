package repositories;

import java.util.List;
import models.InvoiceCategory;

public interface InvoiceCategoryRepository {
    InvoiceCategory findById(int id);
    InvoiceCategory findByDescriptionAndType(String description, String type);
    List<InvoiceCategory> findAll();
    void insert(InvoiceCategory category);
    void update(InvoiceCategory category);
    void delete(int id);
}
