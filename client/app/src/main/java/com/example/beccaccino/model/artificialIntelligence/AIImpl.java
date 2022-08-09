package com.example.beccaccino.model.artificialIntelligence;

import com.example.beccaccino.model.entities.Play;
import com.example.beccaccino.model.entities.Player;
import com.example.beccaccino.model.entities.ItalianCard.Suit;
import com.example.beccaccino.model.logic.Round;

/**
 * It defines an implementation of the AI.
 */
public class AIImpl implements AI {
    private final Player me;
    private BriscolaSelector selector;
    private final GameAnalyzer gameAnalyzer;
    private final BestPlaySelector chooser;
    private ConditionForTaglio conditionForTaglio;

    /**
     * Class constructor.
     * 
     * @param player is the virtual player associated with the AI.
     * @param gameAnalyzer is a game analyzer useful to act in the best way in a game.
     */
    public AIImpl(final Player player, final GameAnalyzer gameAnalyzer) {
        this.me = player;
        this.selector = new BriscolaSelectorImpl(this.me.getHand().getCards());
        this.gameAnalyzer = gameAnalyzer;
        this.chooser = new BestPlaySelectorImpl(this.gameAnalyzer);
    }

    /**
     * {@inheritDoc}
     */
    public Play makePlay(final Round currentRound) {
        this.gameAnalyzer.updateLastRound();
        this.gameAnalyzer.observePlays(currentRound);
        final Play myPlay;
        if (!currentRound.hasJustStarted()) {
            if (this.conditionForTaglio.areRespected()) { // if he can taglio
                myPlay = this.chooser.doTheBestTaglio();
            } else {
                myPlay = this.chooser.doTheBestPlayFrom(currentRound.getPlayableCards());
            }
        } else { //is the first to play in the round
            myPlay = this.chooser.doTheBestPlayFrom(currentRound.getPlayableCards());
        }
        this.gameAnalyzer.addMyPlay(myPlay);
        return myPlay;
    }

    /**
     * {@inheritDoc}
     */
    public Suit selectBriscola() {
        final Suit briscola = this.selector.getPreferredSuit();
        this.conditionForTaglio = new ConditionForTaglioImpl(this.gameAnalyzer, briscola);
        return briscola;
    }

    /**
     * {@inheritDoc}
     */
    public void setBriscola(final Suit briscola) {
        this.gameAnalyzer.setBriscola(briscola);
        this.conditionForTaglio = new ConditionForTaglioImpl(this.gameAnalyzer, briscola);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((me == null) ? 0 : me.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AIImpl other = (AIImpl) obj;
        if (me == null) {
            if (other.me != null) {
                return false;
            }
        } else if (!me.equals(other.me)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return this.me.toString();
    }

}
