package com.example.beccaccino;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.beccaccino.room.Settings;
import com.travijuu.numberpicker.library.NumberPicker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CreateActivity extends AppCompatActivity {
    private Switch criccaSetting;
    private Spinner matchLimitType;
    private List<String> friends = new ArrayList<>();
    RadioGroup radioGroup;
    public int position = 4;
    String chosen = new String();
    private List<ImageView> images = new ArrayList<>();
    private List<TextView> usernames = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private ImageView userPicture;
    private NumberPicker numberPicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // invia richiesta al server per creare la lobby

        // inserire nel text apposito l'id

        this.criccaSetting = (Switch) findViewById(R.id.criccaOn);
        this.matchLimitType = (Spinner) findViewById(R.id.matchLimitType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.match_type, R.layout.spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.matchLimitType.setAdapter(adapter);

        //((TextView) this.matchLimitType.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorAccent));
        //((TextView) this.matchLimitType.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorAccent));

        numberPicker = (NumberPicker) findViewById(R.id.number_picker);


        this.sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor edit = this.sharedPreferences.edit();
        edit.remove("player2");
        edit.remove("player3");
        edit.remove("player4");
        edit.commit();

        this.numberPicker.setValue(sharedPreferences.getInt("match_limit", 31));
        this.matchLimitType.setSelection(adapter.getPosition(sharedPreferences.getString("match_limit_type", "Punti")));
        this.criccaSetting.setChecked(sharedPreferences.getBoolean("cricca", false));


        TextView me = (TextView) findViewById(R.id.player1Name);
        me.setText(MainActivity.getUsername(this));
        this.userPicture = (ImageView) findViewById(R.id.playerImage);
        loadImageFromStorage();


        this.friends.add("IA Facile");
        this.friends.add("IA Media");

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
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
                if (criccaSetting.isChecked()) {
                    myEdit.putBoolean("cricca", true);
                } else {
                    myEdit.putBoolean("cricca", false);
                }
                myEdit.putInt("match_limit", numberPicker.getValue());
                myEdit.putString("match_limit_type", matchLimitType.getSelectedItem().toString());
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

        for (final ImageView imageView : images) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = images.indexOf(imageView);
                    chooseFriends();
                }
            });
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
        if(names.contains("Scegli")) {
            return false;
        } else {
            return true;
        }
    }

    private void saveViewElements() {
        ImageView player2Image = (ImageView) findViewById(R.id.imagePlayer2);
        ImageView player3Image = (ImageView) findViewById(R.id.imagePlayer3);
        ImageView player4Image = (ImageView) findViewById(R.id.imagePlayer4);
        images.add(player2Image);
        images.add(player3Image);
        images.add(player4Image);
        TextView player2Name = (TextView) findViewById(R.id.player2Name);
        TextView player3Name = (TextView) findViewById(R.id.player3Name);
        TextView player4Name = (TextView) findViewById(R.id.player4Name);
        usernames.add(player2Name);
        usernames.add(player3Name);
        usernames.add(player4Name);
    }

    private void chooseFriends() {
        radioGroup = new RadioGroup(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Scegli chi invitare");
        alert.setTitle("Invita un amico");
        for (String username : friends) {
            radioGroup.addView(new RadioButton(this));
        }
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            String username = friends.get(i);
            ((RadioButton) radioGroup.getChildAt(i)).setText(username);
        }
        radioGroup.clearCheck();
        alert.setView(radioGroup);
        alert.setCancelable(true);
        alert.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    View selected = radioGroup.findViewById(radioButtonID);
                    int idx = radioGroup.indexOfChild(selected);
                    RadioButton radioSelected = (RadioButton) radioGroup.getChildAt(idx);
                    chosen = radioSelected.getText().toString();
                    radioGroup.removeView(selected);
                    showFriend();
                } else {
                    chooseFriends();
                }
            }
        });
        alert.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private void showFriend() {
        images.get(position).setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        usernames.get(position).setText(chosen);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        position += 2;
        myEdit.putString("player" + position, chosen);
        myEdit.commit();
        if (!chosen.equals("IA Facile") && !chosen.equals("IA Media")) {
            friends.remove(chosen);
        }
        position = 4;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadImageFromStorage() {
        String path = "/data/user/0/com.faventia.beccaccino/app_imageDir";
        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            this.userPicture.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        if (criccaSetting.isChecked()) {
            myEdit.putBoolean("cricca", true);
        } else {
            myEdit.putBoolean("cricca", false);
        }
        myEdit.putInt("match_limit", numberPicker.getValue());
        myEdit.putString("match_limit_type", matchLimitType.getSelectedItem().toString());
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
