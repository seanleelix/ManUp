/* 
 * Project:Man-Ups   1.0 10/4/2014
 * 
 * Copyright CityU,EE5415,Group5
 *  
 * This Android software is Group5 project.  
 * 
 * Please be careful of all the variables' names and format!  - Sean
 *  
 */
package com.seanlee.manups.activities;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seanlee.manups.R;
import com.seanlee.manups.databases.DatabaseOperation;
import com.seanlee.manups.services.ManupService;
import com.seanlee.manups.utils.ManUpUtils;
import com.seanlee.manups.utils.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ClassName: PushupsActivity Function: To manage the Push-up Activity .
 * date:2015-10-29
 *
 * @author Sean Lee
 * @version 1.5
 */
public class PushupsActivity extends BasicActivity implements
        SensorEventListener {

    // To get the digit image
    public int digit[] = {R.drawable.situps_digit0, R.drawable.situps_digit1,
            R.drawable.situps_digit2, R.drawable.situps_digit3,
            R.drawable.situps_digit4, R.drawable.situps_digit5,
            R.drawable.situps_digit6, R.drawable.situps_digit7,
            R.drawable.situps_digit8, R.drawable.situps_digit9,};

    // To set a counter for counting push-up number
    // The previousCounter is used for getting the previous data from database
    private int mCounter = 0, mPreviousCounter;
    private Button mStartButton, mSitupsButton, mRunningButton, mRecordButton,
            mCompleteButton, mIntroductionButton;
    private LinearLayout mBottomButtonLayout;
    private TextView mDateTextView;
    private ImageView mThousandImageView, mHundredImageView, mDecadeImageView,
            mUnitsImageView;
    private SensorManager mSensorManager;
    private Sensor mProximity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushups);

        mStartButton = (Button) findViewById(R.id.start_button);
        mIntroductionButton = (Button) findViewById(R.id.pushup_introduction_button);
        mSitupsButton = (Button) findViewById(R.id.situps_button);
        mRunningButton = (Button) findViewById(R.id.running_button);
        mRecordButton = (Button) findViewById(R.id.record_button);

        //recalculate bottom button height
        mBottomButtonLayout = (LinearLayout) findViewById(R.id.bottom_buttons_layout);
        mBottomButtonLayout.setLayoutParams(getBottomButtonLayoutParams(this));

        mCompleteButton = (Button) findViewById(R.id.complete_button);
        mDateTextView = (TextView) findViewById(R.id.date_textview);
        mThousandImageView = (ImageView) findViewById(R.id.thousand);
        mHundredImageView = (ImageView) findViewById(R.id.hundred);
        mDecadeImageView = (ImageView) findViewById(R.id.decade);
        mUnitsImageView = (ImageView) findViewById(R.id.units);

        ItemsOnClickListener ItemOnClickListener = new ItemsOnClickListener();
        mSitupsButton.setOnClickListener(ItemOnClickListener);
        mRunningButton.setOnClickListener(ItemOnClickListener);
        mRecordButton.setOnClickListener(ItemOnClickListener);
        mCompleteButton.setOnClickListener(ItemOnClickListener);

        //// TO define the sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // To set the date
        SimpleDateFormat mDate = new SimpleDateFormat("yyyy-MM-dd");
        mDateTextView.setText(mDate.format(new Date()));

        // To start counting
        mStartButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(mStartButton, "rotationX", 0f, 90f);
                rotateAnimator.setDuration(Settings.START_BUTTON_ANIMATION_DURATION);
                rotateAnimator.start();

                mStartButton.setClickable(false);

                mCompleteButton.setClickable(true);
                mCompleteButton.setAlpha(1);

                // Disable the other buttons
                mSitupsButton.setClickable(false);
                mRunningButton.setClickable(false);
                mRecordButton.setClickable(false);
                mSitupsButton.setAlpha(0.2f);
                mRunningButton.setAlpha(0.2f);
                mRecordButton.setAlpha(0.2f);

                mSensorManager.registerListener(PushupsActivity.this,
                        mProximity, SensorManager.SENSOR_DELAY_NORMAL);

                Toast.makeText(PushupsActivity.this,
                        getResources().getString(R.string.counter_start_toast),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // To update the database and set the counter panel to zero
        mCompleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mSensorManager.unregisterListener(PushupsActivity.this);

                mPreviousCounter += mCounter;
                mCounter = 0;
                mThousandImageView.setImageResource(digit[0]);
                mHundredImageView.setImageResource(digit[0]);
                mDecadeImageView.setImageResource(digit[0]);
                mUnitsImageView.setImageResource(digit[0]);

                DatabaseOperation databaseOperation = new DatabaseOperation(PushupsActivity.this);
                databaseOperation.setCount(mPreviousCounter, "pushup", mDateTextView.getText().toString());

                // StartButton animation
                ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(mStartButton, "rotationX", 90f, 0f);
                scaleAnimator.setDuration(Settings.START_BUTTON_ANIMATION_DURATION);
                scaleAnimator.start();
                mStartButton.setClickable(true);

                //Complete button unclickable
                mCompleteButton.setClickable(false);
                mCompleteButton.setAlpha(0.2f);

                // Enable the other buttons
                mSitupsButton.setClickable(true);
                mRunningButton.setClickable(true);
                mRecordButton.setClickable(true);
                mSitupsButton.setAlpha(1);
                mRunningButton.setAlpha(1);
                mRecordButton.setAlpha(1);

                Toast.makeText(PushupsActivity.this, getResources().getString(R.string.counter_complete_toast),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // To show the introduction dialog
        mIntroductionButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(PushupsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.pushup_introduction);
                dialog.show();
                Button confirm = (Button) dialog
                        .findViewById(R.id.dialog_confirm_button);
                confirm.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        mIntroductionButton.startAnimation(ManUpUtils.defaultBreathingAnimation());

        // To start a service for updating the database everyday
        Intent serviceIntent = new Intent(this, ManupService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseOperation databaseOperation = new DatabaseOperation(this);
        mPreviousCounter = databaseOperation.getPreviousCount(mDateTextView.getText().toString(), "pushup");

        // cannot click complete Button now
        mCompleteButton.setClickable(false);
        mCompleteButton.setAlpha(0.2f);

        // check whether the application is working after the user turn back
        // from other application
        if (!mStartButton.isClickable()) {
            mSensorManager.registerListener(PushupsActivity.this, mProximity,
                    SensorManager.SENSOR_DELAY_NORMAL);
            mCompleteButton.setClickable(true);
            mCompleteButton.setAlpha(1);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /**
     * displayDigital
     *
     * @param counter
     * @author Sean Lee
     */
    private void displayDigital(int counter) {

        int thousand, hundred, decade, units;
        thousand = counter / 1000;
        hundred = (counter % 1000) / 100;
        decade = (counter % 100) / 10;
        units = counter % 10;
        mThousandImageView.setImageResource(digit[thousand]);
        mHundredImageView.setImageResource(digit[hundred]);
        mDecadeImageView.setImageResource(digit[decade]);
        mUnitsImageView.setImageResource(digit[units]);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.values[0] == 0) {
            mCounter++;
            displayDigital(mCounter);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
