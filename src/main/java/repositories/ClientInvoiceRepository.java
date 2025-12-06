package repositories;

import java.util.List;
import models.ClientInvoice;

public interface ClientInvoiceRepository {
    ClientInvoice findById(int id);
    List<ClientInvoice> findAll();
    List<ClientInvoice> findByClientId(int clientId);
    void insert(ClientInvoice invoice);
    void update(ClientInvoice invoice);
    void delete(int id);
    ClientInvoice findByPointOfSaleAndNumber(String pointOfSale, String invoiceNumber, String invoiceType);
    ClientInvoice findByPointOfSaleAndNumberSuffix(String pointOfSale, String pointOfSaleSuffix,
            String numberSuffix, String invoiceType, Integer clientId);
}
