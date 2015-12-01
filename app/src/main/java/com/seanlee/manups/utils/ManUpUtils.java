package com.seanlee.manups.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.seanlee.manups.R;
import com.seanlee.manups.interfaces.SettingInterface;

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

    public static void setting(final Context context, final SettingInterface settingInterface) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.running_setting);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.x = 21;
        layoutParams.y = 15;

        final EditText setWeight, setHeight;

        setWeight = (EditText) dialog.findViewById(R.id.weightEditText);
        setHeight = (EditText) dialog.findViewById(R.id.heightEditText);

        setWeight.setText("" + PreferenceUtil.getFloat(context, PreferenceUtil.BODY_WEIGHT, Settings.DEFAULT_WEIGHT));
        setHeight.setText("" + PreferenceUtil.getFloat(context, PreferenceUtil.USER_HEIGHT, Settings.DEFAULT_HEIGHT));

        Button confirm = (Button) dialog.findViewById(R.id.confirm);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        // The setting dialog confirm button
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (setWeight.getText().toString().equals("")) {
                    Toast.makeText(
                            context,
                            context.getResources().getString(
                                    R.string.setting_weight_error),
                            Toast.LENGTH_SHORT).show();
                } else if (setHeight.getText().toString().equals("")) {
                    Toast.makeText(
                            context,
                            context.getResources().getString(
                                    R.string.setting_height_error),
                            Toast.LENGTH_SHORT).show();
                } else {
                    float mUserWeight = Float.parseFloat(setWeight.getText()
                            .toString());
                    float mUserHeight = Float.parseFloat(setHeight.getText()
                            .toString());
                    float mStepLength = mUserHeight / 100 * 0.414f;
                    Toast.makeText(context,
                            context.getResources().getString(R.string.setting_success),
                            Toast.LENGTH_SHORT).show();

                    PreferenceUtil.setPref(context, PreferenceUtil.USER_HEIGHT, mUserHeight);
                    PreferenceUtil.setPref(context, PreferenceUtil.BODY_WEIGHT, mUserWeight);
                    PreferenceUtil.setPref(context, PreferenceUtil.STEP_LENGTH, mStepLength);
                    PreferenceUtil.setPref(context, PreferenceUtil.SET_USER_INFO, true);

                    settingInterface.onSettingFinishListener();

                    dialog.dismiss();
                }
            }
        });

        // The setting dialog cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
