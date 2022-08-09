package com.example.beccaccino.room;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "metadata")
public class Metadata {
    @PrimaryKey(autoGenerate = true)
    public int id;

    private int seed;

    private final String player1;

    private final String player2;

    private final String player3;

    private final String player4;

    private String briscola;

    private int points13;

    private int points24;

    private int gamesPlayed;

    private boolean over;

    private boolean matchOver;

    private boolean cricca;

    private String type;

    private int limit;


    public Metadata(int seed, String player1, String player2, String player3, String player4) {
        this.seed = seed;
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.player4 = player4;
    }

    @Ignore
    public Metadata(String player1, String player2, String player3, String player4) {
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.player4 = player4;
    }


    /*Create new game*/
    @Ignore
    public Metadata(Metadata metadata, int seed) {
        this.id = metadata.id;
        this.player1 = metadata.player1;
        this.player2 = metadata.player2;
        this.player3 = metadata.player3;
        this.player4 = metadata.player4;
        this.briscola = null;
        this.limit = metadata.limit;
        this.type = metadata.type;
        this.points13 = metadata.points13;
        this.points24 = metadata.points24;
        this.gamesPlayed = metadata.gamesPlayed;

        this.seed = seed;
    }

    /*Set the briscola*/
    @Ignore
    public Metadata(Metadata metadata, String briscola) {
        this.id = metadata.id;
        this.player1 = metadata.player1;
        this.player2 = metadata.player2;
        this.player3 = metadata.player3;
        this.player4 = metadata.player4;
        this.seed = metadata.seed;
        this.limit = metadata.limit;
        this.type = metadata.type;
        this.points13 = metadata.points13;
        this.points24 = metadata.points24;
        this.gamesPlayed = metadata.gamesPlayed;

        this.briscola = briscola;
    }

    /*Game over*/
    @Ignore
    public Metadata(Metadata metadata, int points13, int points24) {
        this.id = metadata.id;
        this.player1 = metadata.player1;
        this.player2 = metadata.player2;
        this.player3 = metadata.player3;
        this.player4 = metadata.player4;
        this.seed = metadata.seed;
        this.limit = metadata.limit;
        this.type = metadata.type;
        this.briscola = metadata.briscola;

        this.over = true;
        this.points13 = metadata.points13 + points13;
        this.points24 = metadata.points24 + points24;
        this.gamesPlayed = metadata.gamesPlayed + 1;
    }


    public int getSeed() {
        return seed;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public String getPlayer3() {
        return player3;
    }

    public String getPlayer4() {
        return player4;
    }

    public String getBriscola() {
        return briscola;
    }

    public void setBriscola(String briscola) {
        this.briscola = briscola;
    }

    public int getPoints13() {
        return points13;
    }

    public void setPoints13(int points13) {
        this.points13 = points13;
    }

    public int getPoints24() {
        return points24;
    }

    public void setPoints24(int points24) {
        this.points24 = points24;
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public boolean isCricca() {
        return cricca;
    }

    public void setCricca(boolean cricca) {
        this.cricca = cricca;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isMatchOver() {
        return matchOver;
    }

    public void setMatchOver(boolean matchOver) {
        this.matchOver = matchOver;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }
}
