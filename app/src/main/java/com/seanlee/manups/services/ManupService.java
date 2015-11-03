package com.seanlee.manups.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.seanlee.manups.databases.DatabaseOperation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/*
 * @author Sean Lee
 */

//This service is for updating the database everyday
public class ManupService extends Service {

    boolean isServiceOn = false;
    long timeInterval = 0;

    //Change to timer later
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

        SimpleDateFormat mDate = new SimpleDateFormat("yyyy-MM-dd");
        String date = mDate.format(new Date());

        DatabaseOperation databaseOperation = new DatabaseOperation(this);
        databaseOperation.createNewDateRecord(date);

        Message msg = myHandler.obtainMessage();
        myHandler.sendMessageDelayed(msg, 24 * 60 * 60 * 1000);

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
