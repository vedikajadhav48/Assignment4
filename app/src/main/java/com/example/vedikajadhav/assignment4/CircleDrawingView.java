package com.example.vedikajadhav.assignment4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Vedika Jadhav on 4/17/2015.
 */
public class CircleDrawingView extends View implements GestureDetector.OnGestureListener{
    public static final String TAG = "CircleDrawingView";
    private Circle mCurrentCircle;
    private ArrayList<Circle> mCircles = new ArrayList<Circle>();
    private Paint mCirclePaint;
    private Paint mBackgroundPaint;
    private GestureDetectorCompat mGestureDetector;
    PointF curr;
    private boolean swipeInProgress = false;
    private double startX;
    private double startY;
    private VelocityTracker velocity;
    private MotionEvent lastEvent;


    //Used when creating the view in code
    public CircleDrawingView(Context context) {
        this(context, null);
    }

    //Used when inflating the view from XML
    public CircleDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Paint the boxes a nice semitransparent red (ARGB)
        mCirclePaint = new Paint();
        mCirclePaint.setColor(0x22ff0000);

        //Paint the background off-white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);

        mGestureDetector = new GestureDetectorCompat(getContext(), this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        curr = new PointF(motionEvent.getX(), motionEvent.getY());
        int action = MotionEventCompat.getActionMasked(motionEvent);
        mGestureDetector.onTouchEvent(motionEvent);

        Log.i(TAG, "Received event at X= " + curr.x + ",at Y= " + curr.y + ":");
        switch(action){
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "ACTION_DOWN");
                //Reset the drawing state
                boolean insideCircle = false;
                for(Circle circle:mCircles){
                    if(circle.inCircle(curr)){
                        mCurrentCircle = circle;
                        insideCircle = true;
                    }
                }
                if(!insideCircle) {
                    if (mCircles.size() <= 15) {
                        mCurrentCircle = new Circle(curr);
                        mCircles.add(mCurrentCircle);
                        invalidate();
                    }
                }
                velocity = VelocityTracker.obtain();
                velocity.addMovement(motionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "ACTION_MOVE");
                velocity.addMovement(motionEvent);
                lastEvent = motionEvent;
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP");
                velocity.computeCurrentVelocity(1);
                Log.i(TAG, "X vel " + velocity.getXVelocity() + " Y vel " + velocity.getYVelocity());
                mCurrentCircle.setVelocity(velocity, this);
                velocity.recycle();
                velocity = null;
                mCurrentCircle.stopIncreasingRadius();
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, "ACTION_CANCEL");
                mCurrentCircle = null;
                break;
            default:
                return super.onTouchEvent(motionEvent);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Fill the background
        canvas.drawPaint(mBackgroundPaint);

        for(Circle circle:mCircles){
            canvas.drawCircle(circle.getOrigin().x, circle.getOrigin().y, circle.getRadius(), mCirclePaint);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
      //  Log.i(TAG, "Long Press");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.i(TAG, "onShow Press");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(TAG, "on single tap up");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i(TAG, "on Scroll ");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i(TAG, "Long Press");
        Log.i(TAG, "Circle Id" + mCurrentCircle.getId());
        mCurrentCircle.keepIncreasingRadius(this);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    private boolean handleActionDown(MotionEvent motionEvent){
        swipeInProgress = true;
        startX = motionEvent.getX();
        startY = motionEvent.getY();
        return true;
    }

    private boolean handleActionUp(MotionEvent motionEvent){
        if(!swipeInProgress){
            return false;
        }
        double endX = motionEvent.getX();
        double endY = motionEvent.getY();
        Log.d(TAG, "swipeDistance" + (endX - startX));
        swipeInProgress = false;
        return true;
    }
}
