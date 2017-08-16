package com.example.desent.desent.models;

import com.example.desent.desent.R;
import com.example.desent.desent.utils.EstimationType;
import com.example.desent.desent.utils.TimeScale;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Abstract class that describes "calculated indicators" (expenses, carbon footprint, energy consumption, distance, and energy consumption).
 * Allows to change the time scale, the estimation type, and calculate the resulting values.
 * The calculated values are stored in an array that contains the part that comes from transportation (index 0) and energy (index 1).
 */
public abstract class Indicator {

    protected TimeScale timeScale = TimeScale.TODAY;
    protected EstimationType estimationType = EstimationType.NONE;
    protected float pvSystemSize;
    protected float walkingDistance;
    protected float cyclingDistance;
    protected float drivingDistance;

    protected String name;
    protected String explanation = "";

    protected float maxValue;
    protected float limitValue;
    protected String unit;
    protected int decimalsNumber;

    protected Energy energy;
    protected Transportation transport;
    protected VehicleCost vehicleCost;

    protected float[] averageValues = new float[]{0,0};

    public float getWalkingDistance() {
        return walkingDistance;
    }

    public void setWalkingDistance(float walkingDistance) {
        this.walkingDistance = walkingDistance;
    }

    public float getCyclingDistance() {
        return cyclingDistance;
    }

    public void setCyclingDistance(float cyclingDistance) {
        this.cyclingDistance = cyclingDistance;
    }

    public float getDrivingDistance() {
        return drivingDistance;
    }

    public void setDrivingDistance(float drivingDistance) {
        this.drivingDistance = drivingDistance;
    }

    public float getPvSystemSize() {
        return pvSystemSize;
    }

    public void setPvSystemSize(float pvSystemSize) {
        this.pvSystemSize = pvSystemSize;
    }

    public TimeScale getTimeScale() {
        return timeScale;
    }

    public void setTimeScale(TimeScale timeScale) {
        this.timeScale = timeScale;
    }

    public EstimationType getEstimationType() {
        return estimationType;
    }

    public void setEstimationType(EstimationType estimationType) {
        this.estimationType = estimationType;
    }

    public float getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(float limitValue) {
        this.limitValue = limitValue;
    }

    public int getDecimalsNumber() {
        return decimalsNumber;
    }

    public void setDecimalsNumber(int decimalsNumber) {
        this.decimalsNumber = decimalsNumber;
    }


    public Indicator(String name, String unit, String explanation){
        this.name = name;
        this.unit = unit;
        this.explanation = explanation;
    }

    public Indicator(String name, String unit, String explanation, Energy energy){
        this.name = name;
        this.unit = unit;
        this.explanation = explanation;
        this.energy = energy;
    }

    public Indicator(String name, String unit, String explanation, Transportation transport){
        this.name = name;
        this.unit = unit;
        this.explanation = explanation;
        this.transport = transport;
    }

    public Indicator(String name, String unit, String explanation, Energy energy, Transportation transport){
        this.name = name;
        this.unit = unit;
        this.explanation = explanation;
        this.energy = energy;
        this.transport = transport;
    }

    public Indicator(String name, String unit, String explanation, Energy energy, Transportation transport, VehicleCost vehicleCost){
        this.name = name;
        this.unit = unit;
        this.explanation = explanation;
        this.energy = energy;
        this.transport = transport;
        this.vehicleCost = vehicleCost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float calculateTotalAverageValue(){
        return averageValues[0] + averageValues[1];
    }

    public abstract void calculateValues();

    public float[] calculateAverage(ArrayList<ArrayList<Float>> values) {

        float[] weekAverage = new float[2];
        float temp;
        int n;

        for (int i=0; i<values.size(); i++){

            temp = 0;
            n = values.get(i).size();
            for (int j=0; j<n; j++){
                temp = temp + values.get(i).get(j);
            }

            weekAverage[i] = temp/n;
        }

        return weekAverage;
    }


    public float getSumValues() {
        return averageValues[0] + averageValues[1];
    }

    public void setAverageValues(float[] averageValues){
        this.averageValues = averageValues;
    }

    public float[] getAverageValues(){
        return this.averageValues;
    }

    public float getDailyValue() {
        float sum = 0;
        for (float dailyValue : averageValues){
            sum = sum + dailyValue;
        }
        return sum;
    }

}
