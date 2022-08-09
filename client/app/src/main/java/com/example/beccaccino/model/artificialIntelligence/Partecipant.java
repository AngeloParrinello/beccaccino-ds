package com.example.beccaccino.model.artificialIntelligence;

import com.example.beccaccino.model.entities.ItalianCard;
import com.example.beccaccino.model.entities.Play;

import java.util.List;

/**
 * It defines a participant in a match.
 */
public interface Partecipant {

    /**
     * It adds the play made by the participant.
     *
     * @param play is the play done by the Partecipant.
     */
    void addPlay(Play play);

    /**
     * It returns plays made by the participant.
     *
     * @return the list of cards played by the participant in the game.
     */
    List<Play> getPlays();

    /**
     * It removes the card to avoid calculating the probability that it is in
     * the participant's hand. it is called after a card has been played.
     *
     * @param card is the card to consider.
     */
    void removeCard(ItalianCard card);

    /**
     * It set the probability that a card is in the participant's hand.
     *
     * @param card        is the card to consider.
     * @param probability is the probability to be set.
     */
    void setProbabilityOf(ItalianCard card, int probability);

    /**
     * it returns the probability that a card is in the participant's hand.
     *
     * @param card is the card to evaluate.
     * @return the probability that the card is in the participant's hand.
     */
    int getProbabilityOf(ItalianCard card);

}
