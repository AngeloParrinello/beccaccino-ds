package it.unibo.sd.beccacino;

import it.unibo.sd.beccacino.controller.game.AfkGuard;
import it.unibo.sd.beccacino.controller.game.GameStub;
import it.unibo.sd.beccacino.controller.lobby.LobbiesStub;

import java.util.concurrent.CountDownLatch;

public class Launcher {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        GameStub gameStub = new GameStub();
        LobbiesStub lobbiesStub = new LobbiesStub(gameStub);
        AfkGuard afkGuard = new AfkGuard(gameStub);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
