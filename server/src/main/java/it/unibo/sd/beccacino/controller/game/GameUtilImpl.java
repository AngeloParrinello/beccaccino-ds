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
    public boolean isLobbyFull(String id) {
        Lobby lobby = this.dbManager.getLobbyById(id);
        return lobby.getPlayersCount() == 4;
    }

    @Override
    public boolean doesLobbyExists(String id) {
        return this.dbManager.getLobbyById(id) != null;
    }

    @Override
    public boolean isPlayerCurrentPlayer(Game game, Player requestingPlayer) {
        return game.getPublicData().getCurrentPlayer().equals(requestingPlayer);
    }

    @Override
    public boolean isBriscolaSet(Game game) {
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
        if (game.getPublicData().getCardsOnTableCount() != 0) {
            if (canPlayerAnswerToPlay(game, playableCards)) {
                return isCardInPlayerHand && isCardDominantSuit;
            }
        }
        return isCardInPlayerHand;
    }

    private boolean canPlayerAnswerToPlay(Game game, List<Card> cardsInHand) {
        Suit dominantSuit = game.getPublicData().getDominantSuit();
        long numCardsDominantSuit = cardsInHand.stream()
                .filter((c) -> (c.getSuit() == dominantSuit)).count();
        return numCardsDominantSuit > 0;
    }

    @Override
    public boolean makePlay(GameRequest request) {
        this.checkAndClearTable(request.getGameId());
        this.setDominantSuitIfNecessary(request);
        if (this.dbManager.setMessage(request.getCardMessage(), request.getGameId())) {
            boolean status = this.dbManager.registerPlay(request.getCardPlayed(), request.getGameId());
            if(status){
                dbManager.removeCardFromHand(request.getGameId(), request.getCardPlayed());
                //System.out.println("Carta rimossa dalla mano");
            }
            return status;
        } else {
            return false;
        }
    }

    private void setDominantSuitIfNecessary(GameRequest request) {
        Game game = this.getGameById(request.getGameId());
        System.out.println("NumOfCardsOnTable: " + game.getPublicData().getCardsOnTableCount());
        if (game.getPublicData().getCardsOnTableCount() == 4) {
            this.dbManager.setDominantSuit(null, request.getGameId());
        }
        if (game.getPublicData().getCardsOnTableCount() == 0) {
            this.dbManager.setDominantSuit(request.getCardPlayed().getSuit(), request.getGameId());
        }
    }

    @Override
    public void updateCurrentPlayer(String gameID) {
        Game game = this.getGameById(gameID);
        Player currentPlayer = game.getPublicData().getCurrentPlayer();
        int indexOfCurrentPlayer = game.getPlayersList().indexOf(currentPlayer);
        if (indexOfCurrentPlayer == 3) {
            indexOfCurrentPlayer = -1;
        }
        Player nextPlayer = game.getPlayersList().get(indexOfCurrentPlayer + 1);
        this.dbManager.setPlayerTurn(nextPlayer, gameID);
    }

    @Override
    public void checkAndClearTable(String gameId) {
        Game game = this.getGameById(gameId);
        System.out.println("n cards on table: " + game.getPublicData().getCardsOnTableCount());
        if (game.getPublicData().getCardsOnTableCount() == 4) {
            this.dbManager.clearCardsOnTable(gameId);
            System.out.println("Clear table - Card number: " + game.getPublicData().getCardsOnTableCount());
        }
    }

    @Override
    public void computeWinnerAndSetNextPlayer(String gameId) {
        Game game = this.getGameById(gameId);
        if (game.getPublicData().getCardsOnTableCount() == 4) {
            List<Card> cardsPlayed = game.getPublicData().getCardsOnTableList();
            BeccacinoBunchOfCards cardsUtil = new BeccacinoBunchOfCards(cardsPlayed);
            Optional<Card> winningCard = cardsUtil.getHighestCardOfSuit(game.getPublicData().getBriscola());
            int index;
            if (winningCard.isPresent()) {
                index = cardsPlayed.indexOf(winningCard.get());
            } else {
                winningCard = cardsUtil.getHighestCardOfSuit(game.getPublicData().getDominantSuit());
                index = cardsPlayed.indexOf(winningCard.get());
            }
            int playerIndex = game.getPlayersList().indexOf(game.getPublicData().getCurrentPlayer());
            for (int i = 0; i < index; i++) {
                playerIndex++;
                if (playerIndex == 4) {
                    playerIndex = 0;
                }
            }
            if (isMatchOver(game)) {
                this.computePoints(game);
            } else {
                this.dbManager.setPlayerTurn(game.getPlayersList().get(playerIndex), gameId);
                switch (playerIndex) {
                    case 0, 2 -> this.dbManager.saveCardsWon(cardsPlayed, gameId, 1);
                    case 1, 3 -> this.dbManager.saveCardsWon(cardsPlayed, gameId, 2);
                }
            }
        }
    }

    private void computePoints(Game game) {
        BeccacinoBunchOfCards team1Cards = new BeccacinoBunchOfCards(game.getPublicData().getTeam1CardWonList());
        int team1Points = team1Cards.getPoints();
        this.dbManager.updateTeamPoints(game.getId(), team1Points, 1);
        BeccacinoBunchOfCards team2Cards = new BeccacinoBunchOfCards(game.getPublicData().getTeam2CardWonList());
        int team2Points = team2Cards.getPoints();
        this.dbManager.updateTeamPoints(game.getId(), team2Points, 2);
    }

    private boolean isMatchOver(Game game) {
        for (int i = 0; i < 4; i++) {
            if (game.getPrivateData(i).getMyCardsCount() != 0) {
                return false;
            }
        }
        return true;
    }
}
