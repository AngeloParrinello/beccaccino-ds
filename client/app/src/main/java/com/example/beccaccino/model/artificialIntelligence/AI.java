package com.example.beccaccino.model.artificialIntelligence;

import com.example.beccaccino.model.entities.ItalianCard.Suit;
import com.example.beccaccino.model.entities.Play;
import com.example.beccaccino.model.logic.Round;

/**
 * An AI making decisions for non-human players.
 */
public interface AI {
    /**
     * Give the AI the opportunity to make its move.
     *
     * @param currentRound - list of the plays made since this AI last turn,
     *                     chronologically ordered.
     * @return the play this AI decides to make.
     */
    Play makePlay(Round currentRound);

    /**
     * Ask the AI which suit it prefers.
     *
     * @return the suit selected by the AI.
     */
    Suit selectBriscola();

    /**
     * It sets the briscola chosen by the players for this match.
     *
     * @param briscola is the briscola chosen for the match.
     */
    void setBriscola(Suit briscola);
}
