package com.example.x.aplikacija;

import android.database.Cursor;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static android.R.attr.max;
import static android.R.attr.min;

/**
 * Created by x on 16.1.2018..
 */

public class GameLevel implements VariableChangeListener
{

    int id_igre;
    String tema, rjesenje, hint;
    int level;
    String[] asoc;
    String[] synonim;
    com.example.x.aplikacija.CountDownTimer cdt;
    long time_left;
    boolean reverted = false;
    int score = 0;
    int num_clicked = 0;

    int[] asoc_len;
    int[] pocetnaX = new int[4];
    int [] pocetnaY = new int[4];
    //int[] bonusi = new int[144];
    char[][] grid_values = new char[12][12];
    boolean[][] is_revealed = new boolean[12][12];
    char[] smjerovi = new char[4]; // D - dijagonalno, H - horizontalno, O - okomito


    public GameLevel(int id_igre, String tema, String rjesenje, int level, String hint, Cursor asocijacije, Cursor synonyms)
    {
        this.id_igre = id_igre;
        this.tema = tema;
        this.rjesenje = rjesenje;
        this.hint = hint;
        this.level = level;
        this.asoc = new String[6];
        this.asoc_len = new int[6];
        this.synonim = new String[synonyms.getCount()];

        for (int i = 0; i < 6; i++)
        {
            asoc[i] = asocijacije.getString(2);
            asoc_len[i] = asoc[i].length();
            asocijacije.moveToNext();
        }

        for(int i = 0; i < synonim.length; i++)
        {
            synonim[i] = synonyms.getString(0);
            synonyms.moveToNext();
        }
       setupAssociations();
        setupBonuses();
    }

    public void onVariableChanged(boolean changed, int x, int y )
    {
        is_revealed[x][y] = changed;
        num_clicked++;
        Log.e("num_clicked", Integer.toString(num_clicked));
    }


    void setupAssociations()
    {
        Random r = new Random();

        //pozicije asocijacija, uzimam prve 4 zasad
        for(int i = 0; i < 4; i++)
        {
            int x = 0;
            int y = 0;
            int smjer = r.nextInt(3);
            boolean dobar = false;
            switch (smjer) {
                case 0:
                    smjerovi[i] = 'D'; //udaljenost od oba ruba mora biti manja od duljine
                    while(!dobar)
                    {
                        x = r.nextInt(12-asoc_len[i]);
                        y = r.nextInt(12-asoc_len[i]);
                        dobar = true;

                        int k = y;
                        for(int j = x; j < x + asoc_len[i] && k < y + asoc_len[i]; j++, k++)
                        {
                            if (grid_values[j][k] != '\u0000' )
                            {
                                dobar = false;
                                break;
                            }
                        }
                    }
                    int k = y;
                    for(int j = x; j < x + asoc_len[i] && k < y + asoc_len[i]; j++, k++)
                        grid_values[j][k] =  asoc[i].charAt(j - x);
                    break;

                case 1:
                    smjerovi[i] = 'H';
                    while(!dobar)
                    {
                        y = r.nextInt(12-asoc_len[i]);
                        x = r.nextInt(12);
                        dobar = true;

                        for(int j = y; j < y + asoc_len[i] ; j++)
                        {
                            if (grid_values[x][j] != '\u0000')
                            {
                                dobar = false;
                                break;
                            }
                        }
                    }
                    for(int j = y; j < y + asoc_len[i]; j++)
                        grid_values[x][j] =  asoc[i].charAt(j - y);
                    break;

                case 2:
                    smjerovi[i] = 'O';
                    while(!dobar)
                    {
                        y = r.nextInt(12);
                        x = r.nextInt(12-asoc_len[i]);
                        dobar = true;

                        for(int j = x; j < x + asoc_len[i] ; j++)
                        {
                            if (grid_values[j][y] != '\u0000' )
                            {
                                dobar = false;
                                break;
                            }
                        }
                    }
                    for(int j = x; j < x + asoc_len[i]; j++)
                        grid_values[j][y] =  asoc[i].charAt(j - x);
                    break;
            }
            pocetnaX[i] = x;
            pocetnaY[i] = y;
        }
    }


