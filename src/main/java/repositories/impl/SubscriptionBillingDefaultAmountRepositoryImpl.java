package repositories.impl;

import mappers.SubscriptionBillingDefaultAmountMapper;
import models.SubscriptionBillingDefaultAmount;
import repositories.SubscriptionBillingDefaultAmountRepository;

public class SubscriptionBillingDefaultAmountRepositoryImpl implements SubscriptionBillingDefaultAmountRepository {

    private final SubscriptionBillingDefaultAmountMapper mapper;

    public SubscriptionBillingDefaultAmountRepositoryImpl(SubscriptionBillingDefaultAmountMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public SubscriptionBillingDefaultAmount findEnabled() {
        return mapper.findEnabled();
    }

    @Override
    public void disableEnabled() {
        mapper.disableEnabled();
    }

    @Override
    public void insert(SubscriptionBillingDefaultAmount value) {
        mapper.insert(value);
    }
}
