package controllers;

import java.util.List;
import models.Category;
import services.CategoryService;

public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<Category> findAll() {
        return categoryService.findAll();
    }

    public Category findById(int id) {
        return categoryService.findById(id);
    }

    public Category findByName(String name) {
        return categoryService.findByName(name);
    }

    public void create(Category category) {
        categoryService.create(category);
    }

    public void update(Category category) {
        categoryService.update(category);
    }

    public void delete(int id) {
        categoryService.delete(id);
    }
}
