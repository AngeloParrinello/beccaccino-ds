package it.unibo.sd.beccacino.controller.game;

import it.unibo.sd.beccacino.*;
import org.bson.BsonValue;
import org.bson.Document;

public class GameRequestHandlerImpl implements GameRequestHandler {
    private final GameStub gameStub;
    private final GameUtil gameUtil;

    public GameRequestHandlerImpl(GameStub gameStub) {
        this.gameStub = gameStub;
        this.gameUtil = new GameUtilImpl();
    }

    @Override
    public void handleRequest(GameRequest request) {
        switch (request.getRequestType()) {
            case ("start") -> this.startGameRequestHandler(request);
            case ("briscola") -> this.setBriscolaRequestHandler(request);
            case ("play") -> this.makePlayRequestHandler(request);
            default -> this.gameStub.sendGameResponse(null, ResponseCode.PERMISSION_DENIED);
        }
    }

    private void startGameRequestHandler(GameRequest request) {
        if (gameUtil.doesLobbyExists(request.getLobby().getId())) {
            if (this.gameUtil.isLobbyFull(request)) {
                if (this.gameUtil.isPlayerLobbyLeader(request)) {
                    Document emptyGameDocument = this.gameUtil.createNewGame(request);
                    BsonValue insertResponse = this.gameUtil.insertGame(emptyGameDocument);
                    String createdGameID = insertResponse.asObjectId().getValue().toString();
                    if (!createdGameID.equals("")) {
                        Game createdGame = this.gameUtil.getGameById(createdGameID);
                        this.gameUtil.removeLobby(request.getLobby().getId());
                        this.gameStub.sendGameResponse(createdGame, ResponseCode.OK);
                    } else {
                        this.gameStub.sendGameResponse(null, ResponseCode.START);
                    }
                } else {
                    this.gameStub.sendGameResponse(null, ResponseCode.PERMISSION_DENIED);
                }
            } else {
                this.gameStub.sendGameResponse(null, ResponseCode.START);
            }
        } else {
            this.gameStub.sendGameResponse(null, ResponseCode.ILLEGAL_REQUEST);
        }
    }

    private void setBriscolaRequestHandler(GameRequest request) {
        if (this.gameUtil.isPlayerCurrentPlayer(request)) {
            if (!this.gameUtil.isBriscolaSet(request)) {
                if (this.gameUtil.setBriscola(request)) {
                    Game updatedGame = this.gameUtil.getGameById(request.getGameId());
                    this.gameStub.sendGameResponse(updatedGame, ResponseCode.OK);
                } else {
                    this.gameStub.sendGameResponse(null, ResponseCode.FAIL);
                }
            } else {
                this.gameStub.sendGameResponse(null, ResponseCode.ILLEGAL_REQUEST);
            }
        } else {
            this.gameStub.sendGameResponse(null, ResponseCode.PERMISSION_DENIED);
        }
    }

    // TODO: if its the 4th play of a round we need to reset.
    private void makePlayRequestHandler(GameRequest request) {
        if (this.gameUtil.isPlayerCurrentPlayer(request)) {
            if (this.gameUtil.isBriscolaSet(request)) {
                if (this.gameUtil.isCardPlayable(request)) {
                    if(this.gameUtil.makePlay(request)) {
                        this.gameUtil.updateCurrentPlayer(request.getGameId());
                        // TODO we need to move the method below because if we clear here other player can't see which is the 4th card played
                        this.gameUtil.checkAndClearTable(request.getGameId());
                        Game updatedGame = this.gameUtil.getGameById(request.getGameId());
                        System.out.println(updatedGame.getPublicData().getCardsOnTableList());
                        this.gameStub.sendGameResponse(updatedGame, ResponseCode.OK);
                    } else {
                        this.gameStub.sendGameResponse(null, ResponseCode.FAIL);
                    }
                } else {
                    this.gameStub.sendGameResponse(null, ResponseCode.ILLEGAL_REQUEST);
                }
            } else {
                this.gameStub.sendGameResponse(null, ResponseCode.ILLEGAL_REQUEST);
            }
        } else {
            this.gameStub.sendGameResponse(null, ResponseCode.PERMISSION_DENIED);
        }
    }
}
