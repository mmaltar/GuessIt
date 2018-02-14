package com.example.x.aplikacija;

import java.io.IOException;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBAdapter
{
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public DBAdapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public DBAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DBAdapter open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    public Cursor getTestData()
    {
        try
        {
            String sql ="SELECT * FROM igra";

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null)
            {
                mCur.moveToNext();
            }
            return mCur;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }


    public Cursor getRandomGame(int level)
    {

        return mDb.query("igra",  new String[] {"id", "tema", "rjesenje", "level", "hint"}, "level = " + level,
                null, null, null, "RANDOM()", "1");

        //return mDb.rawQuery("SELECT * from igra WHERE level = 1 ORDER BY RANDOM() LIMIT 1", null);

    }

    //vraca 6 asocijacija za tu igru
    public Cursor getAssociations(int game_id)
    {
        return mDb.query("asocijacija",  new String[] {"id", "id_igre", "asoc"}, "id_igre = " + game_id,
                null, null, null, "RANDOM()", "6");

    }

    public Cursor getSynonyms(int game_id)
    {
        return mDb.query("igra_rjesenje_sinonim",  new String[] { "sinonim" }, "id_igre = " + game_id,
                null, null, null, null );

    }



}

  /*  argumenti od query:
        0 - opcionalni Booelan specificira da li je ispis DISTINCT
        1 - ime tablice
        2 - popis atributa
        3 - where formulacija
        4 - where 2. dio
        5 - group by
        6 - having
        7 - order by
        8 - opcionalno LIMIT dio*/


