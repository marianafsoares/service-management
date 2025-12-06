
package repositories.impl;

import java.util.List;
import mappers.ProductMapper;
import repositories.ProductRepository;
import models.Product;

public class ProductRepositoryImpl implements ProductRepository {

    private final ProductMapper productMapper;

    public ProductRepositoryImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public List<Product> findAll() {
        return productMapper.findAll();
    }
    @Override
    public List<Product> findEnabled() {
        return productMapper.findEnabled();
    }

    @Override
    public Product findByCode(String code) {
        return productMapper.findByCode(code);
    }

    @Override
    public void save(Product product) {
        productMapper.insert(product);
    }

    @Override
    public void update(Product product) {
        productMapper.update(product);
    }

    @Override
    public void delete(String code) {
        productMapper.delete(code);
    }

    @Override
    public void updateStock(String code, Float stockQuantity) {
        productMapper.updateStock(code, stockQuantity);
    }
}
