package repositories;

import java.util.List;
import models.InvoiceType;

public interface InvoiceTypeRepository {
    List<InvoiceType> findAll();
}
