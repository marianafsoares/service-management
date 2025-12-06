package services;

import java.util.List;
import models.Bank;
import repositories.BankRepository;

public class BankService {

    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public List<Bank> findAll() {
        return bankRepository.findAll();
    }

    public Bank findById(int id) {
        return bankRepository.findById(id);
    }

    public void create(Bank bank) {
        Bank existing = bankRepository.findByName(bank.getName());
        if (existing != null) {
            throw new IllegalArgumentException("Ya existe un banco con ese nombre.");
        }
        bankRepository.insert(bank);
    }

    public void update(Bank bank) {
        Bank existing = bankRepository.findByName(bank.getName());
        if (existing != null && existing.getId() != bank.getId()) {
            throw new IllegalArgumentException("Ya existe un banco con ese nombre.");
        }
        bankRepository.update(bank);
    }

    public void delete(int id) {
        bankRepository.delete(id);
    }
}

