package repositories.impl;

import java.util.List;
import mappers.CardMapper;
import models.Card;
import repositories.CardRepository;

public class CardRepositoryImpl implements CardRepository {

    private final CardMapper cardMapper;

    public CardRepositoryImpl(CardMapper cardMapper) {
        this.cardMapper = cardMapper;
    }

    @Override
    public List<Card> findAll() {
        return cardMapper.findAll();
    }

    @Override
    public Card findById(int id) {
        return cardMapper.findById(id);
    }

    @Override
    public Card findByName(String name) {
        return cardMapper.findByName(name);
    }

    @Override
    public void insert(Card card) {
        cardMapper.insert(card);
    }

    @Override
    public void update(Card card) {
        cardMapper.update(card);
    }

    @Override
    public void delete(int id) {
        cardMapper.delete(id);
    }
}

