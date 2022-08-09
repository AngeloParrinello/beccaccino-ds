package com.example.beccaccino.model.artificialIntelligence;

import com.example.beccaccino.model.entities.ItalianCard;
import com.example.beccaccino.model.entities.Play;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * it is an implementation of "Partecipant" in the case of a game of Beccaccino.
 */
public class PartecipantImpl implements Partecipant {

    private static final int INITIALPROBABILITY = 33;

    private final Map<ItalianCard, Integer> cardsProbability;
    private final List<Play> plays;

    /**
     * class constructor.
     *
     * @param remainingCards are the cards still playable.
     */
    public PartecipantImpl(final List<ItalianCard> remainingCards) {
        this.cardsProbability = this.setInitialCardProbability(remainingCards);
        this.plays = new LinkedList<>();
    }

    /**
     * {@inheritDoc}
     */
    public void addPlay(final Play play) {
        this.plays.add(play);
    }

    /**
     * {@inheritDoc}
     */
    public List<Play> getPlays() {
        return this.plays;
    }

    /**
     * {@inheritDoc}
     */
    public void removeCard(final ItalianCard card) {
        this.cardsProbability.remove(card);
    }

    /**
     * {@inheritDoc}
     */
    public void setProbabilityOf(final ItalianCard card, final int probability) {
        if (this.cardsProbability.containsKey(card)) {
            this.cardsProbability.put(card, probability);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getProbabilityOf(final ItalianCard card) {
        return this.cardsProbability.get(card);
    }

    // *** UTILITY ****//

    /**
     * it serves to set the initial probability that a card still playable is in
     * the partecipant's hand (33 percent).
     *
     * @param remainingCards are the card still playable.
     * @return a map that associates for each remaining card a probability that
     * it is in the participant's hand (33 percent).
     */
    private Map<ItalianCard, Integer> setInitialCardProbability(final List<ItalianCard> remainingCards) {
        final Map<ItalianCard, Integer> map = new HashMap<>();
        for (ItalianCard card : remainingCards) {
            map.put(card, INITIALPROBABILITY);
        }
        return map;
    }


}
