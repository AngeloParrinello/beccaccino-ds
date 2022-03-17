package it.unibo.sd.beccacino.controller.game;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import it.unibo.sd.beccacino.*;
import org.bson.BsonValue;
import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
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
        if (this.checkRequestingPlayer(request)) {
            Document emptyGameDocument = this.createEmptyGame(request);
            BsonValue insertResponse = this.dbManager.insertDocument(emptyGameDocument, "games");
            String createdGameID = insertResponse.asObjectId().getValue().toString();
            if (!createdGameID.equals("")) {
                Game createdGame = this.getGameById(createdGameID);
                this.dbManager.removeDocument("_id", request.getLobby().getId(), "lobbies");
                this.gameStub.sendGameResponse(createdGame, ResponseCode.OK);
            } else {
                this.gameStub.sendGameResponse(null, ResponseCode.START);
            }
        } else {
            this.gameStub.sendGameResponse(null, ResponseCode.PERMISSION_DENIED);
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
                .setMessage("")
                .setCurrentPlayer(playerList.get(0))
                .build();
        Game game = Game.newBuilder()
                .setPublicData(publicData)
                .addAllPlayers(playerList)
                .setRound(1)
                .build();
        try {
            return Document.parse(JsonFormat.printer().print(game.toBuilder()));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return null;
        }
    }
}
