package com.seanlee.manups.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.seanlee.manups.R;
import com.seanlee.manups.activities.RunningActivity;

/*
 * @author Sean Lee
 * @version 1.3
 */

public class RunningService extends Service implements SensorEventListener {

    public int steps = 0;

    private NotificationManager notificationManager;
    private BroadcastReceiver serviceBroadcastReceiver;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    // To define a timer
    long mTimer = 0;
    private float WALKING_THRESHOLD = 35;
    private float[] preCoordinate;
    private double currentTime = 0, lastTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        // To define the sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(RunningService.this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI);

        // To get NotificationManager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Service receiver
        serviceBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // Get the command from activity
                int command = intent.getIntExtra("command", -1);
                // To decide what should do according to the command
                switch (command) {
                    case RunningActivity.SERVICE_STOP: {
                        notificationManager.cancel(0);
                        stopSelf();
                        break;
                    }
                    case RunningActivity.SEND_TIMER: {
                        Intent timerIntent = new Intent();
                        timerIntent.setAction(RunningActivity.ACTIVITY_INTENT_ACTION);
                        timerIntent.putExtra("timer", mTimer);
                        sendBroadcast(timerIntent);
                        break;
                    }
                    default: {
                        Log.e("Running Service",
                                "Can not find difined broadcast method");
                        break;
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(RunningActivity.SERVICE_INTENT_ACTION);
        registerReceiver(serviceBroadcastReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Set the timer
        mTimer = SystemClock.elapsedRealtime();
        // Add Notification
        showNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    public void showNotification() {

        Intent intent = new Intent(this, RunningActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new Notification();
        notification.icon = R.drawable.ic_launcher;
        notification.setLatestEventInfo(this,
                getResources().getString(R.string.notification_title),
                getResources().getString(R.string.notification_subtitle), pi);
        notificationManager.notify(0, notification);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceBroadcastReceiver);
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        currentTime = System.currentTimeMillis();
        if (currentTime - lastTime > 400) {
            if (preCoordinate == null) {
                preCoordinate = new float[3];
                for (int i = 0; i < 3; i++) {
                    preCoordinate[i] = event.values[i];
                }
            } else {
                int angle = calculateAngle(event.values, preCoordinate);
                if (angle >= WALKING_THRESHOLD) {
                    steps++;
                }
                for (int i = 0; i < 3; i++) {
                    preCoordinate[i] = event.values[i];
                }
            }
            lastTime = currentTime;
        }
        updateData();
    }

    public void updateData() {
        Intent intent = new Intent();
        intent.setAction(RunningActivity.ACTIVITY_INTENT_ACTION);
        intent.putExtra("steps", steps);
        sendBroadcast(intent);
    }

    public int calculateAngle(float[] newPoints, float[] oldPoints) {
        int angle = 0;
        float vectorProduct = 0;
        float newMold = 0;
        float oldMold = 0;
        for (int i = 0; i < 3; i++) {
            vectorProduct += newPoints[i] * oldPoints[i];
            newMold += newPoints[i] * newPoints[i];
            oldMold += oldPoints[i] * oldPoints[i];

        }
        newMold = (float) Math.sqrt(newMold);
        oldMold = (float) Math.sqrt(oldMold);

        float cosineAngle = (float) (vectorProduct / (newMold * oldMold));
        float fangle = (float) Math.toDegrees(Math.acos(cosineAngle));

        angle = (int) fangle;
        return angle;
    }

}
