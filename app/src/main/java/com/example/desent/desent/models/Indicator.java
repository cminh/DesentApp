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
 * Created by celine on 24/04/17.
 */
public class Indicator {

    protected TimeScale timeScale;
    protected EstimationType estimationType;
    protected int pvSystemSize;
    protected float walkingDistance;
    protected float cyclingDistance;
    protected float drivingDistance;

    protected InputStream inputStream;
    protected String name;
    protected String explanation = "";
    protected ArrayList<String> columnNames;

    //Charts configs
    protected ArrayList<Integer> colors;
    protected int limitColor;

    protected float maxValue;
    protected float limitValue;
    protected String unit;
    protected int decimalsNumber;

    protected Energy energy;
    protected Transportation transport;

    //TODO: remove later
    protected Date date;

    //TODO: adapt to real data
    protected float[] averageValues = new float[]{0,0};
    protected ArrayList<ArrayList<Float>> weeklyValues = new ArrayList<ArrayList<Float>>();
    protected ArrayList<ArrayList<Float>> monthlyValues = new ArrayList<ArrayList<Float>>();
    //protected ArrayList<ArrayList<Integer>> yearlyValues = new ArrayList<ArrayList<Integer>>();


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

    public int getPvSystemSize() {
        return pvSystemSize;
    }

