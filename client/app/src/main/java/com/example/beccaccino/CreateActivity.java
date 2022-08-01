package com.example.beccaccino;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.travijuu.numberpicker.library.NumberPicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CreateActivity extends AppCompatActivity {
    //private Switch criccaSetting;
    private List<TextView> usernames = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // invia richiesta al server per creare la lobby

        // inserire nel text apposito l'id

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.match_type, R.layout.spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        this.sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor edit = this.sharedPreferences.edit();
        edit.remove("player2");
        edit.remove("player3");
        edit.remove("player4");
        edit.commit();

        TextView me = findViewById(R.id.player1Name);
        me.setText(MainActivity.getUsername(this));

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(CreateActivity.this, MainActivity.class);
                CreateActivity.this.startActivity(myIntent);
            }
        });

        final Button startMatch = (Button) findViewById(R.id.startMatch);
        startMatch.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                // cricca non implementata .. sempre a false
                myEdit.putBoolean("cricca", false);
                myEdit.putString("player1", MainActivity.getUsername(getApplicationContext()));
                myEdit.commit();
                if(checkNumPlayer()) {
                    Intent myIntent = new Intent(CreateActivity.this, GameActivity.class);
                    CreateActivity.this.startActivity(myIntent);
                }else{
                    SingleToast.show(getApplicationContext(), "Seleziona tutti i giocatori prima di iniziare", 3000);
                }
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
        if(names.contains("Scegli")) {
            return false;
        } else {
            return true;
        }
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
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean("cricca", false);
        myEdit.apply();
        
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
