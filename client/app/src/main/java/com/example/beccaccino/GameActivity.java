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
import com.rabbitmq.client.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class GameActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {
    private final List<Button> buttonsMessage = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final String todoQueueGames = "todoQueueGames";
    private final String resultsQueueGames = "resultsQueueGames";
    private Game game;
    private MyAdapter mAdapter;
    private RecyclerView recyclerView;
    private Button buttonMessageSelected;
    private Map<String, ImageView> gameField;
    private Map<String, TextView> messageField;
    private Channel channel;
    private Connection connection;
    private boolean isMyTurn;
    private List<Card> playableCards;
    private boolean amITheFirst = false;
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

        this.showUsers(game.getPlayersList().stream().map(Player::getNickname).collect(Collectors.toList()));
        this.update();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (connection == null || !connection.isOpen()) {
            setupRabbitMQ();
        }
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

    private void setupRabbitMQ() {
        executorService.execute(() -> {
            try {
                connection = Utilities.createConnection();
                channel = connection.createChannel();

                Utilities.createQueue(channel, todoQueueGames, BuiltinExchangeType.DIRECT, todoQueueGames,
                        false, false, false, null, "");
                Utilities.createQueue(channel, resultsQueueGames + game.getId() + myPlayer.getId(), BuiltinExchangeType.DIRECT, resultsQueueGames + game.getId() + myPlayer.getId(),
                        false, false, false, null, "");

                channel.basicConsume(resultsQueueGames + game.getId() + myPlayer.getId(), new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope,
                                               AMQP.BasicProperties properties, byte[] body) throws IOException {
                        GameResponse response = GameResponse.parseFrom(body);
                        switch (response.getResponseCode()) {
                            case (301), (302) -> {
                                game = response.getGame();
                                System.out.println("Turn: " + game.getRound());
                                System.out.println("Current Player: " + game.getPublicData().getCurrentPlayer().getNickname());
                                System.out.println("Briscola: " + game.getPublicData().getBriscola());
                                update();
                            }

                            case (402) -> SingleToast.show(getApplicationContext(), "Impossibile unirsi", 3000);

                            case (405) -> SingleToast.show(getApplicationContext(), "Permesso negato", 3000);

                            case (406) -> SingleToast.show(getApplicationContext(), "Richiesta illegale", 3000);

                            case (407) -> SingleToast.show(getApplicationContext(), "Operazione fallita", 3000);

                            default -> throw new IllegalStateException();

                        }
                    }
                });
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
            String cardName = getResources().getResourceEntryName(mAdapter.getItem(position));
            String message;
            if (this.buttonMessageSelected != null) {
                message = this.buttonMessageSelected.getText().toString().toUpperCase();
                this.buttonMessageSelected.getBackground().setColorFilter(Color.parseColor("#9e9e9e"), PorterDuff.Mode.DARKEN);
            } else {
                message = "";
            }
            Log.d("CARTA GIOCATA", cardName);
            Card card = card(cardName);
            if (this.game.getRound() == 1 && game.getPublicData().getBriscola() == Suit.DEFAULT_SUIT) {
                this.confirmBriscola(card.getSuit());
            } else {
                if (playableCards.contains(card)) {
                    executorService.execute(() -> {
                        GameRequest playRequest = GameRequest.newBuilder()
                                .setGameId(game.getId())
                                .setRequestType("play")
                                .setCardPlayed(card)
                                .setCardMessage(message)
                                .setRequestingPlayer(myPlayer)
                                .build();
                        try {
                            channel.basicPublish(todoQueueGames, "", null, playRequest.toByteArray());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    this.isMyTurn = false;
                    this.buttonMessageSelected = null;
                } else {
                    SingleToast.show(this, "Gioca una carta di " + game.getPublicData().getDominantSuit(), Toast.LENGTH_LONG);
                }
            }
        } else {
            SingleToast.show(this, "Non è il tuo turno!", Toast.LENGTH_SHORT);
        }
    }

    private void confirmBriscola(Suit suit) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("La briscola che hai selezionato è " + suit);
        alert.setTitle("Conferma");
        alert.setCancelable(true);
        alert.setPositiveButton("Conferma", (dialog, whichButton) -> executorService.execute(() -> {
            GameRequest setBriscolaRequest = GameRequest.newBuilder()
                    .setGameId(game.getId())
                    .setRequestType("briscola")
                    .setBriscola(suit)
                    .setRequestingPlayer(myPlayer)
                    .build();
            try {
                channel.basicPublish(todoQueueGames, "", null, setBriscolaRequest.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        alert.setNegativeButton("Annulla", (dialog, whichButton) -> {
        });
        alert.show();
    }

    /**
     * Update the players'name, the score and the briscola.
     */
    private void updateMetadata() {
        /*Show Briscola*/
        TextView briscola = findViewById(R.id.briscola);
        briscola.setText(game.getPublicData().getBriscola().toString());

        /*Show score*/
        TextView score = findViewById(R.id.score);
        score.setText("Punti: ");

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
        List<String> myPlayersView = new ArrayList<>();
        myPlayersView.add(circularList.next());
        myPlayersView.add(circularList.next());
        myPlayersView.add(circularList.next());
        myPlayersView.add(circularList.next());
        if (gameField == null) {
            createGameField(myPlayersView);
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

    private void update() {
        runOnUiThread(() -> {
            TextView gameLog = findViewById(R.id.log);
            Player currentPlayer = game.getPublicData().getCurrentPlayer();
            System.out.println("IO: " + myPlayer.getNickname());
            System.out.println("Current: " + currentPlayer.getNickname());
            this.isMyTurn = currentPlayer.equals(this.myPlayer);
            if (game.getRound() == 1 && game.getPublicData().getBriscola() == Suit.DEFAULT_SUIT) {
                if (this.isMyTurn) {
                    Log.d("PRIMO GIOCATORE", "IO");
                    String battezza = "Seleziona la briscola";
                    gameLog.setText(battezza);
                } else {
                    String staBattezzando = currentPlayer.getNickname() + " sta battezzando";
                    gameLog.setText(staBattezzando);
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
                String turn;
                if (this.isMyTurn) {
                    turn = "E'il tuo turno";
                } else {
                    turn = "E' il turno di " + currentPlayer.getNickname();
                }
                gameLog.setText(turn);
            }
            updatePlayableCards();
            updateHand();
            updateButtonsVisibility();
            updateMetadata();
            showPlays();
        });
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
            ImageView placeholder = gameField.get(nicknames.get(decrement(indexCurrent, i + 1)));
            int cardId = R.drawable.retro;
            System.out.println("i: " + i);
            System.out.println("decrementedIndex: " + nicknames.get(decrement(indexCurrent, i + 1)));

            if (i < cards.size()) {
                Card card = cards.get(cards.size() - i - 1);
                cardId = getResources().getIdentifier(name(card), "drawable", "com.faventia.beccaccino");
            }

            if (i == cards.size() - 1) {
                TextView messageHolder = messageField.get(nicknames.get(decrement(indexCurrent, i + 1)));
                if (messageHolder != null) {
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

    private void updatePlayableCards() {
        playableCards = new ArrayList<>(this.game.getPrivateData(0).getMyCardsList());
        Suit dominantSuit = this.game.getPublicData().getDominantSuit();
        if (playableCards.stream().anyMatch(c -> c.getSuit() == dominantSuit)) {
            playableCards = playableCards.stream().filter(c -> c.getSuit() == dominantSuit).collect(Collectors.toList());
        }
    }

    private String name(Card card) {
        return card.getValue().toString().toLowerCase() + "di" + card.getSuit().toString().toLowerCase();
    }

    private Card card(String name) {
        String[] tokens = name.split("di");

        return Card.newBuilder()
                .setValue(Value.valueOf(tokens[0].toUpperCase()))
                .setSuit(Suit.valueOf(tokens[1].toUpperCase()))
                .build();
    }

    private int decrement(int num, int i) {
        num -= i;
        if (num < 0) {
            num = 4 + num;
        }
        return num;
    }

}
