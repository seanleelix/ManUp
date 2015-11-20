package com.seanlee.manups.utils;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by Sean Lee on 5/11/15.
 */
public class ManUpUtils {

    public static AlphaAnimation defaultBreathingAnimation() {
        AlphaAnimation alphaAnimationText = new AlphaAnimation(1, 0.1f);
        alphaAnimationText.setDuration(1300);
        alphaAnimationText.setRepeatMode(Animation.REVERSE);
        alphaAnimationText.setRepeatCount(Animation.INFINITE);
        return alphaAnimationText;
    }

}
