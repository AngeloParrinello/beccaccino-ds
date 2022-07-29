package com.example.beccaccino.model.entities;

import java.util.List;
import java.util.Optional;
import com.example.beccaccino.model.entities.ItalianCard.*;

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
    List<ItalianCard> getCardsOfSuit(Suit suit);

    /**
     * Returns contained cards of specified value.
     * 
     * @param value - the value you want to get cards of
     * @return a list of cards of specified value
     */
    List<ItalianCard> getCardsOfValue(Value value);

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
    Optional<ItalianCard> getLowestCardOfSuit(Suit suit);

    /**
     * Returns highest value contained card of specified suit.
     * 
     * @param suit - the suit you want to get card of
     * @return an optional containing highest value card of given suit if
     * present, an empty optional otherwise
     */
    Optional<ItalianCard> getHighestCardOfSuit(Suit suit);

    /**
     * Returns contained cards associated with the highest points.
     * 
     * @return a list of cards with highest points associated.
     */
    List<ItalianCard> getCardsWithMostPoints();

    /**
     * Returns contained cards associated with the lowest points.
     * 
     * @return a list of cards with lowest points associated.
     */
    List<ItalianCard> getCardsWithLeastPoints();

    /**
     * Returns contained cards associated with the highest value.
     * 
     * @return a list of cards with highest value.
     */
    List<ItalianCard> getHighestCards();

    /**
     * Returns contained cards associated with the highest value.
     * 
     * @return a list of cards with lowest value.
     */
    List<ItalianCard> getLowestCards();

    /**
     * Returns contained cards ordered by ascending value.
     * 
     * @return a list of cards ordered by ascending value
     */
    List<ItalianCard> getOrderedCards();
}
