package controllers;

import java.util.List;
import models.Bank;
import services.BankService;

public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    public List<Bank> findAll() {
        return bankService.findAll();
    }

    public Bank findById(int id) {
        return bankService.findById(id);
    }

    public void create(Bank bank) {
        bankService.create(bank);
    }

    public void update(Bank bank) {
        bankService.update(bank);
    }

    public void delete(int id) {
        bankService.delete(id);
    }
}

