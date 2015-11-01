/*

 *  This is a class for control our database
 */
package com.seanlee.manups.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * ClassName: DatabaseHelper Function: To create a Database or manage database
 * update . date: 2014-3-7
 * 
 * @author LI Xiao
 * @version 1.3
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private String TABLE_NAME = "DEFAULT";

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public void setTableName(String mTableName) {

		this.TABLE_NAME = mTableName;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("create a Database");
		db.execSQL("CREATE TABLE "
				+ TABLE_NAME
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, date varchar(50), "
				+ "pushups int, situps int, running int)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {

	}

}
