package it.unibo.sd.beccacino.model;

import it.unibo.sd.beccacino.Card;
import it.unibo.sd.beccacino.Suit;
import it.unibo.sd.beccacino.Value;

import java.util.List;
import java.util.Optional;

/**
 * A simple card container with utility methods.
 */
public interface BunchOfCards {
    /**
     * Returns contained cards of specified suit.
     *
     * @param suit - the suit you want to get cards of
     * @return a list of cards of specified suit, ordered by ascending value
     */
    List<Card> getCardsOfSuit(Suit suit);

    /**
     * Returns contained cards of specified value.
     *
     * @param value - the value you want to get cards of
     * @return a list of cards of specified value
     */
    List<Card> getCardsOfValue(Value value);

    /**
     * Compute the sum of points associated to each contained cards.
     *
     * @return points contained cards are worth
     */
    int getPoints();

    /**
     * Returns lowest value contained card of specified suit.
     *
     * @param suit - the suit you want to get card of
     * @return an optional containing lowest value card of given suit if
     * present, an empty optional otherwise
     */
    Optional<Card> getLowestCardOfSuit(Suit suit);

    /**
     * Returns highest value contained card of specified suit.
     *
     * @param suit - the suit you want to get card of
     * @return an optional containing highest value card of given suit if
     * present, an empty optional otherwise
     */
    Optional<Card> getHighestCardOfSuit(Suit suit);

    /**
     * Returns contained cards associated with the highest points.
     *
     * @return a list of cards with highest points associated.
     */
    List<Card> getCardsWithMostPoints();

    /**
     * Returns contained cards associated with the lowest points.
     *
     * @return a list of cards with lowest points associated.
     */
    List<Card> getCardsWithLeastPoints();

    /**
     * Returns contained cards associated with the highest value.
     *
     * @return a list of cards with highest value.
     */
    List<Card> getHighestCards();

    /**
     * Returns contained cards associated with the highest value.
     *
     * @return a list of cards with lowest value.
     */
    List<Card> getLowestCards();

    /**
     * Returns contained cards ordered by ascending value.
     *
     * @return a list of cards ordered by ascending value
     */
    List<Card> getOrderedCards();
}
