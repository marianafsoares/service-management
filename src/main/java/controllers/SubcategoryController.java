package controllers;

import java.util.List;
import models.Subcategory;
import services.SubcategoryService;

public class SubcategoryController {

    private final SubcategoryService subcategoryService;

    public SubcategoryController(SubcategoryService subcategoryService) {
        this.subcategoryService = subcategoryService;
    }

    public List<Subcategory> findAll() {
        return subcategoryService.findAll();
    }

    public Subcategory findById(int id) {
        return subcategoryService.findById(id);
    }

    public Subcategory findByNameAndCategoryId(String name, int categoryId) {
        return subcategoryService.findByNameAndCategoryId(name, categoryId);
    }

    public void create(Subcategory subcategory) {
        subcategoryService.create(subcategory);
    }

    public void update(Subcategory subcategory) {
        subcategoryService.update(subcategory);
    }

    public void delete(int id) {
        subcategoryService.delete(id);
    }
}

