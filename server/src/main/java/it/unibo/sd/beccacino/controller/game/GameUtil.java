package it.unibo.sd.beccacino.controller.game;

import it.unibo.sd.beccacino.Game;
import it.unibo.sd.beccacino.GameRequest;
import it.unibo.sd.beccacino.Player;
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

    /**
     * Check if the lobby is full, thus the game can be started.
     * @param request the game start request sent by the client.
     * @return true if the lobby is full, false otherwise.
     */
    boolean isLobbyFull(GameRequest request);

    /**
     * Check if the specified lobby exist.
     * @param id the id of the lobby.
     * @return true if the lobby exists, false otherwise.
     */
    boolean doesLobbyExists(String id);

    /**
     * Check if the player who made the request is the current player of the match.
     * @param game the current game.
     * @param requestingPlayer the player who made the request.
     * @return true if the requesting player is the current player, false otherwise.
     */
    boolean isPlayerCurrentPlayer(Game game, Player requestingPlayer);

    /**
     * Check if the briscola has been set.
     * @param game the current game.
     * @return true if the briscola has been set, false otherwise.
     */
    boolean isBriscolaSet(Game game);

    /**
     * Set the briscola for this round.
     * @param request the request sent by the client.
     * @return true if the operation is successful, false otherwise.
     */
    boolean setBriscola(GameRequest request);

    /**
     * Check if the player can play the card specified in the request.
     * @param request the request made by the client.
     * @return true if the player can play the card, false otherwise.
     */
    boolean isCardPlayable(GameRequest request);

    /**
     * Register the play made by the player.
     * @param request the request made by the client.
     * @return true if the operation is successful, false otherwise.
     */
    boolean makePlay(GameRequest request);

    /**
     * Update the current player of the round, to the next player.
     * @param GameID the game's ID.
     */
    void updateCurrentPlayer(String GameID);

    /**
     * Check if the 4th play is made, and eventually clear the table.
     * This method also check is the game is ended.
     * @param gameId the game's ID.
     */
    void checkAndClearTable(String gameId);

    /**
     * Check which player has won the round, and set the current player to the winner.
     * @param gameId the game's ID
     */
    void computeWinnerAndSetNextPlayer(String gameId);
}
