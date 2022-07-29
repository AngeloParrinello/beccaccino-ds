package com.example.beccaccino;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.beccaccino.model.artificialIntelligence.AI;
import com.example.beccaccino.model.artificialIntelligence.AIImpl;
import com.example.beccaccino.model.artificialIntelligence.GameAnalyzer;
import com.example.beccaccino.model.artificialIntelligence.GameBasicAnalyzer;
import com.example.beccaccino.model.artificialIntelligence.GameMediumAnalyzer;
import com.example.beccaccino.model.entities.BeccaccinoHand;
import com.example.beccaccino.model.entities.Hand;
import com.example.beccaccino.model.entities.ItalianCard;
import com.example.beccaccino.model.entities.PlayImpl;
import com.example.beccaccino.model.entities.Player;
import com.example.beccaccino.model.entities.PlayerImpl;
import com.example.beccaccino.model.entities.Team;
import com.example.beccaccino.model.entities.TeamImpl;
import com.example.beccaccino.model.logic.BasicTurnOrder;
import com.example.beccaccino.model.logic.BeccaccinoGame;
import com.example.beccaccino.model.logic.BeccaccinoGameWithCricca;
import com.example.beccaccino.model.logic.Game;
import com.example.beccaccino.model.logic.Round;
import com.example.beccaccino.model.logic.TurnOrder;
import com.example.beccaccino.repository.BeccaccinoRepository;
import com.example.beccaccino.repository.BeccaccinoRepositoryImpl;
import com.example.beccaccino.room.Metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameViewModel extends AndroidViewModel {
    private MutableLiveData<List<Round>> rounds;
    private MutableLiveData<List<ItalianCard>> hand;

    private LiveData<Metadata> metadata;
    private LiveData<List<PlayImpl>> plays;
    private BeccaccinoRepository repository;
    private Game game;
    private List<Player> players;
    private Map<Player, AI> ais;
    private Player firstPlayer;
    private Handler handler = new Handler();
    private int limit;
    private String matchType;

    private int aiDelay;



    public GameViewModel(@NonNull Application application) {
        super(application);
        this.repository = new BeccaccinoRepositoryImpl(application);


        plays = repository.getPlays();
        this.repository.clearMetadata();

        List<String> names = new ArrayList<>();
        SharedPreferences sh
                = application.getSharedPreferences("Settings",
                Context.MODE_PRIVATE);
        names.add(sh.getString("player1", "Paolo"));
        names.add(sh.getString("player2", "Tizio"));
        names.add(sh.getString("player3", "Caio"));
        names.add(sh.getString("player4", "Sempronio"));

        this.players = this.newPlayers(this.makeNamesUnique(names));

        this.limit = sh.getInt("match_limit", 31);
        this.matchType = sh.getString("match_limit_type", "punti");
        this.aiDelay = sh.getInt("ai_delay", 5000);

        this.ais = new HashMap<>();
        this.hand = new MutableLiveData<>();
        this.rounds = new MutableLiveData<>();
        this.metadata = this.getMetadata();
        this.createMetadata();
    }

    public void setBriscola(ItalianCard.Suit briscola){
        Metadata md =  this.metadata.getValue();
        Metadata updatedMetadata = new Metadata(md, briscola.toString());

        this.repository.updateMetadata(updatedMetadata);

        this.game.setBriscola(briscola);
        this.rounds.postValue(this.game.getRounds());
        for(AI ai : this.ais.values()){
            ai.setBriscola(briscola);
        }
        this.checkGameState();
    }

    public LiveData<List<Round>> getRounds(){
        return this.rounds;
    }

    public LiveData<List<ItalianCard>> getHand(){
        return this.hand;
    }

    public LiveData<Metadata> getMetadata() { return this.repository.getMetadata(); }

    public String getFirstPlayer() { return this.firstPlayer.getName(); }

    public void shutDownGame(){this.clearGame();}




    private void createMetadata(){
        Metadata metadata = new Metadata(players.get(0).getName(), players.get(1).getName(), players.get(2).getName(), players.get(3).getName());
        metadata.setLimit(this.limit);
        metadata.setType(this.matchType);
        metadata.setCricca(this.isCriccaEnabled());

        this.repository.addMetadata(metadata);
        this.repository.updateMetadata(metadata);
    }

    public void createGame(){
        //Metadata existentMetadata = this.metadata.getValue();
        if(/*existentMetadata == null*/true){
            /*If a new game should be started*/
            Log.d("VIEWMODEL","START NEW GAME");

            TurnOrder to = new BasicTurnOrder(this.players);
            List<Team> teams = this.createTeams(this.players);
            /*Check if normal or "with cricca" game should be created*/
            if(this.isCriccaEnabled()){
                if(this.firstPlayer == null){
                    this.game = new BeccaccinoGameWithCricca(to, teams.get(0), teams.get(1));
                }else{
                    this.game = new BeccaccinoGameWithCricca(to, teams.get(0), teams.get(1), this.getFirstPlayer());
                }
            }else{
                if(this.firstPlayer == null){
                    this.game = new BeccaccinoGame(to, teams.get(0), teams.get(1));
                }else{
                    this.game = new BeccaccinoGame(to, teams.get(0), teams.get(1), this.getFirstPlayer());
                }
            }
            Log.d("SEED", this.game.getSeed() + "");
            this.setAIs();
            this.repository.updateMetadata(new Metadata(this.metadata.getValue(), this.game.getSeed()));
            if(this.plays.getValue() != null){
                throw new IllegalStateException("Ci sono plays in memoria senza un metadata attivo");
            }
            this.makeAISelectBriscola();
        }else{
            /*If a game is already in progress*/
            Log.d("VIEWMODEL","RESUME GAME");
            /*List<String> names = new ArrayList<>();
            names.add(existentMetadata.getPlayer1());
            names.add(existentMetadata.getPlayer2());
            names.add(existentMetadata.getPlayer3());
            names.add(existentMetadata.getPlayer4());
            this.players = this.newPlayers(names);

            TurnOrder to = new BasicTurnOrder(this.players);
            List<Team> teams = this.createTeams(this.players);
            this.game = new BeccaccinoGame(to, teams.get(0), teams.get(1));
            this.setAIs();
            for(PlayImpl play : this.plays.getValue()){
                //TODO
            }
            */

        }
        this.firstPlayer = this.game.getCurrentPlayer();
        this.hand.postValue(this.players.get(0).getHand().getCards());
        this.rounds.postValue(new ArrayList<Round>());
        Log.d("PRIMOOOOOOOOOOOO", this.firstPlayer + " è il primo");
    }

    public void makePlay(PlayImpl play){
        this.addPlayToRepository(play);

        this.hand.postValue(this.players.get(0).getHand().getCards());

        this.checkGameState();
    }

    private void scheduleAIPlay(){
        final Runnable r = new Runnable() {
            public void run() {
                makeAIPlay();
            }
        };
        this.handler.postDelayed(r, this.aiDelay);
    }


    private void makeAIPlay(){
        final AI ai = this.ais.get(this.game.getCurrentPlayer());
        if (ai != null) {
            final PlayImpl play = (PlayImpl) ai.makePlay(this.game.getCurrentRound());
            this.addPlayToRepository(play);
            Log.d("AI PLAY", play.getCard().toString());
        }else{
            throw new NullPointerException("Player not in ais");
        }


        this.checkGameState();
    }


    private void checkGameState(){
        if(this.game.isOver()){
            List<Team> teams = this.game.getTeams();
            Metadata md =  this.metadata.getValue();
            Metadata updatedMetadata = new Metadata(md, teams.get(0).getPoints() / 3, teams.get(1).getPoints() / 3);

            if(updatedMetadata.getType().equals("Punti")) {
                if(updatedMetadata.getPoints13() >= updatedMetadata.getLimit() || updatedMetadata.getPoints24() >= updatedMetadata.getLimit()){
                    updatedMetadata.setMatchOver(true);
                }
            }
            if(updatedMetadata.getType().equals("Round")) {
                if(updatedMetadata.getGamesPlayed() == updatedMetadata.getLimit()){
                    updatedMetadata.setMatchOver(true);
                }
            }

            this.repository.updateMetadata(updatedMetadata);
            this.repository.clearPlays();

        }else{
            if(this.ais.containsKey(game.getCurrentPlayer())){
                this.scheduleAIPlay();
            }
        }
    }

    private void makeAISelectBriscola(){
        final AI ai = this.ais.get(game.getCurrentPlayer());

        if(ai != null){
            final Runnable r = new Runnable() {
                public void run() {
                    setBriscola(ai.selectBriscola());
                }
            };
            this.handler.postDelayed(r, 2000);
        }
    }


    private void setAIs(){
        List<String> aiEasyNames = new ArrayList<>();
        List<String> aiMediumNames = new ArrayList<>();
        aiEasyNames.add("IA Facile");
        aiEasyNames.add("IA Facile 1");
        aiEasyNames.add("IA Facile 2");
        aiEasyNames.add("IA Facile 3");
        aiMediumNames.add("IA Media");
        aiMediumNames.add("IA Media 1");
        aiMediumNames.add("IA Media 2");
        aiMediumNames.add("IA Media 3");
        for(Player player : this.players){
            if(aiEasyNames.contains(player.getName())){
                this.ais.put(player, this.newAI(player, "easy"));
            }
            if(aiMediumNames.contains(player.getName())){
                this.ais.put(player, this.newAI(player, "medium"));
            }
        }
    }


    private List<Player> newPlayers(final List<String> names){
        List<Player> players = new ArrayList<>();
        for(String name : names){
            players.add(this.newPlayer(name));
        }
        return players;
    }


    private Player newPlayer(final String name) {
        Hand hand = new BeccaccinoHand();
        return new PlayerImpl(name, hand);
    }


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

    public AI newAI(final Player player, final String difficulty) {
        GameAnalyzer analyzer;

        if (difficulty == null || difficulty.equals("easy")) {
            analyzer = new GameBasicAnalyzer(player.getHand().getCards());
        } else {
            analyzer = new GameMediumAnalyzer(player.getHand().getCards());
        }

        return new AIImpl(player, analyzer);
    }

    private void addPlayToRepository(PlayImpl play){
        this.game.makeTurn(play);
        this.rounds.postValue(this.game.getRounds());
        this.repository.addPlay(play);
    }

    private List<String> makeNamesUnique(List<String> names){
        List<String> result = new ArrayList<>();
        for(String name : names){
            if(result.contains(name)){
                int index = result.indexOf(name);
                result.set(index, result.get(index) + " 1");
                result.add(name + " 2");
            }else if(result.contains(name + " 1")){
                result.add(name + " 3");
            }else{
                result.add(name);
            }
        }
        return result;
    }

    private boolean isCriccaEnabled(){
        SharedPreferences sh
                = getApplication().getSharedPreferences("Settings",
                Context.MODE_PRIVATE);
        return sh.getBoolean("cricca", false);
    }

    private void clearGame() {
        this.repository.clearMetadata();
        this.repository.clearPlays();
    }
}
