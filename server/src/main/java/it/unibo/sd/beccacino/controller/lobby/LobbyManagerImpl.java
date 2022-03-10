package it.unibo.sd.beccacino.controller.lobby;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.mongodb.client.result.InsertOneResult;
import it.unibo.sd.beccacino.DBManager;
import it.unibo.sd.beccacino.Lobby;
import it.unibo.sd.beccacino.Player;
import it.unibo.sd.beccacino.Request;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class LobbyManagerImpl implements LobbyManager {

    private final DBManager dbManager;
    private final LobbiesStub lobbiesStub;
    private int lobbyCounter;

    private static final int ROOM_CAPACITY = 4;
    private static final int TARGET_SCORE = 41;

    private static final int LOBBY_EXIST_ERROR = 400;
    private static final int LOBBY_LEAVE_ERROR = 401;
    private static final int LOBBY_JOIN_ERROR = 402;
    private static final int LOBBY_OK = 200;

    public LobbyManagerImpl(LobbiesStub lobbiesStub) {
        this.dbManager = new DBManager();
        this.lobbiesStub = lobbiesStub;
        this.lobbyCounter = 1;
    }

    @Override
    public void handleRequest(Request request) {
        switch (request.getLobbyMessage()) {
            case ("create"):
                this.createLobbyRequestHandler(request);
                break;
            case ("join"):
                this.joinLobbyRequestHandler(request);
                break;
            case ("leave"):
                this.leaveLobbyRequestHandler(request);
                break;
        }
    }

    private void createLobbyRequestHandler(Request createLobbyRequest) {
        int newLobbyId = 394; //this.generateLobbyId();
        int playerCreatorId = createLobbyRequest.getRequestingPlayer().getId();
        String nicknamePlayerCreator = createLobbyRequest.getRequestingPlayer().getNickname();
        if (!this.doesLobbyExist(newLobbyId)) {
            if(this.createNewLobby(newLobbyId, playerCreatorId, nicknamePlayerCreator)){
                Lobby lobbyUpdated = this.getLobbyUpdate(newLobbyId);
                this.lobbiesStub.sendLobbyResponse(lobbyUpdated, LOBBY_OK);
            } else {
                this.lobbiesStub.sendLobbyResponse(null, LOBBY_EXIST_ERROR);
            }
        } else {
            this.lobbiesStub.sendLobbyResponse(null, LOBBY_EXIST_ERROR);
        }
    }

    private void joinLobbyRequestHandler(Request joinLobbyRequest) {
        int joinLobbyId = joinLobbyRequest.getLobbyId();
        Player playerJoined = joinLobbyRequest.getRequestingPlayer();

        if (this.doesLobbyExist(joinLobbyId) && this.getLobbySize(joinLobbyId) < ROOM_CAPACITY) {
            boolean statusRequest = this.dbManager.updateLobbyPlayers(playerJoined, joinLobbyId);
            Lobby lobbyUpdated = this.getLobbyUpdate(joinLobbyId);
            if(statusRequest) {
                this.lobbiesStub.sendLobbyResponse(lobbyUpdated, LOBBY_OK);
            } else {
                this.lobbiesStub.sendLobbyResponse(lobbyUpdated, LOBBY_JOIN_ERROR);
            }
        } else {
            this.lobbiesStub.sendLobbyResponse(null, LOBBY_EXIST_ERROR);
        }
    }

    private void leaveLobbyRequestHandler(Request leaveLobbyRequest) {
        Player removedPlayer = leaveLobbyRequest.getRequestingPlayer();
        int lobbyId = leaveLobbyRequest.getLobbyId();

        boolean statusRequest = this.dbManager.removeLobbyPlayer(removedPlayer, lobbyId);
        Lobby lobbyUpdated = this.getLobbyUpdate(lobbyId);

        if (statusRequest) {
            this.lobbiesStub.sendLobbyResponse(lobbyUpdated, LOBBY_OK);
            if (this.getLobbySize(lobbyId) > 0) this.deleteLobby(lobbyId);
        } else {
            this.lobbiesStub.sendLobbyResponse(lobbyUpdated, LOBBY_LEAVE_ERROR);
        }
    }

    private Lobby getLobbyUpdate(int lobbyId) {
        return this.dbManager.getLobbyById(lobbyId);
    }

    private void deleteLobby(int lobbyId) {
        this.dbManager.removeDocument("_id", lobbyId, "lobbies");
    }

    private int getLobbySize(int lobbyId) {
        return this.dbManager.getLobbyById(lobbyId).getPlayersCount();
    }

    private boolean doesLobbyExist(int lobbyId) {
        return (this.dbManager.getLobbyById(lobbyId)!=null);
    }

    private boolean createNewLobby(int newLobbyId, int playerCreatorId, String nicknamePlayerCreator) {
        Document newLobby = new Document("_id", newLobbyId)
                .append("room_capacity", ROOM_CAPACITY)
                .append("target_score", TARGET_SCORE)
                .append("players", List.of(new Document("_id", playerCreatorId).append("nickname", nicknamePlayerCreator)));

        return this.dbManager.insertDocument(newLobby,
                "lobbies").wasAcknowledged();

    }

    private int generateLobbyId() {
        return ++this.lobbyCounter;
    }
}
