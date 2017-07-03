package com.example.desent.desent.models;

import com.example.desent.desent.views.EstimationButton;

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

    protected InputStream inputStream;
    protected String name;
    protected ArrayList<String> columnNames;

    //Charts configs
    protected ArrayList<Integer> colors;
    protected int limitColor;

    protected float maxValue;
    protected float limitValue;
    protected String unit;
    protected int decimalsNumber;

    protected Energy energy;

    //TODO: remove later
    protected Date date;

    //TODO: adapt to real data
    protected float[] dailyValues = new float[2];
    protected ArrayList<ArrayList<Float>> weeklyValues = new ArrayList<ArrayList<Float>>();
    protected ArrayList<ArrayList<Float>> monthlyValues = new ArrayList<ArrayList<Float>>();
    //protected ArrayList<ArrayList<Integer>> yearlyValues = new ArrayList<ArrayList<Integer>>();


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

    public InputStream getInputStream() {

        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void calculateTodaysEnergyValue() {}

    public void estimateTodaysValueWithSolarPanel(int pvSystemSize) {}

    public void calculateTodaysTransportationValue() {
        estimateDailyValues(columnNames.get(0), 0);
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

    public void readValues() {
        readTodaysValues();
        readWeeklyValues();
        readMonthlyValues();
    }

    public void estimateDailyValues(EstimationButton estimationButton) {
        estimateDailyValues(estimationButton.getName(), estimationButton.getCategoryIndex());
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
                dailyValues[categoryIndex] = Float.parseFloat(raw.get(columnIndex));
            }

            else if ((categoryIndex < columnNames.size()) && (raw.contains(columnNames.get(categoryIndex)))){
                columnIndex = raw.indexOf(columnNames.get(categoryIndex));

                while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
                }
                raw = new ArrayList<>(Arrays.asList(line.split(",")));
                dailyValues[categoryIndex] = Float.parseFloat(raw.get(columnIndex)); //TODO: throw exception
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

    public void readTodaysValues(){
        calculateTodaysEnergyValue();
        estimateDailyValues(columnNames.get(0), 0);

    }

    /**public void readTodaysValues(){
        dailyValues.clear();
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
                dailyValues.add(Float.parseFloat(raw.get(columnIndex)));
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

    public void estimateWeeklyValues(EstimationButton estimationButton){
        estimateWeeklyValues(estimationButton.getName(), estimationButton.getCategoryIndex());
    }

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

    public void estimateMonthlyValues(EstimationButton estimationButton) {
        estimateMonthlyValues(estimationButton.getName(), estimationButton.getCategoryIndex());
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

    public void setDailyValues (float[] dailyValues){
        this.dailyValues = dailyValues;
    }

    public float[] getDailyValues(){
        return this.dailyValues;
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
        for (float dailyValue : dailyValues){
            sum = sum + dailyValue;
        }
        return sum;
    }

}
