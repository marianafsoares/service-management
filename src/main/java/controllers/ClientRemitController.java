package controllers;

import java.util.List;
import models.ClientRemit;
import services.ClientRemitService;

public class ClientRemitController {

    private final ClientRemitService clientRemitService;

    public ClientRemitController(ClientRemitService clientRemitService) {
        this.clientRemitService = clientRemitService;
    }

    public ClientRemit findById(int id) {
        return clientRemitService.findById(id);
    }

    public List<ClientRemit> findByClient(int clientId) {
        return clientRemitService.findByClient(clientId);
    }

    public List<ClientRemit> findAll() {
        return clientRemitService.findAll();
    }

    public void save(ClientRemit remit) {
        clientRemitService.save(remit);
    }

    public void update(ClientRemit remit) {
        clientRemitService.update(remit);
    }

    public void delete(int id) {
        clientRemitService.delete(id);
    }
}
