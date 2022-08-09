package com.example.beccaccino.model.entities;

/**
 * Alessia Rocco 
 * Player Implementation.
 */
public class PlayerImpl implements Player {
    private String name;
    private Hand hand;

    /**
     * Class constructor.
     * 
     * @param name name of the player
     * @param hand hand of the player
     */
    public PlayerImpl(final String name, final Hand hand) {
        this.name = name;
        this.hand = hand;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    public Hand getHand() {
        return this.hand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
        PlayerImpl other = (PlayerImpl) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.name;
    }

}
