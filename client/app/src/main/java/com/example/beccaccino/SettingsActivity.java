package com.example.beccaccino;

import android.app.AlertDialog;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.travijuu.numberpicker.library.NumberPicker;

import java.io.*;

public class SettingsActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String path_to_picture = "/data/user/0/com.faventia.beccaccino/app_imageDir/profile.jpg";
    // Layout's components.
    private ImageView profilePicture;
    private TextView username;
    private ImageView changeProfilePicture;
    private ImageView changeUsername;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_settings);

        // Get the elements from the layout.
        this.profilePicture = findViewById(R.id.playerImage);
        this.changeUsername = findViewById(R.id.editUsername);
        this.username = findViewById(R.id.username);
        this.username.setText(MainActivity.getUsername(this));

        Switch music = findViewById(R.id.musicSwitch);
        music.setChecked(getSharedPreferences("Settings", MODE_PRIVATE).getBoolean("music", false));

        NumberPicker np = findViewById(R.id.number_picker_ia);
        np.setValue(getSharedPreferences("Settings", MODE_PRIVATE).getInt("ai_delay", 5000));

        File file = new File("/data/user/0/com.faventia.beccaccino/app_imageDir/profile.jpg");
        if (file.exists()) {
            loadImageFromStorage();
        }


        ((Switch) findViewById(R.id.musicSwitch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor myEdit = getSharedPreferences("Settings", MODE_PRIVATE).edit();
                if (isChecked) {
                    myEdit.putBoolean("music", true);
                    MusicManager.start(getApplicationContext(), 0);
                } else {
                    myEdit.putBoolean("music", false);
                    MusicManager.release();
                }
                myEdit.commit();
            }
        });

        // Add listeners for edit buttons.
        this.changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUsername();
            }
        });

        this.profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String path = saveToInternalStorage(imageBitmap);
            Log.d("PAAAAAAATTTTHHH", path);
            loadImageFromStorage();
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage() {
        String path = "/data/user/0/com.faventia.beccaccino/app_imageDir";
        try {
            File f = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            this.profilePicture.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setUsername() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText userName = new EditText(this);
        alert.setMessage("Scegli il tuo Username");
        alert.setTitle("Username");
        alert.setView(userName);
        alert.setCancelable(false);
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                Editable usernameChoosed = userName.getText();


                String filename = MainActivity.PATH_TO_USERNAME;
                String fileContents = usernameChoosed.toString();

                try (FileOutputStream fos = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
                    fos.write(fileContents.getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                username.setText(MainActivity.getUsername(getApplicationContext()));


            }
        });
        alert.show();
    }


    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor myEdit = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        myEdit.putInt("ai_delay", ((NumberPicker) findViewById(R.id.number_picker_ia)).getValue());
        myEdit.apply();


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
}
