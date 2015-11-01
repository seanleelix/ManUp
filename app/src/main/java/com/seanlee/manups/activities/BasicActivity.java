/* 
 * @Man-Ups   1.0 10/4/2014
 * 
 * Copyright CityU,EE5415,Group5
 *  
 * This Android software is Group5 project.  Run in 4.2.2 Android System, 3.7 inches screen, 480*800 resolution and 240 density.
 * 
 * Please be careful of all the variables' names and format!  
 * This activity is used for implementing menu and other common setting.
 * Every one should pay attention to application structure.
 * 
 * Mainly obligation:    LI Xiao     In charge of all the implements including functions and images.
 *                       LIN JuanYIN Sensor control and data return and relating algorithm.      Deadline:15.3.2014
 *                       LI WeiMin Using database to save and extract data.                      Deadline:15.3.2014
 *                       Cai JianBin Take charge of all the images and interface structure design.
 *                       Zhao YuKun  Testing application and computing all the images sizes.
 *                                                                                                 -LI Xiao 27.2.2014
 */
package com.seanlee.manups.activities;

import java.util.Locale;

import com.seanlee.manups.R;
import com.seanlee.manups.databases.DatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * 
 * @version 1.3
 * @author LI Xiao
 * 
 */

// This class is used for listening the item buttons which is set to change
public class BasicActivity extends Activity {

	public boolean isExit = false;
	Handler handleExit = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}

	};
	AlertDialog.Builder builder;
	AlertDialog dialog;

	public class ItemsOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.pushups_button: {
				// intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); It's
				// useful, but if it call finish(), the second activity will
				// still show up with animation
				Intent intent = new Intent(BasicActivity.this,
						PushupsActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(0, 0);
				break;
			}
			case R.id.situps_button: {
				Intent intent = new Intent(BasicActivity.this,
						SitupsActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(0, 0); // to show as a whole activity
				break;
			}
			case R.id.running_button: {
				Intent intent = new Intent(BasicActivity.this,
						RunningActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(0, 0);
				break;
			}
			case R.id.record_button: {
				Intent intent = new Intent(BasicActivity.this,
						RecordActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(0, 0);
				break;
			}
			default: {
				Log.e("Man-Up Error",
						"Some unknown error --- Please check item button part.");
			}
			}
		}
	}

	/**
	 * 
	 * getReadableDB
	 * 
	 * @author Sean Lee
	 * @return
	 */
	public SQLiteDatabase getReadableDB() {
		DatabaseHelper dbHelper = new DatabaseHelper(BasicActivity.this,
				"manupsdb", null, 1);
		dbHelper.setTableName("manups");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db;
	}

	/**
	 * 
	 * getWritableDB
	 * 
	 * @author Sean Lee
	 * @return
	 */
	public SQLiteDatabase getWritableDB() {
		DatabaseHelper dbHelper = new DatabaseHelper(BasicActivity.this,
				"manupsdb", null, 1);
		dbHelper.setTableName("manups");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db;
	}

	// Menu is here
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.menu_exit): {
			finish();
			break;
		}
		case (R.id.language_change): {

			String[] language = getResources().getStringArray(R.array.language);
			builder = new AlertDialog.Builder(this);
			builder.setSingleChoiceItems(language, -1,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							Resources resources = getResources();
							Configuration config = resources.getConfiguration();
							DisplayMetrics dm = resources.getDisplayMetrics();
							
							switch (which) {
							case 0: {
								config.locale = Locale.ENGLISH;
								break;
							}
							case 1: {
								config.locale = Locale.SIMPLIFIED_CHINESE;
								break;
							}
							case 2: {
								config.locale = Locale.TRADITIONAL_CHINESE;
								break;
							}
							case 3: {
								config.locale = Locale.getDefault();
								break;
							}
							}
							resources.updateConfiguration(config, dm);
							dialog.dismiss();
							finish();
							Intent intent = new Intent();
							intent.setClass(BasicActivity.this,
									PushupsActivity.class);
							startActivity(intent);
						}
					});
			builder.show();
			break;
		}
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitJudgement();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void exitJudgement() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(BasicActivity.this,
					getResources().getString(R.string.exit_judge),
					Toast.LENGTH_SHORT).show();
			Message msg = handleExit.obtainMessage();
			handleExit.sendMessageDelayed(msg, 2000);
		} else {
			finish();
		}

	}

}