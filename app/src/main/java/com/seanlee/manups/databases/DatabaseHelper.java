package com.seanlee.manups.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Sean Lee
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    protected static final String DB_NAME = "manup.db";
    private static final int DB_VERSION = 2;

    public static String RECORD_TABLE = "record";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + RECORD_TABLE + "("
                + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                + "date NUMERIC NOT NULL,"
                + "pushup INTEGER NOT NULL DEFAULT 0,"
                + "pushup_calorie REAL NOT NULL DEFAULT 0,"
                + "situp INTEGER NOT NULL DEFAULT 0,"
                + "situp_calorie REAL NOT NULL DEFAULT 0,"
                + "running INTEGER NOT NULL DEFAULT 0,"
                + "running_calorie REAL NOT NULL DEFAULT 0"
                + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {

    }

}
