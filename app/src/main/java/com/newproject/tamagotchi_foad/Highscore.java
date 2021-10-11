package com.newproject.tamagotchi_foad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class Highscore extends AppCompatActivity {

    TextView time, score;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.highscorelayout);

        time = findViewById(R.id.timeView);
        score = findViewById(R.id.highScoreView);

        Intent intent = getIntent();

        int seconds = intent.getExtras().getInt("Time");
        time.setText("Elapsed time: " + seconds/60/60 + "hrs " + seconds/60%60 + "min " + seconds%60 + "seconds");
        score.setText("Collected score: " + intent.getExtras().getInt("Score"));
    }
}
