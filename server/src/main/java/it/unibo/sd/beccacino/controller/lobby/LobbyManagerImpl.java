package it.unibo.sd.beccacino.controller.lobby;

import it.unibo.sd.beccacino.*;
import org.bson.BsonValue;
import org.bson.Document;

import java.util.List;
import java.util.Objects;

public class LobbyManagerImpl implements LobbyManager {

    private final DBManager dbManager;
    private final LobbiesStub lobbiesStub;

    private static final int ROOM_CAPACITY = 4;
    private static final int TARGET_SCORE = 31;

    public LobbyManagerImpl(LobbiesStub lobbiesStub) {
        this.dbManager = new DBManager();
        this.lobbiesStub = lobbiesStub;
    }

    @Override
    public void handleRequest(Request request) {
        switch (request.getLobbyMessage()) {
            case ("create") -> this.createLobbyRequestHandler(request);
            case ("join") -> this.joinLobbyRequestHandler(request);
            case ("leave") -> this.leaveLobbyRequestHandler(request);
            default -> {
            } // TODO: Log illegal request received.
        }
    }

    private void createLobbyRequestHandler(Request createLobbyRequest) {
        Player requestingPlayer = createLobbyRequest.getRequestingPlayer();
        String roomID = this.createNewLobby(requestingPlayer).asObjectId().getValue().toString();
        if (!Objects.equals(roomID, "")) {
            Lobby lobbyUpdated = this.getLobby(roomID);
            this.lobbiesStub.sendLobbyResponse(lobbyUpdated, ResponseCode.OK);
        } else {
            this.lobbiesStub.sendLobbyResponse(null, ResponseCode.CREATE);
        }
    }

    private void joinLobbyRequestHandler(Request joinLobbyRequest) {
        String joinLobbyId = joinLobbyRequest.getLobbyId();
        Player playerJoined = joinLobbyRequest.getRequestingPlayer();
        if (this.doesLobbyExist(joinLobbyId) && this.getLobbySize(joinLobbyId) < ROOM_CAPACITY) {
            boolean statusRequest = this.dbManager.updateLobbyPlayers(playerJoined, joinLobbyId);
            Lobby lobbyUpdated = this.getLobby(joinLobbyId);
            if (statusRequest) {
                this.lobbiesStub.sendLobbyResponse(lobbyUpdated, ResponseCode.OK);
            } else {
                this.lobbiesStub.sendLobbyResponse(lobbyUpdated, ResponseCode.JOIN);
            }
        } else {
            // TODO could be also a join error because the lobby is full.
            this.lobbiesStub.sendLobbyResponse(null, ResponseCode.CREATE);
        }
    }

    private void leaveLobbyRequestHandler(Request leaveLobbyRequest) {
        Player removedPlayer = leaveLobbyRequest.getRequestingPlayer();
        String lobbyId = leaveLobbyRequest.getLobbyId();

        boolean statusRequest = this.dbManager.removeLobbyPlayer(removedPlayer, lobbyId);
        Lobby lobbyUpdated = this.getLobby(lobbyId);

        if (statusRequest) {
            this.lobbiesStub.sendLobbyResponse(lobbyUpdated, ResponseCode.OK);
            if (this.getLobbySize(lobbyId) == 0) {
                this.deleteLobby(lobbyId);
            }
        } else {
            this.lobbiesStub.sendLobbyResponse(lobbyUpdated, ResponseCode.LEAVE);
        }
    }

    private Lobby getLobby(String id) {
        return this.dbManager.getLobbyById(id);
    }

    private void deleteLobby(String id) {
        this.dbManager.removeDocument("_id", id, "lobbies");
    }

    private int getLobbySize(String id) {
        return this.getLobby(id).getPlayersList().size();
    }

    private boolean doesLobbyExist(String id) {
        return !this.dbManager.getLobbyById(id).getId().equals("");
    }

    private BsonValue createNewLobby(Player requestingPlayer) {
        // TODO use proto and parser class of protobuf to create proto-message and then convert in Json/Document
        // to push on DB
        Document newLobby = new Document("room_capacity", ROOM_CAPACITY)
                .append("target_score", TARGET_SCORE)
                .append("players", List.of(new Document("_id", requestingPlayer.getId())
                        .append("nickname", requestingPlayer.getNickname())));
        return this.dbManager.insertDocument(newLobby, "lobbies");
    }
}