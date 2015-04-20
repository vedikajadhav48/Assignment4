package com.example.vedikajadhav.assignment4;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.util.Log;
import android.view.VelocityTracker;

/**
 * Created by Vedika Jadhav on 4/17/2015.
 */
public class Circle {
    private static final String TAG = "Circle";
    private PointF mOrigin;
    private int mRadius;
    private static int currentId = 0;
    private int mId;
    private boolean increaseRadius = false;
    private Handler increaseRadiusHandler = new Handler();
    private Handler velocityHandler = new Handler();
    private PointF mVelocity;

    public Circle(PointF origin) {
        mOrigin = origin;
        mRadius = 50;
        mId = currentId++;
        mVelocity = new PointF();
    }

    public void setOrigin(PointF origin){
        mOrigin = origin;
    }

    public void setRadius(int radius) {
        mRadius = radius;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public int getRadius() {
        return mRadius;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public PointF getVelocity() {
        return mVelocity;
    }

    public void setVelocity(PointF velocity) {
        mVelocity = velocity;
    }

    public boolean inCircle(PointF curr){
        double square_distance;
        square_distance = ((this.mOrigin.x - curr.x)*(this.mOrigin.x - curr.x)) + ((this.mOrigin.y - curr.y)*(this.mOrigin.y - curr.y));
        return square_distance < (this.mRadius)*(this.mRadius);
    }

    public void keepIncreasingRadius(CircleDrawingView circleDrawingView){
        Log.i(TAG, "keepIncreasing");
        increaseRadius = true;
        increaseRadiusHandler.post(new IncreaseRadiusThread(circleDrawingView));
    }

    public void stopIncreasingRadius(){
        Log.i(TAG, "stopIncreasing");
        increaseRadius = false;
    }

    public void setVelocity(VelocityTracker velocity, CircleDrawingView circleDrawingView){
        Log.i(TAG, "setVelocity");
        mVelocity.x = velocity.getXVelocity();
        mVelocity.y = velocity.getYVelocity();
        velocityHandler.post(new VelocityHandler(circleDrawingView));
    }

    private class IncreaseRadiusThread implements Runnable{
        private CircleDrawingView mCircleDrawingView;

        private IncreaseRadiusThread(CircleDrawingView circleDrawingView) {
            mCircleDrawingView =circleDrawingView;
        }

        @Override
        public void run() {
            Log.i(TAG, "run");
            if (increaseRadius && mRadius<100){
                mRadius = mRadius + 1;
                mCircleDrawingView.postInvalidate();
                increaseRadiusHandler.post(new IncreaseRadiusThread(mCircleDrawingView));
            }
        }
    }

    private class VelocityHandler implements Runnable{
        private CircleDrawingView mCircleDrawingView;
        private int mViewHeight;
        private int mViewWidth;

        private VelocityHandler(CircleDrawingView circleDrawingView){
            mCircleDrawingView = circleDrawingView;
            mViewHeight = mCircleDrawingView.getHeight();
            mViewWidth = mCircleDrawingView.getWidth();
        }

        @Override
        public void run() {
            mOrigin.x = mOrigin.x + mVelocity.x;
            mOrigin.y = mOrigin.y + mVelocity.y;
            if(mOrigin.x + mRadius >= mViewWidth || mOrigin.x - mRadius < 0){
                mVelocity.x = mVelocity.x * (-1);
            }
            if(mOrigin.y + mRadius >= mViewHeight || mOrigin.y - mRadius < 0){
                mVelocity.y = mVelocity.y * (-1);
            }

            mCircleDrawingView.postInvalidate();
            velocityHandler.post(new VelocityHandler(mCircleDrawingView));
        }
    }
}
