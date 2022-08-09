package com.example.beccaccino.model.entities;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Optional;

/**
 * Alessia Rocco
 * Play Implementation.
 */
@Entity(tableName = "plays")
public class PlayImpl implements Play {
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @Ignore
    private final ItalianCard card;
    @Embedded
    private final ItalianCardImpl italianCard;
    private final String string;

    /**
     * Class constructor.
     *
     * @param card    the card has been played
     * @param message the eventually message thrown with the card
     */
    @Ignore
    public PlayImpl(final ItalianCard card, final Optional<String> message) {
        this.card = card;
        this.italianCard = (ItalianCardImpl) card;
        if (message.isPresent())
            this.string = message.get();
        else this.string = null;
    }

    public PlayImpl(final ItalianCardImpl italianCard, final String string) {
        this.card = italianCard;
        this.italianCard = italianCard;
        if (string.equals("")) {
            this.string = null;
        } else {
            this.string = string;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Ignore
    public ItalianCard getCard() {
        return this.card;
    }

    public ItalianCardImpl getItalianCard() {
        return this.italianCard;
    }

    /**
     * {@inheritDoc}
     */
    public String getString() {
        return this.string;
    }

    /**
     * {@inheritDoc}
     */
    @Ignore
    public Optional<String> getMessage() {
        if (string != null) {
            return Optional.of(this.string);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((card == null) ? 0 : card.hashCode());
        result = prime * result + ((string == null) ? 0 : string.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object play) {
        if (this == play) {
            return true;
        }
        if (play == null) {
            return false;
        }
        if (getClass() != play.getClass()) {
            return false;
        }
        PlayImpl other = (PlayImpl) play;
        if (card == null) {
            if (other.card != null) {
                return false;
            }
        } else if (!card.equals(other.card)) {
            return false;
        }
        if (string == null) {
            return other.string == null;
        } else return string.equals(other.string);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return card.toString() + " " + string;
    }
}
