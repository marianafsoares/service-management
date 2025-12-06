package repositories;

import java.util.List;
import models.Client;

public interface ClientRepository {

    List<Client> findAll();

    Client findById(int id);

    void insert(Client client);

    void update(Client client);

    void delete(int id);
}

