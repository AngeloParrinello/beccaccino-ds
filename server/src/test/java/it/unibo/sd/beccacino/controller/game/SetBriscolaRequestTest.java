package it.unibo.sd.beccacino.controller.game;

import it.unibo.sd.beccacino.*;
import it.unibo.sd.beccacino.controller.lobby.LobbiesStub;
import it.unibo.sd.beccacino.controller.lobby.LobbyManager;
import it.unibo.sd.beccacino.controller.lobby.LobbyManagerImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SetBriscolaRequestTest {
    private final GameStub gameStub = new GameStub();
    private final GameRequestHandler gameRequestHandler = new GameRequestHandlerImpl(gameStub);
    private final LobbiesStub lobbiesStub = new LobbiesStub();
    private final LobbyManager lobbyManager = new LobbyManagerImpl(lobbiesStub);
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
    void testSetBriscola() {
        Lobby testLobby = createLobby();
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("start")
                .setLobby(testLobby)
                .setRequestingPlayer(this.player)
                .build());
        String gameID = this.gameStub.getLastOperation().getId();
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("briscola")
                .setBriscola(Suit.COPPE)
                .setGameId(gameID)
                .setRequestingPlayer(this.player)
                .build());
        Assertions.assertEquals(Suit.COPPE, this.gameStub.getLastOperation().getPublicData().getBriscola());
    }

    @Test
    void testWrongPlayerSetBriscola() {
        Lobby testLobby = createLobby();
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("start")
                .setLobby(testLobby)
                .setRequestingPlayer(this.player)
                .build());
        String gameID = this.gameStub.getLastOperation().getId();
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("briscola")
                .setBriscola(Suit.COPPE)
                .setGameId(gameID)
                .setRequestingPlayer(this.player3)
                .build());
        Assertions.assertEquals(ResponseCode.PERMISSION_DENIED, this.gameStub.getLastResponseCode());
    }

    @Test
    void testSetBriscolaAlreadyDone() {
        Lobby testLobby = createLobby();
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("start")
                .setLobby(testLobby)
                .setRequestingPlayer(this.player)
                .build());
        String gameID = this.gameStub.getLastOperation().getId();
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("briscola")
                .setBriscola(Suit.COPPE)
                .setGameId(gameID)
                .setRequestingPlayer(this.player)
                .build());
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("briscola")
                .setBriscola(Suit.BASTONI)
                .setGameId(gameID)
                .setRequestingPlayer(this.player)
                .build());
        Assertions.assertEquals(ResponseCode.ILLEGAL_REQUEST, this.gameStub.getLastResponseCode());
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
