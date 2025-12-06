package mappers;

import java.util.List;
import models.ProviderPayment;

public interface ProviderPaymentMapper {
    ProviderPayment findById(int id);
    List<ProviderPayment> findAll();
}

