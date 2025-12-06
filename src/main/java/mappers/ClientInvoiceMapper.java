package mappers;

import java.util.List;
import models.ClientInvoice;
import org.apache.ibatis.annotations.Param;

public interface ClientInvoiceMapper {
    ClientInvoice findById(int id);
    List<ClientInvoice> findAll();
    List<ClientInvoice> findByClientId(int clientId);
    void insert(ClientInvoice invoice);
    void update(ClientInvoice invoice);
    void delete(int id);
    ClientInvoice findByPointOfSaleAndNumber(@Param("pointOfSale") String pointOfSale,
            @Param("invoiceNumber") String invoiceNumber,
            @Param("invoiceType") String invoiceType);
    ClientInvoice findByPointOfSaleAndNumberSuffix(@Param("pointOfSale") String pointOfSale,
            @Param("pointOfSaleSuffix") String pointOfSaleSuffix,
            @Param("numberSuffix") String numberSuffix,
            @Param("invoiceType") String invoiceType,
            @Param("clientId") Integer clientId);
}

