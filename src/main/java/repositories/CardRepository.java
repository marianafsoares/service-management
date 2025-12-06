package repositories;

import java.util.List;
import models.Card;

public interface CardRepository {
    List<Card> findAll();
    Card findById(int id);
    Card findByName(String name);
    void insert(Card card);
    void update(Card card);
    void delete(int id);
}

