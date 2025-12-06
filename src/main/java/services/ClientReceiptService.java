package services;

import java.util.List;
import models.receipts.ClientReceipt;
import repositories.ClientReceiptRepository;

public class ClientReceiptService {

    private final ClientReceiptRepository clientReceiptRepository;

    public ClientReceiptService(ClientReceiptRepository clientReceiptRepository) {
        this.clientReceiptRepository = clientReceiptRepository;
    }

    public ClientReceipt findById(int id) {
        return clientReceiptRepository.findById(id);
    }

    public List<ClientReceipt> findAll() {
        return clientReceiptRepository.findAll();
    }

    public List<ClientReceipt> findByClientId(int clientId) {
        return clientReceiptRepository.findByClientId(clientId);
    }

    public void save(ClientReceipt receipt) {
        if (receipt.getId() == null) {
            clientReceiptRepository.insert(receipt);
        } else {
            clientReceiptRepository.update(receipt);
        }
    }

    public void delete(int id) {
        clientReceiptRepository.delete(id);
    }
}
