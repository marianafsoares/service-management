package services;

import java.util.List;
import models.Category;
import repositories.CategoryRepository;

public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(int id) {
        return categoryRepository.findById(id);
    }

    public Category findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        return categoryRepository.findByName(name.trim());
    }

    public void create(Category category) {
        Category existing = categoryRepository.findByName(category.getName());
        if (existing != null) {
            throw new IllegalArgumentException("Ya existe un rubro con ese nombre.");
        }
        categoryRepository.insert(category);
    }

    public void update(Category category) {
        Category existing = categoryRepository.findByName(category.getName());
        if (existing != null && existing.getId() != category.getId()) {
            throw new IllegalArgumentException("Ya existe un rubro con ese nombre.");
        }
        categoryRepository.update(category);
    }

    public void delete(int id) {
        categoryRepository.delete(id);
    }
}
