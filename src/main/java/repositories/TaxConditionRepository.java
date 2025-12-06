package repositories;

import java.util.List;
import models.TaxCondition;

public interface TaxConditionRepository {
    List<TaxCondition> findAll();
    TaxCondition findById(int id);
    void insert(TaxCondition taxCondition);
    void update(TaxCondition taxCondition);
    void delete(int id);
}
