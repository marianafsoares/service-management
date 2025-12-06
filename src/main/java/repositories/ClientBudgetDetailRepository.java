package repositories;

import java.util.List;
import models.ClientBudgetDetail;

public interface ClientBudgetDetailRepository {
    List<ClientBudgetDetail> findByBudgetId(int budgetId);
    void insert(ClientBudgetDetail detail);
    void deleteByBudgetId(int budgetId);
}
