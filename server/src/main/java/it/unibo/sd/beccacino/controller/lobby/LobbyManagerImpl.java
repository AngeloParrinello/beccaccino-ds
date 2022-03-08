package it.unibo.sd.beccacino.controller.lobby;

import it.unibo.sd.beccacino.Lobby;
import it.unibo.sd.beccacino.Player;
import it.unibo.sd.beccacino.Request;
import it.unibo.sd.beccacino.controller.lobby.LobbyManager;

public class LobbyManagerImpl implements LobbyManager {
    private int id;
    private String message;
    private Player sender;

    @Override
    public void handleRequest(Request request) {
        this.id = request.getId();
        this.message = request.getLobbyMessage();
        this.sender = request.getRequestingPlayer();
    }

    private void getLobbies() {}

    private void createLobby() {}

    private void deleteLobby(int lobbyID) {}

    private Lobby getLobby(int lobbyID) {
        return null;
    }

    public void joinLobby(int lobbyID) {}

    public void leaveLobby(int lobbyID) {}
}
