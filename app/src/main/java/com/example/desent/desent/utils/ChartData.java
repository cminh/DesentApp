package com.example.desent.desent.utils;

/**
 * Created by magnust on 14.06.2017.
 */


import android.app.Application;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Region;
import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class ChartData extends Application implements Serializable {

    private static final long serialVersionUID = 1L;

    private Float y_values, x_values, size, left, top, right, bottom, data, mValue, highest_value;
    private Float lowest_value,opening, closing;
    private final Path mPath = new Path();
    private final Region mRegion = new Region();
    private String coordinate, pieLabel, sectorLabel,chartName, labels, pyramidLabel, legends, rows, column;;
    List<ChartData> list;

    private int pyramid_value, heat_value, x_value, y_value;
    private String Issum, trendlineText;

    // Magnus mod.
    private int barColor = 0;
    private final static String LOGG = "ChartData";

    public static final String LineChart = "LineChart";
    public static final String BarChart = "BarChart";
    public static final String AreaChart = "AreaChart";
    public static final String SplineChart = "SplineChart";
    public static final String issum = "issum";

    private JSONObject radarData;
    private Float[] y_list;

    public ChartData(JSONObject data){
        this.radarData = data;
    }

    public ChartData(){
    }

    public ChartData(Float y_values, Float x_values){
        this.y_values = y_values;
        this.x_values = x_values;
    }


    public ChartData(Float y_values, Float x_values, Float size){
        this.y_values = y_values;
        this.x_values = x_values;
        this.size = size;
    }

    protected ChartData(Float left, Float top, Float right, Float bottom){
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    protected ChartData(Float y_axis, Float x_axis, Float size, String coordinate){
        this.y_values = y_axis;
        this.x_values = x_axis;
        this.size = size;
        this.coordinate = coordinate;
    }

    public ChartData(String label, Float val){
        this.data = val;
        this.pieLabel = label;
    }

    public ChartData(Float y_axis, Float x_axis, String coordinate){
        this.y_values = y_axis;
        this.x_values = x_axis;
        this.coordinate = coordinate;

    }

    public ChartData(List<ChartData> list){
        this.list = list;
    }

    public ChartData(Float y_axis, String x_axis){
        this.y_values = y_axis;
        this.labels = x_axis;
    }

    public ChartData(Float x_values, Float highest_value, Float lowest_value, Float opening, Float closing){
        this.x_values = x_values;
        this.highest_value = highest_value;
        this.lowest_value = lowest_value;
        this.opening = opening;
        this.closing = closing;
    }

    public ChartData(List<ChartData> list, String chartName){
        this.list = list;
        this.chartName = chartName;
    }

    public ChartData(Float val){

        this.data = val;
    }

    public ChartData(String label, int value){
        this.pyramidLabel = label;
        this.pyramid_value = value;
    }

    public ChartData(Float[] y_axis, String legends){
        this.legends = legends;
        this.y_list = y_axis;
    }

    // Magnus mod.
    public ChartData(Float[] y_axis, String legends, String barColor ){
        this.legends = legends;
        this.y_list = y_axis;

        try {
            this.barColor = Color.parseColor(barColor);
        } catch (Exception e) {
            Log.e(LOGG, "The color is not written correct!");
        }

    }


    public ChartData(String labels){
        this.labels = labels;
    }

    public ChartData(Float y_axis, Float x_axis, String trendlineText1, String trendlineText){
        this.y_values = y_axis;
        this.x_values = x_axis;
        this.coordinate = trendlineText1;
        this.trendlineText = trendlineText;

    }

    public ChartData(String row, String column, int value){
        this.rows = row;
        this.column = column;
        this.heat_value = value;
    }

    public ChartData(int y , int x){
        this.y_value = y;
        this.x_value = x;
    }

    public int getXValue(){
        return  this.x_value;
    }

    public int getYValue(){
        return this.y_value;
    }

    public String getRows(){
        return rows;
    }

    public String getColumn(){
        return column;
    }

    public int getHeat_value(){
        return heat_value;
    }

    public String getPyramidLabel(){
        return pyramidLabel;
    }

    public void setPyramidLabel(String label){
        this.pyramidLabel = label;
    }

    public int getPyramid_value(){
        return pyramid_value;
    }

    public void setPyramid_value(int value){
        this.pyramid_value = value;
    }

    public Float getY_values(){return y_values;}

    public Float getX_values(){ return x_values;}

    // Magnus mod.
    public int getBarColor(){

        return  barColor;
    }

    public void setY_values(Float y_values){ this.y_values = y_values;}

    public  void setX_values(Float x_values){ this.x_values = x_values;}

    public  void setSize(Float size){ this.size = size;}

    public Float getSize(){ return  size; }

    public  void setLeft(Float left){ this.left = left;}

    public Float getLeft(){ return  left; }

    public  void setTop(Float top){ this.top = top;}

    public Float getTop(){ return  top; }

    public  void setRight(Float right){ this.right = right;}

    public Float getRight(){ return  right; }

    public  void setBottom(Float bottom){ this.bottom = bottom;}

    public Float getBottom(){ return  bottom; }

    public  void setValue(Float data){ this.data = data;}

    public String getPieLabel(){
        return this.pieLabel;
    }

    public Float getValue(){ return  this.data; }

    public  void setCoordinate(String coordinate){ this.coordinate = coordinate;}

    public String getCoordinate(){ return  this.coordinate; }

    public Path getPath() {
        return mPath;
    }

    public Region getRegion() {
        return mRegion;
    }

    public float getSectorValue() {
        return mValue;
    }

    public void setSectorValue(float value) {
        mValue = value;
    }

    public JSONObject getRadarData(){
        return this.radarData;
    }

    public void setHighest_value(float highest_value){ this.highest_value = highest_value;}

    public Float getHighest_value(){ return highest_value;}

    public void setLowest_value(float lowest_value){ this.lowest_value = lowest_value;}

    public Float getLowest_value(){ return lowest_value;}

    public void setOpening(float opening){ this.opening = opening; }

    public Float getOpening(){ return opening;}

    public void setClosing(Float closing){ this.closing = closing;}

    public Float getClosing(){ return closing;}

    public void setList(List<ChartData> list){ this.list = list;}

    public List<ChartData> getList(){ return  list; }

    public String getSectorLabel() {
        return sectorLabel;
    }

    public void setSectorLabel(String value) {
        sectorLabel = value;
    }

    public void setChartName(String chartName){ this.chartName = chartName;}

    public String getChartName(){return  chartName; }

    public String getLabels(){
        if(labels == null){
            labels = coordinate;
        }
        return  labels;
    }

    public void setLabels(String labels){ this.labels = labels;}

    public void setY_List(Float [] y_list){ this.y_list = y_list;}

    public Float[] getY_List(){ return  y_list;}

    public void  setLegends(String legends){ this.legends = legends;}

    public String getLegends(){return  legends;}


    public ChartData(String issum, String labels){
        this.Issum = issum;
        this.labels = labels;
    }

    public void setIssum(String issum){ this.Issum = issum;}

    public String Issum(){ return  Issum; }

    public void setTrendlineText(String trendlineText){ this.trendlineText = trendlineText;}

    public String getTrendlineText(){ return  trendlineText;}
}
