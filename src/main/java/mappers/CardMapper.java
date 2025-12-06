
package mappers;

import models.Card;
import java.util.List;

public interface CardMapper {

    Card findById(int id);
    Card findByName(String name);
    List<Card> findAllEnabled();
    List<Card> findAll();
    void insert(Card card);
    void update(Card card);
    void delete(int id);
}
