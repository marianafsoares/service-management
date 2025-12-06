package controllers;

import java.math.BigDecimal;
import javax.swing.JTable;
import models.Client;
import services.ClientService;

public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    public BigDecimal fillTable(JTable table, String value) {
        return clientService.fillTable(table, value);
    }

    public String formatCurrency(Object value) {
        return clientService.formatCurrency(value);
    }

    public Client findById(int id) {
        return clientService.findById(id);
    }

    public Client findByDocument(String documentNumber) {
        return clientService.findByDocument(documentNumber);
    }

    public void update(Client client) {
        clientService.update(client);
    }

    public void save(Client client) {
        clientService.save(client);
    }

    public BigDecimal getBalance(Integer clientId) {
        return clientService.getBalance(clientId);
    }
}

