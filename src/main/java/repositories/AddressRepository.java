package repositories;

import java.util.List;
import models.Address;

public interface AddressRepository {
    List<Address> findAll();
    Address findById(int id);
    Address findByName(String name);
    void insert(Address address);
    void update(Address address);
    void delete(int id);
}
