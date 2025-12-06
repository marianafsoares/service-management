package repositories.impl;

import java.util.List;
import mappers.ClientPaymentMapper;
import models.ClientPayment;
import repositories.ClientPaymentRepository;

public class ClientPaymentRepositoryImpl implements ClientPaymentRepository {

    private final ClientPaymentMapper clientPaymentMapper;

    public ClientPaymentRepositoryImpl(ClientPaymentMapper clientPaymentMapper) {
        this.clientPaymentMapper = clientPaymentMapper;
    }

    @Override
    public ClientPayment findById(int id) {
        return clientPaymentMapper.findById(id);
    }

    @Override
    public List<ClientPayment> findAll() {
        return clientPaymentMapper.findAll();
    }
}

