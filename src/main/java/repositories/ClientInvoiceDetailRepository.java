package repositories;

import java.util.List;
import models.ClientInvoiceDetail;

public interface ClientInvoiceDetailRepository {
    List<ClientInvoiceDetail> findByInvoiceId(int invoiceId);
    void insert(ClientInvoiceDetail detail);
    void deleteByInvoiceId(int invoiceId);
}

