package repositories.impl;

import java.util.List;
import mappers.ClientBudgetDetailMapper;
import models.ClientBudgetDetail;
import repositories.ClientBudgetDetailRepository;

public class ClientBudgetDetailRepositoryImpl implements ClientBudgetDetailRepository {

    private final ClientBudgetDetailMapper clientBudgetDetailMapper;

    public ClientBudgetDetailRepositoryImpl(ClientBudgetDetailMapper clientBudgetDetailMapper) {
        this.clientBudgetDetailMapper = clientBudgetDetailMapper;
    }

    @Override
    public List<ClientBudgetDetail> findByBudgetId(int budgetId) {
        return clientBudgetDetailMapper.findByBudgetId(budgetId);
    }

    @Override
    public void insert(ClientBudgetDetail detail) {
        clientBudgetDetailMapper.insert(detail);
    }

    @Override
    public void deleteByBudgetId(int budgetId) {
        clientBudgetDetailMapper.deleteByBudgetId(budgetId);
    }
}
