
package mappers.receipts;

import models.receipts.ClientReceipt;
import java.util.List;

public interface ClientReceiptMapper {

    ClientReceipt findById(int id);
    List<ClientReceipt> findAll();
    List<ClientReceipt> findByClientId(int clientId);
    void insert(ClientReceipt receipt);
    void update(ClientReceipt receipt);
    void delete(int id);
}
