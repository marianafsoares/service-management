package mappers;

import java.util.List;
import models.ClientBudgetDetail;

public interface ClientBudgetDetailMapper {
    List<ClientBudgetDetail> findByBudgetId(int budgetId);
    void insert(ClientBudgetDetail detail);
    void deleteByBudgetId(int budgetId);
}
