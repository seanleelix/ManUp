package com.seanlee.manups.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.seanlee.manups.databases.DatabaseHelper;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;


/*
 * @author LI Xiao
 * @version 1.3
 */

//This service is for updating the database everyday
public class ManupService extends Service {

	boolean isServiceOn = false;
	long timeInterval = 0;

	private static final String TABLE_NAME = "manups";
	// database variable
	private SQLiteDatabase database;

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			uploadData();
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();

		Calendar calendar = Calendar.getInstance();
		long passTime = calendar.getTimeInMillis();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.get(Calendar.DAY_OF_MONTH) + 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		long nextDay = calendar.getTimeInMillis();
		timeInterval = nextDay - passTime;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		isServiceOn = true;
		Message msg = myHandler.obtainMessage();
		myHandler.sendMessageDelayed(msg, timeInterval);
		
		return START_STICKY;
	}

	public void uploadData() {
		
		database = getWritableDB();
		SimpleDateFormat mDate = new SimpleDateFormat("yyyy-MM-dd");
		String date = mDate.format(new Date());
		Cursor cursor = database.query(TABLE_NAME, new String[] { "date",
				"pushups", "situps", "running" }, "date=?",
				new String[] { date }, null, null, null);
		if (cursor.moveToFirst() == false) {
			ContentValues contentValue = new ContentValues();
			contentValue.put("date", date);
			contentValue.put("pushups", 0);
			contentValue.put("situps", 0);
			contentValue.put("running", 0);
			database.insert(TABLE_NAME, null, contentValue);
		}
		cursor.close();
		database.close();
		Message msg = myHandler.obtainMessage();
		myHandler.sendMessageDelayed(msg, 24 * 60 * 60 * 1000);
		
	}

	public SQLiteDatabase getWritableDB() {
		DatabaseHelper dbHelper = new DatabaseHelper(ManupService.this,
				"manupsdb", null, 1);
		dbHelper.setTableName("manups");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
