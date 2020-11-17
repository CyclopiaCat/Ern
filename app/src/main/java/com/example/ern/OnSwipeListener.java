package com.example.ern;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

// https://stackoverflow.com/questions/13095494/how-to-detect-swipe-direction-between-left-right-and-up-down/26387629#26387629
public class OnSwipeListener extends GestureDetector.SimpleOnGestureListener {


    //TODO: swipe distance customization
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        float x1 = e1.getX();
        float y1 = e1.getY();

        float x2 = e2.getX();
        float y2 = e2.getY();

        Direction direction = getDirection(x1,y1,x2,y2);
        return onSwipe(direction);
    }

    public boolean onSwipe(Direction direction) {
        return false;
    }

    public Direction getDirection(float x1, float y1, float x2, float y2){
        double angle = getAngle(x1, y1, x2, y2);
        return Direction.fromAngle(angle);
    }

    public double getAngle(float x1, float y1, float x2, float y2) {

        double rad = Math.atan2(y1-y2,x2-x1) + Math.PI;
        double ret = (rad*180/Math.PI + 180)%360;
        Log.d("LISTENER", String.valueOf(ret));
        return ret;
    }

    //TODO: use radians
    public enum Direction{
        UP,
        DOWN,
        LEFT,
        RIGHT;

        public static Direction fromAngle(double angle){
            if(inRange(angle, 45, 135)){
                return Direction.UP;
            }
            else if(inRange(angle, 0,45) || inRange(angle, 315, 360)){
                return Direction.RIGHT;
            }
            else if(inRange(angle, 225, 315)){
                return Direction.DOWN;
            }
            else{
                return Direction.LEFT;
            }

        }

        private static boolean inRange(double angle, float init, float end){
            return (angle >= init) && (angle < end);
        }
    }
}