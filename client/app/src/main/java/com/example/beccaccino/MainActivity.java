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
import java.util.concurrent.TimeoutException;


public class MainActivity extends AppCompatActivity {
    public static final String PATH_TO_USERNAME = "username_file";
    private final String exchangeName = "exchangeName";
    private final String queueNameSend = "queueNameSend";
    private final String queueNameReceive = "queueNameReceive";

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
        nuovaPartita.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, CreateActivity.class);
            MainActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
        });

        Button cercaPartita = findViewById(R.id.cercaPartita);
        cercaPartita.setOnClickListener(v -> {
            searchMatch(this);
        });
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
        if (getSharedPreferences("Settings", MODE_PRIVATE).getBoolean("music", false)) {
            MusicManager.start(this, 0);
        }
    }

    private static void setUsername(final Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText userName = new EditText(context);
        alert.setMessage("Scegli il tuo Username");
        alert.setTitle("Username");
        alert.setView(userName);
        alert.setCancelable(false);
        alert.setPositiveButton("Confirm", (dialog, whichButton) -> {
            //What ever you want to do with the value
            Editable usernameChoosed = userName.getText();
            String fileContents = usernameChoosed.toString();

            try (FileOutputStream fos = context.openFileOutput(PATH_TO_USERNAME, Context.MODE_PRIVATE)) {
                fos.write(fileContents.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        alert.show();
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

    private void searchMatch(final Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText matchID = new EditText(context);
        alert.setMessage("Digita l'ID della partita");
        alert.setTitle("MatchID");
        alert.setView(matchID);
        alert.setCancelable(false);
        alert.setPositiveButton("Cerca", (dialog, whichButton) -> {
            String matchIDInserted = matchID.getText().toString();
            try {
                Connection connection = Utilies.createConnection();
                Channel channel = connection.createChannel();
                // creo le code per ricevere e mandare
                Utilies.createSendQueue(channel, exchangeName, BuiltinExchangeType.DIRECT, "",
                        queueNameSend, false, false, false, null);
                Utilies.createReceiveQueue(channel, queueNameReceive, false, false, true, null);

                // TODO: mandare messaggio corretto al server
                channel.basicPublish(exchangeName, "", null, matchIDInserted.getBytes());
                channel.basicConsume(queueNameReceive, new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        // TODO: gestire correttamente la risposta .. cosa deve fare quando gli arriva?
                        // TODO: se ok cambiare activity sennò popup di errore
                    }
                });
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });
        alert.show();
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
                Log.d("MyApp", username);
            }
        } else {
            setUsername(this);
        }
    }


}
