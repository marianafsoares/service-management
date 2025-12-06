package repositories.impl;

import java.util.List;
import mappers.ClientMapper;
import models.Client;
import repositories.ClientRepository;

public class ClientRepositoryImpl implements ClientRepository {

    private final ClientMapper clientMapper;

    public ClientRepositoryImpl(ClientMapper clientMapper) {
        this.clientMapper = clientMapper;
    }

    @Override
    public List<Client> findAll() {
        return clientMapper.findAll();
    }

    @Override
    public Client findById(int id) {
        return clientMapper.findById(id);
    }

    @Override
    public void insert(Client client) {
        clientMapper.insert(client);
    }

    @Override
    public void update(Client client) {
        clientMapper.update(client);
    }

    @Override
    public void delete(int id) {
        clientMapper.delete(id);
    }
}

