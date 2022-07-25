package it.unibo.sd.beccacino.controller.game;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import it.unibo.sd.beccacino.*;
import it.unibo.sd.beccacino.model.BeccacinoBunchOfCards;
import it.unibo.sd.beccacino.model.BeccacinoGame;
import it.unibo.sd.beccacino.model.BeccacinoGameImpl;
import org.bson.BsonValue;
import org.bson.Document;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Override
    public boolean isCardPlayable(GameRequest request) {
        Game game = this.getGameById(request.getGameId());
        PrivateData requestingPlayerData = game.getPrivateData(game.getPlayersList().indexOf(request.getRequestingPlayer()));
        List<Card> playableCards = requestingPlayerData.getMyCardsList();
        boolean isCardInPlayerHand = playableCards.contains(request.getCardPlayed());
        boolean isCardDominantSuit = request.getCardPlayed().getSuit() == (game.getPublicData().getDominantSuit());
        boolean isCardBriscola = request.getCardPlayed().getSuit() == (game.getPublicData().getBriscola());
        if (game.getPublicData().getCardsOnTableCount() == 0) {
            return isCardInPlayerHand;
        } else if (canPlayerAnswerToPlay(game, playableCards)) {
            return isCardInPlayerHand && (isCardBriscola || isCardDominantSuit);
        } else {
            return true;
        }
    }

    private boolean canPlayerAnswerToPlay(Game game, List<Card> cardsInHand) {
        Suit briscola = game.getPublicData().getBriscola();
        Suit dominantSuit = game.getPublicData().getDominantSuit();
        long playableCards = cardsInHand.stream()
                .filter((c) -> (c.getSuit() == briscola || c.getSuit() == dominantSuit)).count();
        return playableCards > 0;
    }

    @Override
    public boolean makePlay(GameRequest request) {
        this.checkAndClearTable(request.getGameId());
        this.setDominantSuitIfNecessary(request);
        if (this.dbManager.setMessage(request.getCardMessage(), request.getGameId())) {
            return this.dbManager.registerPlay(request.getCardPlayed(), request.getGameId());
        } else {
            return false;
        }
    }

    private void setDominantSuitIfNecessary(GameRequest request) {
        Game game = this.getGameById(request.getGameId());
        if(game.getPublicData().getCardsOnTableCount() == 0) {
            this.dbManager.setDominantSuit(request.getCardPlayed().getSuit(), request.getGameId());
        }
    }

    @Override
    public void updateCurrentPlayer(String gameID) {
        Game game = this.getGameById(gameID);
        Player currentPlayer = game.getPublicData().getCurrentPlayer();
        int indexOfCurrentPlayer = game.getPlayersList().indexOf(currentPlayer);
        if(indexOfCurrentPlayer == 3) {
            indexOfCurrentPlayer = -1;
        }
        Player nextPlayer = game.getPlayersList().get(indexOfCurrentPlayer + 1);
        this.dbManager.setPlayerTurn(nextPlayer, gameID);
    }

    @Override
    public void checkAndClearTable(String gameId) {
        Game game = this.getGameById(gameId);
        if(game.getPublicData().getCardsOnTableCount() == 4) {
            this.dbManager.clearCardsOnTable(gameId);
        }
    }

    @Override
    public void computeWinnerAndSetNextPlayer(String gameId) {
        Game game = this.getGameById(gameId);
        if(game.getPublicData().getCardsOnTableCount() == 4) {
            List<Card> cardsPlayed = game.getPublicData().getCardsOnTableList();
            BeccacinoBunchOfCards cardsUtil = new BeccacinoBunchOfCards(cardsPlayed);
            Optional<Card> winningCard = cardsUtil.getHighestCardOfSuit(game.getPublicData().getBriscola());
            int index = 0;
            if (winningCard.isPresent()) {
                index = cardsPlayed.indexOf(winningCard.get());
            } else {
                winningCard = cardsUtil.getHighestCardOfSuit(game.getPublicData().getDominantSuit());
                index = cardsPlayed.indexOf(winningCard.get());
            }
            int playerIndex = game.getPlayersList().indexOf(game.getPublicData().getCurrentPlayer());
            for(int i=0; i<index; i++) {
                playerIndex++;
                if(playerIndex == 4) {
                    playerIndex = 0;
                }
            }
            this.dbManager.setPlayerTurn(game.getPlayersList().get(playerIndex), gameId);
            switch (playerIndex) {
             case 0, 2 -> this.dbManager.saveCardsWon(cardsPlayed, gameId, 1);
             case 1, 3 -> this.dbManager.saveCardsWon(cardsPlayed, gameId, 2);
            }
        }
    }
}
