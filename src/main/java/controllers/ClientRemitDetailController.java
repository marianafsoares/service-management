package controllers;

import java.util.List;
import models.ClientRemitDetail;
import services.ClientRemitDetailService;

public class ClientRemitDetailController {

    private final ClientRemitDetailService clientRemitDetailService;

    public ClientRemitDetailController(ClientRemitDetailService clientRemitDetailService) {
        this.clientRemitDetailService = clientRemitDetailService;
    }

    public List<ClientRemitDetail> findByRemit(int remitId) {
        return clientRemitDetailService.findByRemit(remitId);
    }

    public void save(ClientRemitDetail detail) {
        clientRemitDetailService.save(detail);
    }

    public void deleteByRemit(int remitId) {
        clientRemitDetailService.deleteByRemit(remitId);
    }
}
