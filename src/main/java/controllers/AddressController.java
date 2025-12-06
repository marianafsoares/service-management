package controllers;

import java.util.List;
import javax.swing.JComboBox;
import models.Address;
import services.AddressService;

public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    public List<Address> findAll() {
        return addressService.findAll();
    }

    public Address findById(int id) {
        return addressService.findById(id);
    }

    public void create(Address address) {
        addressService.create(address);
    }

    public void update(Address address) {
        addressService.update(address);
    }

    public void delete(int id) {
        addressService.delete(id);
    }

    public void loadAddresses(JComboBox combo) {
        combo.removeAllItems();
        combo.addItem("Seleccione...");
        List<Address> addresses = findAll();
        for (Address address : addresses) {
            combo.addItem(address.getName());
        }
    }
}
