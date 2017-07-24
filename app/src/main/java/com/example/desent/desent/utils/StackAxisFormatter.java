package com.example.desent.desent.utils;

/**
 * Created by magnust on 14.06.2017.
 */


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class StackAxisFormatter {


    public List<Float> horizontal_width_list = new ArrayList<>();
    float ver_ratio, hor_ratio, horstart, graphheight, width,border, next_horizontal_width, horizontal_width, graphwidth, height;
    float colwidth, maxY_values;
    int label_size, size;
    Canvas canvas, yAxisCanvas;
    Paint paint, textPaint;
    Float[] values;
    List<String> hori_labels;
    String description;
    private List<String> colorList = new ArrayList<>();
    private  int legendTop,legendLeft, legendRight, legendBottom;
    private RectF legends;
    private final static String LOGG = "StackAxisformatter";
    //  private Yaxis yaxis;

    private boolean percentage_stacked = false;

    // Plot XY Lables
    public void PlotXYLabels(float graphheight,float width,
                             float graphwidth, float height,
                             List<String> hori_labels, float maxY_values, Canvas canvas,
                             float horstart, float border,  List<Float> horizontal_width_list,
                             float horizontal_width, Paint paint, Float[] values,
                             float maxX_values, String description, boolean stacked){

        this.graphheight = graphheight;
        this.width = width;
        this.graphwidth = graphwidth;
        this.height = height;
        this.hori_labels = hori_labels;
        this.canvas = canvas;
        this.horstart = horstart;
        this.border = border;
        this.horizontal_width_list = horizontal_width_list;
        this.horizontal_width = horizontal_width;
        this.paint = paint;
        this.values = values;
        this.description = description;
        this.percentage_stacked = stacked;
        this.maxY_values = maxY_values;


        init();

    }

    protected void init(){
        paint.setTextAlign(Paint.Align.LEFT);
        size = values.length;

        if(hori_labels != null) {
            size = hori_labels.size();
        }

        label_size = size - 1;
        ver_ratio =  maxY_values/label_size;  // Vertical label ratio
        paint.setColor(Color.BLACK);

        if(!percentage_stacked) {
            for (int i = 0; i < size; i++) {
                paint.setTextSize(30); //18
                createY_axis(i);
            }
        }
        else{
            paint.setTextSize(30);//18
            createY_percentage();
        }

        if(hori_labels != null) {
            size = hori_labels.size();

        }

        if(values[0] != null){

            for(int j =0; j< size+1 ; j++){

                createX_axis(j);
            }
        }


        if(description !=null){
            Description();
        }


        paint.setTextSize(30); //18

    }


    protected void createY_percentage(){

        for(int i = 0; i < 5; i++){

            float ver_height = ((graphheight / 4) * i) + border;

            if(i == 4){
                canvas.drawLine(horstart, ver_height, width - (border), ver_height, paint); // Draw vertical line
            }
            else {
                canvas.drawLine(horstart, ver_height , border, ver_height, paint); // Draw vertical line
            }

            paint.setColor(Color.BLACK);

            int y_labels = (4-i) * 25;
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(""+y_labels, horstart-10, ver_height - 10, paint);
            paint.setTextAlign(Paint.Align.LEFT);
        }
    }


    protected void createY_axis(int i){

        float ver_height = ((graphheight / label_size) * i) + border;

        if(i== values.length-1){
            // Drawing the x-axis
            canvas.drawLine(0, ver_height, width - (border), ver_height, paint); // Draw vertical line

        }
        else {
            // Drawing in y-axis view
            // canvas.drawLine(horstart, ver_height , border, ver_height, paint); // Draw vertical line
        }

        paint.setColor(Color.BLACK);
        //int Y_labels =  (int) size - 1- i;

        //String y_labels = String.format("%.1f", Y_labels*ver_ratio);
        //paint.setTextAlign(Paint.Align.RIGHT);
        // Drawing in y-axis view
        //canvas.drawText(y_labels, horstart-10, ver_height - 10, paint);
        //paint.setTextAlign(Paint.Align.LEFT);
    }

    protected void createX_axis(int i){


        horizontal_width = ((graphwidth / size) * i) + horstart;
        next_horizontal_width = ((graphwidth / size) * (i+1)) + horstart;

        horizontal_width_list.add(horizontal_width);
        // canvas.drawLine(horstart, graphheight + border, horstart, border, paint);
        if(i==0){
            // Drawing in y-axis view
            // canvas.drawLine(horizontal_width, graphheight +border, horizontal_width, border, paint);

        } else{
            // vertical pegs on x-axis
            canvas.drawLine(horizontal_width, graphheight + border, horizontal_width, graphheight + 2 * border, paint);
        }

        DrawLabelsString(i);

    }

    protected void DrawLabelsString(int i){
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);

        if(i >1){

            colwidth = horizontal_width_list.get(1) -  horizontal_width_list.get(0);
            // all except first peg-label on x-axis
            float rightX = horizontal_width;// - (colwidth - 5);
            float centerX = rightX - (next_horizontal_width - rightX)/2;
            canvas.drawText(hori_labels.get(i-1),centerX , height - 38, paint);

        } else if(i !=0 && i==1){
            // first label
            canvas.drawText(hori_labels.get(i-1),  horizontal_width - ((next_horizontal_width-horizontal_width)/2), height - 38, paint);
        }
        paint.setTextAlign(Paint.Align.RIGHT);
    }
    protected void Description(){

        paint.setTextSize(28);
        float text_width = paint.measureText(description, 0, description.length());

        this.canvas.drawText(description, graphwidth - text_width, height + 50, paint);


    }


}

