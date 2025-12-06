package services;

import java.util.List;
import models.ClientRemitDetail;
import repositories.ClientRemitDetailRepository;

public class ClientRemitDetailService {

    private final ClientRemitDetailRepository clientRemitDetailRepository;

    public ClientRemitDetailService(ClientRemitDetailRepository clientRemitDetailRepository) {
        this.clientRemitDetailRepository = clientRemitDetailRepository;
    }

    public List<ClientRemitDetail> findByRemit(int remitId) {
        return clientRemitDetailRepository.findByRemitId(remitId);
    }

    public void save(ClientRemitDetail detail) {
        clientRemitDetailRepository.insert(detail);
    }

    public void deleteByRemit(int remitId) {
        clientRemitDetailRepository.deleteByRemitId(remitId);
    }
}
