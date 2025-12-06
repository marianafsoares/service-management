package services;

import java.util.List;
import models.ProviderPayment;
import repositories.ProviderPaymentRepository;

public class ProviderPaymentService {

    private final ProviderPaymentRepository providerPaymentRepository;

    public ProviderPaymentService(ProviderPaymentRepository providerPaymentRepository) {
        this.providerPaymentRepository = providerPaymentRepository;
    }

    public List<ProviderPayment> findAll() {
        return providerPaymentRepository.findAll();
    }

    public ProviderPayment findById(int id) {
        return providerPaymentRepository.findById(id);
    }
}

