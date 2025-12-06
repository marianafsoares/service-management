package services;

import java.util.List;
import models.Subcategory;
import repositories.SubcategoryRepository;

public class SubcategoryService {

    private final SubcategoryRepository subcategoryRepository;

    public SubcategoryService(SubcategoryRepository subcategoryRepository) {
        this.subcategoryRepository = subcategoryRepository;
    }

    public List<Subcategory> findAll() {
        return subcategoryRepository.findAll();
    }

    public Subcategory findById(int id) {
        return subcategoryRepository.findById(id);
    }

    public Subcategory findByNameAndCategoryId(String name, int categoryId) {
        if (name == null || name.trim().isEmpty() || categoryId == 0) {
            return null;
        }
        return subcategoryRepository.findByNameAndCategoryId(name.trim(), categoryId);
    }

    public void create(Subcategory subcategory) {
        validateCategory(subcategory);
        Subcategory existing = subcategoryRepository.findByNameAndCategoryId(
                subcategory.getName(), subcategory.getCategory().getId());
        if (existing != null) {
            throw new IllegalArgumentException("Ya existe un subrubro con ese nombre en el rubro seleccionado.");
        }
        subcategoryRepository.insert(subcategory);
    }

    public void update(Subcategory subcategory) {
        validateCategory(subcategory);
        Subcategory existing = subcategoryRepository.findByNameAndCategoryId(
                subcategory.getName(), subcategory.getCategory().getId());
        if (existing != null && existing.getId() != subcategory.getId()) {
            throw new IllegalArgumentException("Ya existe un subrubro con ese nombre en el rubro seleccionado.");
        }
        subcategoryRepository.update(subcategory);
    }

    public void delete(int id) {
        subcategoryRepository.delete(id);
    }

    private void validateCategory(Subcategory subcategory) {
        if (subcategory.getCategory() == null || subcategory.getCategory().getId() == 0) {
            throw new IllegalArgumentException("Debe seleccionar un rubro válido.");
        }
    }
}

