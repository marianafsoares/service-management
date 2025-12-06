package mappers;

import java.util.List;
import models.ClientBudget;

public interface ClientBudgetMapper {
    ClientBudget findById(int id);
    List<ClientBudget> findAll();
    List<ClientBudget> findOpen();
    List<ClientBudget> findClosed();
    List<ClientBudget> findByClientId(int clientId);
    void insert(ClientBudget budget);
    void update(ClientBudget budget);
    void delete(int id);
}
