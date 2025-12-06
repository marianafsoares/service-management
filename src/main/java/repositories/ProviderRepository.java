package repositories;

import java.util.List;
import models.Provider;

public interface ProviderRepository {

    List<Provider> findAll();

    Provider findById(int id);

    void insert(Provider provider);

    void update(Provider provider);

    void delete(int id);
}

