package it.unibo.sd.beccacino.controller.game;

import it.unibo.sd.beccacino.DBManager;
import it.unibo.sd.beccacino.ResponseCode;
import it.unibo.sd.beccacino.model.BeccacinoGameImpl;

import java.util.Timer;
import java.util.TimerTask;

public class AfkGuard {
    private final GameStub stub;
    private final DBManager dbManager;
    private Timer timer;

    public AfkGuard(GameStub stub) {
        this.stub = stub;
        this.dbManager = new DBManager();
        timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dbManager.getStuckGamesList().forEach(game -> {
                    stub.sendGameResponse(game, ResponseCode.GAME_DELETED);
                });
            }
        }, 10000, 10000);
    }
}
