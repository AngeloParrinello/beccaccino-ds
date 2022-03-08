package it.unibo.sd.beccacino.model;

import java.util.*;

public class DeckImpl implements Deck{
    private final List<ItalianCard> deck;
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
        for (Suit suit : suitList) {
            for (Value value : valueList) {
                ItalianCard card = new ItalianCardImpl(suit, value);
                this.deck.add(card);
            }
        }
    }

    @Override
    public ItalianCard drawCard() {
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
