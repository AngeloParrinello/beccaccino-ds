package com.example.beccaccino.model.entities;

import java.util.List;

/**
 * A team competing in the game.
 */
public interface Team {
    /**
     * Adds a player to this team.
     *
     * @param player to be added
     * @return true if the player has been successfully added, false otherwise
     */
    boolean addPlayer(Player player);

    /**
     * Returns this team players.
     *
     * @return a list of this team players.
     */
    List<Player> getPlayers();

    /**
     * Add a card to the cards won by this team.
     *
     * @param card - the won card
     */
    void addWonCard(ItalianCard card);

    /**
     * Returns all cards won by this team.
     *
     * @return a list of cards won by this team at the current moment. The order
     * is chronological
     */
    List<ItalianCard> getWonCards();

    /**
     * Assign points to this team.
     *
     * @param points - points to be assigned
     */
    void assignPoints(int points);

    /**
     * Returns points currently assigned to this team.
     *
     * @return this team points
     */
    int getPoints();

}
