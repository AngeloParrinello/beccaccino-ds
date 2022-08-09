package com.example.beccaccino;

import com.example.beccaccino.model.entities.Player;
import com.example.beccaccino.model.logic.BasicTurnOrder;

import java.util.ArrayList;
import java.util.List;

public class CircularList {
    private final List<String> players;
    private int index;

    /**
     *
     * @param players - a list of players. The sorting of the list determines
     * the turn order.
     */
    public CircularList(final List<String> players) {
        this.players = new ArrayList<>(players);
        this.index = 0;
    }

    /**
     * {@inheritDoc}
     */
    public String next() {
        final String nextPlayer = this.players.get(index);
        if (this.index == this.players.size() - 1) {
            this.index = 0;
        } else {
            this.index++;
        }
        return nextPlayer;
    }

    /**
     * {@inheritDoc}
     */
    public void setNext(final String player) {
        this.index = this.players.indexOf(player);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getPlayers() {
        final List<String> defensiveCopy = new ArrayList<>(this.players);
        return defensiveCopy;
    }

    /**
     * {@inheritDoc}.
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + ((players == null) ? 0 : players.hashCode());
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
        CircularList other = (CircularList) obj;
        if (index != other.index) {
            return false;
        }
        if (players == null) {
            if (other.players != null) {
                return false;
            }
        } else if (!players.equals(other.players)) {
            return false;
        }
        return true;
    }
}
