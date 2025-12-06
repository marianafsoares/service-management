package services;

import java.util.List;
import models.City;
import repositories.CityRepository;

public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<City> findAll() {
        return cityRepository.findAll();
    }

    public City findById(int id) {
        return cityRepository.findById(id);
    }

    public void create(City city) {
        City existing = cityRepository.findByName(city.getName());
        if (existing != null) {
            throw new IllegalArgumentException("Ya existe una ciudad con ese nombre.");
        }
        cityRepository.insert(city);
    }

    public void update(City city) {
        City existing = cityRepository.findByName(city.getName());
        if (existing != null && existing.getId() != city.getId()) {
            throw new IllegalArgumentException("Ya existe una ciudad con ese nombre.");
        }
        cityRepository.update(city);
    }

    public void delete(int id) {
        cityRepository.delete(id);
    }
}
