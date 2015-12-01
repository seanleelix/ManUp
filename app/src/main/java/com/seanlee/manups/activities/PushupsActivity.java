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
import com.seanlee.manups.interfaces.SettingInterface;
import com.seanlee.manups.utils.ManUpUtils;
import com.seanlee.manups.utils.MyUtil;
import com.seanlee.manups.utils.PreferenceUtil;
import com.seanlee.manups.utils.Settings;

/**
 * ClassName: PushupsActivity Function: To manage the Push-up Activity .
 * date:2015-10-29
 *
 * @author Sean Lee
 * @version 1.5
 */
public class PushupsActivity extends BasicActivity implements SensorEventListener, View.OnClickListener, SettingInterface {

    // To get the digit image
    public int digit[] = {R.drawable.situps_digit0, R.drawable.situps_digit1,
            R.drawable.situps_digit2, R.drawable.situps_digit3,
            R.drawable.situps_digit4, R.drawable.situps_digit5,
            R.drawable.situps_digit6, R.drawable.situps_digit7,
            R.drawable.situps_digit8, R.drawable.situps_digit9,};

    // To set a counter for counting push-up number
    // The previousCounter is used for getting the previous data from database
    private int mCounter = 0, mPreviousCounter;
    private Button mStartButton, mCompleteButton, mIntroductionButton;
    private LinearLayout mBottomButtonLayout;
    private TextView mCalorieTextView;
    private float mPreviousCalorie;
    private ImageView mThousandImageView, mHundredImageView, mDecadeImageView,
            mUnitsImageView;
    private SensorManager mSensorManager;
    private Sensor mProximity;

    private float mUserWeight = 50, mArmLength = 0.25f, mUserHeight = 165f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushups);

        initView();
        initBottomButtons();
        loadSharedPreference();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        DatabaseOperation databaseOperation = new DatabaseOperation(this);
        mPreviousCalorie = databaseOperation.getPreviousCalorie(MyUtil.getCurrentDate(), "pushup_calorie");
        mCalorieTextView.setText(String.format(getString(R.string.calorie_unit), mPreviousCalorie));

    }

    private void initView() {

        mStartButton = (Button) findViewById(R.id.start_button);
        mIntroductionButton = (Button) findViewById(R.id.pushup_introduction_button);

        mBottomButtonLayout = (LinearLayout) findViewById(R.id.bottom_buttons_layout);
        mBottomButtonLayout.setLayoutParams(getBottomButtonLayoutParams(this));

        mCompleteButton = (Button) findViewById(R.id.complete_button);
        mCalorieTextView = (TextView) findViewById(R.id.calorie_textview);
        mThousandImageView = (ImageView) findViewById(R.id.thousand);
        mHundredImageView = (ImageView) findViewById(R.id.hundred);
        mDecadeImageView = (ImageView) findViewById(R.id.decade);
        mUnitsImageView = (ImageView) findViewById(R.id.units);

        mStartButton.setOnClickListener(this);

        mCompleteButton.setOnClickListener(this);

        mIntroductionButton.setOnClickListener(this);
        mIntroductionButton.startAnimation(ManUpUtils.defaultBreathingAnimation());
    }

    private void loadSharedPreference() {
        mUserHeight = PreferenceUtil.getFloat(this, PreferenceUtil.USER_HEIGHT, Settings.DEFAULT_HEIGHT);
        mUserWeight = PreferenceUtil.getFloat(this, PreferenceUtil.BODY_WEIGHT, Settings.DEFAULT_WEIGHT);
        mArmLength = PreferenceUtil.getFloat(this, PreferenceUtil.STEP_LENGTH, Settings.DEFAULT_ARM_LENGTH);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check whether the application is working after the user turn back
        // from other application
        if (!mStartButton.isClickable()) {
            mSensorManager.registerListener(PushupsActivity.this, mProximity,
                    SensorManager.SENSOR_DELAY_NORMAL);
            mCompleteButton.setClickable(true);
            mCompleteButton.setAlpha(1);
        } else {
            // cannot click complete Button now
            mCompleteButton.setClickable(false);
            mCompleteButton.setAlpha(0.2f);
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

            mPreviousCalorie += mUserWeight * 9.8 * mArmLength / (4.184 * 100);
            mCalorieTextView.setText(String.format(getString(R.string.calorie_unit), mPreviousCalorie));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button: {

                if (!PreferenceUtil.getBoolean(this, PreferenceUtil.SET_USER_INFO, false)) {
                    ManUpUtils.setting(this, this);
                    Toast.makeText(this,
                            getResources().getString(R.string.setting_require),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

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
                break;
            }
            case R.id.complete_button: {

                DatabaseOperation databaseOperation = new DatabaseOperation(this);
                mPreviousCounter = databaseOperation.getPreviousCount(MyUtil.getCurrentDate(), "pushup");

                mSensorManager.unregisterListener(PushupsActivity.this);

                mPreviousCounter += mCounter;
                mCounter = 0;
                mThousandImageView.setImageResource(digit[0]);
                mHundredImageView.setImageResource(digit[0]);
                mDecadeImageView.setImageResource(digit[0]);
                mUnitsImageView.setImageResource(digit[0]);

                databaseOperation.setCount(mPreviousCounter, "pushup", MyUtil.getCurrentDate());
                databaseOperation.setCalorie(mPreviousCalorie, "pushup_calorie", MyUtil.getCurrentDate());

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

                break;
            }
            case R.id.pushup_introduction_button: {

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

                break;
            }
        }
    }

    @Override
    public void onSettingFinishListener() {
        mUserWeight = PreferenceUtil.getFloat(this, PreferenceUtil.USER_HEIGHT, 50f);
        mUserHeight = PreferenceUtil.getFloat(this, PreferenceUtil.USER_HEIGHT, 165f);
        mArmLength = mUserHeight / 100 * 0.147f;
    }
}
