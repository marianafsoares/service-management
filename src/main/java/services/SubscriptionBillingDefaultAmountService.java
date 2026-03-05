package services;

import java.math.BigDecimal;
import models.SubscriptionBillingDefaultAmount;
import repositories.SubscriptionBillingDefaultAmountRepository;
import utils.Constants;
import utils.InvoiceTypeUtils;

public class SubscriptionBillingDefaultAmountService {

    private final SubscriptionBillingDefaultAmountRepository repository;

    public SubscriptionBillingDefaultAmountService(SubscriptionBillingDefaultAmountRepository repository) {
        this.repository = repository;
    }

    public SubscriptionBillingDefaultAmount getEnabledSetting() {
        return repository.findEnabled();
    }

    public BigDecimal getEnabledAmount() {
        SubscriptionBillingDefaultAmount current = getEnabledSetting();
        if (current == null || current.getAmount() == null) {
            return BigDecimal.ZERO;
        }
        return current.getAmount();
    }

    public String getEnabledInvoiceType() {
        SubscriptionBillingDefaultAmount current = getEnabledSetting();
        if (current == null || current.getInvoiceType() == null || current.getInvoiceType().isBlank()) {
            return Constants.FACTURA_C_ABBR;
        }
        return InvoiceTypeUtils.toAbbreviation(current.getInvoiceType().trim());
    }

    public void replaceEnabledAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El importe por defecto debe ser mayor a cero.");
        }

        SubscriptionBillingDefaultAmount current = getEnabledSetting();
        String invoiceType = current != null ? getEnabledInvoiceType() : Constants.FACTURA_C_ABBR;

        repository.disableEnabled();

        SubscriptionBillingDefaultAmount value = new SubscriptionBillingDefaultAmount();
        value.setAmount(amount);
        value.setInvoiceType(invoiceType);
        value.setEnabled(Boolean.TRUE);
        repository.insert(value);
    }
}
