package it.unibo.sd.beccacino.controller.game;

import it.unibo.sd.beccacino.GameRequest;

public interface GameRequestHandler {

    void handleRequest(GameRequest request);
    String startGameRequestHandler(GameRequest request);

    public GameStub getGameStub();

    public GameUtil getGameUtil();

}
