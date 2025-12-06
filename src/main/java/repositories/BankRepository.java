package repositories;

import java.util.List;
import models.Bank;

public interface BankRepository {
    List<Bank> findAll();
    Bank findById(int id);
    Bank findByName(String name);
    void insert(Bank bank);
    void update(Bank bank);
    void delete(int id);
}

