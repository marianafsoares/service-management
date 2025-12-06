package controllers;

import java.util.List;
import models.ClientPayment;
import services.ClientPaymentService;

public class ClientPaymentController {

    private final ClientPaymentService clientPaymentService;

    public ClientPaymentController(ClientPaymentService clientPaymentService) {
        this.clientPaymentService = clientPaymentService;
    }

    public List<ClientPayment> findAll() {
        return clientPaymentService.findAll();
    }

    public ClientPayment findById(int id) {
        return clientPaymentService.findById(id);
    }
}

