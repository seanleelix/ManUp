/* 
 * Project:Man-Ups  1.0 7/3/2014
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
import com.seanlee.manups.utils.ManUpUtils;
import com.seanlee.manups.utils.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * update: 22/4/2014
 *
 * @author LI Xiao
 * @version 1.3
 */
public class SitupsActivity extends BasicActivity implements
        SensorEventListener, View.OnClickListener {

    double x, y, z, angle, preangle = 0;
    int upCounter, downCounter;
    boolean upFlag = false, downFlag = false;
    boolean leftHand = false, rightHand = false;
    public int digit[] = {R.drawable.situps_digit0, R.drawable.situps_digit1,
            R.drawable.situps_digit2, R.drawable.situps_digit3,
            R.drawable.situps_digit4, R.drawable.situps_digit5,
            R.drawable.situps_digit6, R.drawable.situps_digit7,
            R.drawable.situps_digit8, R.drawable.situps_digit9,};
    private Button mStartButton, mPushupsButton, mRunningButton, mRecordButton,
            mCompleteButton, mIntroductionButton;
    private LinearLayout mBottomButtonLayout;
    private TextView mDateTextView;
    private ImageView mThousandImageView, mHundredImageView, mDecadeImageView,
            mUnitsImageView;
    private int mCounter = 0, mPreviousCounter;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_situps);

        initView();
        initBottomButtons();

        // TO define the sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initView() {

        mPushupsButton = (Button) findViewById(R.id.pushups_button);
        mRunningButton = (Button) findViewById(R.id.running_button);
        mRecordButton = (Button) findViewById(R.id.record_button);
        mCompleteButton = (Button) findViewById(R.id.complete_button);

        mBottomButtonLayout = (LinearLayout) findViewById(R.id.bottom_buttons_layout);
        mBottomButtonLayout.setLayoutParams(getBottomButtonLayoutParams(this));

        mDateTextView = (TextView) findViewById(R.id.date_textview);
        mThousandImageView = (ImageView) findViewById(R.id.thousand);
        mHundredImageView = (ImageView) findViewById(R.id.hundred);
        mDecadeImageView = (ImageView) findViewById(R.id.decade);
        mUnitsImageView = (ImageView) findViewById(R.id.units);
        mStartButton = (Button) findViewById(R.id.start_button);
        mIntroductionButton = (Button) findViewById(R.id.situp_introduction_button);

        SimpleDateFormat mDate = new SimpleDateFormat("yyyy-MM-dd");
        mDateTextView.setText(mDate.format(new Date()));

        mStartButton.setOnClickListener(this);

        mCompleteButton.setOnClickListener(this);

        mIntroductionButton.setOnClickListener(this);
        mIntroductionButton.startAnimation(ManUpUtils.defaultBreathingAnimation());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mStartButton.isClickable()) {
            mSensorManager.registerListener(SitupsActivity.this,
                    mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            mCompleteButton.setEnabled(true);
            mCompleteButton.setAlpha(1);
        } else {
            // cannot click complete Button now
            mCompleteButton.setEnabled(false);
            mCompleteButton.setAlpha(0.2f);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        x = -event.values[0];
        y = -event.values[1];
        z = -event.values[2];

        displayDigital(analyseSitup(x, y, z));
    }

    private int analyseSitup(double x, double y, double z) {

        if (x > 0 && y > 0)
            // angle = Math.atan(y / x) / Math.PI * 180;
            angle = Math.toDegrees(Math.atan(y / x));
        else if (x < 0 && y > 0)
            // angle = Math.atan(y / x) / Math.PI * 180 + 180;
            angle = Math.toDegrees(Math.atan(y / x)) + 180;
        else if (x < 0 && y < 0)
            // angle = Math.atan(y / x) / Math.PI * 180 + 180;
            angle = Math.toDegrees(Math.atan(y / x)) + 180;
        else
            // angle= 360 + Math.atan(y / x) * 180 / Math.PI;
            angle = Math.toDegrees(Math.atan(y / x)) + 360;

        // to analyze whether the user is sitting up
        if (angle > preangle) {
            if (!(angle > 270 && preangle < 90)) {
                upCounter++;
                downCounter = 0;
            }
        }// or the user is lying down
        else {
            if (!(angle < 90 && preangle > 270)) {
                downCounter++;
                upCounter = 0;
            }
        }

        // to analyze whether the user has finished a sit-up motion, and the
        // number is used for adjusting sensitivity
        // smaller the number , more sensitive
        if (!rightHand && upCounter > 5) {
            upFlag = true;
            leftHand = true;
        }
        if (leftHand && upFlag && downCounter > 4)
            downFlag = true;

        if (!leftHand && downCounter > 5) {
            upFlag = true;
            rightHand = true;
        }
        if (rightHand && upFlag && upCounter > 4)
            downFlag = true;

        if (upFlag && downFlag) {
            mCounter++;
            upFlag = false;
            downFlag = false;
        }
        preangle = angle;

        return mCounter;
    }

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
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button: {

                ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(mStartButton, "rotationX", 0f, -90f);
                rotateAnimator.setDuration(Settings.START_BUTTON_ANIMATION_DURATION);
                rotateAnimator.start();

                mStartButton.setClickable(false);

                mCompleteButton.setEnabled(true);
                mCompleteButton.setAlpha(1);

                // Disable the other buttons
                mPushupsButton.setClickable(false);
                mRunningButton.setClickable(false);
                mRecordButton.setClickable(false);
                mPushupsButton.setAlpha(0.2f);
                mRunningButton.setAlpha(0.2f);
                mRecordButton.setAlpha(0.2f);

                mSensorManager.registerListener(SitupsActivity.this,
                        mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

                Toast.makeText(SitupsActivity.this,
                        getResources().getString(R.string.counter_start_toast),
                        Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.complete_button: {
                DatabaseOperation databaseOperation = new DatabaseOperation(this);
                mPreviousCounter = databaseOperation.getPreviousCount(mDateTextView.getText().toString(), "situp");

                mPreviousCounter += mCounter;
                mCounter = 0;
                mThousandImageView.setImageResource(digit[0]);
                mHundredImageView.setImageResource(digit[0]);
                mDecadeImageView.setImageResource(digit[0]);
                mUnitsImageView.setImageResource(digit[0]);

                databaseOperation.setCount(mPreviousCounter, "situp", mDateTextView.getText().toString());

                // StartButton animation
                ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(mStartButton, "rotationX", -90f, 0f);
                scaleAnimator.setDuration(Settings.START_BUTTON_ANIMATION_DURATION);
                scaleAnimator.start();

                mStartButton.setClickable(true);
                mSensorManager.unregisterListener(SitupsActivity.this);
                mCompleteButton.setEnabled(false);
                mCompleteButton.setAlpha(0.2f);

                // Enable the other buttons
                mPushupsButton.setClickable(true);
                mRunningButton.setClickable(true);
                mRecordButton.setClickable(true);
                mPushupsButton.setAlpha(1);
                mRunningButton.setAlpha(1);
                mRecordButton.setAlpha(1);

                Toast.makeText(SitupsActivity.this, getResources().getString(R.string.counter_complete_toast),
                        Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.situp_introduction_button: {

                final Dialog dialog = new Dialog(SitupsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.situp_introduction);
                dialog.show();

                Button confirm = (Button) dialog
                        .findViewById(R.id.dialog_confirm_button);
                confirm.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                break;
            }

        }
    }
}
