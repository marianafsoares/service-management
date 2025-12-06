package mappers;

import java.util.Date;
import java.util.List;
import models.ClientInvoice;
import models.ProviderInvoice;
import org.apache.ibatis.annotations.Param;

public interface VatBookMapper {
    List<ClientInvoice> findSalesBetween(@Param("start") Date start,
                                         @Param("end") Date end,
                                         @Param("issuerCuit") String issuerCuit,
                                         @Param("types") List<String> types);
    List<ProviderInvoice> findPurchasesBetween(@Param("start") Date start,
                                               @Param("end") Date end,
                                               @Param("issuerCuit") String issuerCuit,
                                               @Param("types") List<String> types);
}

