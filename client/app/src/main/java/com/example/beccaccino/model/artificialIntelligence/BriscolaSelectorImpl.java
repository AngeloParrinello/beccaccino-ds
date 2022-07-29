package com.example.beccaccino.model.artificialIntelligence;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.example.beccaccino.model.entities.BeccaccinoBunchOfCards;
import com.example.beccaccino.model.entities.BunchOfCards;
import com.example.beccaccino.model.entities.ItalianCard;
import com.example.beccaccino.model.entities.ItalianCard.Suit;
import com.example.beccaccino.model.entities.ItalianCard.Value;
import com.example.beccaccino.model.entities.ItalianCardImpl;

/**
 * It is an implementation of briscola selector.
 */
public class BriscolaSelectorImpl implements BriscolaSelector {

    private final List<ItalianCard> myCards;
    private List<Suit> preferredSuitList;
    private Suit preferredSuit;
    private static final int UNACARTA = 1;
    private static final int DUECARTE = 2;
    private static final int TRECARTE = 3;
    private static final int QUATTROCARTE = 4;
    private static final int CINQUECARTE = 5;

    /**
     * Class constructor.
     * 
     * @param myCards are the cards in AI's hand.
     */
    public BriscolaSelectorImpl(final List<ItalianCard> myCards) {
        this.myCards = myCards;
        this.preferredSuitList = this.maxNumberCardsOfSuit();
        this.selector();
    }

    /**
     * {@inheritDoc}
     */
    public Suit getPreferredSuit() {
        return this.preferredSuit;
    }

    /**
     * It is used to understand the suits with more cards in AI's hand.
     * 
     * @return a list of suit that have more cards.
     */
    private List<Suit> maxNumberCardsOfSuit() {
        int max = 0;
        List<ItalianCard> cardOf;
        final BunchOfCards bunchOfCards = new BeccaccinoBunchOfCards(this.myCards);
        List<Suit> bestSuit = new LinkedList<>();
        for (ItalianCard card : this.myCards) {
            if (!bestSuit.contains(card.getSuit())) {
                cardOf = bunchOfCards.getCardsOfSuit(card.getSuit());
                if (cardOf.size() > max) {
                    max = cardOf.size();
                    bestSuit = new LinkedList<>();
                    bestSuit.add(card.getSuit());
                } else if (cardOf.size() == max) {
                    bestSuit.add(card.getSuit());
                }
            }
        }
        return bestSuit;
    }

    /**
     * It allows to select the best suit from AI hand.
     */
    private void selector() {
        int cont = 0;
        final Random rnd = new Random();
        while (this.preferredSuitList.size() != UNACARTA) {
            cont++;
            this.maxValueOfSuit();
            if (cont == DUECARTE && this.preferredSuitList.size() == QUATTROCARTE) {
                this.preferredSuit = this.preferredSuitList.get(rnd.nextInt(this.preferredSuitList.size()));
            } else if (cont == TRECARTE && this.preferredSuitList.size() == TRECARTE) {
                this.preferredSuit = this.preferredSuitList.get(rnd.nextInt(this.preferredSuitList.size()));
            } else if ((cont == CINQUECARTE || cont == QUATTROCARTE || cont == TRECARTE)
                    && this.preferredSuitList.size() == DUECARTE) {
                this.preferredSuit = this.preferredSuitList.get(rnd.nextInt(this.preferredSuitList.size()));
            }
        }
        // if it still does not come out of while it means that I have seeds with
        // the same number of cards and with the same card value

        this.preferredSuit = this.preferredSuitList.get(rnd.nextInt(this.preferredSuitList.size()));
    }

    /**
     * It allows to update the preferred suit list by comparing the values their
     * cards.
     */
    private void maxValueOfSuit() {
        ItalianCard max = new ItalianCardImpl(Suit.BASTONI, Value.QUATTRO); 
        final BeccaccinoCardComparator comparator = new BeccaccinoCardComparator();
        final BunchOfCards bunchOfCards = new BeccaccinoBunchOfCards(this.myCards);
        for (Suit suit : this.preferredSuitList) {
            if (bunchOfCards.getHighestCardOfSuit(suit).isPresent()) {
                ItalianCard maxOfSuit = bunchOfCards.getHighestCardOfSuit(suit).get();
                if (comparator.compare(maxOfSuit, max) > 0) {
                    max = maxOfSuit;
                    this.preferredSuitList = new LinkedList<>();
                    this.preferredSuitList.add(suit);
                } else if (comparator.compare(maxOfSuit, max) == 0) {
                    this.preferredSuitList.add(suit);
                }
            }
        }
        for (Suit suit : this.preferredSuitList) {
            if (bunchOfCards.getHighestCardOfSuit(suit).isPresent()) {
                this.myCards.remove(bunchOfCards.getHighestCardOfSuit(suit).get()); 
            }
        }
    }

}
