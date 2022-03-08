package it.unibo.sd.beccacino.model;

/**
 * A deck made of italian cards.
 */
public interface Deck {
    /**
     * Draw a card from the deck.
     * @return the first card.
     */
    ItalianCard drawCard();

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
