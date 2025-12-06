
package mappers;

import java.util.List;
import models.Product;
import org.apache.ibatis.annotations.Param;

public interface ProductMapper {

    Product findByCode(String code);
    List<Product> findAll();
    List<Product> findEnabled();
    void insert(Product product);
    void update(Product product);
    void delete(String code);
    void updateStock(@Param("code") String code, @Param("stockQuantity") Float stockQuantity);
}
