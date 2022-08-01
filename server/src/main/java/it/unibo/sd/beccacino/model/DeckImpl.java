package it.unibo.sd.beccacino.model;

import it.unibo.sd.beccacino.Card;
import it.unibo.sd.beccacino.Suit;
import it.unibo.sd.beccacino.Value;

import java.util.*;

public class DeckImpl implements Deck{
    private final List<Card> deck;
    private final int seed;

    public DeckImpl(int seed) {
        this.seed = seed;
        this.deck = new ArrayList<>();
        this.populateDeck();
        Collections.shuffle(this.deck, new Random(this.seed));
    }

    private void populateDeck() {
        final EnumSet<Suit> suitList = EnumSet.allOf(Suit.class);
        final EnumSet<Value> valueList = EnumSet.allOf(Value.class);
        suitList.remove(Suit.UNRECOGNIZED);
        valueList.remove(Value.UNRECOGNIZED);
        suitList.remove(Suit.SUIT);
        valueList.remove(Value.VALUE);
        suitList.remove(Suit.DEFAULT_SUIT);
        for (Suit suit : suitList) {
            for (Value value : valueList) {
                Card card =  Card.newBuilder().setSuit(suit).setValue(value).build();
                this.deck.add(card);
            }
        }
    }

    @Override
    public Card drawCard() {
        if (remainingCards() == 0) {
            throw new IllegalStateException("Cards cannot be drawn if the deck is empty.");
        } else {
            return this.deck.remove(0);
        }
    }

    @Override
    public int getSeed() {
        return this.seed;
    }

    @Override
    public int remainingCards() {
        return this.deck.size();
    }
}