    public void setPvSystemSize(int pvSystemSize) {
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

    public int getLimitColor() {
        return limitColor;
    }

    public void setLimitColor(int limitColor) {
        this.limitColor = limitColor;
    }

    public int getDecimalsNumber() {
        return decimalsNumber;
    }

    public void setDecimalsNumber(int decimalsNumber) {
        this.decimalsNumber = decimalsNumber;
    }


    public Indicator(InputStream inputStream, String name, String unit, ArrayList<String> columnNames){
        this.inputStream = inputStream;
        this.name = name;
        this.unit = unit;
        this.columnNames = columnNames;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = dateFormat.parse("2017-04-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Indicator(InputStream inputStream, String name, String unit, String columnName){
        this.inputStream = inputStream;
        this.name = name;
        this.unit = unit;
        this.columnNames = new ArrayList<>();
        columnNames.add(columnName);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = dateFormat.parse("2017-04-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setColor(int color) {
        this.colors = new ArrayList<>();
        this.colors.add(color);
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

    public void setColors(ArrayList<Integer> colors) {this.colors = colors;}

    public ArrayList<Integer> getColors() {
        return colors;
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

    public void calculateValues() {

        //TODO: temporary, will be removed later
        switch(estimationType) {
            case NONE:
                estimateDailyValues("Tranportation", 0);
                estimateDailyValues("Housing", 1);
                break;
            case SOLAR_INSTALLATION:
                estimateDailyValues("Tranportation", 0);
                estimateDailyValues("Solar panel", 1);
                break;
            case WALKING:
                estimateDailyValues("Walking", 0);
                estimateDailyValues("Housing", 1);
                break;
            case CYCLING:
                estimateDailyValues("Cycling", 0);
                estimateDailyValues("Housing", 1);
                break;
            case ELECTRIC_CAR:
                estimateDailyValues("Electric car", 0);
                estimateDailyValues("Housing", 1);
                break;
        }

    }

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

    public float[] calculateWeekAverage() {
        return calculateAverage(weeklyValues);
    }

    public float[] calculateMonthAverage() {
        return calculateAverage(monthlyValues);
    }

    public void estimateValues(String columnName, int categoryIndex) {
        estimateDailyValues(columnName, categoryIndex);
        estimateWeeklyValues(columnName, categoryIndex);
        estimateMonthlyValues(columnName, categoryIndex);
    }

    public void estimateDailyValues(String columnName, int categoryIndex) {
        int columnIndex;
        int value = -1;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //TODO: move, maybe

        try{

            String line;
            while((!((line = reader.readLine()).startsWith(name))) || (line == null)){
            }
            line = reader.readLine();;
            ArrayList<String> raw = new ArrayList<>(Arrays.asList(line.split(",")));

            if (raw.contains(columnName)) {
                columnIndex = raw.indexOf(columnName);

                while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
                }
                raw = new ArrayList<>(Arrays.asList(line.split(",")));
                averageValues[categoryIndex] = 80f; // Float.parseFloat(raw.get(columnIndex));
                //This is where the database value can be entered
            }

            else if ((categoryIndex < columnNames.size()) && (raw.contains(columnNames.get(categoryIndex)))){
                columnIndex = raw.indexOf(columnNames.get(categoryIndex));

                while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
                }
                raw = new ArrayList<>(Arrays.asList(line.split(",")));
                averageValues[categoryIndex] = Float.parseFloat(raw.get(columnIndex)); //TODO: throw exception
                //or change here
            }


        } catch (IOException e) {
            e.printStackTrace(); //TODO: see errors
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**public void readTodaysValues(){
        averageValues.clear();
        ArrayList<Integer> columnIndexes = new ArrayList<>();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //TODO: move, maybe

        try{

            String line;
            while((!((line = reader.readLine()).startsWith(name))) || (line == null)){
            }
            line = reader.readLine();;
            ArrayList<String> raw = new ArrayList<>(Arrays.asList(line.split(",")));

            for (String columnName: columnNames) {
                if (raw.contains(columnName))
                    columnIndexes.add(raw.indexOf(columnName));
            }
            while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
            }
            raw = new ArrayList<>(Arrays.asList(line.split(",")));

            for (Integer columnIndex: columnIndexes) {
                averageValues.add(Float.parseFloat(raw.get(columnIndex)));
            }

        } catch (IOException e) {
            e.printStackTrace(); //TODO: see errors
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }**/

    public void estimateWeeklyValues(String columnName, int categoryIndex){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //TODO: move, maybe
        int columnIndex;
        try{
            String line;
            while((!((line = reader.readLine()).startsWith(name))) || (line == null)){
            }
            line = reader.readLine();
            ArrayList<String> raw = new ArrayList<>(Arrays.asList(line.split(",")));

            if (raw.contains(columnName)) {
                columnIndex = raw.indexOf(columnName);

                while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
                }
                for (int i=0; i<7; i++){ //TODO: prevent missing data
                    raw = new ArrayList<>(Arrays.asList(line.split(",")));
                    weeklyValues.get(categoryIndex).set(i, Float.parseFloat(raw.get(columnIndex)));
                    line = reader.readLine();
                }
            } else if ((categoryIndex < columnNames.size()) && (raw.contains(columnNames.get(categoryIndex)))) {
                columnIndex = raw.indexOf(columnNames.get(categoryIndex));

                while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
                }
                for (int i=0; i<7; i++){ //TODO: prevent missing data
                    raw = new ArrayList<>(Arrays.asList(line.split(",")));
                    weeklyValues.get(categoryIndex).set(i, Float.parseFloat(raw.get(columnIndex)));
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void readWeeklyValues() {
        weeklyValues.clear();
        ArrayList<Integer> columnIndexes = new ArrayList<Integer>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //TODO: move, maybe
        try{
            String line;
            while((!((line = reader.readLine()).startsWith(name))) || (line == null)){
            }
            line = reader.readLine();
            ArrayList<String> raw = new ArrayList<String>(Arrays.asList(line.split(",")));

            for (String columnName: columnNames) {
                if (raw.contains(columnName)) {
                    columnIndexes.add(raw.indexOf(columnName));
                    weeklyValues.add(new ArrayList<Float>());
                }
            }
            while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
            }
            for (int i=0; i<7; i++){ //TODO: prevent missing data
                raw = new ArrayList<String>(Arrays.asList(line.split(",")));
                for (int j=0; j < columnIndexes.size(); j++)
                    weeklyValues.get(j).add(Float.parseFloat(raw.get(columnIndexes.get(j))));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void estimateMonthlyValues(String columnName, int categoryIndex){
        int columnIndex;
        int i = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        try{
            String line;
            while((!((line = reader.readLine()).startsWith(name))) || (line == null)){
            }
            line = reader.readLine();
            ArrayList<String> raw = new ArrayList<String>(Arrays.asList(line.split(",")));

            if (raw.contains(columnName)) {
                columnIndex = raw.indexOf(columnName);

                while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
                }
                while((line.startsWith(dateFormat.format(date))) && (line != null)) {
                    raw = new ArrayList<String>(Arrays.asList(line.split(",")));
                    monthlyValues.get(categoryIndex).set(i, Float.parseFloat(raw.get(columnIndex)));//TODO: improve, errors
                    line = reader.readLine();
                    i++;
                }
            } else if ((categoryIndex < columnNames.size()) && (raw.contains(columnNames.get(categoryIndex)))) {
                columnIndex = raw.indexOf(columnNames.get(categoryIndex));

                while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
                }
                while((line.startsWith(dateFormat.format(date))) && (line != null)) {
                    raw = new ArrayList<>(Arrays.asList(line.split(",")));
                    monthlyValues.get(categoryIndex).set(i, Float.parseFloat(raw.get(columnIndex)));//TODO: improve, errors
                    line = reader.readLine();
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void readMonthlyValues(){
        monthlyValues.clear();
        ArrayList<Integer> columnIndexes = new ArrayList<Integer>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        try{
            String line;
            while((!((line = reader.readLine()).startsWith(name))) || (line == null)){
            }
            line = reader.readLine();
            ArrayList<String> raw = new ArrayList<String>(Arrays.asList(line.split(",")));

            for (String columnName: columnNames) {
                if (raw.contains(columnName)) {
                    columnIndexes.add(raw.indexOf(columnName));
                    monthlyValues.add(new ArrayList<Float>());
                }
            }
            while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
            }
            while((line.startsWith(dateFormat.format(date))) && (line != null)) {
                raw = new ArrayList<>(Arrays.asList(line.split(",")));
                for (int j = 0; j < columnIndexes.size(); j++)
                    monthlyValues.get(j).add(Float.parseFloat(raw.get(columnIndexes.get(j))));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAverageValues(float[] averageValues){
        this.averageValues = averageValues;
    }

    public float[] getAverageValues(){
        return this.averageValues;
    }

    public void setWeeklyValues (ArrayList<ArrayList<Float>> weeklyValues){
        this.weeklyValues = weeklyValues;
    }

    public ArrayList<ArrayList<Float>> getWeeklyValues(){
        return this.weeklyValues;
    }

    public void setMonthlyValues (ArrayList<ArrayList<Float>> monthlyValues){
        this.monthlyValues = monthlyValues;
    }

    public ArrayList<ArrayList<Float>> getMonthlyValues (){
        return this.monthlyValues;
    }

    public float getDailyValue() {
        float sum = 0;
        for (float dailyValue : averageValues){
            sum = sum + dailyValue;
        }
        return sum;
    }

}
