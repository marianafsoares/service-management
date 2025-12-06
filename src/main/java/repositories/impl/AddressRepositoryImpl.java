package repositories.impl;

import java.util.List;
import mappers.AddressMapper;
import models.Address;
import repositories.AddressRepository;

public class AddressRepositoryImpl implements AddressRepository {

    private final AddressMapper addressMapper;

    public AddressRepositoryImpl(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    @Override
    public List<Address> findAll() {
        return addressMapper.findAll();
    }

    @Override
    public Address findById(int id) {
        return addressMapper.findById(id);
    }

    @Override
    public Address findByName(String name) {
        return addressMapper.findByName(name);
    }

    @Override
    public void insert(Address address) {
        addressMapper.insert(address);
    }

    @Override
    public void update(Address address) {
        addressMapper.update(address);
    }

    @Override
    public void delete(int id) {
        addressMapper.delete(id);
    }
}
