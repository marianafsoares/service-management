package repositories;

import java.util.List;
import models.City;

public interface CityRepository {
    List<City> findAll();
    City findById(int id);
    City findByName(String name);
    void insert(City city);
    void update(City city);
    void delete(int id);
}
