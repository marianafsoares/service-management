package services;

import java.util.List;
import models.Brand;
import repositories.BrandRepository;

public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    public Brand findById(int id) {
        return brandRepository.findById(id);
    }

    public Brand findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        return brandRepository.findByName(name.trim());
    }

    public void create(Brand brand) {
        Brand existing = brandRepository.findByName(brand.getName());
        if (existing != null) {
            throw new IllegalArgumentException("Ya existe una marca con ese nombre.");
        }
        brandRepository.insert(brand);
    }

    public void update(Brand brand) {
        Brand existing = brandRepository.findByName(brand.getName());
        if (existing != null && existing.getId() != brand.getId()) {
            throw new IllegalArgumentException("Ya existe una marca con ese nombre.");
        }
        brandRepository.update(brand);
    }

    public void delete(int id) {
        brandRepository.delete(id);
    }
}

