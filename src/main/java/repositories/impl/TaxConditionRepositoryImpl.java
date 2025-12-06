package repositories.impl;

import java.util.List;
import mappers.TaxConditionMapper;
import models.TaxCondition;
import repositories.TaxConditionRepository;

public class TaxConditionRepositoryImpl implements TaxConditionRepository {

    private final TaxConditionMapper taxConditionMapper;

    public TaxConditionRepositoryImpl(TaxConditionMapper taxConditionMapper) {
        this.taxConditionMapper = taxConditionMapper;
    }

    @Override
    public List<TaxCondition> findAll() {
        return taxConditionMapper.findAll();
    }

    @Override
    public TaxCondition findById(int id) {
        return taxConditionMapper.findById(id);
    }

    @Override
    public void insert(TaxCondition taxCondition) {
        taxConditionMapper.insert(taxCondition);
    }

    @Override
    public void update(TaxCondition taxCondition) {
        taxConditionMapper.update(taxCondition);
    }

    @Override
    public void delete(int id) {
        taxConditionMapper.delete(id);
    }
}
