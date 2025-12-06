package repositories.impl;

import java.util.List;
import mappers.ClientRemitDetailMapper;
import models.ClientRemitDetail;
import repositories.ClientRemitDetailRepository;

public class ClientRemitDetailRepositoryImpl implements ClientRemitDetailRepository {

    private final ClientRemitDetailMapper clientRemitDetailMapper;

    public ClientRemitDetailRepositoryImpl(ClientRemitDetailMapper clientRemitDetailMapper) {
        this.clientRemitDetailMapper = clientRemitDetailMapper;
    }

    @Override
    public List<ClientRemitDetail> findByRemitId(int remitId) {
        return clientRemitDetailMapper.findByRemitId(remitId);
    }

    @Override
    public void insert(ClientRemitDetail detail) {
        clientRemitDetailMapper.insert(detail);
    }

    @Override
    public void deleteByRemitId(int remitId) {
        clientRemitDetailMapper.deleteByRemitId(remitId);
    }
}
