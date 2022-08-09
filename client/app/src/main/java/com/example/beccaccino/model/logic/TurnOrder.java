package com.example.beccaccino.model.logic;

import com.example.beccaccino.model.entities.Player;

import java.util.List;

/**
 * A circular iterator over a list of players.
 */
public interface TurnOrder {
    /**
     * Returns the next player in the iteration.
     *
     * @return the next player
     */
    Player next();

    /**
     * Set the given player as next.
     *
     * @param player - the player to be set as next
     */
    void setNext(Player player);

    /**
     * Get a list view of players.
     *
     * @return a list of all players scheduled in this iterator
     */
    List<Player> getPlayers();
}
