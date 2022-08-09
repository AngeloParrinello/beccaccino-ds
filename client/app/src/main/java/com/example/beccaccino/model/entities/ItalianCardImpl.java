package com.example.beccaccino.model.entities;


/**
 * Implementation of the ItalianCard Interface.
 */
public class ItalianCardImpl implements ItalianCard {
    private final Suit suit;
    private final Value value;

    /**
     * Creates an instance of an ItalianCard.
     *
     * @param suit  - the suit of the card to be created.
     * @param value - the value of the card to be created.
     */
    public ItalianCardImpl(final Suit suit, final Value value) {
        this.suit = suit;
        this.value = value;
    }

    public ItalianCardImpl(String string) {
        String[] tokens = string.split("di");

        this.value = Value.valueOf(tokens[0].toUpperCase());
        this.suit = Suit.valueOf(tokens[1].toUpperCase());
    }

    /**
     * {@inheritDoc}
     */
    public Suit getSuit() {
        return this.suit;
    }

    /**
     * {@inheritDoc}
     */
    public Value getValue() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((suit == null) ? 0 : suit.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ItalianCardImpl other = (ItalianCardImpl) obj;
        if (suit != other.suit) {
            return false;
        }
        return value == other.value;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return this.value.toString().toLowerCase() + "di" + this.suit.toString().toLowerCase();
    }

}
