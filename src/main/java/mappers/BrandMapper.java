
package mappers;

import models.Brand;
import java.util.List;

public interface BrandMapper {

    Brand findById(int id);
    Brand findByName(String name);
    List<Brand> findAll();
    void insert(Brand brand);
    void update(Brand brand);
    void delete(int id);
}
