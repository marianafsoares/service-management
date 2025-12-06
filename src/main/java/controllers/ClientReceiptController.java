package controllers;

import java.util.List;
import models.receipts.ClientReceipt;
import services.ClientReceiptService;

public class ClientReceiptController {

    private final ClientReceiptService clientReceiptService;

    public ClientReceiptController(ClientReceiptService clientReceiptService) {
        this.clientReceiptService = clientReceiptService;
    }

    public ClientReceipt findById(int id) {
        return clientReceiptService.findById(id);
    }

    public List<ClientReceipt> findAll() {
        return clientReceiptService.findAll();
    }

    public List<ClientReceipt> findByClient(int clientId) {
        return clientReceiptService.findByClientId(clientId);
    }

    public void save(ClientReceipt receipt) {
        clientReceiptService.save(receipt);
    }

    public void delete(int id) {
        clientReceiptService.delete(id);
    }
}
