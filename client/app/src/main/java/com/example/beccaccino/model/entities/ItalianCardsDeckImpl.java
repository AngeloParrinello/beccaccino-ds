package com.example.beccaccino.model.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

/**
 * Implementation of the interface ItalianCardDeck.
 */
public class ItalianCardsDeckImpl implements ItalianCardsDeck {
    private List<ItalianCardImpl> cardDeck;
    private final int indexFirstCard = 0;
    private int seed;
    /**
     * Creates an instance of ItalianCardDeckImpl and populates it with all of the possible ItalianCards.
     */
    public ItalianCardsDeckImpl() {
        cardDeck = new ArrayList<ItalianCardImpl>();
        populateDeck();
        this.seed = new Random().nextInt();
        Collections.shuffle(cardDeck, new Random(this.seed));
    }

    public ItalianCardsDeckImpl(final int seed) {
        cardDeck = new ArrayList<ItalianCardImpl>();
        populateDeck();
        this.seed = seed;
        Collections.shuffle(cardDeck, new Random(this.seed));
    }

    /**
     * {@inheritDoc}
     */
    public ItalianCard drawCard() {
        if (remainingCards() == 0) {
            throw new IllegalStateException("Cards cannot be drawn if the deck is empty.");
        } else {
            return cardDeck.remove(indexFirstCard);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int remainingCards() {
        return cardDeck.size();
    } 
    /**
     * This private method populates a deck with all existing ItalianCards.
     */
    private void populateDeck() {
        EnumSet<ItalianCard.Suit> suitList = EnumSet.allOf(ItalianCard.Suit.class);
        EnumSet<ItalianCard.Value> valueList = EnumSet.allOf(ItalianCard.Value.class);
        for (ItalianCard.Suit suit : suitList) {
            for (ItalianCard.Value value : valueList) {
                cardDeck.add(new ItalianCardImpl(suit, value));
            }
        }
    }

    public int getSeed(){
        return this.seed;
    }

}
