package it.unibo.sd.beccacino.controller.game;

import it.unibo.sd.beccacino.*;
import it.unibo.sd.beccacino.controller.lobby.LobbiesStub;
import it.unibo.sd.beccacino.controller.lobby.LobbyManager;
import it.unibo.sd.beccacino.controller.lobby.LobbyManagerImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MakePlayRequestTest {
    private final GameStub gameStub = new GameStub();
    private final GameRequestHandler gameRequestHandler = new GameRequestHandlerImpl(gameStub);
    private final LobbiesStub lobbiesStub = new LobbiesStub();
    private final LobbyManager lobbyManager = new LobbyManagerImpl(lobbiesStub);
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;

    @BeforeEach
    void setup() {
        this.player1 = Player.newBuilder().setId("1").setNickname("first_player").build();
        this.player2 = Player.newBuilder().setId("2").setNickname("second_player").build();
        this.player3 = Player.newBuilder().setId("3").setNickname("third_player").build();
        this.player4 = Player.newBuilder().setId("4").setNickname("fourth_player").build();
    }

    @Test
    void testMakePlay() {
        Game testGame = this.startGame();
        Card testCard = testGame.getPrivateData(0).getMyCards(0);
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("play")
                .setGameId(testGame.getId())
                .setCardPlayed(testCard)
                .setRequestingPlayer(this.player1)
                .build());
        Card cardOnTable = this.gameStub.getLastOperation().getPublicData().getCardsOnTable(0);
        Assertions.assertEquals(testCard, cardOnTable);
    }

    @Test
    void testMakePlayWithMessage() {
        Game testGame = this.startGame();
        Card testCard = testGame.getPrivateData(0).getMyCards(0);
        String testMessage = "Busso";
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("play")
                .setGameId(testGame.getId())
                .setCardPlayed(testCard)
                .setCardMessage(testMessage)
                .setRequestingPlayer(this.player1)
                .build());
        String returnedMessage = this.gameStub.getLastOperation().getPublicData().getMessage();
        Assertions.assertEquals(testMessage, returnedMessage);
    }

    @Test
    void testWrongPlayerMakePlay() {
        Game testGame = this.startGame();
        Card testCard = testGame.getPrivateData(0).getMyCards(0);
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("play")
                .setGameId(testGame.getId())
                .setCardPlayed(testCard)
                .setRequestingPlayer(this.player3)
                .build());
        Assertions.assertEquals(ResponseCode.PERMISSION_DENIED, this.gameStub.getLastResponseCode());
    }

    @Test
    void testPlayNotInHandCard() {
        Game testGame = this.startGame();
        Card testCard = testGame.getPrivateData(2).getMyCards(0);
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("play")
                .setGameId(testGame.getId())
                .setCardPlayed(testCard)
                .setRequestingPlayer(this.player1)
                .build());
        Assertions.assertEquals(ResponseCode.ILLEGAL_REQUEST, this.gameStub.getLastResponseCode());
    }

    @Test
    void testPlayWrongSuitCard() {}

    private Game startGame() {
        this.lobbyManager.handleRequest(Request.newBuilder()
                .setLobbyMessage("create")
                .setRequestingPlayer(this.player1)
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
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("start")
                .setLobby(testLobby)
                .setRequestingPlayer(this.player1)
                .build());
        this.gameRequestHandler.handleRequest(GameRequest.newBuilder()
                .setRequestType("briscola")
                .setBriscola(Suit.COPPE)
                .setGameId(this.gameStub.getLastOperation().getId())
                .setRequestingPlayer(this.player1)
                .build());
        return this.gameStub.getLastOperation();
    }
}