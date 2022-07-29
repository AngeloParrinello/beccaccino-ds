package com.example.beccaccino.model.entities;

/**
 * A user playing the game.
 */
public interface Player {
    /**
     * @return this player nickname
     */
    String getName();

    /**
     * @return this player hand
     */
    Hand getHand();
}
