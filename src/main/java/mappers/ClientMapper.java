package mappers;

import models.Client;

import java.util.List;

public interface ClientMapper {

    Client findById(int id);
    List<Client> findAll();
    void insert(Client client);    
    void update(Client client);
    void delete(int id);
}
