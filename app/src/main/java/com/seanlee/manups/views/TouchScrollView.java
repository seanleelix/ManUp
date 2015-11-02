package com.seanlee.manups.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.widget.Space;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * @author Sean Lee
 *         Created at 30/10/2015
 */
public class TouchScrollView extends LinearLayout implements GestureDetector.OnGestureListener {

    // Used for Fling Event
    // private int verticalMinDistance = 20;
    // private int minVelocity = 2;

    private final static int MOVE_PIXEL_UNIT = 2;
    private final static int MOVE_UNIT_TIME = 1; //millisecond every pixel unit

    private final static int MOVE_UP = 1;
    private final static int MOVE_DOWN = 2;

    private int contentHeight = 0;
    private Button dropDownButton;

    private Context mContext;
    private GestureDetector mGestureDetector;
    private float mScrollY;

    // Not use now
    // private GroupViewClosedEvent groupViewClosedEvent = null;
    // private GroupViewOpenedEvent groupViewOpenedEvent = null;
    //    public interface TouchScrollViewClosedEvent {
    //        void onTouchScrollViewClosed(View groupView);
    //    }
    //
    //    public interface TouchScrollViewOpenedEvent {
    //        void onTouchScrollViewOpened(View groupView);
    //    }

    public TouchScrollView(Context context, View contentView, int dropButtonResource, Space topIconSpace) {
        super(context);
        this.mContext = context;

        mGestureDetector = new GestureDetector(mContext, this);
        mGestureDetector.setIsLongpressEnabled(false);

        LayoutParams contentViewLayoutParams = (LayoutParams) contentView.getLayoutParams();
        contentViewLayoutParams.weight = 1;// need this -- check why later TODO
        contentView.setLayoutParams(contentViewLayoutParams);

        // Calculate drop down button height
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), dropButtonResource, options);
        int dropButtonWidth = options.outWidth;
        int dropButtonHeight = options.outHeight;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point screenDisplayPoint = new Point();
        wm.getDefaultDisplay().getSize(screenDisplayPoint);
        int dropButtonDisplayHeight = dropButtonHeight * screenDisplayPoint.x / dropButtonWidth;

        // Leave a space to the top for record list
        topIconSpace.setMinimumHeight(dropButtonHeight);

        // Calculate Content height
        LayoutParams touchScrollViewLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, screenDisplayPoint.y);
        touchScrollViewLayoutParams.bottomMargin = 0;
        contentHeight = touchScrollViewLayoutParams.height - dropButtonDisplayHeight;

        this.setLayoutParams(touchScrollViewLayoutParams);
        this.setOrientation(LinearLayout.VERTICAL);

        dropDownButton = new Button(context);
        dropDownButton.setBackgroundResource(dropButtonResource);
        dropDownButton.setGravity(Gravity.CENTER);
        LayoutParams dropDownButtonLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, dropButtonDisplayHeight);
        dropDownButtonLayoutParams.gravity = Gravity.CENTER;
        dropDownButton.setLayoutParams(dropDownButtonLayoutParams);
        dropDownButton.setOnTouchListener(handlerTouchEvent);
        this.addView(dropDownButton);

    }

    private View.OnTouchListener handlerTouchEvent = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_UP: {
                    LayoutParams touchScrollViewLayoutParams = (LayoutParams) TouchScrollView.this.getLayoutParams();

                    if (touchScrollViewLayoutParams.bottomMargin >= (-contentHeight / 2)) {
                        moveTouchScrollView(MOVE_UP, touchScrollViewLayoutParams.bottomMargin);

                    } else if (touchScrollViewLayoutParams.bottomMargin < (-contentHeight / 2)) {
                        moveTouchScrollView(MOVE_DOWN, touchScrollViewLayoutParams.bottomMargin);
                    }
                    break;
                }
            }

            return mGestureDetector.onTouchEvent(event);
        }
    };

    private void moveTouchScrollView(int moveDirection, int bottomMargin) {
        ValueAnimator valueAnimator = null;

        if (moveDirection == MOVE_UP) {
            valueAnimator = ValueAnimator.ofInt(bottomMargin, 0);
            valueAnimator.setDuration(Math.abs(bottomMargin * MOVE_UNIT_TIME / MOVE_PIXEL_UNIT));
        } else if (moveDirection == MOVE_DOWN) {
            valueAnimator = ValueAnimator.ofInt(bottomMargin, -contentHeight);
            valueAnimator.setDuration(Math.abs((contentHeight + bottomMargin) * MOVE_UNIT_TIME / MOVE_PIXEL_UNIT));
        }

        if (valueAnimator != null) {
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentValue = (Integer) animation.getAnimatedValue();

                    LayoutParams touchScrollViewLayoutParams = (LayoutParams) TouchScrollView.this.getLayoutParams();
                    touchScrollViewLayoutParams.bottomMargin = currentValue;
                    TouchScrollView.this.setLayoutParams(touchScrollViewLayoutParams);
                }
            });
            valueAnimator.start();


        }
    }

    //------------------------  Gesture  -----------------------------------
    // First Called Method
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    // For informing the user that they have pressed
    @Override
    public void onShowPress(MotionEvent e) {
    }

    // Just Tap the button
    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        LayoutParams touchScrollViewLayoutParams = (LayoutParams) TouchScrollView.this.getLayoutParams();
        if (touchScrollViewLayoutParams.bottomMargin < 0)
            moveTouchScrollView(MOVE_UP, touchScrollViewLayoutParams.bottomMargin);
        else if (touchScrollViewLayoutParams.bottomMargin >= 0)
            moveTouchScrollView(MOVE_DOWN, touchScrollViewLayoutParams.bottomMargin);

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {

        mScrollY = e2.getY() - e1.getY();

        LayoutParams touchScrollViewLayoutParams = (LayoutParams) TouchScrollView.this.getLayoutParams();

        if (touchScrollViewLayoutParams.bottomMargin > -(contentHeight) && mScrollY > 0) {
            touchScrollViewLayoutParams.bottomMargin = Math.max((touchScrollViewLayoutParams.bottomMargin - (int) mScrollY),
                    -contentHeight);
            TouchScrollView.this.setLayoutParams(touchScrollViewLayoutParams);
        } else if (touchScrollViewLayoutParams.bottomMargin < 0 && mScrollY < 0) {
            touchScrollViewLayoutParams.bottomMargin = Math.min((touchScrollViewLayoutParams.bottomMargin - (int) mScrollY), 0);
            TouchScrollView.this.setLayoutParams(touchScrollViewLayoutParams);
        }

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // if (e1.getY() - e2.getY() > verticalMinDistance && Math.abs(velocityY) > minVelocity) {
        // // Move Down
        // } else if (e2.getY() - e1.getY() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
        // // Move Up
        // }
        return false;
    }

}