package repositories;

import java.util.List;
import models.Subcategory;

public interface SubcategoryRepository {
    List<Subcategory> findAll();
    Subcategory findById(int id);
    Subcategory findByNameAndCategoryId(String name, int categoryId);
    void insert(Subcategory subcategory);
    void update(Subcategory subcategory);
    void delete(int id);
}

