package repositories.impl;

import java.util.List;
import mappers.ClientInvoiceDetailMapper;
import models.ClientInvoiceDetail;
import repositories.ClientInvoiceDetailRepository;

public class ClientInvoiceDetailRepositoryImpl implements ClientInvoiceDetailRepository {

    private final ClientInvoiceDetailMapper clientInvoiceDetailMapper;

    public ClientInvoiceDetailRepositoryImpl(ClientInvoiceDetailMapper clientInvoiceDetailMapper) {
        this.clientInvoiceDetailMapper = clientInvoiceDetailMapper;
    }

    @Override
    public List<ClientInvoiceDetail> findByInvoiceId(int invoiceId) {
        return clientInvoiceDetailMapper.findByInvoiceId(invoiceId);
    }

    @Override
    public void insert(ClientInvoiceDetail detail) {
        clientInvoiceDetailMapper.insert(detail);
    }

    @Override
    public void deleteByInvoiceId(int invoiceId) {
        clientInvoiceDetailMapper.deleteByInvoiceId(invoiceId);
    }
}

