package com.example.beccaccino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 111;
    private FirebaseAuth mAuth;
    private static boolean _running = true;
    public static final String PATH_TO_USERNAME = "username_file";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        try {
            checkUsername();
        } catch (IOException e) {
            e.printStackTrace();
        }
            /*
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.AnonymousBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
                    */

        Button nuovaPartita = (Button) findViewById(R.id.nuovaPartita);
        nuovaPartita.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CreateActivity.class);
                MainActivity.this.startActivity(myIntent);
                overridePendingTransition(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
            }
        });

        Button cercaPartita = (Button) findViewById(R.id.cercaPartita);
        cercaPartita.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // pop up per inserire la partita e attesa di risposta dal server dopo aver inserito il codice
                Toast.makeText(MainActivity.this, "Inserire numero prima o poi...", Toast.LENGTH_LONG);
            }
        });

        Button settings = (Button) findViewById(R.id.impostazioni);
        settings.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        }));


    }




        @Override
        protected void onStart() {
            super.onStart();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LOGIN_ANONYMOUS", "signInAnonymously:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LOGIN_ANONYMOUS", "signInAnonymously:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    });
        }

        private void updateUI(FirebaseUser currentUser) {
            if (currentUser != null) {
            }
        }

        private void checkUsername() throws IOException {
            String[] files = this.fileList();
            boolean isPresent = false;
            for(int i=0; i<files.length; i++){
                if(files[i].equals(PATH_TO_USERNAME)){
                    isPresent = true;
                }
            }

            if(isPresent) {
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
                    Log.d("MyApp",username);
                }
                /*
                byte[] bytes = new byte[1024];
                FileInputStream fis = openFileInput(PATH_TO_USERNAME);
                fis.read(bytes);
                fis.close();
                String username = new String(bytes);
                */


            } else {
                setUsername(this);
            }
        }

        public static void setUsername(final Context context) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            final EditText userName = new EditText(context);
            alert.setMessage("Scegli il tuo Username");
            alert.setTitle("Username");
            alert.setView(userName);
            alert.setCancelable(false);
            alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //What ever you want to do with the value
                    Editable usernameChoosed = userName.getText();


                    String filename = PATH_TO_USERNAME;
                    String fileContents = usernameChoosed.toString();

                    try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
                        fos.write(fileContents.getBytes());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            });
            alert.show();
        }

        public static String getUsername(Context context){
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
            return username.substring(0,username.length()-1);
        }

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
