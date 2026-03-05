package mappers;

import models.SubscriptionBillingDefaultAmount;

public interface SubscriptionBillingDefaultAmountMapper {

    SubscriptionBillingDefaultAmount findEnabled();

    void disableEnabled();

    void insert(SubscriptionBillingDefaultAmount value);
}
