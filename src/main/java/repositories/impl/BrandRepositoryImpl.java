package repositories.impl;

import java.util.List;
import mappers.BrandMapper;
import models.Brand;
import repositories.BrandRepository;

public class BrandRepositoryImpl implements BrandRepository {

    private final BrandMapper brandMapper;

    public BrandRepositoryImpl(BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    @Override
    public List<Brand> findAll() {
        return brandMapper.findAll();
    }

    @Override
    public Brand findById(int id) {
        return brandMapper.findById(id);
    }

    @Override
    public Brand findByName(String name) {
        return brandMapper.findByName(name);
    }

    @Override
    public void insert(Brand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public void update(Brand brand) {
        brandMapper.update(brand);
    }

    @Override
    public void delete(int id) {
        brandMapper.delete(id);
    }
}

