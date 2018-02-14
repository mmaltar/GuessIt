package com.example.x.aplikacija;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EndLevel extends Activity {

    String username;
    int score, complete_score, guesses_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_level);

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        score = intent.getIntExtra("score", 0);
        complete_score = intent.getIntExtra("complete_score", 0);
        guesses_left = intent.getIntExtra("Guesses left", 3);

        TextView score_view = findViewById(R.id.score_end_level);
        score_view.setText(Integer.toString(complete_score));



    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void nextLevel(View view) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Username", username);
        intent.putExtra("complete_score", complete_score);
        intent.putExtra("Guesses left", guesses_left);


        //score dodat, guess left, bonus neki?
        startActivity(intent);
    }

}