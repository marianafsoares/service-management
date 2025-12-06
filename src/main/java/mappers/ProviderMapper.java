
package mappers;

import models.Provider;
import java.util.List;

public interface ProviderMapper {

    Provider findById(int id);
    List<Provider> findAll();
    void insert(Provider provider);
    void update(Provider provider);
    void delete(int id);
}
