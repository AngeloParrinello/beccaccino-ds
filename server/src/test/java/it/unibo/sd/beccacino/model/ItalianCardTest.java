package it.unibo.sd.beccacino.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItalianCardTest {
    private ItalianCard firstTestCard;
    private ItalianCard secondTestCard;

    @BeforeEach
    void setup() {
        this.firstTestCard = new ItalianCardImpl(Suit.DENARI, Value.SETTE);
        this.secondTestCard = new ItalianCardImpl(Suit.COPPE, Value.DUE);
    }

    @Test
    void testGetValue() {
        Assertions.assertEquals(Value.SETTE, this.firstTestCard.getValue());
    }

    @Test
    void testGetSuit() {
        Assertions.assertEquals(Suit.COPPE, this.secondTestCard.getSuit());
    }

    @Test
    void testCompare() {
        Assertions.assertTrue(this.firstTestCard.getValue().toInteger() <
                                        this.secondTestCard.getValue().toInteger());
    }
}
