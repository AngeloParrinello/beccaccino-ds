package com.example.beccaccino.model.entities;


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

    /**
     * Italian playing cards are divided into these four suits.
     */
    enum Suit {
        BASTONI, SPADE, DENARI, COPPE
    }

    /**
     * Italian playing cards can assume these ten values.
     */
    enum Value {
        ASSO, DUE, TRE, QUATTRO, CINQUE, SEI, SETTE, FANTE, CAVALLO, RE

    }
}
