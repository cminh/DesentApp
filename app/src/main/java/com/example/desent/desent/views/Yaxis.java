package com.example.desent.desent.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.desent.desent.utils.ChartData;

import java.util.List;

/**
 * Created by magnust on 19.06.2017.
 */

public class Yaxis extends View {

    private View mView;
    private String LOGG = "Yaxis";
    private String str = "Nope";
    private Canvas canvas;
    private int parentHeight;
    private int parentWidth;
    private int border;
    private int horstart;
    private List<ChartData> value;
    private Paint paint = new Paint();
    private Float[] firstRow;
    private float maxYvalue;
    private float ver_ratio;

    public Yaxis(Context context, AttributeSet attributeSet){
        super(context, attributeSet);


        //mView = findViewById(R.id.y_axis);
    }

    protected void onDraw(Canvas canvas) {
        Log.i(LOGG, "onDraw");
        this.canvas = canvas;
        paint.setColor(Color.BLACK);
        paint.setTextSize(40f);


        drawYaxis();

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setBorder(int border){
        this.border = border;

    }

    public void setFirstValueSet(List<ChartData> value){
        this.value = value;
        this.maxYvalue = getMaxY_Values(value);
        this.firstRow = value.get(0).getY_List();
    }

    public void drawYaxis(){

        int graphheight = (parentHeight-60) - (3*border);
        int size = firstRow.length;
        int label_size = size -1;
        horstart = 2 * border;
        float ver_height =0;
        ver_ratio =  maxYvalue/label_size;

        for(int i=0; i<label_size; i++){
            ver_height = ((graphheight / label_size) * i) + border;
            canvas.drawLine(0, ver_height , parentWidth, ver_height, paint);

            int Y_labels =  (int) size - 1- i;
            String y_labels = String.format("%.1f", Y_labels*ver_ratio);
            paint.setTextAlign(Paint.Align.RIGHT);
            // Drawing in y-axis view
            canvas.drawText(y_labels, parentWidth - 10, ver_height - 10, paint);
            paint.setTextAlign(Paint.Align.LEFT);
        }
        ver_height = ((graphheight / label_size) * label_size) + border;
        canvas.drawLine(parentWidth-1, 0 , parentWidth-1, ver_height, paint);



    }

    public float getMaxY_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        float largest1 = 0;

        for (int i = 0; i < values.size(); i++) {

            for (int j = 0; j < values.get(i).getY_List().length; j++){
                if (values.get(i).getY_List()[j] > largest)
                    largest = values.get(i).getY_List()[j];
            }
            largest1 +=largest;
        }
        return largest1;
    }


}
