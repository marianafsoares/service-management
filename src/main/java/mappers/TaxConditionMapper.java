
package mappers;

import models.TaxCondition;
import java.util.List;

public interface TaxConditionMapper {

    TaxCondition findById(int id);
    List<TaxCondition> findAll();
    void insert(TaxCondition taxCondition);
    void update(TaxCondition taxCondition);
    void delete(int id);
}
