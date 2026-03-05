package repositories;

import models.SubscriptionBillingDefaultAmount;

public interface SubscriptionBillingDefaultAmountRepository {

    SubscriptionBillingDefaultAmount findEnabled();

    void disableEnabled();

    void insert(SubscriptionBillingDefaultAmount value);
}
