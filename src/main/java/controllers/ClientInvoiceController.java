package controllers;

import java.util.List;
import models.ClientInvoice;
import services.ClientInvoiceService;

public class ClientInvoiceController {

    private final ClientInvoiceService clientInvoiceService;

    public ClientInvoiceController(ClientInvoiceService clientInvoiceService) {
        this.clientInvoiceService = clientInvoiceService;
    }

    public ClientInvoice findById(int id) {
        return clientInvoiceService.findById(id);
    }

    public List<ClientInvoice> findByClient(int clientId) {
        return clientInvoiceService.findByClient(clientId);
    }

    public List<ClientInvoice> findAll() {
        return clientInvoiceService.findAll();
    }

    public void save(ClientInvoice invoice) {
        clientInvoiceService.save(invoice);
    }

    public void update(ClientInvoice invoice) {
        clientInvoiceService.update(invoice);
    }

    public void delete(int id) {
        clientInvoiceService.delete(id);
    }

    public ClientInvoice findByPointOfSaleAndNumber(String pointOfSale, String invoiceNumber, String invoiceType) {
        return clientInvoiceService.findByPointOfSaleAndNumber(pointOfSale, invoiceNumber, invoiceType);
    }

    public ClientInvoice findByPointOfSaleAndNumberSuffix(String pointOfSale, String numberSuffix,
            String invoiceType, Integer clientId) {
        return clientInvoiceService.findByPointOfSaleAndNumberSuffix(pointOfSale, numberSuffix, invoiceType, clientId);
    }
}
