package services;

import java.text.DecimalFormat;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import models.Provider;
import models.ProviderInvoice;
import models.receipts.ProviderReceipt;
import repositories.ProviderRepository;
import repositories.ProviderInvoiceRepository;
import repositories.ProviderReceiptRepository;
import utils.DocumentValidator;
import utils.InvoiceTypeUtils;

public class ProviderService {

    private final ProviderRepository providerRepository;
    private final ProviderInvoiceRepository providerInvoiceRepository;
    private final ProviderReceiptRepository providerReceiptRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this(providerRepository, null, null);
    }

    public ProviderService(ProviderRepository providerRepository,
            ProviderInvoiceRepository providerInvoiceRepository,
            ProviderReceiptRepository providerReceiptRepository) {
        this.providerRepository = providerRepository;
        this.providerInvoiceRepository = providerInvoiceRepository;
        this.providerReceiptRepository = providerReceiptRepository;
    }

    public BigDecimal fillTable(JTable table, String value) {
        List<Provider> providers = providerRepository.findAll();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        TableColumnModel columnModel = table.getColumnModel();
        while (columnModel.getColumnCount() > table.getColumnCount()) {
            columnModel.removeColumn(columnModel.getColumn(0));
        }
        String upper = value == null ? "" : value.toUpperCase(Locale.ROOT);
        List<Provider> displayedProviders = new ArrayList<>();
        BigDecimal totalBalance = BigDecimal.ZERO;
        for (Provider provider : providers) {
            BigDecimal balance = calculateBalance(provider.getId());
            String rowData = (provider.getId() + " "
                    + safe(provider.getName()) + " "
                    + safe(provider.getPhone()) + " "
                    + safe(formatCurrency(balance))).toUpperCase(Locale.ROOT);
            if (rowData.contains(upper)) {
                Object[] row = {
                    provider.getId(),
                    provider.getName(),
                    provider.getPhone(),
                    formatCurrency(balance)
                };
                model.addRow(row);
                displayedProviders.add(provider);
                if (balance != null) {
                    totalBalance = totalBalance.add(balance);
                }
            }
        }
        table.putClientProperty("providersList", displayedProviders);
        table.repaint();
        return totalBalance;
    }

    public Provider findById(int id) {
        return providerRepository.findById(id);
    }

    public Provider findByDocument(String documentNumber) {
        String normalized = DocumentValidator.normalizeCuit(documentNumber);
        return providerRepository.findAll().stream()
                .filter(p -> normalized.equals(DocumentValidator.normalizeCuit(p.getDocumentNumber())))
                .findFirst()
                .orElse(null);
    }

    public void update(Provider provider) {
        providerRepository.update(provider);
    }

    public void save(Provider provider) {
        providerRepository.insert(provider);
    }

    public List<Provider> findAll() {
        return providerRepository.findAll();
    }

    public void delete(int id) {
        providerRepository.delete(id);
    }

    public boolean hasAssociations(Integer providerId) {
        if (providerId == null) {
            return false;
        }
        if (providerInvoiceRepository != null) {
            List<ProviderInvoice> invoices = providerInvoiceRepository.findByProviderId(providerId);
            if (invoices != null && !invoices.isEmpty()) {
                return true;
            }
        }
        if (providerReceiptRepository != null) {
            List<ProviderReceipt> receipts = providerReceiptRepository.findByProviderId(providerId);
            if (receipts != null && !receipts.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }

    public String formatCurrency(Object value) {
        if (value == null) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("0.00");
        String result = df.format(value);
        return result.replace(',', '.');
    }

    private BigDecimal calculateBalance(Integer providerId) {
        BigDecimal balance = BigDecimal.ZERO;

        if (providerInvoiceRepository != null) {
            List<ProviderInvoice> invoices = providerInvoiceRepository.findByProviderId(providerId);
            if (invoices != null) {
                for (ProviderInvoice invoice : invoices) {
                    if (invoice.getTotal() == null) {
                        continue;
                    }
                    String categoryDescription = invoice.getCategory() != null
                            ? invoice.getCategory().getDescription() : "";
                    boolean isCredit = InvoiceTypeUtils.isCreditDocument(categoryDescription)
                            || InvoiceTypeUtils.isCreditDocument(invoice.getInvoiceType());
                    if (isCredit) {
                        balance = balance.subtract(invoice.getTotal());
                    } else {
                        balance = balance.add(invoice.getTotal());
                    }
                }
            }
        }

        if (providerReceiptRepository != null) {
            List<ProviderReceipt> receipts = providerReceiptRepository.findByProviderId(providerId);
            if (receipts != null) {
                for (ProviderReceipt receipt : receipts) {
                    if (receipt.getTotal() != null) {
                        balance = balance.subtract(receipt.getTotal());
                    }
                }
            }
        }

        return balance;
    }
}

