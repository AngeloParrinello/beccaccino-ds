package it.unibo.sd.beccacino.controller.game;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import it.unibo.sd.beccacino.*;
import org.bson.BsonValue;
import org.bson.Document;

import java.util.List;
import java.util.Optional;

public class GameManagerImpl implements GameManager {

    private final DBManager dbManager;
    private final GameStub gameStub;

    public GameManagerImpl(GameStub gameStub) {
        this.dbManager = new DBManager();
        this.gameStub = gameStub;
    }


    @Override
    public void handleRequest(GameRequest request) {
        switch (request.getRequestType()) {
            case ("start") -> this.startGameRequestHandler(request);
            case ("set_briscola") -> this.setBriscolaRequestHandler(request);
            default -> {
            } // TODO: Log illegal request received.
        }
    }

    private void setBriscolaRequestHandler(GameRequest request) {
    }

    private void startGameRequestHandler(GameRequest request) {

    }

    private void createGame(GameRequest request) {
        if (this.checkRequestingPlayer(request)) {
            Document emptyGameDocument = this.createEmptyGame(request);
            BsonValue insertResponse = this.dbManager.insertDocument(emptyGameDocument, "games");
            String createdGameID = insertResponse.asObjectId().getValue().toString();
            if (!createdGameID.equals("")) {
                Game createdGame = this.getGameById(createdGameID);
                // TODO stub.sendresponse(createdGame)
            } else {
                // TODO cannot create game
            }
        } else {
            // TODO unauthorized player request
        }
    }

    private boolean checkRequestingPlayer(GameRequest request) {
        return request.getRequestingPlayer().equals(request.getLobby().getPlayers(0));
    }

    private Game getGameById(String id) {
        return this.dbManager.getGameById(id);
    }

    private void setBriscola(Suit suit) { /* TODO document why this method is empty */ }

    private void makePlay(Card playedCard, Optional<String> message) { /* TODO document why this method is empty */ }

    private void endGame(int gameID) { /* TODO document why this method is empty */ }

    private Document createEmptyGame(GameRequest request) {
        List<Player> playerList = request.getLobby().getPlayersList();
        PublicData publicData = PublicData.newBuilder()
                .setScoreTeam1(0)
                .setScoreTeam2(0)
                .setCurrentPlayer(playerList.get(0))
                .build();
        Game game = Game.newBuilder()
                .setPlayers(0, playerList.get(0))
                .setPlayers(1, playerList.get(1))
                .setPlayers(2, playerList.get(2))
                .setPlayers(3, playerList.get(3))
                .setPublicData(publicData)
                .build();
        String gameJson = "";
        try {
            JsonFormat.parser().ignoringUnknownFields().merge(gameJson, game.toBuilder());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return Document.parse(gameJson);
    }
}
