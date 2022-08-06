package com.example.beccaccino;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.rabbitmq.client.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;


public class MainActivity extends AppCompatActivity {
    public static final String PATH_TO_USERNAME = "username_file";
    private final String todoQueueLobbies = "todoQueueLobbies";
    private final String resultsQueueLobbies = "resultsQueueLobbies";
    private Connection connection;
    private Player myPlayer;
    private Channel channel;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            checkUsername();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button nuovaPartita = findViewById(R.id.nuovaPartita);
        nuovaPartita.setOnClickListener(v -> createMatch());

        Button cercaPartita = findViewById(R.id.cercaPartita);
        cercaPartita.setOnClickListener(v -> searchMatch(MainActivity.this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        executorService.execute(() -> {
            try {
                connection = Utilities.createConnection();
                channel = connection.createChannel();
                Utilities.createQueue(channel, todoQueueLobbies, BuiltinExchangeType.DIRECT, todoQueueLobbies,
                        false, false, false, null, "");
                Utilities.createQueue(channel, resultsQueueLobbies, BuiltinExchangeType.DIRECT, resultsQueueLobbies,
                        false, false, false, null, "");
                System.out.println("Main Activity Intialized Queue!");
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        });
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
        if (getSharedPreferences("Settings", MODE_PRIVATE).getBoolean("music", false)) {
            MusicManager.start(this, 0);
        }
    }

    private void createMatch() {
        Request createLobbyRequest = Request.newBuilder()
                .setLobbyMessage("create")
                .setRequestingPlayer(myPlayer).build();

        executorService.execute(() -> {
            try {
                channel.basicPublish(todoQueueLobbies, "", null, createLobbyRequest.toByteArray());

                channel.basicConsume(resultsQueueLobbies, new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope,
                                               AMQP.BasicProperties properties, byte[] body) throws IOException {
                        responseHandler(Response.parseFrom(body));
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void searchMatch(final Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText matchID = new EditText(context);
        alert.setMessage("Digita l'ID della partita");
        alert.setTitle("MatchID");
        alert.setView(matchID);
        alert.setCancelable(false);
        alert.setPositiveButton("Cerca", (dialog, whichButton) -> executorService.execute(() -> {
            String matchIDInserted = matchID.getText().toString();
            try {
                Request searchLobbyRequest = Request.newBuilder().setLobbyId(matchIDInserted)
                        .setLobbyMessage("join")
                        .setRequestingPlayer(myPlayer)
                        .build();

                channel.basicPublish(todoQueueLobbies, "", null, searchLobbyRequest.toByteArray());
                channel.basicConsume(resultsQueueLobbies, new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope,
                                               AMQP.BasicProperties properties, byte[] body) throws IOException {
                        System.out.println("Risposta parsata " + Response.parseFrom(body));
                        responseHandler(Response.parseFrom(body));
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        alert.show();
    }

    private void setUsername(final Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText userName = new EditText(context);
        alert.setMessage("Scegli il tuo Username");
        alert.setTitle("Username");
        alert.setView(userName);
        alert.setCancelable(false);
        alert.setPositiveButton("Confirm", (dialog, whichButton) -> {
            //What ever you want to do with the value
            Editable usernameChoosed = userName.getText();
            // TODO l'id è messo in automatico da Proto?
            setPlayer(userName.getText().toString());
            String fileContents = usernameChoosed.toString();
            try (FileOutputStream fos = context.openFileOutput(PATH_TO_USERNAME, Context.MODE_PRIVATE)) {
                fos.write(fileContents.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        alert.show();
    }

    private void setPlayer(final String nickname) {
        myPlayer = Player.newBuilder()
                            .setId(String.valueOf(new Random()
                            .nextInt())).setNickname(nickname).build();
    }

    public static String getUsername(Context context) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(PATH_TO_USERNAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
        }
        String username = stringBuilder.toString();
        return username.substring(0, username.length() - 1);
    }

    private void checkUsername() throws IOException {
        String[] files = this.fileList();
        boolean isPresent = false;

        for (String file : files) {
            if (file.equals(PATH_TO_USERNAME)) {
                isPresent = true;
                break;
            }
        }

        if (isPresent) {
            FileInputStream fis = getApplicationContext().openFileInput(PATH_TO_USERNAME);
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
                String username = stringBuilder.toString();
                // TODO l'id è messo in automatico da Proto?
                setPlayer(username);
                Log.d("MyApp", username);
            }
        } else {
            setUsername(this);
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

    private void responseHandler(Response response) {
        switch (response.getResponseCode()) {
            case(200) -> {
                System.out.println("LOBBY ID IMMESSO: " + response.getLobby().getId());
                Intent data = new Intent(MainActivity.this, CreateActivity.class);
                data.putExtra("lobby", response.getLobby().toByteArray());
                data.putExtra("player", myPlayer.toByteArray());
                MainActivity.this.startActivity(data);
                overridePendingTransition(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
            }
            case (402) -> SingleToast.show(getApplicationContext(), "Impossibile unirsi", 3000);

            case (405) -> SingleToast.show(getApplicationContext(), "Permesso negato", 3000);

            case (406) -> SingleToast.show(getApplicationContext(), "Richiesta illegale", 3000);

            case (407) -> SingleToast.show(getApplicationContext(), "Operazione fallita", 3000);

            default -> throw new IllegalStateException();
        }
    }
}
