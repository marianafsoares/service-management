package controllers;

import java.util.List;
import models.ProviderPayment;
import services.ProviderPaymentService;

public class ProviderPaymentController {

    private final ProviderPaymentService providerPaymentService;

    public ProviderPaymentController(ProviderPaymentService providerPaymentService) {
        this.providerPaymentService = providerPaymentService;
    }

    public List<ProviderPayment> findAll() {
        return providerPaymentService.findAll();
    }

    public ProviderPayment findById(int id) {
        return providerPaymentService.findById(id);
    }
}

