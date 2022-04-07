package it.unibo.sd.beccacino.controller.game;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import it.unibo.sd.beccacino.*;
import it.unibo.sd.beccacino.model.BeccacinoGame;
import it.unibo.sd.beccacino.model.BeccacinoGameImpl;
import org.bson.BsonValue;
import org.bson.Document;

import java.util.List;
import java.util.Objects;

public class GameUtilImpl implements GameUtil {
    private final DBManager dbManager;
    private final BeccacinoGame game;

    public GameUtilImpl() {
        this.dbManager = new DBManager();
        this.game = new BeccacinoGameImpl();
    }

    @Override
    public Document createNewGame(GameRequest request) {
        List<Player> playerList = request.getLobby().getPlayersList();
        List<PrivateData> privateDataList = game.dealCards(playerList);
        PublicData publicData = PublicData.newBuilder()
                .setScoreTeam1(0)
                .setScoreTeam2(0)
                .setMessage("")
                .setBriscola(Suit.DEFAULT_SUIT)
                .setCurrentPlayer(playerList.get(0))
                .build();
        Game game = Game.newBuilder()
                .setPublicData(publicData)
                .addAllPrivateData(privateDataList)
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

    @Override
    public boolean isPlayerLobbyLeader(GameRequest request) {
        return request.getRequestingPlayer().equals(request.getLobby().getPlayers(0));
    }

    @Override
    public Game getGameById(String id) {
        return this.dbManager.getGameById(id);
    }

    @Override
    public BsonValue insertGame(Document gameDocument) {
        return this.dbManager.insertDocument(gameDocument, "games");
    }

    @Override
    public void removeLobby(String id) {
        this.dbManager.removeDocument("_id", id, "lobbies");
    }

    @Override
    public boolean isLobbyFull(GameRequest request) {
        return this.dbManager.getLobbyById(request.getLobby().getId()).getPlayersCount() == 4;
    }

    @Override
    public boolean doesLobbyExists(String id) {
        return this.dbManager.getLobbyById(id) != null;
    }

    @Override
    public boolean isPlayerCurrentPlayer(GameRequest request) {
        Game game = this.getGameById(request.getGameId());
        return game.getPublicData().getCurrentPlayer().equals(request.getRequestingPlayer());
    }



    @Override
    public boolean isBriscolaSet(GameRequest request) {
        Game game = this.getGameById(request.getGameId());
        return (!Objects.equals(game.getPublicData().getBriscola(), Suit.DEFAULT_SUIT));
    }

    @Override
    public boolean setBriscola(GameRequest request) {
        return this.dbManager.updateBriscola(request.getBriscola(), request.getGameId());
    }
}
