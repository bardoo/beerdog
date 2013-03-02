package com.bardo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 */
public class BeerTableHelper extends SQLiteOpenHelper {
    private static final int TABLE_VERSION = 1;
    public static final String BEER_TABLE_NAME = "beer";

    public static final String BEER_ID = "_id";
    public static final String BEER_NAME = "name";
    public static final String BREWERY = "brewery";
    public static final String BEER_IMAGE = "beer_image";
    public static final String RATING = "rating";

    private static final String BEER_TABLE_CREATE =
            "CREATE TABLE " + BEER_TABLE_NAME + " (" +
                    "_id INTEGER PRIMARY KEY, " +
                    BEER_NAME + " TEXT, " +
                    BREWERY + " TEXT, " +
                    BEER_IMAGE + " TEXT, " +
                    RATING + " INTEGER);";

    public BeerTableHelper(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, TABLE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BEER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // nothing yet
    }
}
