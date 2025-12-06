package services;

import java.util.List;
import models.Card;
import repositories.CardRepository;

public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<Card> findAll() {
        return cardRepository.findAll();
    }

    public Card findById(int id) {
        return cardRepository.findById(id);
    }

    public void create(Card card) {
        Card existing = cardRepository.findByName(card.getName());
        if (existing != null) {
            throw new IllegalArgumentException("Ya existe una tarjeta con ese nombre.");
        }
        cardRepository.insert(card);
    }

    public void update(Card card) {
        Card existing = cardRepository.findByName(card.getName());
        if (existing != null && existing.getId() != card.getId()) {
            throw new IllegalArgumentException("Ya existe una tarjeta con ese nombre.");
        }
        cardRepository.update(card);
    }

    public void delete(int id) {
        cardRepository.delete(id);
    }
}

