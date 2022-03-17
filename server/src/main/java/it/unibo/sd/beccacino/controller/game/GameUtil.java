package it.unibo.sd.beccacino.controller.game;

import it.unibo.sd.beccacino.Game;
import it.unibo.sd.beccacino.GameRequest;
import org.bson.BsonValue;
import org.bson.Document;

/**
 * This class provides method to facilitate the handle of a game request.
 */
public interface GameUtil {

    /**
     * Creates the game document containing the initial state of the match.
     * @param request The details about the game to be created.
     * @return The game's Document.
     */
    Document createNewGame(GameRequest request);

    /**
     * Check if the player who made the start request is the lobby leader.
     * @param request the request made.
     * @return True if the player is the leader, false otherwise.
     */
    boolean isPlayerLobbyLeader(GameRequest request);

    /**
     * Retrieve a game from the db.
     * @param id the game's id.
     * @return the game.
     */
    Game getGameById(String id);

    /**
     * Add a game in the db.
     * @param gameDocument the initial state of the game.
     * @return the BsonValue containing the Game's ID.
     */
    BsonValue insertGame(Document gameDocument);

    /**
     * Remove a lobby from the DB. Usually performed after the start of a match.
     * @param id the lobby to remove.
     */
    void removeLobby(String id);


    boolean isLobbyFull(GameRequest request);

    boolean doesLobbyExists(String id);

    boolean isPlayerCurrentPlayer(GameRequest request);

    boolean isBriscolaSet(GameRequest request);

    boolean setBriscola(GameRequest request);
}
