package it.unibo.sd.beccacino.controller;

import it.unibo.sd.beccacino.model.ItalianCard;
import it.unibo.sd.beccacino.model.Suit;

import java.util.Optional;

public interface GameManager {

    /**
     * Start the game for a given room.
     * @param roomID the room's ID.
     */
    void startGame(int roomID);

    /**
     * Set the briscola for the current match.
     * @param suit the briscola's suit.
     */
    void setBriscola(Suit suit);

    /**
     * Register a players' play.
     * @param playedCard the played card.
     */
    void makePlay(ItalianCard playedCard, Optional<String> message);

    /**
     * End the current game.
     * @param gameID the game's ID.
     */
    void endGame(int gameID);


}
