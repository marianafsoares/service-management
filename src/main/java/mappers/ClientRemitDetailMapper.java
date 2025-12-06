package mappers;

import java.util.List;
import models.ClientRemitDetail;

public interface ClientRemitDetailMapper {
    List<ClientRemitDetail> findByRemitId(int remitId);
    void insert(ClientRemitDetail detail);
    void deleteByRemitId(int remitId);
}
