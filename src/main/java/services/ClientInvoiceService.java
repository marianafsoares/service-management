package services;

import java.util.List;
import models.ClientInvoice;
import repositories.ClientInvoiceRepository;

public class ClientInvoiceService {

    private final ClientInvoiceRepository clientInvoiceRepository;

    public ClientInvoiceService(ClientInvoiceRepository clientInvoiceRepository) {
        this.clientInvoiceRepository = clientInvoiceRepository;
    }

    public ClientInvoice findById(int id) {
        return clientInvoiceRepository.findById(id);
    }

    public List<ClientInvoice> findByClient(int clientId) {
        return clientInvoiceRepository.findByClientId(clientId);
    }

    public List<ClientInvoice> findAll() {
        return clientInvoiceRepository.findAll();
    }

    public void save(ClientInvoice invoice) {
        clientInvoiceRepository.insert(invoice);
    }

    public void update(ClientInvoice invoice) {
        clientInvoiceRepository.update(invoice);
    }

    public void delete(int id) {
        clientInvoiceRepository.delete(id);
    }

    public ClientInvoice findByPointOfSaleAndNumber(String pointOfSale, String invoiceNumber, String invoiceType) {
        return clientInvoiceRepository.findByPointOfSaleAndNumber(pointOfSale, invoiceNumber, invoiceType);
    }

    public ClientInvoice findByPointOfSaleAndNumberSuffix(String pointOfSale, String numberSuffix,
            String invoiceType, Integer clientId) {
        String cleanedPointOfSale = trimToNull(pointOfSale);
        String pointOfSaleSuffix = resolvePointOfSaleSuffix(cleanedPointOfSale);
        return clientInvoiceRepository.findByPointOfSaleAndNumberSuffix(cleanedPointOfSale, pointOfSaleSuffix,
                numberSuffix, invoiceType, clientId);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String resolvePointOfSaleSuffix(String pointOfSale) {
        if (pointOfSale == null) {
            return null;
        }
        String digits = pointOfSale.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return pointOfSale;
        }
        try {
            int numeric = Integer.parseInt(digits);
            return Integer.toString(numeric);
        } catch (NumberFormatException ex) {
            return digits;
        }
    }
}
