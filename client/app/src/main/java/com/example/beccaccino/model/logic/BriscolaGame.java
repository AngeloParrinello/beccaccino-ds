package com.example.beccaccino.model.logic;

import com.example.beccaccino.model.entities.Play;
import com.example.beccaccino.model.entities.Player;
import com.example.beccaccino.model.entities.Team;

import java.util.ArrayList;
import java.util.List;

public class BriscolaGame extends GameTemplate {

    private final List<Team> teams;

    public BriscolaGame(TurnOrder turnOrder, final Team team1, final Team team2) {
        super(turnOrder, null, null);
        this.teams = new ArrayList<>();
        this.teams.add(team1);
        this.teams.add(team2);
    }

    @Override
    public List<Team> getTeams() {
        return this.teams;
    }

    @Override
    protected Round newRound(TurnOrder turnOrder) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void firstTurnRoutine(Play play) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void roundOverRoutine() {
        // TODO Auto-generated method stub

    }

    @Override
    protected Player selectFirstPlayer() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getSeed() {
        // TODO Auto-generated method stub
        return 0;
    }

}
