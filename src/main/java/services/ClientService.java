package services;

import java.text.DecimalFormat;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import models.Client;
import models.ClientInvoice;
import models.receipts.ClientReceipt;
import repositories.ClientRepository;
import repositories.ClientInvoiceRepository;
import repositories.ClientReceiptRepository;
import repositories.ClientRemitRepository;
import utils.Constants;
import utils.DocumentValidator;

public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientInvoiceRepository clientInvoiceRepository;
    private final ClientReceiptRepository clientReceiptRepository;
    private final ClientRemitRepository clientRemitRepository;

    public ClientService(ClientRepository clientRepository) {
        this(clientRepository, null, null, null);
    }

    public ClientService(ClientRepository clientRepository,
            ClientInvoiceRepository clientInvoiceRepository,
            ClientReceiptRepository clientReceiptRepository) {
        this(clientRepository, clientInvoiceRepository, clientReceiptRepository, null);
    }

    public ClientService(ClientRepository clientRepository,
            ClientInvoiceRepository clientInvoiceRepository,
            ClientReceiptRepository clientReceiptRepository,
            ClientRemitRepository clientRemitRepository) {
        this.clientRepository = clientRepository;
        this.clientInvoiceRepository = clientInvoiceRepository;
        this.clientReceiptRepository = clientReceiptRepository;
        this.clientRemitRepository = clientRemitRepository;
    }

    public BigDecimal fillTable(JTable table, String value) {
        List<Client> clients = clientRepository.findAll();
        Set<Integer> clientsWithOpenRemits = findClientsWithOpenRemits();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        TableColumnModel columnModel = table.getColumnModel();
        while (columnModel.getColumnCount() > table.getColumnCount()) {
            columnModel.removeColumn(columnModel.getColumn(0));
        }
        String upper = value == null ? "" : value.toUpperCase(Locale.ROOT);
        List<Client> displayedClients = new ArrayList<>();
        BigDecimal totalBalance = BigDecimal.ZERO;
        for (Client client : clients) {
            String preferredPhone = client.getMobile();
            if (preferredPhone == null || preferredPhone.trim().isEmpty()) {
                preferredPhone = client.getPhone();
            }
            if (client != null && client.getId() != null) {
                client.setHasOpenRemits(clientsWithOpenRemits.contains(client.getId()));
            }
            BigDecimal balance = calculateBalance(client.getId());
            String rowData = (client.getId() + " "
                    + safe(client.getFullName()) + " "
                    + safe(client.getPhone()) + " "
                    + safe(client.getMobile()) + " "
                    + safe(formatCurrency(balance))).toUpperCase(Locale.ROOT);
            if (rowData.contains(upper)) {
                Object[] row = {
                    client.getId(),
                    client.getFullName(),
                    safe(preferredPhone),
                    formatCurrency(balance)
                };
                model.addRow(row);
                displayedClients.add(client);
                if (balance != null) {
                    totalBalance = totalBalance.add(balance);
                }
            }
        }
        table.putClientProperty("clientsList", displayedClients);
        table.repaint();
        return totalBalance;
    }

    public Client findById(int id) {
        return clientRepository.findById(id);
    }

    public Client findByDocument(String documentNumber) {
        String normalized = DocumentValidator.normalizeCuit(documentNumber);
        return clientRepository.findAll().stream()
                .filter(c -> normalized.equals(DocumentValidator.normalizeCuit(c.getDocumentNumber())))
                .findFirst()
                .orElse(null);
    }

    public void save(Client client) {
        clientRepository.insert(client);
    }

    public void update(Client client) {
        clientRepository.update(client);
    }

    public BigDecimal getBalance(Integer clientId) {
        if (clientId == null) {
            return BigDecimal.ZERO;
        }
        return calculateBalance(clientId);
    }

    public List<Client> findActiveClients() {
        List<Client> clients = clientRepository.findAll();
        if (clients == null) {
            return Collections.emptyList();
        }
        return clients.stream()
                .filter(Client::isActive)
                .collect(Collectors.toList());
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }

    private Set<Integer> findClientsWithOpenRemits() {
        if (clientRemitRepository == null) {
            return Collections.emptySet();
        }
        List<Integer> clientIds = clientRemitRepository.findClientIdsWithOpenRemits();
        if (clientIds == null || clientIds.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(clientIds);
    }

    public String formatCurrency(Object value) {
        if (value == null) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("0.00");
        String result = df.format(value);
        return result.replace(',', '.');
    }

    private BigDecimal calculateBalance(Integer clientId) {
        BigDecimal balance = BigDecimal.ZERO;

        if (clientInvoiceRepository != null) {
            List<ClientInvoice> invoices = clientInvoiceRepository.findByClientId(clientId);
            if (invoices != null) {
                for (ClientInvoice invoice : invoices) {
                    if (invoice.getTotal() == null) {
                        continue;
                    }
                    String type = invoice.getInvoiceType();
                    if (Constants.NOTA_CREDITO_A_ABBR.equals(type)
                            || Constants.NOTA_CREDITO_B_ABBR.equals(type)
                            || Constants.NOTA_DEVOLUCION_ABBR.equals(type)) {
                        balance = balance.subtract(invoice.getTotal());
                    } else {
                        balance = balance.add(invoice.getTotal());
                    }
                }
            }
        }

        if (clientReceiptRepository != null) {
            List<ClientReceipt> receipts = clientReceiptRepository.findByClientId(clientId);
            if (receipts != null) {
                for (ClientReceipt receipt : receipts) {
                    if (receipt.getTotal() != null) {
                        balance = balance.subtract(receipt.getTotal());
                    }
                }
            }
        }

        return balance;
    }
}

