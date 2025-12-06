package repositories.impl;

import java.util.List;
import mappers.CityMapper;
import models.City;
import repositories.CityRepository;

public class CityRepositoryImpl implements CityRepository {

    private final CityMapper cityMapper;

    public CityRepositoryImpl(CityMapper cityMapper) {
        this.cityMapper = cityMapper;
    }

    @Override
    public List<City> findAll() {
        return cityMapper.findAll();
    }

    @Override
    public City findById(int id) {
        return cityMapper.findById(id);
    }

    @Override
    public City findByName(String name) {
        return cityMapper.findByName(name);
    }

    @Override
    public void insert(City city) {
        cityMapper.insert(city);
    }

    @Override
    public void update(City city) {
        cityMapper.update(city);
    }

    @Override
    public void delete(int id) {
        cityMapper.delete(id);
    }
}
