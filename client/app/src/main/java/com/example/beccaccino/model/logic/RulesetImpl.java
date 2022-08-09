/*
package com.example.beccaccino.model.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.beccaccino.model.artificialIntelligence.AI;
import com.example.beccaccino.model.artificialIntelligence.AIImpl;
import com.example.beccaccino.model.artificialIntelligence.GameAnalyzer;
import com.example.beccaccino.model.artificialIntelligence.GameBasicAnalyzer;
import com.example.beccaccino.model.artificialIntelligence.GameMediumAnalyzer;
import com.example.beccaccino.model.entities.BeccaccinoHand;
import com.example.beccaccino.model.entities.Hand;
import com.example.beccaccino.model.entities.Player;
import com.example.beccaccino.model.entities.PlayerImpl;
import com.example.beccaccino.model.entities.Team;
import com.example.beccaccino.model.entities.TeamImpl;

*/
/**
 * The implementation of the Interface Ruleset.
 * <p>
 * {@inheritDoc}
 * <p>
 * {@inheritDoc}
 * <p>
 * {@inheritDoc}
 * <p>
 * {@inheritDoc}
 * <p>
 * This method creates two teams from a list of 4.
 *
 * @param playerList - The list of player to be divided.
 * @return teams
 *//*


public class RulesetImpl implements Ruleset {

    */
/**
 * {@inheritDoc}
 *//*

    public Game newGame(final List<Player> players) {
        TurnOrder turnOrder = new BasicTurnOrder(players);
        List<Team> teams = createTeams(players);
        Game game = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") 
                                                                        + System.getProperty("file.separator") 
                                                                        + "res" + System.getProperty("file.separator") + "settings.txt"));
            String line = reader.readLine();
            if (line == null) {
                reader.close();
                throw new FileNotFoundException();
            }
            if (line.equals("points_for_cricca: TRUE")) {
                game = new BeccaccinoGameWithCricca(turnOrder, teams.get(0), teams.get(1));
            } else {
                game = new BeccaccinoGame(turnOrder, teams.get(0), teams.get(1));
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error in reading Settings file.\nShutting down...");
            System.exit(1);
        }
        return game;
    }

    */
/**
 * {@inheritDoc}
 *//*

    public GameViewImpl newGameView(final Game game, final Stage stage) {
        GameViewImpl gameView = new GameViewImpl(game, stage);
        return gameView;
    }

    */
/**
 * {@inheritDoc}
 *//*

    public Player newPlayer(final String name) {
        Hand hand = new BeccaccinoHand();
        Player player = new PlayerImpl(name, hand);
        return player;
    }

    */
/**
 * {@inheritDoc}
 *//*

    public Optional<AI> newAI(final Player player, final String difficulty) {
        GameAnalyzer analyzer;
        if (difficulty == null || difficulty.equals("Basic AI")) {
            analyzer = new GameBasicAnalyzer(player.getHand().getCards());
        } else {
            analyzer = new GameMediumAnalyzer(player.getHand().getCards());
        }
        Optional<AI> ai = Optional.of(new AIImpl(player, analyzer));
        return ai;
    }
    */
/**
 * This method creates two teams from a list of 4.
 * @param playerList - The list of player to be divided.
 * @return teams
 *//*

    private List<Team> createTeams(final List<Player> playerList) {
        final List<Team> teams = new ArrayList<>();
        final Team team1 = new TeamImpl();
        final Team team2 = new TeamImpl();
        for (int i = 0; i < playerList.size(); i++) {
            if (i % 2 == 0) {
                team1.addPlayer(playerList.get(i));
            } else {
                team2.addPlayer(playerList.get(i));
            }
        }
        teams.add(team1);
        teams.add(team2);
        return teams;
    }

}
*/
