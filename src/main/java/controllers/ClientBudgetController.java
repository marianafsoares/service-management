package controllers;

import java.util.List;
import models.ClientBudget;
import services.ClientBudgetService;

public class ClientBudgetController {

    private final ClientBudgetService clientBudgetService;

    public ClientBudgetController(ClientBudgetService clientBudgetService) {
        this.clientBudgetService = clientBudgetService;
    }

    public ClientBudget findById(int id) {
        return clientBudgetService.findById(id);
    }

    public List<ClientBudget> findOpen() {
        return clientBudgetService.findOpen();
    }

    public List<ClientBudget> findClosed() {
        return clientBudgetService.findClosed();
    }

    public List<ClientBudget> findByClient(int clientId) {
        return clientBudgetService.findByClient(clientId);
    }

    public List<ClientBudget> findAll() {
        return clientBudgetService.findAll();
    }

    public void save(ClientBudget budget) {
        clientBudgetService.save(budget);
    }

    public void update(ClientBudget budget) {
        clientBudgetService.update(budget);
    }

    public void delete(int id) {
        clientBudgetService.delete(id);
    }
}
