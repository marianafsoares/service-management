package repositories.impl;

import java.util.List;
import mappers.BankMapper;
import models.Bank;
import repositories.BankRepository;

public class BankRepositoryImpl implements BankRepository {

    private final BankMapper bankMapper;

    public BankRepositoryImpl(BankMapper bankMapper) {
        this.bankMapper = bankMapper;
    }

    @Override
    public List<Bank> findAll() {
        return bankMapper.findAll();
    }

    @Override
    public Bank findById(int id) {
        return bankMapper.findById(id);
    }

    @Override
    public Bank findByName(String name) {
        return bankMapper.findByName(name);
    }

    @Override
    public void insert(Bank bank) {
        bankMapper.insert(bank);
    }

    @Override
    public void update(Bank bank) {
        bankMapper.update(bank);
    }

    @Override
    public void delete(int id) {
        bankMapper.delete(id);
    }
}

