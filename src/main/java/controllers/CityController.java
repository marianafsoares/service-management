package controllers;

import java.util.List;
import javax.swing.JComboBox;
import models.City;
import services.CityService;

public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    public List<City> findAll() {
        return cityService.findAll();
    }

    public City findById(int id) {
        return cityService.findById(id);
    }

    public void create(City city) {
        cityService.create(city);
    }

    public void update(City city) {
        cityService.update(city);
    }

    public void delete(int id) {
        cityService.delete(id);
    }

    public void loadCities(JComboBox combo) {
        combo.removeAllItems();
        combo.addItem("Seleccione...");
        List<City> cities = findAll();
        for (City city : cities) {
            combo.addItem(city.getName());
        }
    }
}
