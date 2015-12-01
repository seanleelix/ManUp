package com.seanlee.manups.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.seanlee.manups.R;
import com.seanlee.manups.services.ManupService;
import com.seanlee.manups.utils.PreferenceUtil;

/**
 * @author LI Weimin
 * @author Sean Lee
 *         <p/>
 *         Modified at 29/10/2015
 */

public class SplashActivity extends Activity {

    private boolean isFirstIn = false;

    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;

    private static final long SPLASH_DELAY_MILLIS = 2000;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // To start a service for updating the database everyday
        Intent serviceIntent = new Intent(this, ManupService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {

        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case GO_HOME:
                            goHome();
                            break;
                        case GO_GUIDE:
                            goGuide();
                            break;
                    }
                    super.handleMessage(msg);
                }
            };
        }

        PreferenceUtil.getBoolean(this, PreferenceUtil.FIRST_IN_KEY, true);

        if (!isFirstIn) {
            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
        }

    }

    private void goHome() {
        Intent intent = new Intent(SplashActivity.this, PushupsActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    private void goGuide() {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isFirstIn)
            mHandler.removeMessages(GO_GUIDE);
        else
            mHandler.removeMessages(GO_HOME);
    }
}
