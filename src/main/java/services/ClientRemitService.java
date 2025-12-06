package services;

import java.util.List;
import models.ClientRemit;
import repositories.ClientRemitRepository;

public class ClientRemitService {

    private final ClientRemitRepository clientRemitRepository;

    public ClientRemitService(ClientRemitRepository clientRemitRepository) {
        this.clientRemitRepository = clientRemitRepository;
    }

    public ClientRemit findById(int id) {
        return clientRemitRepository.findById(id);
    }

    public List<ClientRemit> findAll() {
        return clientRemitRepository.findAll();
    }

    public List<ClientRemit> findByClient(int clientId) {
        return clientRemitRepository.findByClientId(clientId);
    }

    public void save(ClientRemit remit) {
        clientRemitRepository.insert(remit);
    }

    public void update(ClientRemit remit) {
        clientRemitRepository.update(remit);
    }

    public void delete(int id) {
        clientRemitRepository.delete(id);
    }
}
