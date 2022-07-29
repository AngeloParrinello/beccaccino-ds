package com.example.beccaccino.model.entities;

/**
 * A deck made of italian cards.
 */
public interface ItalianCardsDeck {
    /**
     * @return the top card, removing it from the deck
     */
    ItalianCard drawCard();

    /**
     * @return the number of remaining cards
     */
    int remainingCards();

    /**
     * @return the seed to generate again the same deck
     */
    int getSeed();
}
