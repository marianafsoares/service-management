package controllers;

import java.util.List;
import models.Card;
import services.CardService;

public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    public List<Card> findAll() {
        return cardService.findAll();
    }

    public Card findById(int id) {
        return cardService.findById(id);
    }

    public void create(Card card) {
        cardService.create(card);
    }

    public void update(Card card) {
        cardService.update(card);
    }

    public void delete(int id) {
        cardService.delete(id);
    }
}

