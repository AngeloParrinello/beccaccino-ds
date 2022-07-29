package com.example.beccaccino.model.artificialIntelligence;

import com.example.beccaccino.model.entities.ItalianCard.Suit;

/**
 * It allows AI to select the "briscola".
 */
public interface BriscolaSelector {

    /**
     * It returns the preferred suit of the AI.
     * 
     * @return the preferred suit.
     */
    Suit getPreferredSuit();

}
