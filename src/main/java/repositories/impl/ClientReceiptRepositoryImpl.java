package repositories.impl;

import java.util.List;
import mappers.receipts.ClientReceiptMapper;
import models.receipts.ClientReceipt;
import repositories.ClientReceiptRepository;

public class ClientReceiptRepositoryImpl implements ClientReceiptRepository {

    private final ClientReceiptMapper clientReceiptMapper;

    public ClientReceiptRepositoryImpl(ClientReceiptMapper clientReceiptMapper) {
        this.clientReceiptMapper = clientReceiptMapper;
    }

    @Override
    public ClientReceipt findById(int id) {
        return clientReceiptMapper.findById(id);
    }

    @Override
    public List<ClientReceipt> findAll() {
        return clientReceiptMapper.findAll();
    }

    @Override
    public List<ClientReceipt> findByClientId(int clientId) {
        return clientReceiptMapper.findByClientId(clientId);
    }

    @Override
    public void insert(ClientReceipt receipt) {
        clientReceiptMapper.insert(receipt);
    }

    @Override
    public void update(ClientReceipt receipt) {
        clientReceiptMapper.update(receipt);
    }

    @Override
    public void delete(int id) {
        clientReceiptMapper.delete(id);
    }
}
