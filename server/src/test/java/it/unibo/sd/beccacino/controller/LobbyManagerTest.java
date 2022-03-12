package it.unibo.sd.beccacino.controller;

import it.unibo.sd.beccacino.Lobby;
import it.unibo.sd.beccacino.Player;
import it.unibo.sd.beccacino.Request;
import it.unibo.sd.beccacino.controller.lobby.LobbiesStub;
import it.unibo.sd.beccacino.controller.lobby.LobbyManager;
import it.unibo.sd.beccacino.controller.lobby.LobbyManagerImpl;
import org.junit.jupiter.api.*;

import java.util.Arrays;

public class LobbyManagerTest {
    private final LobbiesStub lobbiesStub = new LobbiesStub();
    private final LobbyManager lobbyManager = new LobbyManagerImpl(lobbiesStub);
    private Player player;
    private Player player2;

    @BeforeEach
    void setUp(){
        this.player = Player.newBuilder().setId("1").setNickname("my_player").build();
        this.player2 = Player.newBuilder().setId("2").setNickname("joined_player").build();
    }

    @Test
    void testCreateLobby() {
        this.lobbyManager.handleRequest(Request.newBuilder()
                                               .setLobbyMessage("create")
                                               .setRequestingPlayer(this.player)
                                               .build());
        System.out.println(this.lobbiesStub.getLastOperation());
    }

    @Test
    void testJoinLobby() {
        this.lobbyManager.handleRequest(Request.newBuilder()
                .setLobbyMessage("create")
                .setRequestingPlayer(this.player)
                .build());
        System.out.println("[TEST] room created:" + this.lobbiesStub.getLastOperation());
        String lobbyID = this.lobbiesStub.getLastOperation().getId();
        this.lobbyManager.handleRequest(Request.newBuilder()
                                                .setLobbyMessage("join")
                                                .setRequestingPlayer(this.player2)
                                                .setLobbyId(lobbyID)
                                                .build());
        System.out.println("LOBBY UPDATED:" + this.lobbiesStub.getLastOperation());
    }
}
