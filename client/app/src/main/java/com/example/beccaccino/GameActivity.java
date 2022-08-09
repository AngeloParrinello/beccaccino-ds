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
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GameActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {
    private Game game;
    private List<Player> playerList;

    public List<Integer> hand = new ArrayList<>();
    private MyAdapter mAdapter;
    private RecyclerView recyclerView;
    private final List<Button> buttonsMessage = new ArrayList<>();
    //private GameViewModel viewModel;
    private Button buttonMessageSelected;
    private Map<String, ImageView> gameField;
    private Map<String, TextView> messageField;

    private boolean isMyTurn;
    // private List<ItalianCard> playableCards;
    private boolean amITheFirst = false;
    private boolean selectBriscola;
    private String myUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        this.myUsername = MainActivity.getUsername(this);

        Intent intent = getIntent();

        try {
            game = Game.parseFrom(intent.getByteArrayExtra("game"));
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
     *
     */
    private void updateMetadata() {
        // TODO displayare gli user con il metodo showUsers()

        /*Show Briscola*/
        TextView briscola = findViewById(R.id.briscola);
        briscola.setText("TODO SETTARE BRISCOLA");

        /*Show score*/
        TextView score = findViewById(R.id.score);
        score.setText("TODO SETTARE I PUNTI GIUSTI");

        // TODO se la partita è finita showare l'endgameDialog con showGameRecap()
    }

    private void showGameRecap() {
        int score1 = Integer.valueOf("TODO METTERE SCORE 1");
        int score2 = Integer.valueOf("TODO METTERE SCORE 2");
        boolean isMatchOver = Boolean.valueOf("TODO METTERE SE LA PARTITA È FINITA O MENO");
        EndgameDialog recap = new EndgameDialog(this, "TODO METTERE PLAYER1", "TODO METTERE PLAYER2",
                "TODO METTERE PLAYER3", "TODO METTERE PLAYER4", score1, score2, isMatchOver);
        recap.show();
    }

    /*Display users in the place they belong*/
    private void showUsers() {
        List<String> player = new ArrayList<>();
        player.add("TODO AGGIUNGERE PLAYER1");
        player.add("TODO AGGIUNGERE PLAYER2");
        player.add("TODO AGGIUNGERE PLAYER3");
        player.add("TODO AGGIUNGERE PLAYER4");
        CircularList circularList = new CircularList(player);
        circularList.setNext(this.myUsername);
        TextView playerName1 = findViewById(R.id.playerName);
        TextView playerName2 = findViewById(R.id.playerWest);
        TextView playerName3 = findViewById(R.id.playerNorth);
        TextView playerName4 = findViewById(R.id.playerEast);
        playerName1.setText(circularList.next());
        playerName2.setText(circularList.next());
        playerName3.setText(circularList.next());
        playerName4.setText(circularList.next());
        if (gameField == null) {
            createGameField(player);
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
        // TODO Gestione round con il server
        /*        TextView gameLog = findViewById(R.id.log);
        //        if (rounds.isEmpty()) {
        //            if (viewModel.getFirstPlayer().equals(this.myUsername)) {
        //                Log.d("PRIMO GIOCATORE", "IO");
        //                this.isMyTurn = true;
        //                this.selectBriscola = true;
        //                String turn = "Seleziona la briscola";
        //                gameLog.setText(turn);
        //            } else {
        //                String first = viewModel.getFirstPlayer() + " sta battezzando";
        //                gameLog.setText(first);
        //            }
        //        } else {
        //            Round current = rounds.get(rounds.size() - 1);
        //            if (current.getCurrentPlayer().toString().equals(this.myUsername)) {
        //                this.isMyTurn = true;
        //                this.playableCards = current.getPlayableCards();
        //                if (current.hasJustStarted()) {
        //                    this.amITheFirst = true;
        //                }
        //                Log.d("TURN", this.myUsername);
        //            } else {
        //                this.isMyTurn = false;
        //                Log.d("TURN", "THEIRTURN");
        //            }
        //            if (current.hasJustStarted() && rounds.size() > 1) {
        //                this.showPlays(rounds.get(rounds.size() - 2));
        //                //animation(rounds.get(rounds.size()-2), (rounds.get(rounds.size()-1).getCurrentPlayer().toString()));
        //            } else {
        //                this.showPlays(current);
        //            }
        //            String turn = "E' il turno di " + current.getCurrentPlayer();
        //            gameLog.setText(turn);
                }*/
        makeButtonInvisible();
    }

    /*Decide which message buttons to make visible*/
    private void makeButtonInvisible() {
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
//        List<Play> plays = round.getPlays();
//        List<String> users = round.getUsers();
//        for (int i = 0; i < 4; i++) {
//            ImageView placeholder = gameField.get(users.get(i));
//            TextView messageHolder = messageField.get(users.get(i));
//            int cardId = R.drawable.retro;
//            String mess = "";
//            if (i < plays.size()) {
//                Play play = plays.get(i);
//                cardId = getResources().getIdentifier(play.getCard().toString(), "drawable", "com.faventia.beccaccino");
//                if (play.getMessage().isPresent()) {
//                    mess = play.getMessage().get();
//                }
//            }
//            if (placeholder != null) {
//                placeholder.setImageResource(cardId);
//            }
//            if (messageHolder != null) {
//                messageHolder.setText(mess);
//            }
//        }
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
     *
     */
    private void updateHand() {
//        hand = new ArrayList<>();
//        for (ItalianCard card : italianCards) {
//            String nome = card.toString();
//            hand.add(getResources().getIdentifier(nome, "drawable", "com.faventia.beccaccino"));
//        }
//        mAdapter = new MyAdapter(getApplicationContext(), hand);
//        mAdapter.setClickListener(this);
//        recyclerView.setAdapter(mAdapter);
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

}
