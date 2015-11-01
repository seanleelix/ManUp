package com.seanlee.manups.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.seanlee.manups.R;
import com.seanlee.manups.adapters.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LI Weimin
 * @author Sean Lee
 *
 * modified at 29/10/2015
 */

public class GuideActivity extends Activity implements OnPageChangeListener {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<View> guideImageList;

    private ImageView[] dots;

    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initViews();
        initDots();
    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);

        guideImageList = new ArrayList<View>();

        ImageView imageView1 = new ImageView(this);
        imageView1.setImageResource(R.drawable.guide_1);
        guideImageList.add(imageView1);

        ImageView imageView2 = new ImageView(this);
        imageView2.setImageResource(R.drawable.guide_2);
        guideImageList.add(imageView2);

        ImageView imageView3 = new ImageView(this);
        imageView3.setImageResource(R.drawable.guide_3);
        guideImageList.add(imageView3);

        guideImageList.add(inflater.inflate(R.layout.guide_four, null));

        viewPagerAdapter = new ViewPagerAdapter(this, guideImageList);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setOnPageChangeListener(this);
    }

    private void initDots() {
        LinearLayout indicatorLinearLayout = (LinearLayout) findViewById(R.id.indicator_linearlayout);

        dots = new ImageView[guideImageList.size()];


        for (int i = 0; i < guideImageList.size(); i++) {
            dots[i] = (ImageView) indicatorLinearLayout.getChildAt(i);
            dots[i].setEnabled(true);
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false);
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > guideImageList.size() - 1
                || currentIndex == position) {
            return;
        }

        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = position;
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {
    }


    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {

        setCurrentDot(arg0);
    }

}
