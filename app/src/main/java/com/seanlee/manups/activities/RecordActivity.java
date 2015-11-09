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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Space;

import com.seanlee.manups.R;
import com.seanlee.manups.adapters.RecordAdapter;
import com.seanlee.manups.databases.DatabaseOperation;
import com.seanlee.manups.models.RecordModel;
import com.seanlee.manups.utils.PreferenceUtil;
import com.seanlee.manups.views.TouchScrollView;

import java.util.List;
import java.util.Locale;

/**
 * @author Sean Lee
 *         modified at 2/11/2015
 */
public class RecordActivity extends BasicActivity {

    private Button mPushupsButton, mSitupsButton, mRunningButton,
            mRecordClearButton;
    private ListView mDataListView;

    public TouchScrollView touchScrollView;
    public LinearLayout container;
    public Space topIconSpace;
    public ImageView emptyImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initView();
    }

    private void initView() {

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

        topIconSpace = (Space) findViewById(R.id.top_icon_space);

        container = (LinearLayout) findViewById(R.id.container);

        RelativeLayout relativeLayout = (RelativeLayout) View.inflate(this, R.layout.setting_layout, null);
        container.addView(relativeLayout);

        touchScrollView = new TouchScrollView(this, relativeLayout, R.drawable.records_top, topIconSpace);

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
                        DatabaseOperation databaseOperation = new DatabaseOperation(RecordActivity.this);
                        databaseOperation.deleteAllRecord();

                        mDataListView.setAdapter(null);
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

        Button mLanguage = (Button) findViewById(R.id.select_language);
        mLanguage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                Resources languageRes = getResources();
                String title4AlartDialog = languageRes.getString(R.string.language_change);
                builder.setTitle(title4AlartDialog);

                CharSequence[] items = languageRes.getStringArray(R.array.language);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                switchLanguage("en");
                                break;
                            case 1:
                                switchLanguage("zh-rCN");
                                break;
                            case 2:
                                switchLanguage("zh-rTW");
                                break;
                            default:
                                Log.d(this.getClass().toString(), "onClick Error");
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        initData();
    }

    private void initData() {

        DatabaseOperation databaseOperation = new DatabaseOperation(this);
        List<RecordModel> recordModelList = databaseOperation.getRecordModel();

        RecordAdapter recordAdapter = new RecordAdapter(this, recordModelList);
        mDataListView.setAdapter(recordAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void switchLanguage(String language) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else if (language.equals("zh-rCN")){
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if(language.equals("zh-rTW")){
            config.locale = Locale.TRADITIONAL_CHINESE;
        }

        resources.updateConfiguration(config, dm);
        PreferenceUtil.setPref(this, PreferenceUtil.LANGUAGE_KEY, language);

        finish();
        Intent intent = new Intent(RecordActivity.this, RecordActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}