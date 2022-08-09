package com.example.beccaccino;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Objects;

public class EndgameDialog extends Dialog implements View.OnClickListener {

    public Dialog d;
    public Button yes, no;
    public TextView team1Name, team2Name, team1Score, team2Score;
    private String team1, team2;
    private int scoreTeam1, scoreTeam2;
    private GameViewModel gameViewModel;
    private boolean isMatchOver;

    public EndgameDialog(@NonNull Context context, String player1, String player2, String player3, String player4, int scoreTeam1, int scoreTeam2, boolean isMatchOver, GameViewModel viewModel) {
        super(context);
        this.team1 = player1 + "\n" + "&\n" + player3;
        this.team2 = player2 + "\n" + "&\n" + player4;
        this.scoreTeam1 = scoreTeam1;
        this.scoreTeam2 = scoreTeam2;
        this.isMatchOver = isMatchOver;
        this.gameViewModel = viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        setCancelable(false);
        TextView winner = findViewById(R.id.winner);

        if (this.isMatchOver) {
            if (scoreTeam2 > scoreTeam1) {
                winner.setText(R.string.matchLost);
            } else {
                winner.setText(R.string.matchWin);
            }
        } else {
            if (scoreTeam2 > scoreTeam1) {
                winner.setText(R.string.roundLost);
            } else {
                winner.setText(R.string.roundWin);
            }
        }
        this.team1Name = (TextView) findViewById(R.id.team13name);
        this.team2Name = (TextView) findViewById(R.id.team24name);
        this.team1Score = (TextView) findViewById(R.id.team13points);
        this.team2Score = (TextView) findViewById(R.id.team24points);
        this.team1Name.setText(team1);
        this.team2Name.setText(team2);
        this.team1Score.setText(scoreTeam1 + "");
        this.team2Score.setText(scoreTeam2 + "");
        yes = (Button) findViewById(R.id.yesButton);
        no = (Button) findViewById(R.id.noButton);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        if (this.isMatchOver) {
            yes.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yesButton:
                Log.d("DIALOG", "yes!");
                if(!this.isMatchOver) {
                    gameViewModel.createGame();
                }
                break;
            case R.id.noButton:
                gameViewModel.shutDownGame();
                Intent myIntent = new Intent(getContext(), MainActivity.class);
                Objects.requireNonNull(getContext()).startActivity(myIntent);
                Log.d("DIALOG", "no!");
                break;
            default:
                break;
        }
        dismiss();
    }

    @Override
    public void onBackPressed() {
        Button button = findViewById(R.id.noButton);
        button.performClick();
    }
}
