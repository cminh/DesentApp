package com.example.desent.desent.views;

/**
 * Created by magnust on 14.06.2017.
 * Modified to handle several data sets
 */


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.desent.desent.utils.AxisFormatter;
import com.example.desent.desent.utils.ChartData;
import com.example.desent.desent.utils.StackAxisFormatter;
import com.example.desent.desent.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StackBarChart extends View {

    private static final String LOGG = "StackBarChart";
    private Paint paint, textPaint;
    private List<ChartData> values;
    private List<String> hori_labels;
    private List<Float> horizontal_width_list = new ArrayList<>();
    private String description;
    private float horizontal_width,  border = 60, horstart = border * 2;
    private int parentHeight ,parentWidth;
    private float lastheight;
    private int color_no = 0;;
    private Canvas canvas, yAxisCanvas;
    private List<ChartData> list_cordinate = new ArrayList<>();
    private float height ,width, maxY_values, maxX_values, min, graphheight, graphwidth;
    private float left, right, top, bottom, barheight1, barheight2,colwidth;
    private List<Integer> color_code_list = new ArrayList<>();
    JSONObject jsonObject;
    private  List<String> legends_list = new ArrayList<>();
    private  int legendTop,legendLeft, legendRight, legendBottom;
    private  RectF legends;
    private boolean percentage_stacked=false;

    // Magnus modifications:
    private float stackHeight;
    private int indent = 0;

    private int decimalsNumber;

    public int getDecimalsNumber() {
        return decimalsNumber;
    }

    public void setDecimalsNumber(int decimalsNumber) {
        this.decimalsNumber = decimalsNumber;
    }

    public StackBarChart(Context context, AttributeSet attributeSet){
        super(context, attributeSet);

        Paint paint = new Paint();
        this.paint = paint;
    }

    public void setData(List<ChartData> values){

        if(values != null)
            this.values = values;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public void setHorizontal_label(List<String> hori_labels){

        if (hori_labels != null)
            this.hori_labels = hori_labels;
    }

    public void setPercentageStacked(boolean perc){
        this.percentage_stacked = perc;
    }

    // Get the Width and Height defined in the activity xml file
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    protected void onDraw(Canvas canvas) {

        initializeValue(canvas);
        Log.i(LOGG, "onDraw");


        //Just for testing
        //yAxisCanvas.drawLine(3f,3f,3f,100f, paint);

        StackAxisFormatter axisFormatter = new StackAxisFormatter();
        axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, hori_labels, maxY_values, canvas,
                horstart, border, horizontal_width_list, horizontal_width, paint, values.get(0).getY_List(),
                maxX_values, description, percentage_stacked);

        // Her er testen




        colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);
        getBarheight();
        StoredCoordinate();

        if(!percentage_stacked)
            DrawText();

        //Auto generated legends are excluded.
        //setLegegendPoint(getLegends_list(), getColor_code_list());
    }

    private  void StoredCoordinate(){

        AxisFormatter axisFormatter = new AxisFormatter();
        list_cordinate.clear();


        for(int i =0;i<values.get(0).getY_List().length  ;i++){

            for(int j=0; j< values.size();j++){

                left = (i * colwidth) + horstart + indent;

                try {
                    String str = jsonObject.optString(i + "");
                    str = str.replace("[","").replace("]", "");

                    List<String> items = Arrays.asList(str.split(","));
                    Float barheight = Float.parseFloat(items.get(j));



                    lastheight = (border - barheight) + graphheight;
                    right = ((i * colwidth) + horstart) + (colwidth - 1) - indent;

                    if(j == 0){

                        top = lastheight;
                        bottom = graphheight + border;
                        // Magnus modification
                        stackHeight = barheight;

                    }
                    else {

                        top = top - barheight ;
                        //Magnus mod.
                        bottom = (graphheight - stackHeight) + border;
                        stackHeight += barheight;

                    }
                    int tempColor = values.get(j).getBarColor();



                    if(tempColor == 0){
                        paint.setColor(Color.parseColor(axisFormatter.getColorList().get(j)));
                    }else{

                        paint.setColor(tempColor);
                        //axisFormatter.setCustomColor(j, String.format("#%06X", (0xFFFFFF & tempColor)));
                    }


                    canvas.drawRect(left, top, right, bottom, paint);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            list_cordinate.add(new ChartData(left,top));
        }
    }

    private void getBarheight(){

        try {

            int size = values.get(0).getY_List().length;
            jsonObject = new JSONObject();

            for (int j = 0; j < size; j++) {

                List<Float> barheight_list1 = new ArrayList<>();

                if(percentage_stacked) {
                    barheight_list1 = percentage_height(barheight_list1, j);
                }
                else{
                    barheight_list1 = stacked_height(barheight_list1, j);
                }


                jsonObject.put(j + "", barheight_list1.toString());

            }
            Log.e("json", jsonObject.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private List<Float> percentage_height(List<Float> barHeightList, int num){

        float total = getPercentageTotal(num);

        Log.e("total val", ""+total);
        for (int i = 0; i < values.size(); i++) {

            float barheight1 = (graphheight/total) * values.get(i).getY_List()[num] ;
            barHeightList.add(barheight1);
        }

        return barHeightList;
    }


    private float getPercentageTotal( int num){

        float total = 0f;

        for (int i = 0; i < values.size(); i++) {

            Log.e("string i", i+"");

            total += values.get(i).getY_List()[num] ;
        }
        return total;
    }


    private List<Float> stacked_height(List<Float> barHeightList, int num){

        for (int i = 0; i < values.size(); i++) {

            Log.e("string i", i+"");

            float barheight1 = (graphheight/maxY_values) * values.get(i).getY_List()[num] ;
            barHeightList.add(barheight1);
        }
        return barHeightList;
    }


    private void DrawText() {

        Float total_number = 0f;

        for(int i=0; i< list_cordinate.size(); i++){

            total_number = 0f;
            for(int j=0; j< values.size();j++){
                total_number += values.get(j).getY_List()[i];
            }

            canvas.drawText(Utility.floatToStringNDecimals(total_number, decimalsNumber),
                    list_cordinate.get(i).getY_values() + border,
                    list_cordinate.get(i).getX_values() - 30, paint);
        }
    }

    private List<String> getLegends_list(){

        for (int i = 0; i < values.size(); i++) {
            Log.e("legends",values.get(i).getLegends()+"");
            legends_list.add(values.get(i).getLegends());
        }
        return legends_list;
    }

    private void initializeValue(Canvas canvas){

        height = parentHeight -60;
        width = parentWidth;

        maxY_values = getMaxY_Values(values);

        // min = axisFormatter.getMinValues(values);
        graphheight = height - (3 * border);
        graphwidth = width - (3 * border);
        this.canvas = canvas;
    }

    public List<Integer> getColor_code_list(){
        for (int i =0; i< values.size(); i++){
            color_code_list.add(i);
        }
        return color_code_list;
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

    public void setLegendPoint(List<String> legends_list, List<Integer>color_code_list){

        legendTop = (int) height - 10;
        legendLeft = (int) (width * 0.1);
        legendRight = (int) graphwidth;
        legendBottom = (int) height;

        legends = new RectF(legendLeft, legendTop, legendRight, legendBottom);
        Legends(legends_list, color_code_list);

    }

    public void setBarIndent(int indent){
        this.indent = indent;
    }

    private void Legends(List<String> legends_list, List<Integer>color_code_list){
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40f);

        int left = (int) (graphwidth * 0.1);
        for (int i = 0; i < legends_list.size(); i++){

            String label = legends_list.get(i);

            float text_width = textPaint.measureText(label, 0, label.length());

            int color = color_code_list.get(i);
            Log.e("colorcode",color+"");

            if (!((graphwidth - legendLeft) > (text_width + 60))) {

                legendTop -= 60;
                legendLeft = left;
            }

            addLegends(canvas, color, legendTop, legendLeft, legendRight, legendBottom, label);
            legendLeft += ((int)text_width + 60);
        }
    }

    private void addLegends(Canvas canvas, int color, int top, int left, int right, int bottom, String label){

        legends = new RectF(left, top, right, bottom);
        Log.e("lef", left + "");
        AxisFormatter axisFormatter = new AxisFormatter();
        Rect r = new Rect(left, top, left + 30, top + 30);
        paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color)));
        canvas.drawRect(r, paint);
        canvas.drawText(label, left + 40, top + 20, textPaint);
    }


}
