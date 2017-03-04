package com.example.raghavkishan.drawingboard;
/* reference for the entore application:
1)from: https://developer.android.com
2)Class slides
*/
/*
The DrawCirleview contains the implementation of the draw, delete and move mode using objects of Circles class stored in an arraylist.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import java.util.ArrayList;
import java.util.Timer;


import static com.example.raghavkishan.drawingboard.MainActivity.mode;
import static com.example.raghavkishan.drawingboard.MainActivity.paint;

public class DrawCircleView extends View implements View.OnTouchListener{

    Circles circle;
    public static float viewWidth;
    public static float viewHeight;
    VelocityTracker velocity;
    float velocityX,velocityY;
    float downDeletex,downDeletey,upDeletex,upDeletey,downDeleteDistance,upDeleteDistance,downMoveX,downMoveY,upMoveX,upMoveY;
    boolean swipeInProgress;
    float screenWidth,screenHeight;
    ArrayList<Circles> arrayCircle = new ArrayList<Circles>();
    ArrayList<Circles> arrayRemoveCircle = new ArrayList<Circles>();
    ArrayList<Integer> selectedMoveIndices = new ArrayList<Integer>();
    MainActivity paintObject = new MainActivity();
    Timer repeat =new Timer();

    public DrawCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        screenWidth = canvas.getWidth();
        screenHeight = canvas.getHeight();
        super.onDraw(canvas);
        for (Circles each:arrayCircle){
            canvas.drawCircle(each.getCenterx(),each.getCentery(),each.getRadius(),each.getPaint());
        }
        if(swipeInProgress){
            canvas.drawCircle(circle.getCenterx(),circle.getCentery(),circle.getRadius(),circle.getPaint());
        }
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                return handleActionDown(event);
            case MotionEvent.ACTION_MOVE:
                return handleActionMove(event);
            case MotionEvent.ACTION_UP:
                return handleActionUp(event);
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:{
                swipeInProgress = false;
                return false;}
        }
        return false;
    }

    private boolean handleActionDown(MotionEvent event){
        //Log.i("check","Entering handleaction down");
        if(mode == "draw_mode") {
            circle = new Circles();
            swipeInProgress = true;                     //Obtaining the touch points in the form of x and y coordinates.
            circle.setCenterx(event.getX());
            circle.setCentery(event.getY());
            circle.setX(event.getX());
            circle.setY(event.getY());
            circle.setPaint(paint);
            selectedMoveIndices.clear();
            return true;
        }
        if (mode == "delete_mode"){
            //Log.i("check","Entering delete mode");
            downDeletex = event.getX();
            downDeletey = event.getY();
            return true;
        }
        if(mode == "move_mode"){
            downMoveX = event.getX();
            downMoveY = event.getY();
            velocity = VelocityTracker.obtain();
            velocity.addMovement(event);
            circleIdentifyToMove();
            return true;
        }
        return false;
    }

    private boolean handleActionUp(MotionEvent event){
        //Log.i("check","handleActionUp");
        //Log.i("check",mode);
        if (mode == "draw_mode") {
            if (!swipeInProgress) return false;
            circle.setX(event.getX());
            circle.setY(event.getY());
            arrayCircle.add(circle);
            swipeInProgress = false;
            return true;
        }

        if (mode == "delete_mode"){
            //Log.i("check","Entering delete mode");
            upDeletex = event.getX();
            upDeletey = event.getY();
            deleteCircle();
            return true;
        }

        if (mode == "move_mode"){

            velocity.computeCurrentVelocity(1000);
            velocityX = velocity.getXVelocity();
            velocityY = velocity.getYVelocity();
            upMoveX = event.getX();
            upMoveY = event.getY();
            circleMove();
            velocityMax();
            velocity.recycle();
            velocity = null;
            invalidate();
            return true;
        }
        return false;
    }

    private boolean handleActionMove(MotionEvent event){
        if (mode == "draw_mode") {
            circle.setX(event.getX());
            circle.setY(event.getY());
            circle.setRadius();
            return true;
        }

        if(mode == "move_mode"){
            velocity.addMovement(event);
            return true;
        }
        return false;
    }

    private float distance(float x1,float y1,float x2, float y2){
        return (float)Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {         //Determining the size of the view for the bounds.
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = View.MeasureSpec.getSize(heightMeasureSpec);
    }

    private void deleteCircle() {
        for (Circles each : arrayCircle) {
            downDeleteDistance = distance(each.getCenterx(),each.getCentery(),downDeletex,downDeletey);
            upDeleteDistance = distance(each.getCenterx(),each.getCentery(),upDeletex,upDeletey);
            if (downDeleteDistance < each.getRadius() && upDeleteDistance < each.getRadius()){
                arrayRemoveCircle.add(each);
            }
        }
        arrayCircle.removeAll(arrayRemoveCircle);
        //System.out.println("Circle deleted");
    }

    private void circleIdentifyToMove(){                                // Identifying the set of circles that the user wants to move based on the touch value.
        selectedMoveIndices.clear();
        for (int i = 0; i<arrayCircle.size(); i++) {
            Circles presentCircle = arrayCircle.get(i);
            float moveRadius = presentCircle.getRadius();
            if (moveRadius >= distance(arrayCircle.get(i).getCenterx(),arrayCircle.get(i).getCentery(),downMoveX,downMoveY)){
                selectedMoveIndices.add(i);
                Log.i("selected circle",""+i);
            }
            presentCircle.setDeltaX(1);
            presentCircle.setDeltaY(1);
        }
    }

    private void circleMove(){

        if (velocityX !=0 || velocityY!=0){
            for (int i = 0; i < selectedMoveIndices.size();i++) {
                Circles tempCircle = arrayCircle.get(selectedMoveIndices.get(i));
                onCollision(tempCircle);                                //Checking for collision with the boundaries.
                tempCircle.setCenterx((float)(tempCircle.centerx + (0.0035 * velocityX * tempCircle.getDeltaX())));
                tempCircle.setCentery((float)(tempCircle.centery + (0.0035 * velocityY * tempCircle.getDeltaY())));
                //invalidate();
                if(velocityX == 0 || velocityY == 0){
                    if(tempCircle.deltaX == -1){
                        tempCircle.setDeltaX(1);
                    }
                    if(tempCircle.deltaY == -1) {
                        tempCircle.setDeltaY(1);
                    }
                }
            }
            //invalidate();
            if(!(velocityX>50|| velocityX<-50 || velocityY>50 ||velocityY<-50)){
                velocityX = 0;
                velocityY = 0;
                selectedMoveIndices.clear();
            }
            else
            {
                this.postDelayed(new Mover(),1);
            }
        }
    }

    class Mover implements Runnable{                            //Using the postdelayed method to move the circles.
        public void run(){
            velocityX = (float) (velocityX * 0.99);
            velocityY = (float) (velocityY * 0.99);
            if(mode == "draw_mode" || mode == "delete_mode"){
                velocityX = 0;
                velocityY = 0;
            }
            circleMove();
        }
    }


    private boolean xOutOfBounds(Circles c){
        if(c.centerx - c.getRadius() <0){
            return true;
        }
        if(c.centerx + c.getRadius() > screenWidth){
            return true;
        }
        return false;
    }

    private boolean yOutOfBounds(Circles c){
        if(c.centery - c.getRadius() < 0){
            return true;
        }
        if(c.centery + c.getRadius()> screenHeight){
            return true;
        }
        return false;
    }

    private void onCollision(Circles c){
        if(xOutOfBounds(c)){
            c.deltaX *= -1;
        }
        if(yOutOfBounds(c)){
            c.deltaY *= -1;
        }
    }

    private void velocityMax(){
        if(velocityX >7000){
            velocityX = 7000;
        }
        if(velocityX<-7000){
            velocityX = -7000;
        }

        if(velocityY >7000){
            velocityY = 7000;
        }
        if(velocityY<-7000){
            velocityY = -7000;
        }
    }

}
