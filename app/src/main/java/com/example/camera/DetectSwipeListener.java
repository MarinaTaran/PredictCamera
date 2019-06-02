package com.example.camera;

import android.gesture.Gesture;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class DetectSwipeListener extends GestureDetector.SimpleOnGestureListener {

    private static int MIN_SWIPE_DISTANCE_X = 100;
    private static int MIN_SWIPE_DISTANCE_Y = 100;

    private static int MAX_SWIPE_DISTANCE_X = 1000;
    private static int MAX_SWIPE_DISTANCE_Y = 1000;

    private MainActivity activity = null;

    public MainActivity getActivity() {
        return activity;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float deltaX = e1.getX() - e2.getX();
        float deltaY = e1.getY() - e2.getY();


        float deltaXAbc = Math.abs(deltaX);
        float deltaYAbc = Math.abs(deltaY);

        if (deltaXAbc >= MIN_SWIPE_DISTANCE_X && deltaXAbc <= MAX_SWIPE_DISTANCE_X) {
            if (deltaX > 0) {
                this.activity.displayMessage("LET'S GUESS WHAT'S ON YOUR PHOTO");
            } else {
                this.activity.displayMessage("You can know your age");
            }
        }

//        if(deltaYAbc>=MIN_SWIPE_DISTANCE_X && deltaYAbc<=MAX_SWIPE_DISTANCE_Y){
//            if(deltaY>0){
//                this.activity.displayMessage("YOU ");
//            }else {
//                this.activity.displayMessage("You can know your age");
//            }
//        }

        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        this.activity.displayMessage("Single tap");
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        this.activity.displayMessage("Double tap");
        return true;
    }
}
