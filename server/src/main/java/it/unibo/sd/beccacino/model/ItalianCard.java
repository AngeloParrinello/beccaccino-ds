package it.unibo.sd.beccacino.model;

/**
 * A classic, immutable, italian playing card.
 */
public interface ItalianCard {
    /**
     * @return the suit of the card
     */
    Suit getSuit();

    /**
     * @return the value of the card
     */
    Value getValue();
}
