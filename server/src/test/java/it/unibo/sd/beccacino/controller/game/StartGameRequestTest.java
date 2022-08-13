package it.unibo.sd.beccacino.controller.game;

import it.unibo.sd.beccacino.*;
import it.unibo.sd.beccacino.controller.lobby.LobbiesStub;
import it.unibo.sd.beccacino.controller.lobby.LobbyManager;
import it.unibo.sd.beccacino.controller.lobby.LobbyManagerImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StartGameRequestTest {
    private final GameStub gameStub = new GameStub();
    private final GameRequestHandler gameRequestHandler = new GameRequestHandlerImpl(gameStub);
    private final LobbiesStub lobbiesStub = new LobbiesStub(new GameStub());
    private final LobbyManager lobbyManager = new LobbyManagerImpl(lobbiesStub, gameStub);
    private Player player;
    private Player player2;
    private Player player3;
    private Player player4;

    @BeforeEach
    void setup() {
        this.player = Player.newBuilder().setId("1").setNickname("first_player").build();
        this.player2 = Player.newBuilder().setId("2").setNickname("second_player").build();
        this.player3 = Player.newBuilder().setId("3").setNickname("third_player").build();
        this.player4 = Player.newBuilder().setId("4").setNickname("fourth_player").build();
    }

    @Test
    void testStartGame() {
        Lobby testLobby = createLobby();
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("start")
                .setLobby(testLobby)
                .setRequestingPlayer(this.player)
                .build());
        System.out.println(this.gameStub.getLastOperation());
        Assertions.assertNotNull(this.gameStub.getLastOperation());
    }

    @Test
    void testGameAlreadyStarted() {
        Lobby testLobby = createLobby();
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("start")
                .setLobby(testLobby)
                .setRequestingPlayer(this.player)
                .build());
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("start")
                .setLobby(testLobby)
                .setRequestingPlayer(this.player)
                .build());
        Assertions.assertEquals(ResponseCode.ILLEGAL_REQUEST, this.gameStub.getLastResponseCode());
    }

    @Test
    void testStartGameWithLobbyNotFull() {
        Lobby testLobby = createLobby();
        this.lobbyManager.handleRequest(Request.newBuilder()
                .setLobbyMessage("leave")
                .setRequestingPlayer(this.player2)
                .setLobbyId(testLobby.getId())
                .build());
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("start")
                .setLobby(testLobby)
                .setRequestingPlayer(this.player)
                .build());
        Assertions.assertEquals(ResponseCode.START_ERROR, this.gameStub.getLastResponseCode());
    }

    @Test
    void testStartGameWithoutPermissions() {
        Lobby testLobby = createLobby();
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("start")
                .setLobby(testLobby)
                .setRequestingPlayer(this.player2)
                .build());
        Assertions.assertEquals(ResponseCode.PERMISSION_DENIED, this.gameStub.getLastResponseCode());
    }

    private Lobby createLobby() {
        this.lobbyManager.handleRequest(Request.newBuilder()
                .setLobbyMessage("create")
                .setRequestingPlayer(this.player)
                .build());
        String lobbyID = this.lobbiesStub.getLastOperation().getId();
        this.lobbyManager.handleRequest(Request.newBuilder()
                .setLobbyMessage("join")
                .setRequestingPlayer(this.player2)
                .setLobbyId(lobbyID)
                .build());
        this.lobbyManager.handleRequest(Request.newBuilder()
                .setLobbyMessage("join")
                .setRequestingPlayer(this.player3)
                .setLobbyId(lobbyID)
                .build());
        this.lobbyManager.handleRequest(Request.newBuilder()
                .setLobbyMessage("join")
                .setRequestingPlayer(this.player4)
                .setLobbyId(lobbyID)
                .build());
        return this.lobbiesStub.getLastOperation();
    }
}

