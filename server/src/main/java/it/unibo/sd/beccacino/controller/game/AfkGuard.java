package it.unibo.sd.beccacino.controller.game;

import it.unibo.sd.beccacino.DBManager;
import it.unibo.sd.beccacino.ResponseCode;

import java.util.Timer;
import java.util.TimerTask;

public class AfkGuard {
    private final DBManager dbManager;

    public AfkGuard(GameStub stub) {
        this.dbManager = new DBManager();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dbManager.getStuckGamesList().forEach(game -> {
                    stub.sendGameResponse(game, ResponseCode.GAME_DELETED);
                });
            }
        }, 10000, 10000);
    }
}
