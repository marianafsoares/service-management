package services;

import models.CheckInfo;
import models.receipts.ClientReceipt;
import models.receipts.ProviderReceipt;
import models.receipts.ReceiptCheque;
import repositories.ReceiptChequeRepository;
import services.ClientReceiptService;
import services.ProviderReceiptService;

public class CheckService {

    private final ReceiptChequeRepository receiptChequeRepository;
    private final ClientReceiptService clientReceiptService;
    private final ProviderReceiptService providerReceiptService;

    public CheckService(ReceiptChequeRepository receiptChequeRepository,
                        ClientReceiptService clientReceiptService,
                        ProviderReceiptService providerReceiptService) {
        this.receiptChequeRepository = receiptChequeRepository;
        this.clientReceiptService = clientReceiptService;
        this.providerReceiptService = providerReceiptService;
    }

    public CheckInfo findByNumber(String number) {
        ReceiptCheque clientCheque = receiptChequeRepository.findByNumberAndType(number, "CLIENT");
        ReceiptCheque providerCheque = receiptChequeRepository.findByNumberAndType(number, "PROVIDER");

        if (clientCheque == null && providerCheque == null) {
            return null;
        }

        CheckInfo info = new CheckInfo();

        if (providerCheque != null) {
            info.setBankName(providerCheque.getBankName());
            if (providerCheque.getDueDate() != null) {
                info.setDueDate(java.sql.Date.valueOf(providerCheque.getDueDate()));
            }
            if (providerCheque.getAmount() != null) {
                info.setAmount(providerCheque.getAmount().toString());
            }
            ProviderReceipt receipt = providerReceiptService.findById(providerCheque.getReceiptId());
            if (receipt != null && receipt.getProvider() != null) {
                info.setProviderName(receipt.getProvider().getName());
            }
        }

        if (clientCheque != null) {
            info.setBankName(clientCheque.getBankName());
            if (clientCheque.getDueDate() != null) {
                info.setDueDate(java.sql.Date.valueOf(clientCheque.getDueDate()));
            }
            if (clientCheque.getAmount() != null) {
                info.setAmount(clientCheque.getAmount().toString());
            }
            ClientReceipt receipt = clientReceiptService.findById(clientCheque.getReceiptId());
            if (receipt != null && receipt.getClient() != null) {
                info.setClientName(receipt.getClient().getFullName());
            }
        }

        return info;
    }
}

