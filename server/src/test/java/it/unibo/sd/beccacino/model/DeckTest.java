package it.unibo.sd.beccacino.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeckTest {
    private DeckImpl testDeck;
    private static final int SEED = 23;
    private final int numberOfCardOnCreation = 40;

    @BeforeEach
    void setup() {
        this.testDeck = new DeckImpl(SEED);
    }

    @Test
    void testRemainingCards() {
        Assertions.assertEquals(this.numberOfCardOnCreation, this.testDeck.remainingCards());
    }

    @Test
    void testGetSeed() {
        Assertions.assertEquals(SEED, this.testDeck.getSeed());
    }

    @Test
    void testDrawCard() {
        this.testDeck.drawCard();
        Assertions.assertEquals(this.numberOfCardOnCreation - 1, this.testDeck.remainingCards());
    }


}
