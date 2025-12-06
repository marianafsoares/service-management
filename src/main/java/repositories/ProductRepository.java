
package repositories;

import java.util.List;
import models.Product;

public interface ProductRepository {
    List<Product> findAll();
    List<Product> findEnabled();
    Product findByCode(String code);
    void save(Product product);
    void update(Product product);
    void delete(String code);
    void updateStock(String code, Float stockQuantity);
}
