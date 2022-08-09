package com.example.beccaccino.room;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings")
public class Settings {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String player1;
    public String player2;
    public String player3;
    public String player4;

    public boolean cricca;

    public Settings() {

    }

    public Settings(String player1, String player2, String player3, String player4, boolean cricca) {
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.player4 = player4;
        this.cricca = cricca;
    }
}
