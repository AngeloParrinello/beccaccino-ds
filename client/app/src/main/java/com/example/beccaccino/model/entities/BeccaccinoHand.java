package com.example.beccaccino.model.entities;

import androidx.room.Entity;

/**
 * A beccaccino hand, capable of holding a maximum number of {@value #MAX_HAND_SIZE} cards.
 */
@Entity
public class BeccaccinoHand extends HandTemplate {
    private static final int MAX_HAND_SIZE = 10;

    /**
     * Create an empty hand.
     */
    public BeccaccinoHand() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void addCard(final ItalianCard card) {
        super.addCard(card);
        if (this.isFull()) {
            final BeccaccinoBunchOfCards sorter = new BeccaccinoBunchOfCards(this.getCards());
            this.setCards(sorter.getOrderedCards());
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFull() {
        if (this.getCards().size() == MAX_HAND_SIZE) {
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return this.getCards().size() == 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        final BeccaccinoHand hand = (BeccaccinoHand) obj;
        if (hand.getCards().equals(this.getCards())) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return this.getCards().hashCode();
    }
}
