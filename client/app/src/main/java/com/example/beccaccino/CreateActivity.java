package com.example.beccaccino;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class CreateActivity extends AppCompatActivity {
    private List<TextView> usernames = new ArrayList<>();
    private final String todoQueueLobbies = "todoQueueLobbies";
    private final String resultsQueueLobbies = "resultsQueueLobbies";
    private final String todoQueueGames = "todoQueueGames";
    private final String resultsQueueGames = "resultsQueueGames";
    private String matchID;
    private Channel channel;
    private Connection connection;
    private Lobby lobby;
    private Player myPlayer;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        Intent intent = getIntent();

        try {
            lobby = Lobby.parseFrom(intent.getByteArrayExtra("lobby"));
            myPlayer = Player.parseFrom(intent.getByteArrayExtra("player"));
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

        matchID = lobby.getId();
        TextView matchIDTW = findViewById(R.id.matchID);
        matchIDTW.setText(matchID);

        executorService.execute(() -> {
            try {
                connection = Utilities.createConnection();
                channel = connection.createChannel();

                Utilities.createQueue(channel, todoQueueLobbies, BuiltinExchangeType.DIRECT, todoQueueLobbies,
                        false, false, true, null, "");
                Utilities.createQueue(channel, resultsQueueLobbies, BuiltinExchangeType.DIRECT, resultsQueueLobbies,
                        false, false, true, null, "");
                Utilities.createQueue(channel, todoQueueGames, BuiltinExchangeType.DIRECT, todoQueueGames,
                        false, false, true, null, "");
                Utilities.createQueue(channel, resultsQueueGames, BuiltinExchangeType.DIRECT, resultsQueueGames,
                        false, false, true, null, "");

                System.out.println("Create Activity Intialized Queue!");
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        });

        TextView me = findViewById(R.id.player1Name);
        me.setText(MainActivity.getUsername(this));

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> backToMain(CreateActivity.this));

        final Button startMatch = findViewById(R.id.startMatch);
        startMatch.setOnClickListener(v -> startMatch());

        saveViewElements();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*MUSIC*/
        if(getSharedPreferences("Settings", MODE_PRIVATE).getBoolean("music", false)){
            MusicManager.start(this,0);
        }
    }

    private void backToMain(final Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Vuoi lasciare la Lobby?");
        alert.setPositiveButton("Esci", (dialog, whichButton) -> executorService.execute(() -> {
            try {
                Request leaveLobbyRequest = Request.newBuilder().setLobbyId(matchID)
                        .setLobbyMessage("leave")
                        .setRequestingPlayer(myPlayer)
                        .build();

                channel.basicPublish(todoQueueLobbies, "", null, leaveLobbyRequest.toByteArray());
                channel.basicConsume(resultsQueueLobbies, new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope,
                                               AMQP.BasicProperties properties, byte[] body) throws IOException {
                        System.out.println("Risposta parsata " + Response.parseFrom(body));
                        if (Response.parseFrom(body).getResponseCode() == 200) {
                            Intent myIntent = new Intent(CreateActivity.this, MainActivity.class);
                            // TODO gli devo ripassare le info con gli intent? No vero? Boh ci devo guardare (Davide dai fallo tu)
                            CreateActivity.this.startActivity(myIntent);
                        } else {
                            SingleToast.show(context, "Impossibile lasciare la lobby", 3000);
                        }
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        alert.show();




    }

    private void startMatch() {
        // TODO comunicazione con il server
        if(checkNumPlayer()) {
            Intent myIntent = new Intent(CreateActivity.this, GameActivity.class);
            CreateActivity.this.startActivity(myIntent);
        } else {
            SingleToast.show(getApplicationContext(), "Seleziona tutti i giocatori prima di iniziare", 3000);
        }
    }

    private boolean checkNumPlayer() {
        List<String> names = new ArrayList<>();
        SharedPreferences sh
                = getApplication().getSharedPreferences("Settings",
                Context.MODE_PRIVATE);
        names.add(sh.getString("player1", "Paolo"));
        names.add(sh.getString("player2", "Scegli"));
        names.add(sh.getString("player3", "Scegli"));
        names.add(sh.getString("player4", "Scegli"));
        return !names.contains("Scegli");
    }

    private void saveViewElements() {
        TextView player2Name = findViewById(R.id.player2Name);
        TextView player3Name = findViewById(R.id.player3Name);
        TextView player4Name = findViewById(R.id.player4Name);
        usernames.add(player2Name);
        usernames.add(player3Name);
        usernames.add(player4Name);
    }
}
