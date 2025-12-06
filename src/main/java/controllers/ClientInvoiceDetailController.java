package controllers;

import java.util.List;
import models.ClientInvoiceDetail;
import services.ClientInvoiceDetailService;

public class ClientInvoiceDetailController {

    private final ClientInvoiceDetailService clientInvoiceDetailService;

    public ClientInvoiceDetailController(ClientInvoiceDetailService clientInvoiceDetailService) {
        this.clientInvoiceDetailService = clientInvoiceDetailService;
    }

    public List<ClientInvoiceDetail> findByInvoice(int invoiceId) {
        return clientInvoiceDetailService.findByInvoiceId(invoiceId);
    }

    public void save(ClientInvoiceDetail detail) {
        clientInvoiceDetailService.save(detail);
    }

    public void deleteByInvoice(int invoiceId) {
        clientInvoiceDetailService.deleteByInvoiceId(invoiceId);
    }
}

