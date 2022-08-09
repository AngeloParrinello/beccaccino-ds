package com.example.beccaccino.model.artificialIntelligence;

import com.example.beccaccino.model.entities.ItalianCard;
import com.example.beccaccino.model.entities.ItalianCard.Suit;
import com.example.beccaccino.model.entities.Play;
import com.example.beccaccino.model.logic.Round;

import java.util.List;

/**
 * Database useful for AI to memorize important information in order to play the
 * best card in a round.
 */

public interface GameAnalyzer {

    /**
     * It allows the AI to observe the plays already played in the current round
     * before playing his card.
     *
     * @param currentRound is the current round.
     */
    void observePlays(Round currentRound);

    /**
     * it allows the AI to memorize the plays already played in the last round
     * starting from his play.
     */
    void updateLastRound();

    /**
     * It calculates the probability that the team could win the round by
     * playing a card.
     *
     * @param card is the card to consider.
     * @return the probability of winning against the opponent's cards.
     */
    int getWinningTeamProbability(ItalianCard card);

    /**
     * It serves the AI to remember the play made in this round.
     *
     * @param play is the play made by the AI in the current round.
     */
    void addMyPlay(Play play);

    /**
     * It returns the Briscola of match.
     *
     * @return briscola of match.
     */
    Suit getBriscola();

    /**
     * It allows to understand the Briscola called by other player.
     *
     * @param briscola is the briscola of match
     */
    void setBriscola(Suit briscola);

    /**
     * It checks if the teammate of AI is the temporary winner of the round.
     *
     * @return true if he is the temporary winner, false otherwise.
     */
    boolean isTeammateTempWinner();

    /**
     * It checks the card passed as a parameter will definitely win the round.
     *
     * @param card is the card to consider.
     * @return true if the card will win the round, false otherwise.
     */
    boolean willWinTheRound(ItalianCard card);

    /**
     * It returns the cards still playable by other player.
     *
     * @return a list of remaining cards.
     */
    List<ItalianCard> getRemainingCards();

    /**
     * It returns the current round.
     *
     * @return the current round.
     */
    Round getCurrentRound();

    /**
     * It checks whether a "taglio" is made with a card.
     *
     * @param card is the card to evaluate
     * @return true if a "taglio" is made with the card, false otherwise
     */
    boolean isTaglio(ItalianCard card);

}
