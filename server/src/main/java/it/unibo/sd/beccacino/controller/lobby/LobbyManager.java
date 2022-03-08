package it.unibo.sd.beccacino.controller.lobby;

import it.unibo.sd.beccacino.Lobby;

import java.util.List;

public interface LobbyManager {

    /**
     * Get all the lobbies currently available.
     * @return the lobbies list.
     */
    void getLobbies();

    /**
     * Create a new lobby.
     * @return the lobby's ID.
     */
    void createLobby();

    /**
     * Get a specific lobby.
     * @param lobbyID the lobby's ID.
     * @return The lobby.
     */
    Lobby getLobby(int lobbyID);

    /**
     * Join an existent lobby.
     * @param lobbyID the lobby's ID.
     */
    void joinLobby(int lobbyID);

    /**
     *
     * @param lobbyID
     */
    void leaveLobby(int lobbyID);
}
