package services;

import configs.AppConfig;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import models.Client;
import models.ClientInvoice;
import models.ClientInvoiceDetail;
import models.receipts.ClientReceipt;
import repositories.ClientInvoiceRepository;
import repositories.ClientInvoiceDetailRepository;
import repositories.ClientReceiptRepository;
import repositories.ClientRepository;
import utils.Constants;
import utils.InvoiceTypeUtils;

public class SubscriptionBillingService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat AMOUNT_FORMATTER = new DecimalFormat("0.00");

    private final ClientRepository clientRepository;
    private final ClientInvoiceRepository clientInvoiceRepository;
    private final ClientInvoiceDetailRepository clientInvoiceDetailRepository;
    private final ClientReceiptRepository clientReceiptRepository;

    public SubscriptionBillingService(ClientRepository clientRepository,
                                      ClientInvoiceRepository clientInvoiceRepository,
                                      ClientInvoiceDetailRepository clientInvoiceDetailRepository,
                                      ClientReceiptRepository clientReceiptRepository) {
        this.clientRepository = clientRepository;
        this.clientInvoiceRepository = clientInvoiceRepository;
        this.clientInvoiceDetailRepository = clientInvoiceDetailRepository;
        this.clientReceiptRepository = clientReceiptRepository;
    }

    public BigDecimal resolveDefaultSubscriptionAmount() {
        String configured = AppConfig.get("subscription.amount.default", "0");
        try {
            return new BigDecimal(configured.trim());
        } catch (Exception ignored) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal resolveSubscriptionAmount(Client client) {
        return resolveSubscriptionAmount(client, null);
    }

    public BigDecimal resolveSubscriptionAmount(Client client, BigDecimal fallbackAmount) {
        if (client != null && client.getSubscriptionAmount() != null
                && client.getSubscriptionAmount().compareTo(BigDecimal.ZERO) > 0) {
            return client.getSubscriptionAmount();
        }
        if (fallbackAmount != null && fallbackAmount.compareTo(BigDecimal.ZERO) > 0) {
            return fallbackAmount;
        }
        return resolveDefaultSubscriptionAmount();
    }

    public String resolveInvoiceType(Client client, String defaultInvoiceType) {
        if (client != null && client.isFxBilling()) {
            return Constants.PRESUPUESTO_ABBR;
        }
        if (defaultInvoiceType != null && !defaultInvoiceType.isBlank()) {
            return InvoiceTypeUtils.toAbbreviation(defaultInvoiceType.trim());
        }
        String configured = AppConfig.get("subscription.invoice.type.default", Constants.FACTURA_A_ABBR);
        if (configured == null || configured.isBlank()) {
            return Constants.FACTURA_A_ABBR;
        }
        return InvoiceTypeUtils.toAbbreviation(configured.trim());
    }

    public List<ClientInvoice> generateMonthlyInvoices(LocalDate billingDate, String defaultInvoiceType) {
        List<Client> clients = clientRepository.findAll();
        if (clients == null || clients.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDate targetDate = billingDate != null ? billingDate : LocalDate.now();
        List<ClientInvoice> created = new ArrayList<>();
        for (Client client : clients) {
            if (client == null || client.getId() == null || !client.isActive()) {
                continue;
            }
            ClientInvoice invoice = generateInvoiceForClient(client, targetDate, defaultInvoiceType);
            if (invoice != null) {
                created.add(invoice);
            }
        }
        return created;
    }

    public ClientInvoice generateInvoiceForClient(Client client, LocalDate billingDate, String defaultInvoiceType) {
        return generateInvoiceForClient(client, billingDate, defaultInvoiceType, null, null);
    }

    public ClientInvoice generateInvoiceForClient(Client client, LocalDate billingDate, String defaultInvoiceType,
            BigDecimal fallbackAmount, String description) {
        BigDecimal amount = resolveSubscriptionAmount(client, fallbackAmount);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        ClientInvoice invoice = new ClientInvoice();
        invoice.setClient(client);
        invoice.setInvoiceDate(billingDate != null ? billingDate.atStartOfDay() : LocalDateTime.now());
        String invoiceType = resolveInvoiceType(client, defaultInvoiceType);
        invoice.setInvoiceType(invoiceType);
        String pointOfSale = resolvePointOfSale(client, invoiceType);
        invoice.setPointOfSale(pointOfSale);
        String invoiceNumber = sanitizeDigits(nextInvoiceNumber(pointOfSale, invoice.getInvoiceType()));
        invoice.setInvoiceNumber(invoiceNumber.isBlank() ? "1" : invoiceNumber);
        invoice.setIssuerCuit(resolveIssuerCuit());
        invoice.setSubtotal(amount);
        invoice.setTotal(amount);
        invoice.setDescription(description == null || description.isBlank()
                ? "Abono mensual del servicio"
                : description.trim());
        invoice.setPaymentMethod("Cuenta corriente");
        clientInvoiceRepository.insert(invoice);
        persistDetail(invoice, amount, description);
        return invoice;
    }

    public List<AccountStatementLine> buildStatementSinceLastZero(Integer clientId) {
        if (clientId == null) {
            return Collections.emptyList();
        }
        List<AccountStatementLine> lines = new ArrayList<>();
        List<ClientInvoice> invoices = clientInvoiceRepository.findByClientId(clientId);
        if (invoices != null) {
            for (ClientInvoice invoice : invoices) {
                BigDecimal amount = invoice.getTotal();
                if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }

                boolean creditNote = isCreditNote(invoice.getInvoiceType());
                BigDecimal absoluteAmount = amount.abs();

                if (creditNote || amount.compareTo(BigDecimal.ZERO) < 0) {
                    lines.add(AccountStatementLine.invoiceCredit(invoice.getInvoiceDate(), absoluteAmount,
                            formatInvoiceDisplay(invoice), invoice.getInvoiceType()));
                    continue;
                }

                lines.add(AccountStatementLine.invoice(invoice.getInvoiceDate(), absoluteAmount,
                        formatInvoiceDisplay(invoice), invoice.getInvoiceType()));
            }
        }

        List<ClientReceipt> receipts = clientReceiptRepository.findByClientId(clientId);
        if (receipts != null) {
            for (ClientReceipt receipt : receipts) {
                BigDecimal amount = receipt.getTotal();
                if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                lines.add(AccountStatementLine.receipt(receipt.getReceiptDate(), amount,
                        receipt.getReceiptNumber()));
            }
        }

        if (lines.isEmpty()) {
            return Collections.emptyList();
        }

        lines.sort(Comparator.comparing(AccountStatementLine::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

        BigDecimal running = BigDecimal.ZERO;
        int lastZeroIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            AccountStatementLine line = lines.get(i);
            BigDecimal debit = line.getDebit() != null ? line.getDebit() : BigDecimal.ZERO;
            BigDecimal credit = line.getCredit() != null ? line.getCredit() : BigDecimal.ZERO;
            running = running.add(debit).subtract(credit);
            line.setBalance(running);
            if (running.compareTo(BigDecimal.ZERO) == 0) {
                lastZeroIndex = i;
            }
        }

        if (running.compareTo(BigDecimal.ZERO) <= 0) {
            return Collections.emptyList();
        }

        if (lastZeroIndex + 1 < lines.size()) {
            return new ArrayList<>(lines.subList(lastZeroIndex + 1, lines.size()));
        }
        return lines;
    }

    public Optional<String> buildSummaryMessage(Integer clientId) {
        List<AccountStatementLine> lines = buildStatementSinceLastZero(clientId);
        if (lines.isEmpty()) {
            return Optional.empty();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Resumen de cuenta pendiente:\n");
        for (AccountStatementLine line : lines) {
            builder.append(formatDate(line.getDate()))
                    .append(" - ")
                    .append(line.getLabel())
                    .append(": $")
                    .append(formatAmount(line.getAmount()))
                    .append(" | Saldo: $")
                    .append(formatAmount(line.getBalance()))
                    .append("\n");
        }
        builder.append("Saldo a pagar: $").append(formatAmount(lines.get(lines.size() - 1).getBalance()));
        return Optional.of(builder.toString());
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return "(sin fecha)";
        }
        return date.format(DATE_FORMATTER);
    }

    private String formatAmount(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return AMOUNT_FORMATTER.format(value).replace(',', '.');
    }

    private String nextInvoiceNumber(String pointOfSale, String invoiceType) {
        List<ClientInvoice> invoices = clientInvoiceRepository.findAll();
        int currentMax = 0;
        if (invoices != null) {
            for (ClientInvoice invoice : invoices) {
                if (matchesInvoice(invoice, pointOfSale, invoiceType)) {
                    currentMax = Math.max(currentMax, extractInvoiceNumber(invoice));
                }
            }
        }
        return String.valueOf(currentMax + 1);
    }

    private boolean matchesInvoice(ClientInvoice invoice, String pointOfSale, String invoiceType) {
        if (invoice == null) {
            return false;
        }
        boolean posMatches = Objects.equals(normalize(pointOfSale), normalize(invoice.getPointOfSale()));
        boolean typeMatches = normalize(invoiceType) == null || normalize(invoiceType).equals(normalize(invoice.getInvoiceType()));
        return posMatches && typeMatches;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private int extractInvoiceNumber(ClientInvoice invoice) {
        String number = invoice.getInvoiceNumber();
        if (number == null || number.isBlank()) {
            return 0;
        }
        String digits = number.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return 0;
        }
        String suffix = digits.length() > 8 ? digits.substring(digits.length() - 8) : digits;
        try {
            return Integer.parseInt(suffix);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private void persistDetail(ClientInvoice invoice, BigDecimal amount, String description) {
        if (invoice == null || invoice.getId() == null) {
            return;
        }

        ClientInvoiceDetail detail = new ClientInvoiceDetail();
        detail.setInvoice(invoice);
        detail.setArticleCode("99");
        detail.setArticleDescription(description == null || description.isBlank()
                ? "Abono mensual del servicio"
                : description.trim());
        detail.setQuantity(BigDecimal.ONE);
        detail.setUnitPrice(amount);
        detail.setDiscountPercent(BigDecimal.ZERO);
        detail.setVatAmount(BigDecimal.ZERO);
        detail.setSubtotal(amount);

        clientInvoiceDetailRepository.insert(detail);
        invoice.setDetails(Collections.singletonList(detail));
    }

    private String formatInvoiceDisplay(ClientInvoice invoice) {
        if (invoice == null) {
            return "";
        }
        String posDigits = sanitizeDigits(invoice.getPointOfSale());
        String numberDigits = sanitizeDigits(invoice.getInvoiceNumber());
        String formattedPos = leftPad(posDigits, 4);
        String formattedNumber = leftPad(numberDigits, 8);
        return formattedPos + "-" + formattedNumber;
    }

    private String resolvePointOfSale(Client client, String invoiceType) {
        String type = invoiceType == null ? "" : invoiceType.trim();
        boolean fxInvoice = Constants.PRESUPUESTO_ABBR.equalsIgnoreCase(type)
                || Constants.PRESUPUESTO.equalsIgnoreCase(type)
                || (client != null && client.isFxBilling());
        if (fxInvoice) {
            return "0";
        }
        String configured = sanitizeDigits(AppConfig.get("pos.default", "1"));
        if (configured == null || configured.isBlank()) {
            return "1";
        }
        return configured;
    }

    private String sanitizeDigits(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^0-9]", "");
    }

    private String leftPad(String value, int size) {
        String digits = value == null ? "" : value.trim();
        if (digits.length() > size) {
            digits = digits.substring(digits.length() - size);
        }
        return String.format(Locale.ROOT, "%" + size + "s", digits).replace(' ', '0');
    }

    private String resolveIssuerCuit() {
        String cuits = AppConfig.get("company.cuits", "");
        if (cuits == null || cuits.isBlank()) {
            return "";
        }
        String[] parts = cuits.split(",");
        return parts.length == 0 ? "" : parts[0].trim();
    }

    private boolean isCreditNote(String invoiceType) {
        String normalized = normalize(invoiceType);
        if (normalized == null) {
            return false;
        }
        String lowered = normalized.toLowerCase(Locale.ROOT);
        return lowered.contains("nota de credito")
                || lowered.startsWith("nc")
                || lowered.startsWith(Constants.NOTA_CREDITO_A_ABBR.toLowerCase(Locale.ROOT))
                || lowered.startsWith(Constants.NOTA_CREDITO_B_ABBR.toLowerCase(Locale.ROOT))
                || lowered.startsWith(Constants.NOTA_CREDITO_C_ABBR.toLowerCase(Locale.ROOT))
                || lowered.contains("nota de devolucion")
                || lowered.startsWith(Constants.NOTA_DEVOLUCION_ABBR.toLowerCase(Locale.ROOT));
    }

    public static class AccountStatementLine {
        private LocalDateTime date;
        private BigDecimal debit;
        private BigDecimal credit;
        private BigDecimal balance;
        private String label;

        public static AccountStatementLine invoice(LocalDateTime date, BigDecimal amount, String invoiceNumber, String invoiceType) {
            AccountStatementLine line = new AccountStatementLine();
            line.date = date;
            line.debit = amount;
            line.label = (invoiceType != null ? invoiceType + " " : "Factura ") + Objects.toString(invoiceNumber, "").trim();
            return line;
        }

        public static AccountStatementLine invoiceCredit(LocalDateTime date, BigDecimal amount, String invoiceNumber, String invoiceType) {
            AccountStatementLine line = new AccountStatementLine();
            line.date = date;
            line.credit = amount;
            line.label = (invoiceType != null ? invoiceType + " " : "NC ") + Objects.toString(invoiceNumber, "").trim();
            return line;
        }

        public static AccountStatementLine receipt(LocalDateTime date, BigDecimal amount, String receiptNumber) {
            AccountStatementLine line = new AccountStatementLine();
            line.date = date;
            line.credit = amount;
            line.label = "Recibo " + Objects.toString(receiptNumber, "").trim();
            return line;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public BigDecimal getDebit() {
            return debit;
        }

        public BigDecimal getCredit() {
            return credit;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

        public String getLabel() {
            return label;
        }

        public BigDecimal getAmount() {
            if (debit != null) {
                return debit;
            }
            if (credit != null) {
                return credit;
            }
            return BigDecimal.ZERO;
        }
    }
}
