
package mappers;

import models.Address;
import java.util.List;

public interface AddressMapper {

    Address findById(int id);
    Address findByName(String name);
    List<Address> findAll();
    void insert(Address address);
    void update(Address address);
    void delete(int id);
}
