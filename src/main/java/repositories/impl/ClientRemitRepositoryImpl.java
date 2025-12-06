package repositories.impl;

import java.util.List;
import mappers.ClientRemitMapper;
import models.ClientRemit;
import repositories.ClientRemitRepository;

public class ClientRemitRepositoryImpl implements ClientRemitRepository {

    private final ClientRemitMapper clientRemitMapper;

    public ClientRemitRepositoryImpl(ClientRemitMapper clientRemitMapper) {
        this.clientRemitMapper = clientRemitMapper;
    }

    @Override
    public ClientRemit findById(int id) {
        return clientRemitMapper.findById(id);
    }

    @Override
    public List<ClientRemit> findAll() {
        return clientRemitMapper.findAll();
    }

    @Override
    public List<ClientRemit> findByClientId(int clientId) {
        return clientRemitMapper.findByClientId(clientId);
    }

    @Override
    public void insert(ClientRemit remit) {
        clientRemitMapper.insert(remit);
    }

    @Override
    public void update(ClientRemit remit) {
        clientRemitMapper.update(remit);
    }

    @Override
    public void delete(int id) {
        clientRemitMapper.delete(id);
    }

    @Override
    public List<Integer> findClientIdsWithOpenRemits() {
        return clientRemitMapper.findClientIdsWithOpenRemits();
    }
}
