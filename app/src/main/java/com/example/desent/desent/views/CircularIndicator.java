package com.example.desent.desent.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.example.desent.desent.R;
import com.example.desent.desent.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * A circular progress bar that displays several values.
 * If no max value is specified, the circular indicators shows only each value's proportion (as a pie chart).
 */
public class CircularIndicator extends View {

    protected float[] values;

    protected List<Integer> colors = new ArrayList<>();
    protected final int mLightGrey = ContextCompat.getColor(getContext(), R.color.light_grey);

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
    protected Paint mBackgroundPaint = new Paint();

    //Text
    protected float maxValue;
    protected float totalValue;

    public void setMaxValue(float maxValue) { this.maxValue = maxValue; }

    public float getMaxValue() {return this.maxValue; }

    public void setColor(int color) {
        this.colors = new ArrayList<>();
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
        return (int) ((maxValue > 0) ? (sweepAngle*value)/maxValue : (sweepAngle * value) /totalValue);
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

    protected void updateValues() {
        this.updateTotalValue();
    }

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

    protected void drawBackground(Canvas canvas) {
        canvas.drawArc(mCircleBounds, startAngle, sweepAngle, false, mBackgroundPaint);
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


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setUpBounds(canvas);
        drawBackground(canvas);
        drawValues(canvas);
    }
}
