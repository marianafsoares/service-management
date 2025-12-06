package services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import repositories.ProductRepository;
import models.Product;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findEnabled() {
        return productRepository.findEnabled();
    }

    public Product findByCode(String code) {
        return productRepository.findByCode(code);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public void update(Product product) {
        productRepository.update(product);
    }

    public void delete(String code) {
        productRepository.delete(code);
    }

    public void increaseStock(String code, Float quantity) {
        if (code == null || code.trim().isEmpty() || quantity == null || quantity <= 0) {
            return;
        }
        Product product = productRepository.findByCode(code);
        if (product == null) {
            return;
        }
        Float current = product.getStockQuantity() == null ? 0f : product.getStockQuantity();
        Float updated = current + quantity;
        productRepository.updateStock(code, updated);
    }

    public boolean decreaseStock(String code, Float quantity) {
        if (code == null || code.trim().isEmpty() || quantity == null || quantity <= 0) {
            return false;
        }
        Product product = productRepository.findByCode(code);
        if (product == null) {
            return false;
        }
        Float current = product.getStockQuantity() == null ? 0f : product.getStockQuantity();
        if (quantity > current) {
            return false;
        }
        Float updated = current - quantity;
        productRepository.updateStock(code, updated);
        return true;
    }

    public BigDecimal calculateCashPrice(Float vatRate, BigDecimal purchasePrice, Float profitMargin) {
        BigDecimal price = purchasePrice;
        if (profitMargin != null) {
            price = price.multiply(BigDecimal.ONE.add(BigDecimal.valueOf(profitMargin).divide(BigDecimal.valueOf(100))));
        }
        if (vatRate != null && vatRate > 0) {
            price = price.multiply(BigDecimal.ONE.add(BigDecimal.valueOf(vatRate).divide(BigDecimal.valueOf(100))));
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateFinancedPrice(BigDecimal cashPrice, Float interestRate) {
        BigDecimal price = cashPrice;
        if (interestRate != null) {
            price = price.multiply(BigDecimal.ONE.add(BigDecimal.valueOf(interestRate).divide(BigDecimal.valueOf(100))));
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }
}
