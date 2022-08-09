package com.example.beccaccino.model.artificialIntelligence;

import java.util.List;

import com.example.beccaccino.model.entities.ItalianCard;
import com.example.beccaccino.model.entities.Play;

/**
 * It is used to allow the AI to make the best play within a round of a game.
 */
public interface BestPlaySelector {
    /**
     * It allows to make the best play from the set of cards passed by
     * parameter.
     * 
     * @param listOfCards is a list of cards in which to choose one to play.
     * @return the play done.
     */
    Play doTheBestPlayFrom(List<ItalianCard> listOfCards);

    /**
     * It is used to "cut" with the best card the AI has in its hands.
     * 
     * @return the play done.
     */
    Play doTheBestTaglio();
}
