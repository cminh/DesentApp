package com.example.desent.desent.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by magnust on 15.06.2017.
 */

public class StackedBarLabel extends View {

    private final static String LOGG = "StackedBarLabel";
    private Canvas canvas;
    private int parentWidth, parentHeight;
    private float left, top, right, bottom;
    private float textHeight = 40;
    private List<String> colorLabels = new ArrayList<>();
    private List<String> labels = new ArrayList<>();

    public StackedBarLabel(Context context, AttributeSet attributeSet){
        super(context, attributeSet);


    }

    public void clear(){
        colorLabels.clear();
        labels.clear();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    protected void onDraw(Canvas canvas) {
        Log.i(LOGG, "onDraw");
        initializeValue(canvas);
        drawColorLabels();

    }

    private void initializeValue(Canvas canvas){


        this.canvas = canvas;
    }

    public void addColorLabels(String color){
        colorLabels.add(color);
    }

    public void addLabelText(String labelText){
        labels.add(labelText);
    }

    private void drawColorLabels(){

        if(!colorLabels.isEmpty()){

            int numLab = colorLabels.size();
            int xBetween = parentWidth/(numLab);
            int xFromEdge = 0;//xBetween/2 - 80;

            int yBetween = parentHeight/(numLab);
            int yFromEdge = yBetween/2;

            int boxDim;


            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);

            Paint textPaint = new Paint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(textHeight);

            if(yBetween > 80){
                boxDim = 80;
            }else{
                boxDim = yBetween - 10;
            }
            // initial values
            left = xFromEdge ;
            right = xFromEdge + 80;
            int labelsLength = colorLabels.size();

            for (int i=0; i<labelsLength; i++) {

                paint.setColor(Color.parseColor(colorLabels.get(labelsLength-(i+1))));
                //this.paint = paint;

                if(i == 0){
                    top = yFromEdge - (boxDim/2);
                    bottom = yFromEdge + (boxDim/2);
                }else{
                    top += yBetween;
                    bottom += yBetween;
                }

                canvas.drawRect(left, top, right, bottom, paint);
                canvas.drawText(labels.get(labelsLength - (i+1)),right + 10, top+(boxDim/2)+(textHeight/2), textPaint);
            }

        }

    }

}

