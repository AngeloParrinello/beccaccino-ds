package it.unibo.sd.beccacino.model;

import it.unibo.sd.beccacino.Player;
import it.unibo.sd.beccacino.PrivateData;

import java.util.List;

public interface BeccacinoGame {

    List<PrivateData> dealCards(List<Player> playerList);
}
