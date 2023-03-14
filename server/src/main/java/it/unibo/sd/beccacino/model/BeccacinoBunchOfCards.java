package it.unibo.sd.beccacino.model;

import it.unibo.sd.beccacino.Card;
import it.unibo.sd.beccacino.Suit;
import it.unibo.sd.beccacino.Value;

import java.util.*;

/**
 * "BunchOfCard" implementation for a game of "Beccaccino".
 */
public class BeccacinoBunchOfCards implements BunchOfCards {
    private final List<Card> listOfCards;
    private final Map<Value, Integer> pointsMap;

    /**
     * Class constructor.
     *
     * @param listOfCards is a list of cards.
     */
    public BeccacinoBunchOfCards(final List<Card> listOfCards) {
        this.listOfCards = listOfCards;
        if (this.listOfCards.size() == 0) {
            throw new IllegalArgumentException("Can't have 0 cards");
        }
        this.pointsMap = new HashMap<>();
        this.createPointsMap();
    }

    /**
     * {@inheritDoc}
     */
    public List<Card> getCardsOfSuit(final Suit suit) {
        final List<Card> cardsOfSuit = new LinkedList<>();
        final BeccacinoCardComparator comparator = new BeccacinoCardComparator();
        for (Card card : this.listOfCards) {
            if (card.getSuit().equals(suit)) {
                cardsOfSuit.add(card);
            }
        }
        cardsOfSuit.sort(comparator);
        return cardsOfSuit;
    }

    /**
     * {@inheritDoc}
     */
    public List<Card> getCardsOfValue(final Value value) {
        final List<Card> cardsOfValue = new LinkedList<>();
        for (Card card : this.listOfCards) {
            if (card.getValue().equals(value)) {
                cardsOfValue.add(card);
            }
        }
        return cardsOfValue;
    }

    /**
     * {@inheritDoc}
     */
    public int getPoints() {
        int sum = 0;
        for (Card card : this.listOfCards) {
            sum += this.pointsMap.get(card.getValue());
        }
        return sum;
    }

    /**
     * {@inheritDoc}
     */
    public Optional<Card> getLowestCardOfSuit(final Suit suit) {
        final BeccacinoCardComparator comparator = new BeccacinoCardComparator();
        final List<Card> cardsOfSuit = this.getCardsOfSuit(suit);
        cardsOfSuit.sort(comparator);
        if (!cardsOfSuit.isEmpty()) {
            return Optional.of(cardsOfSuit.get(0));
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    public Optional<Card> getHighestCardOfSuit(final Suit suit) {
        final BeccacinoCardComparator comparator = new BeccacinoCardComparator();
        final List<Card> cardsOfSuit = this.getCardsOfSuit(suit);
        cardsOfSuit.sort(comparator);
        if (!cardsOfSuit.isEmpty()) {
            final int indexMax = cardsOfSuit.size() - 1;
            return Optional.of(cardsOfSuit.get(indexMax));
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    public List<Card> getCardsWithMostPoints() {
        List<Card> cardsWithMorePoints = new LinkedList<>();
        int min = 0;
        for (Card card : this.listOfCards) {
            int temp = this.pointsMap.get(card.getValue());
            if (temp > min) {
                min = temp;
                cardsWithMorePoints = new LinkedList<>();
                cardsWithMorePoints.add(card);
            } else if (temp == min) {
                cardsWithMorePoints.add(card);
            }
        }
        return cardsWithMorePoints;
    }

    /**
     * {@inheritDoc}
     */
    public List<Card> getCardsWithLeastPoints() {
        List<Card> cardsWithLeastPoint = new LinkedList<>();
        int max = this.pointsMap.get(Value.ASSO);
        for (Card card : listOfCards) {
            int temp = this.pointsMap.get(card.getValue());
            if (temp < max) {
                max = temp;
                cardsWithLeastPoint = new LinkedList<>();
                cardsWithLeastPoint.add(card);
            } else if (temp == max) {
                cardsWithLeastPoint.add(card);
            }
        }
        return cardsWithLeastPoint;
    }

    /**
     * {@inheritDoc}
     */
    public List<Card> getHighestCards() {
        final BeccacinoCardComparator comparator = new BeccacinoCardComparator();
        this.listOfCards.sort(comparator);
        final int indexMax = this.listOfCards.size() - 1;
        final Value maxValue = this.listOfCards.get(indexMax).getValue();
        return this.getCardsOfValue(maxValue);
    }

    /**
     * {@inheritDoc}
     */
    public List<Card> getLowestCards() {
        final BeccacinoCardComparator comparator = new BeccacinoCardComparator();
        this.listOfCards.sort(comparator);
        final Value minValue = this.listOfCards.get(0).getValue();
        return this.getCardsOfValue(minValue);
    }

    /**
     * {@inheritDoc}
     */
    public List<Card> getOrderedCards() {
        final List<Card> orderedCards = new LinkedList<>();
        orderedCards.addAll(this.getCardsOfSuit(Suit.BASTONI));
        orderedCards.addAll(this.getCardsOfSuit(Suit.COPPE));
        orderedCards.addAll(this.getCardsOfSuit(Suit.DENARI));
        orderedCards.addAll(this.getCardsOfSuit(Suit.SPADE));
        return orderedCards;
    }

    /**
     * It is used to give each value the corresponding score.
     */
    private void createPointsMap() {
        int count = 0;
        pointsMap.put(Value.QUATTRO, count);
        pointsMap.put(Value.CINQUE, count);
        pointsMap.put(Value.SEI, count);
        pointsMap.put(Value.SETTE, count++);
        pointsMap.put(Value.FANTE, count);
        pointsMap.put(Value.CAVALLO, count);
        pointsMap.put(Value.RE, count);
        pointsMap.put(Value.DUE, count);
        pointsMap.put(Value.TRE, count);
        count += 2;
        pointsMap.put(Value.ASSO, count);
    }
}
