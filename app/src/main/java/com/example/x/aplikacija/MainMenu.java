package com.example.x.aplikacija;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");

        TextView hello = (TextView) findViewById(R.id.hello);
        hello.setText("Hello, " + username);


    }



    public void startGame(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);


        intent.putExtra("Username", username);
        startActivity(intent);
    }








}


