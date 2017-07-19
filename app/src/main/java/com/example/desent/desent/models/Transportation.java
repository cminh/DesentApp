package com.example.desent.desent.models;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by magnust on 07.07.2017.
 */

public class Transportation {

    private final static float DIESEL_EMISSIONS = 2.640f; // kgCO2e/L
    private final static float GASOLINE_EMISSIONS = 2.392f; // kgCO2e/L

    private Context context;
    private DatabaseHelper db;
    private float drivingDistanceToday;
    private float kgCo2FromDriving;
    private float consumptionPr10km;
    private float co2Today;
    private float co2Last24;
    private float co2Week;
    private float co2Month;
    private  boolean combustionEngine = true;
    SharedPreferences prefs;


    public Transportation(Context context){
        this.context = context;
        db = new DatabaseHelper(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public float getCo2Today(){
        return co2Today;
    }

    private float getDrivingDistanceToday(){

        drivingDistanceToday = db.getDrivingDistanceToday();

        return drivingDistanceToday;
    }

    private void isCarOwner(){
       if(prefs.getBoolean("pref_key_car_owner", true)){
           co2Today = calculateKgCo2FromDriving(getDrivingDistanceToday(), getEmissionsPrLitre(), getLitrePer10km());
           co2Last24 = 0f;
           co2Week = 0f;
           co2Month = 0f;
        }else{
           co2Today = 0f;
           co2Last24 = 0f;
           co2Week = 0f;
           co2Month = 0f;
       }
    }

    private float getEmissionsPrLitre(){
        String fuelType = prefs.getString("pref_key_car_fuel", "Gasoline");
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

    private float calculateKgCo2FromDriving(float drivingDistance, float emission, float consumption){
        float kgCo2FromDriving = drivingDistance * emission * consumption;

        return kgCo2FromDriving;
    }

}
