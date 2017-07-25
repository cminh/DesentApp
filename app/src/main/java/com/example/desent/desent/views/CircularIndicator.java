package com.example.desent.desent.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import com.example.desent.desent.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by celine on 10/03/17.
 */
public class CircularIndicator extends Chart {

    protected float[] values;

    //Rectangles
    protected RectF mCircleBounds;

    //Sizes
    protected int mBackgroundThicknessFactor = 30;
    protected int mProgressThicknessFactor = 25;


    //Angles
    protected int startAngle;
    protected int sweepAngle;

    //Paints
    private Paint mProgressPaint = new Paint();

    //Text
    protected float maxValue;
    protected float totalValue;

    public void setMaxValue(float maxValue) { this.maxValue = maxValue; }

    public float getMaxValue() {return this.maxValue; }

    public void setColor(int color) {
        this.colors = new ArrayList<Integer>();
        colors.add(color);
    }

    public void setColors(List<Integer> colors) {this.colors = colors; }

    public List<Integer> getColors() {return this.colors; }

    public void setValues(float[] values) {
        this.values = values;
        updateValues();
    }

    public float[] getValues(){return this.values; }

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    public int getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(int sweepAngle) {
        this.sweepAngle = sweepAngle;
    }

    public CircularIndicator(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    protected int valueToAngle(Float value){
        return (int) ((value < 0) ? 0 : ((totalValue<maxValue) ? (sweepAngle * value) / maxValue : (sweepAngle * value) /totalValue));
    }

    public void updateTotalValue(){
        totalValue = 0;
        for (int i=0; i<values.length; i++){
            totalValue = totalValue + values[i];
        }
    }

    protected void drawValues(Canvas canvas){

        float tempStartAngle = startAngle;
        float tempSweepAngle;

        for (int i = 0; i < values.length; i++) {
            tempSweepAngle = valueToAngle(values[i]);
            mProgressPaint.setColor(colors.get(i));
            canvas.drawArc(mCircleBounds, tempStartAngle, tempSweepAngle, false, mProgressPaint);
            tempStartAngle = tempStartAngle + tempSweepAngle;
        }

    }

    @Override
    protected void updateValues() {
        this.updateTotalValue();
    }

    @Override
    protected void init(Context context, AttributeSet attrs){
    }

    protected void setUpBounds(Canvas canvas){

        //Bounds
        int x = this.getWidth();
        int y = this.getHeight();
        int r = (x<y) ? x : y;

        int backgroundThickness = r/mBackgroundThicknessFactor;
        int progressThickness = r/mProgressThicknessFactor;

        mCircleBounds = new RectF((x-r)/2 + progressThickness, (y-r)/2 + progressThickness, (x+r)/2 - progressThickness, (y+r)/2-progressThickness);

        //Paints

        mBackgroundPaint.setColor(mLightGrey);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Style.STROKE);
        mBackgroundPaint.setStrokeWidth(backgroundThickness);

        //mProgressPaint.setColor(colors.get(0));
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Style.STROKE);
        mProgressPaint.setStrokeWidth(progressThickness);

    }

    @Override
    protected void drawBackground(Canvas canvas) {
        canvas.drawArc(mCircleBounds, startAngle, sweepAngle, false, mBackgroundPaint);
    }


    @Override
    protected void drawCaption(Canvas canvas) {
    }

    @Override
    protected
    void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int x = getMeasuredWidth();
        int y = getMeasuredHeight();

        int r = (x<y) ? x : y;
        setMeasuredDimension(r,r);


    }
}
