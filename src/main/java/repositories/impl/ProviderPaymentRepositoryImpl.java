package repositories.impl;

import java.util.List;
import mappers.ProviderPaymentMapper;
import models.ProviderPayment;
import repositories.ProviderPaymentRepository;

public class ProviderPaymentRepositoryImpl implements ProviderPaymentRepository {

    private final ProviderPaymentMapper providerPaymentMapper;

    public ProviderPaymentRepositoryImpl(ProviderPaymentMapper providerPaymentMapper) {
        this.providerPaymentMapper = providerPaymentMapper;
    }

    @Override
    public ProviderPayment findById(int id) {
        return providerPaymentMapper.findById(id);
    }

    @Override
    public List<ProviderPayment> findAll() {
        return providerPaymentMapper.findAll();
    }
}

