package it.unibo.sd.beccacino.controller;

import it.unibo.sd.beccacino.*;
import it.unibo.sd.beccacino.controller.game.GameManager;
import it.unibo.sd.beccacino.controller.game.GameManagerImpl;
import it.unibo.sd.beccacino.controller.game.GameStub;
import it.unibo.sd.beccacino.controller.lobby.LobbiesStub;
import it.unibo.sd.beccacino.controller.lobby.LobbyManager;
import it.unibo.sd.beccacino.controller.lobby.LobbyManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameManagerTest {
    private final GameStub gameStub = new GameStub();
    private final GameManager gameManager = new GameManagerImpl(gameStub);
    private final DBManager dbManager = new DBManager();
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
    void testStartGame() {
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
        Lobby testLobby = this.lobbiesStub.getLastOperation();
        System.out.println("[TEST] room:\n" + testLobby);
        this.gameManager.handleRequest(GameRequest.newBuilder()
                .setRequestType("start")
                .setLobby(testLobby)
                .setRequestingPlayer(this.player)
                .build());
        System.out.println("[TEST] Game created:\n" + this.gameStub.getLastOperation());
    }
}

