package repositories.impl;

import java.util.List;
import mappers.SubcategoryMapper;
import models.Subcategory;
import repositories.SubcategoryRepository;

public class SubcategoryRepositoryImpl implements SubcategoryRepository {

    private final SubcategoryMapper subcategoryMapper;

    public SubcategoryRepositoryImpl(SubcategoryMapper subcategoryMapper) {
        this.subcategoryMapper = subcategoryMapper;
    }

    @Override
    public List<Subcategory> findAll() {
        return subcategoryMapper.findAll();
    }

    @Override
    public Subcategory findById(int id) {
        return subcategoryMapper.findById(id);
    }

    @Override
    public Subcategory findByNameAndCategoryId(String name, int categoryId) {
        return subcategoryMapper.findByNameAndCategoryId(name, categoryId);
    }

    @Override
    public void insert(Subcategory subcategory) {
        subcategoryMapper.insert(subcategory);
    }

    @Override
    public void update(Subcategory subcategory) {
        subcategoryMapper.update(subcategory);
    }

    @Override
    public void delete(int id) {
        subcategoryMapper.delete(id);
    }
}

