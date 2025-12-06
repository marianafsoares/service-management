package services;

import java.util.List;
import models.Address;
import repositories.AddressRepository;

public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    public Address findById(int id) {
        return addressRepository.findById(id);
    }

    public void create(Address address) {
        Address existing = addressRepository.findByName(address.getName());
        if (existing != null) {
            throw new IllegalArgumentException("Ya existe una dirección con ese nombre.");
        }
        addressRepository.insert(address);
    }

    public void update(Address address) {
        Address existing = addressRepository.findByName(address.getName());
        if (existing != null && existing.getId() != address.getId()) {
            throw new IllegalArgumentException("Ya existe una dirección con ese nombre.");
        }
        addressRepository.update(address);
    }

    public void delete(int id) {
        addressRepository.delete(id);
    }
}
