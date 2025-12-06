package repositories.impl;

import java.util.List;
import mappers.ClientBudgetMapper;
import models.ClientBudget;
import repositories.ClientBudgetRepository;

public class ClientBudgetRepositoryImpl implements ClientBudgetRepository {

    private final ClientBudgetMapper clientBudgetMapper;

    public ClientBudgetRepositoryImpl(ClientBudgetMapper clientBudgetMapper) {
        this.clientBudgetMapper = clientBudgetMapper;
    }

    @Override
    public ClientBudget findById(int id) {
        return clientBudgetMapper.findById(id);
    }

    @Override
    public List<ClientBudget> findAll() {
        return clientBudgetMapper.findAll();
    }

    @Override
    public List<ClientBudget> findOpen() {
        return clientBudgetMapper.findOpen();
    }

    @Override
    public List<ClientBudget> findClosed() {
        return clientBudgetMapper.findClosed();
    }

    @Override
    public List<ClientBudget> findByClientId(int clientId) {
        return clientBudgetMapper.findByClientId(clientId);
    }

    @Override
    public void insert(ClientBudget budget) {
        clientBudgetMapper.insert(budget);
    }

    @Override
    public void update(ClientBudget budget) {
        clientBudgetMapper.update(budget);
    }

    @Override
    public void delete(int id) {
        clientBudgetMapper.delete(id);
    }
}
