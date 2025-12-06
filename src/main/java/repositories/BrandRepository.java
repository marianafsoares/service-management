package repositories;

import java.util.List;
import models.Brand;

public interface BrandRepository {
    List<Brand> findAll();
    Brand findById(int id);
    Brand findByName(String name);
    void insert(Brand brand);
    void update(Brand brand);
    void delete(int id);
}

