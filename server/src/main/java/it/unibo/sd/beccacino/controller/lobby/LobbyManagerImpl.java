package it.unibo.sd.beccacino.controller.lobby;

import it.unibo.sd.beccacino.Request;

import javax.print.Doc;

public class LobbyManagerImpl implements LobbyManager {

    @Override
    public void handleRequest(Request request) {
        switch (request.getLobbyMessage()) {
            case ("create"):
            case ("join"):
        }
    }
}
