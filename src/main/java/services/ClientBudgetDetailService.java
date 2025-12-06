package services;

import java.util.List;
import models.ClientBudgetDetail;
import repositories.ClientBudgetDetailRepository;

public class ClientBudgetDetailService {

    private final ClientBudgetDetailRepository clientBudgetDetailRepository;

    public ClientBudgetDetailService(ClientBudgetDetailRepository clientBudgetDetailRepository) {
        this.clientBudgetDetailRepository = clientBudgetDetailRepository;
    }

    public List<ClientBudgetDetail> findByBudget(int budgetId) {
        return clientBudgetDetailRepository.findByBudgetId(budgetId);
    }

    public void save(ClientBudgetDetail detail) {
        clientBudgetDetailRepository.insert(detail);
    }

    public void deleteByBudget(int budgetId) {
        clientBudgetDetailRepository.deleteByBudgetId(budgetId);
    }
}
