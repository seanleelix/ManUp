package com.seanlee.manups.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.seanlee.manups.R;

/**
 * Created by Sean Lee on 5/11/15.
 */
public class ManUpUtils {

    public static RelativeLayout.LayoutParams getBottomButtonLayoutParams(Context context) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), R.drawable.situps_u, options);
        int bottomButtonImageWidth = options.outWidth;
        int bottomButtonImageHeight = options.outHeight;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point screenDisplayPoint = new Point();
        wm.getDefaultDisplay().

                getSize(screenDisplayPoint);

        int bottomButtonLayoutHeight = bottomButtonImageHeight * (screenDisplayPoint.x / 5) / bottomButtonImageWidth;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, bottomButtonLayoutHeight);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        return layoutParams;
    }
}
