
package mappers;

import models.Bank;
import java.util.List;

public interface BankMapper {

    Bank findById(int id);
    Bank findByName(String name);
    List<Bank> findAllEnabled();
    List<Bank> findAll();
    void insert(Bank bank);
    void update(Bank bank);
    void delete(int id);
}
