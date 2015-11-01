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
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.TextView;
import android.widget.Toast;

import com.seanlee.manups.R;
import com.seanlee.manups.utils.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * update: 22/4/2014
 * @version 1.3
 * @author LI Xiao
 * 
 */
public class SitupsActivity extends BasicActivity implements
		SensorEventListener {

	double x, y, z, angle, preangle = 0;
	int upCounter, downCounter;
	boolean upFlag = false, downFlag = false;
	boolean leftHand = false, rightHand = false;
	public int digit[] = { R.drawable.situps_digit0, R.drawable.situps_digit1,
			R.drawable.situps_digit2, R.drawable.situps_digit3,
			R.drawable.situps_digit4, R.drawable.situps_digit5,
			R.drawable.situps_digit6, R.drawable.situps_digit7,
			R.drawable.situps_digit8, R.drawable.situps_digit9, };
	private Button mStartButton, mPushupsButton, mRunningButton, mRecordButton,
			mCompleteButton, mIntroductionButton;
	private TextView mDateTextView;
	private ImageView mThousandImageView, mHundredImageView, mDecadeImageView,
			mUnitsImageView;
	private int mCounter = 0, mPreviousCounter;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private static final String TABLE_NAME = "manups";
	private SQLiteDatabase database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_situps);

		mPushupsButton = (Button) findViewById(R.id.pushups_button);
		mRunningButton = (Button) findViewById(R.id.running_button);
		mRecordButton = (Button) findViewById(R.id.record_button);
		mCompleteButton = (Button) findViewById(R.id.complete_button);
		mDateTextView = (TextView) findViewById(R.id.date_textview);
		mThousandImageView = (ImageView) findViewById(R.id.thousand);
		mHundredImageView = (ImageView) findViewById(R.id.hundred);
		mDecadeImageView = (ImageView) findViewById(R.id.decade);
		mUnitsImageView = (ImageView) findViewById(R.id.units);
		mStartButton = (Button) findViewById(R.id.start_button);
		mIntroductionButton = (Button) findViewById(R.id.situp_introduction_button);

		SimpleDateFormat mDate = new SimpleDateFormat("yyyy-MM-dd");
		mDateTextView.setText(mDate.format(new Date()));

		ItemsOnClickListener ItemOnClickListener = new ItemsOnClickListener();
		mPushupsButton.setOnClickListener(ItemOnClickListener);
		mRunningButton.setOnClickListener(ItemOnClickListener);
		mRecordButton.setOnClickListener(ItemOnClickListener);
		mCompleteButton.setOnClickListener(ItemOnClickListener);

		// TO define the sensor
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mStartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(mStartButton,"rotationX",0f,-90f);
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
			}
		});

		mCompleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPreviousCounter += mCounter;
				mCounter = 0;
				mThousandImageView.setImageResource(digit[0]);
				mHundredImageView.setImageResource(digit[0]);
				mDecadeImageView.setImageResource(digit[0]);
				mUnitsImageView.setImageResource(digit[0]);

				ContentValues contentValue = new ContentValues();

				contentValue.put("situps", mPreviousCounter);
				database.update(TABLE_NAME, contentValue, "date=?",
						new String[] { mDateTextView.getText().toString() });

				// StartButton animation
				ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(mStartButton,"rotationX",-90f,0f);
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
			}

		});

		mIntroductionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		database = getWritableDB();
		Cursor cursor = database.query(TABLE_NAME, new String[] { "date",
				"pushups", "situps", "running" }, "date=?",
				new String[] { mDateTextView.getText().toString() }, null,
				null, null);
		if (cursor.moveToFirst() == false) {
			ContentValues contentValue = new ContentValues();
			contentValue.put("date", mDateTextView.getText().toString());
			contentValue.put("pushups", 0);
			contentValue.put("situps", 0);
			contentValue.put("running", 0);
			database.insert(TABLE_NAME, null, contentValue);
			mPreviousCounter = 0; 
		} else {
			mPreviousCounter = cursor.getInt(cursor.getColumnIndex("situps"));
		}

		// cannot click complete Button now
		mCompleteButton.setEnabled(false);
		mCompleteButton.setAlpha(0.2f);
		
		if (!mStartButton.isClickable()) {
			mSensorManager.registerListener(SitupsActivity.this,
					mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
			mCompleteButton.setEnabled(true);
			mCompleteButton.setAlpha(1);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		database.close();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		x = -event.values[0];
		y = -event.values[1];
		z = -event.values[2];
		
		displayDigital(analyseSitup(x,y,z));
	}
	
	private int analyseSitup(double x,double y,double z){
		
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

}
