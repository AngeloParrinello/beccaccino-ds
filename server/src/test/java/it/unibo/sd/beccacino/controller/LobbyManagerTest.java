package it.unibo.sd.beccacino.controller;

import it.unibo.sd.beccacino.DBManager;
import it.unibo.sd.beccacino.Player;
import it.unibo.sd.beccacino.Request;
import it.unibo.sd.beccacino.controller.lobby.LobbiesStub;
import it.unibo.sd.beccacino.controller.lobby.LobbyManager;
import it.unibo.sd.beccacino.controller.lobby.LobbyManagerImpl;
import org.junit.jupiter.api.*;

class LobbyManagerTest {
    private final LobbiesStub lobbiesStub = new LobbiesStub();
    private final LobbyManager lobbyManager = new LobbyManagerImpl(lobbiesStub);
    private final DBManager dbManager = new DBManager();
    private Player player;
    private Player player2;
    private Player player3;
    private Player player4;
    private Player player5;


    @BeforeEach
    void setUp(){
        this.player = Player.newBuilder().setId("1").setNickname("first_player").build();
        this.player2 = Player.newBuilder().setId("2").setNickname("second_player").build();
        this.player3 = Player.newBuilder().setId("3").setNickname("third_player").build();
        this.player4 = Player.newBuilder().setId("4").setNickname("fourth_player").build();
        this.player5 = Player.newBuilder().setId("5").setNickname("fifth_player").build();
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

    @Test
    void testJoinLobbyFail() {
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
        System.out.println("[TEST] second player join:" + this.lobbiesStub.getLastOperation());
        this.lobbyManager.handleRequest(Request.newBuilder()
                .setLobbyMessage("join")
                .setRequestingPlayer(this.player3)
                .setLobbyId(lobbyID)
                .build());
        System.out.println("[TEST] third player join:" + this.lobbiesStub.getLastOperation());
        this.lobbyManager.handleRequest(Request.newBuilder()
                .setLobbyMessage("join")
                .setRequestingPlayer(this.player4)
                .setLobbyId(lobbyID)
                .build());
        System.out.println("[TEST] fourth join:" + this.lobbiesStub.getLastOperation());
        this.lobbyManager.handleRequest(Request.newBuilder()
                .setLobbyMessage("join")
                .setRequestingPlayer(this.player5)
                .setLobbyId(lobbyID)
                .build());
        System.out.println("[TEST] fifth join:\n" + this.lobbiesStub.getLastOperation());
        System.out.println(this.lobbiesStub.getLastResponseCode());
    }

    @Test
    void testLeaveLobby() {
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
        this.lobbyManager.handleRequest(Request.newBuilder()
                .setLobbyMessage("leave")
                .setRequestingPlayer(this.player2)
                .setLobbyId(lobbyID)
                .build());
        System.out.println("[TEST] Lobby after leave:\n" + this.lobbiesStub.getLastOperation());
        System.out.println(this.lobbiesStub.getLastResponseCode());
    }

    @Test
    void testLastPlayerLeavesLobby() {
        this.lobbyManager.handleRequest(Request.newBuilder()
                .setLobbyMessage("create")
                .setRequestingPlayer(this.player)
                .build());
        System.out.println("[TEST] room created:" + this.lobbiesStub.getLastOperation());
        String lobbyID = this.lobbiesStub.getLastOperation().getId();
        this.lobbyManager.handleRequest(Request.newBuilder()
                .setLobbyMessage("leave")
                .setRequestingPlayer(this.player)
                .setLobbyId(lobbyID)
                .build());
        System.out.println("[TEST] lobby null: \n" + this.dbManager.getLobbyById(lobbyID));
    }
}
