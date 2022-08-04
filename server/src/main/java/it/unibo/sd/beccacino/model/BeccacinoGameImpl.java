package it.unibo.sd.beccacino.model;

import it.unibo.sd.beccacino.Card;
import it.unibo.sd.beccacino.Player;
import it.unibo.sd.beccacino.PrivateData;

import java.util.ArrayList;
import java.util.List;

public class BeccacinoGameImpl implements BeccacinoGame {

    @Override
    public List<PrivateData> dealCards(List<Player> playerList) {
        Deck deck = new DeckImpl(42);
        List<PrivateData> privateDataList = new ArrayList<>();
        for (Player p : playerList) {
            List<Card> cardList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                cardList.add(deck.drawCard());
            }
            privateDataList.add(PrivateData.newBuilder()
                    .setPlayer(p)
                    .addAllMyCards(cardList)
                    .build());
        }
        return privateDataList;
    }
}
