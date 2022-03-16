package it.unibo.sd.beccacino.model;

import it.unibo.sd.beccacino.Card;

/**
 * A beccaccino hand, capable of holding a maximum number of {@value #MAX_HAND_SIZE} cards.
 */
public class BeccacinoHand  extends HandTemplate{
    private static final int MAX_HAND_SIZE = 10;

    /**
     * Create an empty hand.
     */
    public BeccacinoHand() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void addCard(final Card card) {
        super.addCard(card);
        if (this.isFull()) {
            final BeccacinoBunchOfCards sorter = new BeccacinoBunchOfCards(this.getCards());
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
        final BeccacinoHand hand = (BeccacinoHand) obj;
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
