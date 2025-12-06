package repositories;

import java.util.List;
import models.ClientRemitDetail;

public interface ClientRemitDetailRepository {
    List<ClientRemitDetail> findByRemitId(int remitId);
    void insert(ClientRemitDetail detail);
    void deleteByRemitId(int remitId);
}
