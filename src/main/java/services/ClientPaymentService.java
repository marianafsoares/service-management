package services;

import java.util.List;
import models.ClientPayment;
import repositories.ClientPaymentRepository;

public class ClientPaymentService {

    private final ClientPaymentRepository clientPaymentRepository;

    public ClientPaymentService(ClientPaymentRepository clientPaymentRepository) {
        this.clientPaymentRepository = clientPaymentRepository;
    }

    public List<ClientPayment> findAll() {
        return clientPaymentRepository.findAll();
    }

    public ClientPayment findById(int id) {
        return clientPaymentRepository.findById(id);
    }
}

