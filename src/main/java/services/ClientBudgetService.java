package services;

import java.util.List;
import models.ClientBudget;
import repositories.ClientBudgetRepository;

public class ClientBudgetService {

    private final ClientBudgetRepository clientBudgetRepository;

    public ClientBudgetService(ClientBudgetRepository clientBudgetRepository) {
        this.clientBudgetRepository = clientBudgetRepository;
    }

    public ClientBudget findById(int id) {
        return clientBudgetRepository.findById(id);
    }

    public List<ClientBudget> findAll() {
        return clientBudgetRepository.findAll();
    }

    public List<ClientBudget> findOpen() {
        return clientBudgetRepository.findOpen();
    }

    public List<ClientBudget> findClosed() {
        return clientBudgetRepository.findClosed();
    }

    public List<ClientBudget> findByClient(int clientId) {
        return clientBudgetRepository.findByClientId(clientId);
    }

    public void save(ClientBudget budget) {
        clientBudgetRepository.insert(budget);
    }

    public void update(ClientBudget budget) {
        clientBudgetRepository.update(budget);
    }

    public void delete(int id) {
        clientBudgetRepository.delete(id);
    }
}
