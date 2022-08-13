package com.example.beccaccino;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class GameActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {
    private Game game;
    private MyAdapter mAdapter;
    private RecyclerView recyclerView;
    private final List<Button> buttonsMessage = new ArrayList<>();
    //private GameViewModel viewModel;
    private Button buttonMessageSelected;
    private Map<String, ImageView> gameField;
    private Map<String, TextView> messageField;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Channel channel;
    private Connection connection;
    private final String todoQueueGames = "todoQueueGames";
    private final String resultsQueueGames = "resultsQueueGames";

    private boolean isMyTurn;
    // private List<ItalianCard> playableCards;
    private boolean amITheFirst = false;
    private boolean selectBriscola;
    private Player myPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();

        try {
            game = Game.parseFrom(intent.getByteArrayExtra("game"));
            myPlayer = Player.parseFrom(intent.getByteArrayExtra("player"));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        // Add OnClickListener to each message you can send when making a play.
        this.buttonsMessage.add(findViewById(R.id.messageBusso));
        this.buttonsMessage.add(findViewById(R.id.messageStriscio));
        this.buttonsMessage.add(findViewById(R.id.messageVolo));
        for (final Button button : buttonsMessage) {
            button.setOnClickListener(v -> chooseMessage(button));
            button.getBackground().setColorFilter(Color.parseColor("#9e9e9e"), PorterDuff.Mode.DARKEN);
        }

        // Create the horizontal recycler view to show the player's hand.
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        this.setupRabbitMQ();

        this.showUsers(game.getPlayersList().stream().map(Player::getNickname).collect(Collectors.toList()));
        this.updateRound();
    }

    @Override
    protected void onStop() {
        super.onStop();
        executorService.execute(() -> {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Termina partita");
        alertDialog.setMessage("Sei sicuro di voler uscire?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Annulla",
                (dialog, which) -> dialog.dismiss());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Esci",
                (dialog, which) -> {
                    // TODO Gestire l'uscita dalla Lobby
                    GameActivity.super.onBackPressed();
                });
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupRabbitMQ() {
        executorService.execute(() -> {
            try {
                connection = Utilities.createConnection();
                channel = connection.createChannel();

                //TODO aggiungere game-id e player-id
                Utilities.createQueue(channel, todoQueueGames, BuiltinExchangeType.DIRECT, todoQueueGames,
                        false, false, false, null, "");
                Utilities.createQueue(channel, resultsQueueGames, BuiltinExchangeType.DIRECT, resultsQueueGames + myPlayer.getId(),
                        false, false, false, null, "");

            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * On click listener for each card in the hand.
     **/
    @Override
    public void onItemClick(View view, int position) {
        if (this.isMyTurn) {
            String card = getResources().getResourceEntryName(mAdapter.getItem(position));
            String messaggio;
            if (this.buttonMessageSelected != null) {
                messaggio = this.buttonMessageSelected.getText().toString().toUpperCase();
                this.buttonMessageSelected.getBackground().setColorFilter(Color.parseColor("#9e9e9e"), PorterDuff.Mode.DARKEN);
            } else {
                messaggio = "";
            }
            Log.d("CARTA GIOCATA", card);
            // ItalianCardImpl italianCard = new ItalianCardImpl(card);
            if (this.selectBriscola) {
                // TODO CHIAMARE METDO CHE CONTROLLA LA BRISCOLA
                // produce set briscola
                //this.confirmBriscola(italianCard.getSuit());
            } else {
                if (Boolean.valueOf("TODO SE LA CARTA È GIOCABILE ALLORA LA FACCIO GIOCARE SENNO NO")) {
                    // TODO gestire la giocata con il server
                    this.isMyTurn = false;
                    this.buttonMessageSelected = null;
                } else {
                    SingleToast.show(this, "Gioca una carta di TODO METTERE IL TIPO DELLA BRISCOLA", Toast.LENGTH_LONG);
                }
            }
        }
    }

    private void confirmBriscola() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("La briscola che hai selezionato è TODO INSERIRE BRISCOLA");
        alert.setTitle("Conferma");
        alert.setCancelable(true);
        alert.setPositiveButton("Conferma", (dialog, whichButton) -> {
            selectBriscola = false;
            // TODO settare la briscola selezionata con il server

        });
        alert.setNegativeButton("Annulla", (dialog, whichButton) -> {
        });
        alert.show();
    }

    /**
     * Update the players'name, the score and the briscola.
     */
    private void updateMetadata() {
        // TODO displayare gli user con il metodo showUsers()

        /*Show Briscola*/
        TextView briscola = findViewById(R.id.briscola);
        briscola.setText(game.getPublicData().getBriscola().toString());

        /*Show score*/
        TextView score = findViewById(R.id.score);
        score.setText("TODO SETTARE I PUNTI GIUSTI");

        // TODO se la partita è finita showare l'endgameDialog con showGameRecap()
    }

    private void showGameRecap() {
        int score1 = game.getPublicData().getScoreTeam1();
        int score2 = game.getPublicData().getScoreTeam2();
        //boolean isMatchOver = Boolean.valueOf("Nel caso di match a più partite, capire se abbiamo finito o no");
        EndgameDialog recap = new EndgameDialog(this, game.getPlayers(0).getNickname(), game.getPlayers(1).getNickname(),
                game.getPlayers(2).getNickname(), game.getPlayers(3).getNickname(), score1, score2, true);
        recap.show();
    }

    /*Display users in the place they belong*/
    private void showUsers(List<String> players) {
        CircularList circularList = new CircularList(players);
        circularList.setNext(this.myPlayer.getNickname());
        TextView playerName1 = findViewById(R.id.playerName);
        TextView playerName2 = findViewById(R.id.playerWest);
        TextView playerName3 = findViewById(R.id.playerNorth);
        TextView playerName4 = findViewById(R.id.playerEast);
        playerName1.setText(circularList.next());
        playerName2.setText(circularList.next());
        playerName3.setText(circularList.next());
        playerName4.setText(circularList.next());
        if (gameField == null) {
            createGameField(players);
        }
    }

    /*Create fields where played card images will be put into*/
    private void createGameField(List<String> players) {
        gameField = new HashMap<>();
        messageField = new HashMap<>();
        List<ImageView> cardPlayedView = new ArrayList<>();
        cardPlayedView.add(findViewById(R.id.played));
        cardPlayedView.add(findViewById(R.id.cardPlayerWest));
        cardPlayedView.add(findViewById(R.id.cardPlayerNorth));
        cardPlayedView.add(findViewById(R.id.cardPlayerEast));
        List<TextView> messages = new ArrayList<>();
        messages.add(findViewById(R.id.messageSouth));
        messages.add(findViewById(R.id.messageWest));
        messages.add(findViewById(R.id.messageNorth));
        messages.add(findViewById(R.id.messageEast));
        for (int i = 0; i < 4; i++) {
            gameField.put(players.get(i), cardPlayedView.get(i));
            messageField.put(players.get(i), messages.get(i));
        }
    }

    private void updateRound() {
        TextView gameLog = findViewById(R.id.log);
        Player currentPlayer = game.getPublicData().getCurrentPlayer();
        System.out.println("IO: " + myPlayer);
        System.out.println("Current: " + currentPlayer);
        this.isMyTurn = currentPlayer.equals(this.myPlayer);
        if (game.getRound() == 1) {
            if (this.isMyTurn) {
                Log.d("PRIMO GIOCATORE", "IO");
                this.selectBriscola = true;
                String batezza = "Seleziona la briscola";
                gameLog.setText(batezza);
            } else {
                String staBatezzando = currentPlayer.getNickname() + " sta battezzando";
                gameLog.setText(staBatezzando);
            }
        } else {
            if (this.isMyTurn) {
                if (game.getRound() % 4 == 1) {
                    this.amITheFirst = true;
                }
                Log.d("TURN", this.myPlayer.getNickname());
            } else {
                Log.d("TURN", "THEIRTURN");
            }

            /*
            if (game.getRound() % 4 == 1 && game.getRound() > 1) {
                this.showPlays(rounds.get(rounds.size() - 2));
                //animation(rounds.get(rounds.size()-2), (rounds.get(rounds.size()-1).getCurrentPlayer().toString()));
            } else {
                this.showPlays(current);
            }*/
            String turn = "E' il turno di " + currentPlayer.getNickname();
            gameLog.setText(turn);
        }
        updateHand();
        updateButtonsVisibility();
        updateMetadata();
    }

    /*Decide which message buttons to make visible*/
    private void updateButtonsVisibility() {
        if (this.amITheFirst) {
            for (Button button : this.buttonsMessage) {
                button.setVisibility(View.VISIBLE);
            }
            this.amITheFirst = false;
        } else {
            for (Button button : this.buttonsMessage) {
                button.setVisibility(View.INVISIBLE);
            }
        }
    }

    /*Show the plays made in the given round*/
    private void showPlays() {
        List<String> nicknames = game.getPlayersList().stream().map(Player::getNickname).collect(Collectors.toList());

        List<Card> cards = game.getPublicData().getCardsOnTableList();

        int indexCurrent = nicknames.indexOf(game.getPublicData().getCurrentPlayer().getNickname());

        System.out.println("Current player index: " + indexCurrent);

        for (int i = 0; i < 4; i++) {
            ImageView placeholder = gameField.get(nicknames.get(decrement(indexCurrent, i)));
            int cardId = R.drawable.retro;
            System.out.println("i: " + i);
            System.out.println("decrementedIndex: " + nicknames.get(decrement(indexCurrent, i)));

            if(i < cards.size()){
                Card card = cards.get(i);
                cardId = getResources().getIdentifier(name(card), "drawable", "com.faventia.beccaccino");
            }
            if (i == cards.size() - 1){
                TextView messageHolder = messageField.get(nicknames.get(decrement(indexCurrent, i)));
                if(messageHolder!= null){
                    System.out.println("Setting message");
                    messageHolder.setText(game.getPublicData().getMessage());
                }
            }

            if (placeholder != null) {
                placeholder.setImageResource(cardId);
            }
        }
    }

    private void animation(String player) {
        ImageView target = gameField.get(player);
        Collection<ImageView> temp = gameField.values();
        temp.remove(target);
        for (ImageView view : temp) {
            if (target != null) {
                translate(view, target);
            }
        }
    }

    private void translate(View viewToMove, View target) {
        viewToMove.animate()
                .x(target.getX())
                .y(target.getY())
                .setDuration(500)
                .start();
    }

    /**
     * Update the hand of the human player.
     */
    private void updateHand() {
        List<Integer> hand = new ArrayList<>();
        for (Card card : game.getPrivateData(0).getMyCardsList()) {
            String nome = name(card);
            hand.add(getResources().getIdentifier(nome, "drawable", "com.faventia.beccaccino"));
        }
        mAdapter = new MyAdapter(getApplicationContext(), hand);
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Method to highlight the message that the player want to send.
     *
     * @param button the message
     */
    private void chooseMessage(Button button) {
        if (this.buttonMessageSelected == null) {
            this.buttonMessageSelected = button;
            button.setBackgroundColor(getResources().getColor(R.color.green));
        } else {
            for (Button buttonList : this.buttonsMessage) {
                if (buttonList == this.buttonMessageSelected) {
                    if (buttonList == button) {
                        buttonList.setBackgroundColor(getResources().getColor(R.color.com_facebook_device_auth_text));
                        this.buttonMessageSelected = null;
                        break;
                    } else {
                        buttonList.setBackgroundColor(getResources().getColor(R.color.com_facebook_device_auth_text));
                        button.setBackgroundColor(getResources().getColor(R.color.green));
                        this.buttonMessageSelected = button;
                        break;
                    }
                }
            }
        }
    }

    private String readUsername() throws FileNotFoundException {
        FileInputStream fis = getApplicationContext().openFileInput("username_file");
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        } finally {
            return stringBuilder.toString();
        }
    }

    private String name(Card card) {
        return card.getValue().toString().toLowerCase() + "di" + card.getSuit().toString().toLowerCase();
    }

    private int decrement(int num, int i){
        num -= i;
        if(num<0){
            num = 4 + num;
        }
        return num;
    }

}
