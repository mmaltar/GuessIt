package com.example.x.aplikacija;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.attr.level;
import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements Square.OnToggledListener
{

    private DBAdapter mDBHelper;
    private SQLiteDatabase mDb;
    GridLayout gl;
    String username;
    WindowManager wm;
    PopupWindow popupWindow;

    Display d;
    int screenHeight, screenWidth;
    RelativeLayout current_layout;

    Square[][] squares = new Square[12][12];
    GameLevel lvl;

    int id_igre, guesses_left, complete_score;

    View popupView;
    boolean popup_visible = false;

    public TextView timer_view, level_info, user_info, score_view, guess_left_view;

    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";
    private RetainedFragment mRetainedFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate", "poziv onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        complete_score = intent.getIntExtra("complete_score", 0);
        guesses_left = intent.getIntExtra("Guesses left", 3);

        //orijentacija uređaja
        wm = getWindowManager();
        d = wm.getDefaultDisplay();

        screenWidth = d.getWidth();
        screenHeight = d.getHeight();

        timer_view = findViewById(R.id.timer);
        level_info = findViewById(R.id.level_info);
        user_info = findViewById(R.id.user_info);
        score_view = findViewById(R.id.score_view);
        guess_left_view = findViewById(R.id.guess_left_view);


        FragmentManager fm = getFragmentManager();
        mRetainedFragment = (RetainedFragment) fm.findFragmentByTag(TAG_RETAINED_FRAGMENT);
        if (mRetainedFragment == null)
        {
            // add the fragment
            mRetainedFragment = new RetainedFragment();
            fm.beginTransaction().add(mRetainedFragment, TAG_RETAINED_FRAGMENT).commit();
            // load data from a data source or perform any calculation

            //dohvacam bazu
            DBAdapter mDbHelper = new DBAdapter(this);
            mDbHelper.createDatabase();
            mDbHelper.open();

            //postavljamo igru
            Cursor game = mDbHelper.getRandomGame(1);
            game.moveToFirst();
            id_igre = game.getInt(0);
            Log.e("id igre", Integer.toString(id_igre));
            Cursor asocijacije = mDbHelper.getAssociations(id_igre);
            asocijacije.moveToFirst();
            Cursor synonims = mDbHelper.getSynonyms(id_igre);
            synonims.moveToFirst();

            lvl = new GameLevel(game.getInt(0), game.getString(1), game.getString(2), Integer.parseInt(game.getString(3)), game.getString(4), asocijacije, synonims);
            mRetainedFragment.setData(lvl);


            startTimer(30000);
            mDbHelper.close();
        }
        else //restoream stanje
        {
            Log.d("onCreate: ", "restoream stanje" );
            lvl = mRetainedFragment.getData();
            startTimer(lvl.time_left);
        }

        //postavljamo layout
        setupOrientation();
        setup(gl);


        gl.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        int dpValue = 1; // margin in dips
                        Context context = getApplicationContext();
                        float d = context.getResources().getDisplayMetrics().density;
                        final int MARGIN = (int)(dpValue * d); // margin in pixels

                        int pWidth = gl.getWidth();
                        int pHeight = gl.getHeight();
                        int w = pWidth / gl.getColumnCount();
                        int h = pHeight / gl.getRowCount();


                        for (int i = 0; i < 12; i++) {
                            for(int j = 0; j < 12; j++) {
                                GridLayout.LayoutParams params =
                                        (GridLayout.LayoutParams) squares[i][j].getLayoutParams();

                                params.width = w - 2 * MARGIN;
                                params.height = h - 2 * MARGIN;
                                params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
                                squares[i][j].setLayoutParams(params);
                            }
                        }

                        gl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

    }


    @Override
    protected void onPause() {
        Log.e("a", "onPause");
        super.onPause();
        if(popup_visible)
        {
            popup_visible = false;
            popupWindow.dismiss();
        }

    }

    @Override
    protected void onStop() {
        Log.e("a", "onStop");
        super.onStop();
        lvl.cdt.pause();
        Log.e("a", "timer paused");
        lvl.time_left =  lvl.cdt.mPauseTime;
    }

    @Override
    protected void onRestart() {
        Log.e("a", "onRestart");
        super.onRestart();
        if (!popup_visible) startTimer(lvl.time_left);

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    @Override
    public void OnToggled(Square v, boolean touchOn) {

        int x = v.getIdX();
        int y = v.getIdY();

        if (lvl.grid_values[x][y] == 'b')
        {
            Log.e("a", "bomba");
            bombExplosion(x, y);
        }
        else if (lvl.grid_values[x][y] == 'r')
        {
            Log.e("a", "revert");
            revertWords();
        }
        else if (lvl.grid_values[x][y] == 'h')
            showHint();
        else if (lvl.grid_values[x][y] == 't')
            timerChange(3);


    }


    public void setup(GridLayout gl)
    {
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                squares[i][j] = new Square(this, i, j);
                squares[i][j].setVariableChangeListener(lvl);
                //Log.d("is revealed: ",Boolean.toString(lvl.is_revealed[i / 12][i % 12]) );
                squares[i][j].revealed = lvl.is_revealed[i][j];
                squares[i][j].setOnToggledListener(this);
                squares[i][j].letter = lvl.grid_values[i][j];
                gl.addView(squares[i][j]);
            }
        }
        level_info.setText(lvl.tema);
        user_info.setText(username);
        score_view.setText(Integer.toString(complete_score));
        guess_left_view.setText(Integer.toString(guesses_left));
    }

    public void setupOrientation()
    {
        //landscape
        if ((d.getRotation() == Surface.ROTATION_90) || (d.getRotation() == Surface.ROTATION_270))
        {
            current_layout = findViewById(R.id.rll);

            int square_size = screenHeight / 12 - 1;
            int margin = (screenWidth - screenHeight) / 2;

            //timer_view.setWidth(margin);
            //score_view.setWidth(margin);

            //gridlayout trebam od fathera - relativelayout
            gl = (GridLayout) findViewById(R.id.mygridl);

            RelativeLayout rl = current_layout;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(margin, 5, margin, 5);
            gl.setLayoutParams(params);

        }
        else //portrait
        {
            current_layout = findViewById(R.id.rlp);

            int square_size = screenWidth / 12 - 1;
            int bottom_margin = screenHeight - screenWidth - (int) (0.1 * screenHeight);


            gl = (GridLayout) findViewById(R.id.mygridp);

           RelativeLayout rl = current_layout;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(5, (int) (0.1 * screenHeight), 5, bottom_margin);
            gl.setLayoutParams(params);
        }
    }

    public void startTimer(long millisec)
    {

       lvl.cdt = new com.example.x.aplikacija.CountDownTimer(millisec, 1000) {

            public void onTick(long millisUntilFinished) {
                    timer_view.setText(Long.toString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                gameOver();
            }
        }.start();

    }

    public void guessIt(View view)
    {
        //lvl.cdt.cancel();
        lvl.cdt.pause();
        Log.e("Timer: ", "paused");
        //Log.e("Time left:", Long.toString(lvl.time_left));

        // get a reference to the already created main layout
        RelativeLayout mainLayout = current_layout;

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_guess, null);

        // create the popup window
        int width =  (int) (0.6 * screenWidth);
        int height = (int) (0.6 * screenHeight);

        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
        popup_visible = true;

        View edit_text = popupView.findViewById(R.id.guess);
        ViewGroup.LayoutParams params = edit_text.getLayoutParams();
        params.width = (int) (width * 0.8);
        edit_text.setLayoutParams(params);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.e("Timer: ", "resumed");
                popup_visible = false;
                lvl.cdt.resume();
                //Log.e("Time Left:", Long.toString(lvl.time_left));

            }
        });
    }

    public void guessClick(View view) {
        EditText guess = popupView.findViewById(R.id.guess);

        if (guess.getText().toString().toUpperCase().trim().equals(lvl.rjesenje)
                || Arrays.asList(lvl.synonim).contains(guess.getText().toString().toUpperCase().trim()))
        {

            lvl.calculateScore();

            Intent intent = new Intent(this, EndLevel.class);
            intent.putExtra("game_status", 1);
            intent.putExtra("Username", username);
            intent.putExtra("score", lvl.score);
            intent.putExtra("Guesses left", guesses_left);
            intent.putExtra("complete_score", lvl.score + complete_score);

            startActivity(intent);
            finish();
        }
        else
        {
            guesses_left--;
            popupWindow.dismiss();
            guess_left_view.setText(Integer.toString(guesses_left));
            if (guesses_left == 0)
            {
                gameOver();
            }
        }
    }

    public void gameOver()
    {

        Intent intent = new Intent(this, GameOver.class);
        intent.putExtra("Username", username);
        intent.putExtra("score", lvl.score);
        intent.putExtra("complete_score", lvl.score + complete_score);

        startActivity(intent);
        finish();

    }

    public void bombExplosion(int x, int y)
    {
        int i = -1;
        if (x == 0) i = 0;

        for( ; i < 2 && x + i < 12; i++)
        {
            int j = -1;
            if (y == 0) j = 0;


            for( ; j < 2 && y + j < 12; j++)
            {
                //Log.e("bomba", Integer.toString(x+i) + "," + Integer.toString(y+j));
                if (!squares[x+i][y+j].revealed)
                {
                    if ((i != 0 || j != 0) && lvl.grid_values[x+i][y+j] >= 'a')
                    {

                        lvl.grid_values[x+i][y+j] = '\u0000';
                        squares[x+i][y+j].letter = '\u0000';
                    }

                    squares[x+i][y+j].revealed = true;
                    squares[x+i][y+j].invalidate();
                }
            }
        }
    }

    //ako se riječi križaju nastaju problemi
    public void revertWords()
    {
       for(int i = 0; i < 4; i++)
       {
           int x = lvl.pocetnaX[i];
           int y = lvl.pocetnaY[i];

           switch (lvl.smjerovi[i]) {
               case 'D':
                   int k = y;
                   for(int j = x; j < x + lvl.asoc_len[i] && k < y + lvl.asoc_len[i]; j++, k++)
                       {
                           if (!lvl.reverted)
                           {
                               lvl.grid_values[j][k] = lvl.asoc[i].charAt(lvl.asoc_len[i] - 1 - (j - x));
                               squares[j][k].letter = lvl.asoc[i].charAt(lvl.asoc_len[i] - 1 - (j - x));
                           }
                           else
                           {
                               lvl.grid_values[j][k] = lvl.asoc[i].charAt(j - x);
                               squares[j][k].letter = lvl.asoc[i].charAt(j - x);
                           }

                           if (squares[j][k].revealed)
                           {
                               squares[j][k].first_time = true;
                               squares[j][k].invalidate();
                           }
                       }
                   break;
               case 'H':
                   for(int j = y; j < y + lvl.asoc_len[i]; j++) {
                       if (!lvl.reverted) {
                           Log.e("revertWords ", "x = " + Integer.toString(j) + " y = " + Integer.toString(y));
                           Log.e("revertWords ", "asocijacija: " + lvl.asoc[i]);
                           lvl.grid_values[x][j] = lvl.asoc[i].charAt(lvl.asoc_len[i] - 1 - (j - y));
                           squares[x][j].letter = lvl.asoc[i].charAt(lvl.asoc_len[i] - 1 - (j - y));
                       } else
                        {
                            lvl.grid_values[x][j] = lvl.asoc[i].charAt(j - y);
                            squares[x][j].letter = lvl.asoc[i].charAt(j - y);
                        }
                       if (squares[x][j].revealed)
                       {
                           squares[x][j].first_time = true;
                           squares[x][j].invalidate();
                       }
                   }
                   break;

               case 'O':
                   for(int j = x; j < x + lvl.asoc_len[i] ; j++)
                   {
                       if(!lvl.reverted)
                       {
                           lvl.grid_values[j][y] = lvl.asoc[i].charAt(lvl.asoc_len[i] - 1 - (j - x));
                           squares[j][y].letter = lvl.asoc[i].charAt(lvl.asoc_len[i] - 1 - (j - x));
                       }
                       else{
                           lvl.grid_values[j][y] = lvl.asoc[i].charAt(j - x);
                           squares[j][y].letter = lvl.asoc[i].charAt(j - x);
                       }
                       if (squares[j][y].revealed)
                       {
                           squares[j][y].first_time = true;
                           squares[j][y].invalidate();
                       }
                   }
                   break;
           }
       }
     lvl.reverted = !lvl.reverted;
    }

    public void showHint()
    {
       level_info.setText(lvl.hint);
    }


    public void timerChange(int change)
    {

        lvl.cdt.pause();
        long temp = lvl.cdt.mPauseTime;
        lvl.cdt.cancel();
        temp = temp - 1000 * change;
        if (temp <= 0)
        {
            gameOver();
        }
        else
        {
            Log.e("a", "pokrecem timer s vremenom " + Long.toString(temp) );
           startTimer(temp);
        }

    }


}
