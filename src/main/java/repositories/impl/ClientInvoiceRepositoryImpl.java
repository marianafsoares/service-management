package repositories.impl;

import java.util.List;
import mappers.ClientInvoiceMapper;
import models.ClientInvoice;
import repositories.ClientInvoiceRepository;

public class ClientInvoiceRepositoryImpl implements ClientInvoiceRepository {

    private final ClientInvoiceMapper clientInvoiceMapper;

    public ClientInvoiceRepositoryImpl(ClientInvoiceMapper clientInvoiceMapper) {
        this.clientInvoiceMapper = clientInvoiceMapper;
    }

    @Override
    public ClientInvoice findById(int id) {
        return clientInvoiceMapper.findById(id);
    }

    @Override
    public List<ClientInvoice> findAll() {
        return clientInvoiceMapper.findAll();
    }

    @Override
    public List<ClientInvoice> findByClientId(int clientId) {
        return clientInvoiceMapper.findByClientId(clientId);
    }

    @Override
    public void insert(ClientInvoice invoice) {
        clientInvoiceMapper.insert(invoice);
    }

    @Override
    public void update(ClientInvoice invoice) {
        clientInvoiceMapper.update(invoice);
    }

    @Override
    public void delete(int id) {
        clientInvoiceMapper.delete(id);
    }

    @Override
    public ClientInvoice findByPointOfSaleAndNumber(String pointOfSale, String invoiceNumber, String invoiceType) {
        return clientInvoiceMapper.findByPointOfSaleAndNumber(pointOfSale, invoiceNumber, invoiceType);
    }

    @Override
    public ClientInvoice findByPointOfSaleAndNumberSuffix(String pointOfSale, String pointOfSaleSuffix,
            String numberSuffix, String invoiceType, Integer clientId) {
        return clientInvoiceMapper.findByPointOfSaleAndNumberSuffix(pointOfSale, pointOfSaleSuffix,
                numberSuffix, invoiceType, clientId);
    }
}