    void setupBonuses() {
        Random r = new Random();

        int x, y, j;
        int numBombs = r.nextInt(6 - level) + 2;
        int numReverts = r.nextInt(2) + 1;
        //int numBombs = 30;
        //int numReverts = 0; //problem kad se riječi križaju
        int numHints = r.nextInt(2);
        int numNegClocks = r.nextInt(2 + level) + 1;

        //dalo bi se ubrzati to još
        for (int i = 0; i < numBombs ; i++) {
            while (true) {

                x = r.nextInt(11);
                y = r.nextInt(11);

                if (grid_values[x][y] == '\u0000') {
                    grid_values[x][y] = 'b';
                    break;
                }
            }
        }
         for (int i = 0; i < numReverts ; i++) {
            while (true) {

                x = r.nextInt(11);
                y = r.nextInt(11);

                if (grid_values[x][y] == '\u0000') {
                    grid_values[x][y] = 'r';
                    break;
                }
            }
        }

        for (int i = 0; i < numHints ; i++) {
            while (true) {

                x = r.nextInt(11);
                y = r.nextInt(11);

                if (grid_values[x][y] == '\u0000') {
                    grid_values[x][y] = 'h';
                    break;
                }
            }
        }

        for (int i = 0; i < numNegClocks ; i++) {
            while (true) {

                x = r.nextInt(11);
                y = r.nextInt(11);

                if (grid_values[x][y] == '\u0000') {
                    grid_values[x][y] = 't';
                    break;
                }
            }
        }



    }

    void calculateScore()
    {
        score = 5000 - (num_clicked * 20);



    }

    /*
//s križanjem
    void setupAssociations()
    {
        Random r = new Random();

        //pozicije asocijacija, uzimam prve 4 zasad
        for(int i = 0; i < 4; i++)
        {
            int x = 0;
            int y = 0;
            int smjer = r.nextInt(3);
            boolean dobar = false;
            switch (smjer) {
                case 0:
                    smjerovi[i] = 'D'; //udaljenost od oba ruba mora biti manja od duljine
                    while(!dobar)
                    {
                        x = r.nextInt(12-asoc_len[i]);
                        y = r.nextInt(12-asoc_len[i]);
                        dobar = true;

                        int k = y;
                        for(int j = x; j < x + asoc_len[i] && k < y + asoc_len[i]; j++, k++)
                        {
                            if (grid_values[j][k] != '\u0000' && grid_values[j][k] != asoc[i].charAt(j-x))
                            {
                                dobar = false;
                                break;
                            }
                        }
                    }
                    int k = y;
                    for(int j = x; j < x + asoc_len[i] && k < y + asoc_len[i]; j++, k++)
                        grid_values[j][k] =  asoc[i].charAt(j - x);
                    break;

                case 1:
                    smjerovi[i] = 'H';
                    while(!dobar)
                    {
                        y = r.nextInt(12-asoc_len[i]);
                        x = r.nextInt(12);
                        dobar = true;

                        for(int j = y; j < y + asoc_len[i] ; j++)
                        {
                            if (grid_values[x][j] != '\u0000' && grid_values[x][j] != asoc[i].charAt(j-y))
                            {
                                dobar = false;
                                break;
                            }
                        }
                    }
                    for(int j = y; j < y + asoc_len[i]; j++)
                        grid_values[x][j] =  asoc[i].charAt(j - y);
                    break;

                case 2:
                    smjerovi[i] = 'O';
                    while(!dobar)
                    {
                        y = r.nextInt(12);
                        x = r.nextInt(12-asoc_len[i]);
                        dobar = true;

                        for(int j = x; j < x + asoc_len[i] ; j++)
                        {
                            if (grid_values[j][y] != '\u0000' && grid_values[j][y] != asoc[i].charAt(j-x))
                            {
                                dobar = false;
                                break;
                            }
                        }
                    }
                    for(int j = x; j < x + asoc_len[i]; j++)
                        grid_values[j][y] =  asoc[i].charAt(j - x);
                    break;
            }
            pocetnaX[i] = x;
            pocetnaY[i] = y;
        }
    }
    */
}


