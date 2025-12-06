package mappers;

import models.ClientInvoiceDetail;
import java.util.List;

public interface ClientInvoiceDetailMapper {
    List<ClientInvoiceDetail> findByInvoiceId(int invoiceId);
    void insert(ClientInvoiceDetail detail);
    void deleteByInvoiceId(int invoiceId);
}

