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

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.seanlee.manups.R;
import com.seanlee.manups.views.TouchScrollView;

/**
 * @author Sean Lee
 * @version 1.3
 */
public class RecordActivity extends BasicActivity {

    private Button mPushupsButton, mSitupsButton, mRunningButton,
            mRecordClearButton;
    private ListView mDataListView;
    private Cursor cursor = null;
    private SimpleCursorAdapter adapter = null;
    private SQLiteDatabase database = null;

    /**
     * the new view set
     */
    public TouchScrollView touchScrollView;
    public LinearLayout container;
    public ImageView aboutview;
    public ImageView emptyImageView;
    // about view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        mPushupsButton = (Button) findViewById(R.id.pushups_button);
        mSitupsButton = (Button) findViewById(R.id.situps_button);
        mRunningButton = (Button) findViewById(R.id.running_button);
        mRecordClearButton = (Button) findViewById(R.id.record_clear_button);
        mDataListView = (ListView) findViewById(R.id.dataListView);

        ItemsOnClickListener ItemOnClickListener = new ItemsOnClickListener();
        mPushupsButton.setOnClickListener(ItemOnClickListener);
        mSitupsButton.setOnClickListener(ItemOnClickListener);
        mRunningButton.setOnClickListener(ItemOnClickListener);
        mRecordClearButton.setOnClickListener(ItemOnClickListener);

        aboutview = (ImageView) findViewById(R.id.aboutview);
        container = (LinearLayout) findViewById(R.id.container);
        touchScrollView = new TouchScrollView(this, aboutview, R.drawable.records_top);

        emptyImageView = (ImageView) findViewById(R.id.empty_imageview);
        emptyImageView.setVisibility(View.INVISIBLE);

        // Delete the data table
        mRecordClearButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(RecordActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.clear_records_dialog);
                dialog.show();

                Button confirm = (Button) dialog.findViewById(R.id.confirm);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);

                confirm.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        database.delete("manups", null, null);
                        adapter = null;
                        mDataListView.setAdapter(adapter);
                        emptyImageView.setVisibility(View.VISIBLE);
                        mRecordClearButton.setClickable(false);
                        mRecordClearButton.setAlpha(0.2f);
                        dialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

        container.addView(touchScrollView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        database = getReadableDB();
        cursor = database
                .rawQuery(
                        "SELECT _id,date,pushups,situps,running FROM manups ORDER BY _id DESC",
                        null);
        adapter = new SimpleCursorAdapter(
                this,
                R.layout.record_items,
                cursor,
                new String[]{"date", "pushups", "situps", "running"},
                new int[]{R.id.date, R.id.pushups, R.id.situps, R.id.running});
        mDataListView.setAdapter(adapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        database.close();
        cursor.close();
    }

}
