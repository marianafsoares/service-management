package mappers;

import java.util.List;
import models.ClientPayment;

public interface ClientPaymentMapper {
    ClientPayment findById(int id);
    List<ClientPayment> findAll();
}

