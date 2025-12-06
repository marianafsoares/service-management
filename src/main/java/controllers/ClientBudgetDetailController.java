package controllers;

import java.util.List;
import models.ClientBudgetDetail;
import services.ClientBudgetDetailService;

public class ClientBudgetDetailController {

    private final ClientBudgetDetailService clientBudgetDetailService;

    public ClientBudgetDetailController(ClientBudgetDetailService clientBudgetDetailService) {
        this.clientBudgetDetailService = clientBudgetDetailService;
    }

    public List<ClientBudgetDetail> findByBudget(int budgetId) {
        return clientBudgetDetailService.findByBudget(budgetId);
    }

    public void save(ClientBudgetDetail detail) {
        clientBudgetDetailService.save(detail);
    }

    public void deleteByBudget(int budgetId) {
        clientBudgetDetailService.deleteByBudget(budgetId);
    }
}
