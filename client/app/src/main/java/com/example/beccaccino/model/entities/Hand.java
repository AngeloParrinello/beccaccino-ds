package com.example.beccaccino.model.entities;

import java.util.List;

/**
 * An hand capable of holding several italian cards.
 */
public interface Hand {
    /**
     * @return a list of the cards held by this hand. The order depends on the
     * implementing class
     */
    List<ItalianCard> getCards();

    /**
     * Removes the given card, if present.
     *
     * @param card to be removed
     */
    void removeCard(ItalianCard card);

    /**
     * Adds the given card to the hand, if not full.
     *
     * @param card to be added
     */
    void addCard(ItalianCard card);

    /**
     * @return true if the hand is full, false otherwise
     */
    boolean isFull();

    /**
     * @return true if the hand is empty, false otherwise
     */
    boolean isEmpty();
}
