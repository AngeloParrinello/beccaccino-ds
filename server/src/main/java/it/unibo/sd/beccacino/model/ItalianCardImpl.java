package it.unibo.sd.beccacino.model;

public class ItalianCardImpl implements ItalianCard{
    private final Suit suit;
    private final Value value;

    public ItalianCardImpl(Suit suit, Value value) {
        this.suit = suit;
        this.value = value;
    }

    @Override
    public Suit getSuit() {
        return this.suit;
    }

    @Override
    public Value getValue() {
        return this.value;
    }
}
