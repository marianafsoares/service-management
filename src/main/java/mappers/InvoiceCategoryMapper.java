package mappers;

import java.util.List;
import models.InvoiceCategory;
import org.apache.ibatis.annotations.Param;

public interface InvoiceCategoryMapper {
    InvoiceCategory findById(int id);
    InvoiceCategory findByDescriptionAndType(@Param("description") String description,
                                             @Param("type") String type);
    List<InvoiceCategory> findAll();
    void insert(InvoiceCategory category);
    void update(InvoiceCategory category);
    void delete(int id);
}
