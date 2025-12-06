package repositories;

import java.util.List;
import models.Category;

public interface CategoryRepository {
    List<Category> findAll();
    Category findById(int id);
    Category findByName(String name);
    void insert(Category category);
    void update(Category category);
    void delete(int id);
}
