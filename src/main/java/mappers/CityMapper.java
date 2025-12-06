package mappers;

import models.City;
import java.util.List;

public interface CityMapper {

    City findById(int id);
    City findByName(String name);
    List<City> findAll();
    void insert(City city);
    void update(City city);
    void delete(int id);
}
