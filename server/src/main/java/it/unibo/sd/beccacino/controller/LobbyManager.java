package it.unibo.sd.beccacino.controller;

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
    int createLobby();

    /**
     * Delete a lobby from the lobby's list.
     * @param lobbyID the lobby's ID.
     */
    void deleteLobby(int lobbyID);

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
}
