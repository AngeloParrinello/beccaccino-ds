package com.example.beccaccino.model.logic;


import com.example.beccaccino.model.entities.ItalianCard.Suit;
import com.example.beccaccino.model.entities.Play;
import com.example.beccaccino.model.entities.Player;
import com.example.beccaccino.model.entities.Team;

import java.util.List;
import java.util.Optional;

/**
 * A game that needs outer input to proceed through every turn.
 */
public interface Game {
    /**
     * @return a list of this game players
     */
    List<Player> getPlayers();

    /**
     * @return a list of the teams in this game
     */
    List<Team> getTeams();

    /**
     * @return the current round
     */
    Round getCurrentRound();

    /**
     * @return the player that has to make his turn
     */
    Player getCurrentPlayer();

    /**
     * Get this game briscola suit, if present.
     *
     * @return an optional containing this briscola suit, or an empty optional
     * if there isn't a briscola
     */
    Optional<Suit> getBriscola();

    /**
     * Set this game briscola.
     *
     * @param briscola - the suit to be set as briscola
     */
    void setBriscola(Suit briscola);

    /**
     * This method is the only way to proceed through the match.
     *
     * @param play - the play that should be made
     */
    void makeTurn(Play play);

    /**
     * @return true if the match is over, false otherwise
     */
    boolean isOver();

    /**
     * @return the seed of this game
     */
    int getSeed();

    /**
     * @return the rounds of this game
     */
    List<Round> getRounds();
}
