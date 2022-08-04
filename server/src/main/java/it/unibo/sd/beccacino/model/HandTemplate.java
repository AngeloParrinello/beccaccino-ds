package it.unibo.sd.beccacino.model;

import it.unibo.sd.beccacino.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic hand, capable of holding a number of cards depending on extending classes.
 */
public abstract class HandTemplate implements Hand {
    private final List<Card> cards;

    /**
     * Create an empty hand.
     */
    public HandTemplate() {
        this.cards = new ArrayList<>();
    }

    /**
     * @return a defensive copy of cards held by this hand.
     */
    public List<Card> getCards() {
        final List<Card> defensiveCopy = new ArrayList<>(this.cards);
        return defensiveCopy;
    }

    /**
     * Replace this hand cards with given ones.
     *
     * @param cards - list of cards to replace this hand cards with.
     */
    protected void setCards(final List<Card> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
    }

    /**
     * {@inheritDoc}
     */
    public void removeCard(final Card card) {
        this.cards.remove(card);
    }

    /**
     * {@inheritDoc}
     */
    public void addCard(final Card card) {
        if (!this.isFull()) {
            this.cards.add(card);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return this.getCards().toString();
    }
}
