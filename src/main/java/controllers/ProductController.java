package controllers;

import models.Product;
import services.ProductService;

import java.math.BigDecimal;
import java.util.List;

public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public List<Product> findAll() {
        return productService.findAll();
    }

    public List<Product> findEnabled() {
        return productService.findEnabled();
    }

    public Product findByCode(String code) {
        return productService.findByCode(code);
    }

    public void save(Product product) {
        productService.save(product);
    }

    public void update(Product product) {
        productService.update(product);
    }

    public void delete(String code) {
        productService.delete(code);
    }

    public void increaseStock(String code, Float quantity) {
        productService.increaseStock(code, quantity);
    }

    public boolean decreaseStock(String code, Float quantity) {
        return productService.decreaseStock(code, quantity);
    }

    public BigDecimal calculateCashPrice(Float vatRate, BigDecimal purchasePrice, Float profitMargin) {
        return productService.calculateCashPrice(vatRate, purchasePrice, profitMargin);
    }

    public BigDecimal calculateFinancedPrice(BigDecimal cashPrice, Float interestRate) {
        return productService.calculateFinancedPrice(cashPrice, interestRate);
    }
}
