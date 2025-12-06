package repositories.impl;

import java.util.List;
import mappers.ProviderMapper;
import models.Provider;
import repositories.ProviderRepository;

public class ProviderRepositoryImpl implements ProviderRepository {

    private final ProviderMapper providerMapper;

    public ProviderRepositoryImpl(ProviderMapper providerMapper) {
        this.providerMapper = providerMapper;
    }

    @Override
    public List<Provider> findAll() {
        return providerMapper.findAll();
    }

    @Override
    public Provider findById(int id) {
        return providerMapper.findById(id);
    }

    @Override
    public void insert(Provider provider) {
        providerMapper.insert(provider);
    }

    @Override
    public void update(Provider provider) {
        providerMapper.update(provider);
    }

    @Override
    public void delete(int id) {
        providerMapper.delete(id);
    }
}

