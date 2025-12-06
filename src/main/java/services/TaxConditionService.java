package services;

import java.util.List;
import models.TaxCondition;
import repositories.TaxConditionRepository;

public class TaxConditionService {

    private final TaxConditionRepository taxConditionRepository;

    public TaxConditionService(TaxConditionRepository taxConditionRepository) {
        this.taxConditionRepository = taxConditionRepository;
    }

    public List<TaxCondition> findAll() {
        return taxConditionRepository.findAll();
    }

    public TaxCondition findById(int id) {
        return taxConditionRepository.findById(id);
    }

    public void create(TaxCondition taxCondition) {
        taxConditionRepository.insert(taxCondition);
    }

    public void update(TaxCondition taxCondition) {
        taxConditionRepository.update(taxCondition);
    }

    public void delete(int id) {
        taxConditionRepository.delete(id);
    }
}
