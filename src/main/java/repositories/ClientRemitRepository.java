package repositories;

import java.util.List;
import models.ClientRemit;

public interface ClientRemitRepository {
    ClientRemit findById(int id);
    List<ClientRemit> findAll();
    List<ClientRemit> findByClientId(int clientId);
    void insert(ClientRemit remit);
    void update(ClientRemit remit);
    void delete(int id);
    List<Integer> findClientIdsWithOpenRemits();
}
