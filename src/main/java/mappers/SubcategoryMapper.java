
package mappers;

import java.util.List;
import models.Subcategory;
import org.apache.ibatis.annotations.Param;

public interface SubcategoryMapper {

    Subcategory findById(int id);
    Subcategory findByNameAndCategoryId(@Param("name") String name, @Param("categoryId") int categoryId);
    List<Subcategory> findAll();
    void insert(Subcategory subcategory);
    void update(Subcategory subcategory);
    void delete(int id);
}
