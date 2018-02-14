package com.example.x.aplikacija;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

import static android.content.ContentValues.TAG;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Start extends Activity
{

    int screenHeight;
    int screenWidth;
    String username;
    SharedPreferences sp;
    SharedPreferences.Editor ed;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

         sp = getPreferences( MODE_PRIVATE );

        //idem direktno na main_menu
        if (sp.contains("username"))
        {
            username = sp.getString("username", null);
            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra("Username", username);
            startActivity(intent);
            finish();
        }


        //dimenzije ekrana
       /* Display display = getWindowManager().getDefaultDisplay();
        String displayName = display.getName();  // minSdkVersion=17+
        Log.i(TAG, "displayName  = " + displayName);

        // display size in pixels
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.i(TAG, "width        = " + width);
        Log.i(TAG, "height       = " + height);

        // pixels, dpi
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        int densityDpi = metrics.densityDpi;
        float xdpi = metrics.xdpi;
        float ydpi = metrics.ydpi;
        Log.i(TAG, "widthPixels  = " + widthPixels);
        Log.i(TAG, "heightPixels = " + heightPixels);
        Log.i(TAG, "densityDpi   = " + densityDpi);
        Log.i(TAG, "xdpi         = " + xdpi);
        Log.i(TAG, "ydpi         = " + ydpi);

        // deprecated
        screenHeight = display.getHeight();
        screenWidth = display.getWidth();
        Log.i(TAG, "screenHeight = " + screenHeight);
        Log.i(TAG, "screenWidth  = " + screenWidth);

        // orientation (either ORIENTATION_LANDSCAPE, ORIENTATION_PORTRAIT)
        int orientation = getResources().getConfiguration().orientation;
        Log.i(TAG, "orientation  = " + orientation);*/



    }

    public void startGame(View view)
    {
        Intent intent = new Intent(this, MainMenu.class);
        EditText username = (EditText) findViewById(R.id.username);
        String message = username.getText().toString();
        ed = sp.edit();
        ed.putString("username", message );
        ed.commit();

        intent.putExtra("Username", message);
        intent.putExtra("Guesses left", 3);
        startActivity(intent);
    }


}
