package it.unibo.sd.beccacino.model;

import it.unibo.sd.beccacino.Card;

/**
 * A deck made of italian cards.
 */
public interface Deck {
    /**
     * Draw a card from the deck.
     * @return the first card.
     */
    Card drawCard();

    /**
     * Get the seed to generate always the same deck.
     * @return the seed.
     */
    int getSeed();

    /**
     * @return the number of cards left in the deck.
     */
    int remainingCards();
}
