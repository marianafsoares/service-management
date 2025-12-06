package repositories;

import java.util.List;
import models.ProviderPayment;

public interface ProviderPaymentRepository {
    ProviderPayment findById(int id);
    List<ProviderPayment> findAll();
}

