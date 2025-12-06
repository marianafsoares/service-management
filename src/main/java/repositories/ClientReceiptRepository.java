package repositories;

import java.util.List;
import models.receipts.ClientReceipt;

public interface ClientReceiptRepository {
    ClientReceipt findById(int id);
    List<ClientReceipt> findAll();
    List<ClientReceipt> findByClientId(int clientId);
    void insert(ClientReceipt receipt);
    void update(ClientReceipt receipt);
    void delete(int id);
}
