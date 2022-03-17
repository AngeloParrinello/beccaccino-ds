package it.unibo.sd.beccacino.controller.game;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import it.unibo.sd.beccacino.*;
import it.unibo.sd.beccacino.model.Deck;
import it.unibo.sd.beccacino.model.DeckImpl;
import org.bson.BsonValue;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class GameUtilImpl implements GameUtil {
    private final DBManager dbManager;

    public GameUtilImpl() {
        this.dbManager = new DBManager();
    }

    @Override
    public Document createNewGame(GameRequest request) {
        List<Player> playerList = request.getLobby().getPlayersList();
        List<PrivateData> privateDataList = this.dealCards(playerList);
        PublicData publicData = PublicData.newBuilder()
                .setScoreTeam1(0)
                .setScoreTeam2(0)
                .setMessage("")
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

    private List<PrivateData> dealCards(List<Player> playerList) {
        Deck deck = new DeckImpl(42);
        List<PrivateData> privateDataList = new ArrayList<>();
        for (Player p : playerList) {
            List<Card> cardList = new ArrayList<>();
            for(int i=0; i < 10; i++) {
                cardList.add(deck.drawCard());
            }
            privateDataList.add(PrivateData.newBuilder()
                    .setPlayer(p)
                    .addAllMyCards(cardList)
                    .build());
        }
        return privateDataList;
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
}
