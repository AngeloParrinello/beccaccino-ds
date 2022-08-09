package com.example.beccaccino.model.logic;

import com.example.beccaccino.model.entities.Play;
import com.example.beccaccino.model.entities.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This models the abstract concept of round.
 */
public abstract class RoundTemplate implements Round {
    private final TurnOrder turnOrder;
    private final List<Play> plays;
    private Player currentPlayer;
    private final List<String> orderedPlayers;

    /**
     * @param turnOrder - the turn order this round should follow.
     */
    public RoundTemplate(final TurnOrder turnOrder) {
        this.orderedPlayers = new ArrayList<>();
        for (int i = 0; i < turnOrder.getPlayers().size(); i++) {
            this.orderedPlayers.add(turnOrder.next().getName());
        }

        this.turnOrder = turnOrder;
        this.plays = new ArrayList<>();
        this.currentPlayer = this.turnOrder.next();
    }

    /**
     * {@inheritDoc}
     */
    public Player getCurrentPlayer() {
        this.checkIfNotOver();
        return this.currentPlayer;
    }

    /**
     * {@inheritDoc}
     */
    public void addPlay(final Play play) {
        this.checkIfNotOver();
        this.checkPlay(play);
        this.currentPlayer.getHand().removeCard(play.getCard());
        this.plays.add(play);
        this.currentPlayer = this.turnOrder.next();
    }

    /**
     * {@inheritDoc}
     */
    public List<Play> getPlays() {
        final List<Play> defensiveCopy = new ArrayList<>(this.plays);
        return defensiveCopy;
    }

    /**
     * This is a protection method checking the state of this round.
     *
     * @throws IllegalStateException if this round is over
     */
    protected void checkIfNotOver() {
        if (this.isOver()) {
            throw new IllegalStateException("This method can be called only if the round isn't over");
        }
    }

    /**
     * An utility method checking if the round has no plays.
     *
     * @return true if the round has no plays yet, false otherwise
     */
    public boolean hasJustStarted() {
        return this.plays.isEmpty();
    }

    /**
     * @return the players names in turn order
     */
    public List<String> getUsers() {
        return this.orderedPlayers;
    }

    /**
     * Protection method checking if the play is legal.
     *
     * @param play - the play to check
     */
    protected abstract void checkPlay(Play play);


}
