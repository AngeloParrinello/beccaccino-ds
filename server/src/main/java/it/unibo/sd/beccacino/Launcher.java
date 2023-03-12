package it.unibo.sd.beccacino;

import it.unibo.sd.beccacino.controller.game.AfkGuard;
import it.unibo.sd.beccacino.controller.game.GameStub;
import it.unibo.sd.beccacino.controller.lobby.LobbiesStub;

public class Launcher {
    public static void main(String[] args) {
        GameStub gameStub = new GameStub();
        LobbiesStub lobbiesStub = new LobbiesStub(gameStub);
        AfkGuard afkGuard = new AfkGuard(gameStub);
        while (true) {

        }
    }
}
