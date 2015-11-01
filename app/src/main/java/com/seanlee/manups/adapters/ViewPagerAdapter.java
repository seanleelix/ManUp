package com.seanlee.manups.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.seanlee.manups.R;
import com.seanlee.manups.activities.PushupsActivity;
import com.seanlee.manups.utils.Settings;

import java.util.List;

/**
 * @author Sean Lee
 *         modified at 29/10/2015
 */

public class ViewPagerAdapter extends PagerAdapter {


    private List<View> displayViews;
    private Activity activity;

    public ViewPagerAdapter(Activity activity, List<View> displayViews) {
        this.displayViews = displayViews;
        this.activity = activity;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(displayViews.get(position));

        if (position == displayViews.size() - 1) {
            ImageView startImageView = (ImageView) container.findViewById(R.id.iv_start_ManUps);
            startImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setGuided();
                    goHome();
                }
            });
        }


        return displayViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(displayViews.get(position));
    }

    @Override
    public int getCount() {
        return displayViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    private void goHome() {
        Intent intent = new Intent(activity, PushupsActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }


    private void setGuided() {
        SharedPreferences preferences = activity.getSharedPreferences(Settings.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isFirstIn", false);
        editor.commit();
    }

}
