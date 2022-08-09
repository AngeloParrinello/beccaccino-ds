package com.example.beccaccino.model.entities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Alessia Rocco
 * Team implementation.
 */
public class TeamImpl implements Team {
    private final List<Player> players;
    private final List<ItalianCard> cards;
    private int points;

    /**
     * Class constructor.
     *
     * @param players the players' team
     */
    public TeamImpl(final List<Player> players) {
        this.players = players;
        this.cards = new LinkedList<>();
        this.points = 0;
    }

    /**
     * Class constructor.
     */
    public TeamImpl() {
        this.players = new LinkedList<>();
        this.cards = new LinkedList<>();
        this.points = 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean addPlayer(final Player player) {
        return this.players.add(player);
    }

    /**
     * {@inheritDoc}
     */
    public List<Player> getPlayers() {
        List<Player> defenceCopyPlayers = new ArrayList<Player>(this.players);
        return defenceCopyPlayers;
    }

    /**
     * {@inheritDoc}
     */
    public void addWonCard(final ItalianCard card) {
        this.cards.add(card);
    }

    /**
     * {@inheritDoc}
     */
    public List<ItalianCard> getWonCards() {
        List<ItalianCard> defenceCopyWonCards = new ArrayList<ItalianCard>(this.cards);
        return defenceCopyWonCards;
    }

    /**
     * {@inheritDoc}
     */
    public void assignPoints(final int points) {
        this.points = this.points + points;
    }

    /**
     * {@inheritDoc}
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cards == null) ? 0 : cards.hashCode());
        result = prime * result + ((players == null) ? 0 : players.hashCode());
        result = prime * result + points;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object team) {
        if (this == team) {
            return true;
        }
        if (team == null) {
            return false;
        }
        if (getClass() != team.getClass()) {
            return false;
        }
        TeamImpl other = (TeamImpl) team;
        if (cards == null) {
            if (other.cards != null) {
                return false;
            }
        } else if (!cards.equals(other.cards)) {
            return false;
        }
        if (players == null) {
            if (other.players != null) {
                return false;
            }
        } else if (!players.equals(other.players)) {
            return false;
        }
        return points == other.points;
    }
}
