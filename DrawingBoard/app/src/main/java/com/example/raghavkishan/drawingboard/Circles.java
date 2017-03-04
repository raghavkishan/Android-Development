package com.example.raghavkishan.drawingboard;
/* reference for the entore application:
1)from: https://developer.android.com
2)Class slides
*/
/*
This class represents a circle of which objects are created to be used for draw,delete and move.
Using setters and getters to set and obtain the values in other java files in the package.
 */
import android.graphics.Paint;

import static com.example.raghavkishan.drawingboard.DrawCircleView.viewHeight;
import static com.example.raghavkishan.drawingboard.DrawCircleView.viewWidth;


public class Circles {

    float x;
    float y;
    float centerx;
    float centery;
    float deltaX = 1;

    public float getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(float deltaY) {
        this.deltaY = deltaY;
    }

    public float getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(float deltaX) {
        this.deltaX = deltaX;
    }

    float deltaY = 1;
    float topDistance,bottomDistance,leftDistance,rightDistance;

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    float velX,velY;


    public void setRadius(double radius) {
        this.radius = radius;
    }

    public  double radius;
    double currentRadius;
    Paint paint;

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }


    public float getCenterx() {
        return centerx;
    }

    public void setCenterx(float centerx) {
        this.centerx = centerx;
    }

    public float getCentery() {
        return centery;
    }

    public void setCentery(float centery) {
        this.centery = centery;
    }

    public float getRadius() {

        return (float)radius;
    }

    public void setRadius(){                                 //This is to make sure the radius does not cross the bounds of the screen.
        topDistance = centery - 0;
        bottomDistance = viewHeight - centery;
        leftDistance = centerx - 0;
        rightDistance = viewWidth - centerx;
        currentRadius = Math.sqrt(Math.pow((x-centerx), 2) + Math.pow((y-centery), 2));
        radius = Math.min(Math.min(Math.min(leftDistance,rightDistance),Math.min(topDistance,bottomDistance)),currentRadius);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
