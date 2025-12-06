package repositories;

import java.util.List;
import models.ClientPayment;

public interface ClientPaymentRepository {
    ClientPayment findById(int id);
    List<ClientPayment> findAll();
}

