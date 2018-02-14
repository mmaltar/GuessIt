package com.example.x.aplikacija;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameOver extends Activity {

    String username;
    int score, complete_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");

        score = intent.getIntExtra("score", 0);
        complete_score = intent.getIntExtra("complete_score", 0);

        TextView score_view = findViewById(R.id.score_gameover);
        score_view.setText(Integer.toString(complete_score));


    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void newGame(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Username", username);

        startActivity(intent);
        finish();

    }
}
