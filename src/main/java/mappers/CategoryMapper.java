
package mappers;

import models.Category;
import java.util.List;

public interface CategoryMapper {

    Category findById(int id);
    Category findByName(String name);
    List<Category> findAll();
    void insert(Category category);
    void update(Category category);
    void delete(int id);
}
