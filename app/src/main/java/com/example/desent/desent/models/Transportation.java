package com.example.desent.desent.models;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.desent.desent.utils.TimeScale;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by magnust on 07.07.2017.
 */

public class Transportation {

    private final static float DIESEL_EMISSIONS = 2.640f; // kgCO2e/L
    private final static float GASOLINE_EMISSIONS = 2.392f; // kgCO2e/L

    private Context context;
    private DatabaseHelper db;
    private float drivingDistanceToday;
    private float drivingDistanceWeek;
    private float drivingDistanceMonth;
    private float kgCo2FromDriving;
    private float consumptionPr10km;
    private float co2Today;
    private float co2Week;
    private float co2Month;
    private  boolean combustionEngine = true;
    SharedPreferences prefs;


    public Transportation(Context context){
        this.context = context;
        db = new DatabaseHelper(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public DatabaseHelper getDb() {
        return db;
    }

    public void setDb(DatabaseHelper db) {
        this.db = db;
    }

    public float getCo2(TimeScale timeScale) {
        float co2 = 0;

        switch(timeScale){
            case TODAY:
                co2 = co2Today;
                break;
            case WEEK:
                co2 = co2Week;
                break;
            case MONTH:
                co2 = co2Month;
                break;
        }

        return co2;
    }

    public float getCo2Today(){
        return co2Today;
    }

    public float getCo2Week() {
        return co2Week;
    }

    public void setCo2Week(float co2Week) {
        this.co2Week = co2Week;
    }

    public float getCo2Month() {
        return co2Month;
    }

    public void setCo2Month(float co2Month) {
        this.co2Month = co2Month;
    }

    public float getWalkingDistance(TimeScale timeScale) {
        float distance = 0;

        switch (timeScale){
            case TODAY:
                distance = db.getWalkingDistanceToday();
                break;
            case WEEK:
                distance = db.getWeekAverageWalkingDistance();
                break;
            case MONTH:
                distance = db.getMonthAverageWalkingDistance();
                break;
        }

        return distance;
    }

    public float getCyclingDistance(TimeScale timeScale) {
        float distance = 0;

        switch (timeScale){
            case TODAY:
                distance = db.getCyclingDistanceToday();
                break;
            case WEEK:
                distance = db.getWeekAverageCyclingDistance();
                break;
            case MONTH:
                distance = db.getMonthAverageCyclingDistance();
                break;
        }

        return distance;
    }

    public float getDrivingDistance(TimeScale timeScale) {
        float distance = 0;

        switch (timeScale){
            case TODAY:
                distance = db.getDrivingDistanceToday();
                break;
            case WEEK:
                distance = db.getWeekAverageDrivingDistance();
                break;
            case MONTH:
                distance = db.getMonthAverageDrivingDistance();
                break;
        }

        return distance;
    }

    public float getWalkingDistanceToday(){

        return db.getWalkingDistanceToday();
    }

    public float getCyclingDistanceToday(){

        return db.getCyclingDistanceToday();
    }

    public float getDrivingDistanceToday(){

        drivingDistanceToday = db.getDrivingDistanceToday();

        return drivingDistanceToday;
    }

    private void isCarOwner(){
       if(prefs.getBoolean("pref_key_car_owner", true)){
           drivingDistanceToday = db.getDrivingDistanceToday();
           drivingDistanceWeek = db.getWeekAverageDrivingDistance();
           drivingDistanceMonth = db.getMonthAverageDrivingDistance();

           co2Today = calculateKgCo2FromDriving(drivingDistanceToday, getEmissionsPrLitre(), getLitrePer10km());
           co2Week = calculateKgCo2FromDriving(drivingDistanceWeek, getEmissionsPrLitre(), getLitrePer10km());
           co2Month = calculateKgCo2FromDriving(drivingDistanceMonth, getEmissionsPrLitre(), getLitrePer10km());
        }else{
           co2Today = 0f;
           co2Week = 0f;
           co2Month = 0f;
       }
    }

    public float[] getWeekCarbonFootprint() {

        float[] weekDistance = db.getWeekDrivingDistance();
        int length = weekDistance.length;

        float[] weekCarbonFootprint = new float[length];
        for (int i = 0; i<length; i++){
            weekCarbonFootprint[i] = calculateKgCo2FromDriving(weekDistance[i]);
        }

        return weekCarbonFootprint;

    }

    public float[] getMonthCarbonFootprint() {

        float[] monthDistance = db.getWeekDrivingDistance();
        int length = monthDistance.length;

        float[] monthCarbonFootprint = new float[length];

        for (int i = 0; i<length; i++){
            monthCarbonFootprint[i] = calculateKgCo2FromDriving(monthCarbonFootprint[i]);
        }

        return monthCarbonFootprint;

    }

    private float getEmissionsPrLitre(){
        return getEmissionsPrLitre(prefs.getString("pref_key_car_fuel", "Gasoline"));
    }

    public float getEmissionsPrLitre(String fuelType){
        float emissionsPrLitre;
        switch (fuelType){
            case "Gasoline":
                emissionsPrLitre = GASOLINE_EMISSIONS;
                return  emissionsPrLitre;
            case "Diesel":
                emissionsPrLitre = DIESEL_EMISSIONS;
                return  emissionsPrLitre;
            case "Electricity":
                emissionsPrLitre = 0f;
                return  emissionsPrLitre;
            default:
                return 0f;
        }
    }

    private float getLitrePer10km(){
        float fuelConsumption = Float.parseFloat(prefs.getString("pref_key_car_fuel_consumption", "0"));
        if (fuelConsumption>0){
            return fuelConsumption;
        }else{
            return 0f;
        }
    }

    protected float calculateKgCo2FromDriving(){
        return ((prefs.getBoolean("pref_key_car_owner", true) ? calculateKgCo2FromDriving(getDrivingDistanceToday(), getEmissionsPrLitre(), getLitrePer10km()) : 0));
    }

    protected float calculateKgCo2FromDriving(float drivingDistance){
        return ((prefs.getBoolean("pref_key_car_owner", true) ? calculateKgCo2FromDriving(drivingDistance, getEmissionsPrLitre(), getLitrePer10km()) : 0));
    }

    protected float calculateKgCo2FromDriving(float drivingDistance, float emission){
        return ((prefs.getBoolean("pref_key_car_owner", true) ? calculateKgCo2FromDriving(drivingDistance, emission, getLitrePer10km()) : 0));
    }

    private float calculateKgCo2FromDriving(float drivingDistance, float emission, float consumption){
        float kgCo2FromDriving = drivingDistance * emission * consumption;

        return kgCo2FromDriving;
    }

}
