package services;

import java.util.List;
import models.ClientInvoiceDetail;
import repositories.ClientInvoiceDetailRepository;

public class ClientInvoiceDetailService {

    private final ClientInvoiceDetailRepository clientInvoiceDetailRepository;

    public ClientInvoiceDetailService(ClientInvoiceDetailRepository clientInvoiceDetailRepository) {
        this.clientInvoiceDetailRepository = clientInvoiceDetailRepository;
    }

    public List<ClientInvoiceDetail> findByInvoiceId(int invoiceId) {
        return clientInvoiceDetailRepository.findByInvoiceId(invoiceId);
    }

    public void save(ClientInvoiceDetail detail) {
        clientInvoiceDetailRepository.insert(detail);
    }

    public void deleteByInvoiceId(int invoiceId) {
        clientInvoiceDetailRepository.deleteByInvoiceId(invoiceId);
    }
}

