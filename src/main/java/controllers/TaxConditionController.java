package controllers;

import java.util.List;
import javax.swing.JComboBox;
import models.TaxCondition;
import services.TaxConditionService;

public class TaxConditionController {

    private final TaxConditionService taxConditionService;

    public TaxConditionController(TaxConditionService taxConditionService) {
        this.taxConditionService = taxConditionService;
    }

    public List<TaxCondition> findAll() {
        return taxConditionService.findAll();
    }

    public TaxCondition findById(int id) {
        return taxConditionService.findById(id);
    }

    public void loadConditions(JComboBox combo) {
        combo.removeAllItems();
        combo.addItem("Select...");
        List<TaxCondition> conditions = taxConditionService.findAll();
        for (TaxCondition condition : conditions) {
            combo.addItem(condition.getName());
        }
    }
}
