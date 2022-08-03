package com.example.beccaccino;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class CreateActivity extends AppCompatActivity {
    private List<TextView> usernames = new ArrayList<>();
    private final String queueNameSend = "queueNameSend";
    private final String queueNameReceive = "queueNameReceive";
    private String matchID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // controllo se le info sulla lobby son presenti ovvero son arrivato in questa activiyt da quella di joinlobby

        // invia richiesta al server per creare la lobby

        // inserire nel text apposito l'id

        TextView me = findViewById(R.id.player1Name);
        me.setText(MainActivity.getUsername(this));

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(CreateActivity.this, MainActivity.class);
            CreateActivity.this.startActivity(myIntent);
        });

        final Button startMatch = findViewById(R.id.startMatch);
        startMatch.setOnClickListener(v -> {
            if(checkNumPlayer()) {
                Intent myIntent = new Intent(CreateActivity.this, GameActivity.class);
                CreateActivity.this.startActivity(myIntent);
            }else{
                SingleToast.show(getApplicationContext(), "Seleziona tutti i giocatori prima di iniziare", 3000);
            }
        });

        saveViewElements();

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
}
