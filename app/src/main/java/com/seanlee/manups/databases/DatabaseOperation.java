package com.seanlee.manups.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.seanlee.manups.models.RecordModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sean Lee on 3/11/15.
 */
public class DatabaseOperation {

    private DatabaseHelper databaseHelper;

    public DatabaseOperation(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public List<RecordModel> getRecordModel() {

        List<RecordModel> recordModelList = new ArrayList<>();

        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM record ORDER BY id DESC", null);

        while (cursor.moveToNext()) {
            recordModelList.add(new RecordModel(cursor.getString(cursor.getColumnIndex("date")), cursor.getInt(cursor.getColumnIndex
                    ("pushup")), cursor.getInt(cursor.getColumnIndex("situp")), cursor.getInt(cursor.getColumnIndex("running"))));
        }

        cursor.close();
        database.close();

        return recordModelList;
    }

    public void setCalorie(float count, String columnName, String date) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues contentValue = new ContentValues();
        contentValue.put(columnName, count);
        database.update(DatabaseHelper.RECORD_TABLE, contentValue, "date=?", new String[]{date});

        database.close();
    }

    public void setCount(int count, String columnName, String date) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues contentValue = new ContentValues();
        contentValue.put(columnName, count);
        database.update(DatabaseHelper.RECORD_TABLE, contentValue, "date=?", new String[]{date});

        database.close();
    }

    public float getPreviousCalorie(String date, String columnName) {

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM record WHERE date = ?", new String[]{date});

        float previousCalorie;

        if (cursor.moveToFirst()) {
            previousCalorie = cursor.getFloat(cursor.getColumnIndex(columnName));
        } else {
            ContentValues contentValue = new ContentValues();
            contentValue.put("date", date);
            database.insertWithOnConflict(DatabaseHelper.RECORD_TABLE, null, contentValue, SQLiteDatabase.CONFLICT_REPLACE);

            previousCalorie = 0f;
        }
        cursor.close();
        database.close();

        return previousCalorie;
    }

    public int getPreviousCount(String date, String columnName) {

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM record WHERE date = ?", new String[]{date});

        int previousCount;

        if (cursor.moveToFirst()) {
            previousCount = cursor.getInt(cursor.getColumnIndex(columnName));
        } else {
            ContentValues contentValue = new ContentValues();
            contentValue.put("date", date);
            database.insertWithOnConflict(DatabaseHelper.RECORD_TABLE, null, contentValue, SQLiteDatabase.CONFLICT_REPLACE);

            previousCount = 0;
        }
        cursor.close();
        database.close();

        return previousCount;
    }


    public void createNewDateRecord(String date) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM record WHERE date = ?", new String[]{date});

        if (cursor.moveToFirst() == false) {
            ContentValues contentValue = new ContentValues();
            contentValue.put("date", date);
            database.insertWithOnConflict(DatabaseHelper.RECORD_TABLE, null, contentValue, SQLiteDatabase.CONFLICT_REPLACE);
        }

        cursor.close();
        database.close();

    }

    public void deleteAllRecord() {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.delete("record", null, null);
        database.close();
    }

}
