package controllers;

import java.util.List;
import models.Brand;
import services.BrandService;

public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    public List<Brand> findAll() {
        return brandService.findAll();
    }

    public Brand findById(int id) {
        return brandService.findById(id);
    }

    public Brand findByName(String name) {
        return brandService.findByName(name);
    }

    public void create(Brand brand) {
        brandService.create(brand);
    }

    public void update(Brand brand) {
        brandService.update(brand);
    }

    public void delete(int id) {
        brandService.delete(id);
    }
}

