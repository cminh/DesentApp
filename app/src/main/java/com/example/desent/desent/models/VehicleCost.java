package com.example.desent.desent.models;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.desent.desent.R;
import com.example.desent.desent.utils.TimeScale;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by magnust on 25.07.2017.
 */


public class VehicleCost {

    private static final String TAG = "VehicleCost";

    //input (required values)
    private int yrsOwnCar;
    private int carSize; // 0 = small, 1 = medium, 2 = large
    private int cost;
    private boolean newCar;
    private float distancePrYear;

    //default (yearly)

    private float interest = 0.04f;
    private int roadFee = 2820;
    private float fuelCostPrLitre = 14.00f;
    private float taxFactor = 0.76f;
    private float fuelCost = 0;
    private float tires = 0;
    private float insurance = 0;
    private float service = 0;
    private float wash = 0;
    private float repair = 0;
    private float fuelConsumptionPrKm;
    private float valueLossRateYr1=0.20f;
    private float valueLossRateYr2=0.14f;
    private float valueLossRateYr3=0.13f;
    private float valueLossRateYr4=0.12f;
    private float valueLossRateYr5=0.11f;
    private float valueLossRateYr6=0.10f;

    private float[] valueLossRateArray = {0f,0f,0f,0f,0f,0f};



    //Arrays dependent

    private int[] washArray = {600, 300, 0};
    private  float[] insuranceInit = {4000f,4900f,5500f};
    private  float[] serviceInit = {0f,50f,0f};
    private  float[] tireInit = {610f,100f,-650f};
    private  float[] repInit = {4000f,4900f,5500f};
    private float[] defaultFuelConsumptionPrKm = {0.06f, 0.07f, 0.08f};

    // TODO: I think this value should be used somewhere..
    private float[] defaultDistancePrYear = {8000f, 14000f, 20000f};


    private float[][] distFactor = {{12f, 21f, 30f},{10.32f, 18.06f, 25.8f},
            {9.098383838f,15.92222222f,22.7459596f},{8.229781818f, 14.40212121f, 20.57445455f},
            {7.638767677f, 13.36784625f, 19.09691919f},{7.271232323f, 12.72465925f, 18.17808081f}};
    private float[][] repFactor = {
            {0.08f,0.10f,0.12f},
            {0.08f,0.10f,0.12f},
            {0.12f,0.15f,0.18f},
            {0.12f,0.15f,0.18f},
            {0.16f,0.20f,0.24f},
            {0.20f,0.25f,0.30f},
            {0.24f,0.30f,0.36f},
            {0.32f,0.40f,0.48f},
            {0.48f,0.60f,0.72f},
            {0.56f,0.70f,0.84f},
            {0.68f,0.85f,1.02f},
            {0.68f,0.85f,1.02f},
            {0.68f,0.85f,1.02f},
            {0.64f,0.80f,0.96f}
    };

    //Variables
    private float valueLoss = 0;
    private float interestLoss = 0;
    Map<String, String> defaultValueMap = new HashMap<String, String>();
    private boolean preferenceChange = false;

    //info
    private float totCost;
    private float totCostWithoutFuel;
    private float totFuelCostPrKm;
    private float avgCostPrKm;
    private float avgCostPrDay;
    private float marginalCostPr1Km;
    private float marginalCostFuel;

    //Prefs
    private SharedPreferences prefs;
    private DatabaseHelper db;

    public VehicleCost(){
        getRequiredValues();

    }

    public VehicleCost (Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        db = new DatabaseHelper(context);
        if(prefs.getBoolean("pref_key_car_owner", true)){
            getRequiredValues();
        }else{
            setRequiredValues();
        }

    }

    private void setRequiredValues() {
        // input values
        this.newCar = true;
        this.cost = 300000;
        this.distancePrYear = 8000;
        this.yrsOwnCar = 3;
        this.carSize = 0;



        if(preferenceChange){
            calculateYearlyValues(true);
            displayDefaultValues();
        }else{
            if (prefs.getBoolean("pref_car_key_advanced_default", false)) {
                // Custom active - set default and get preferences
                calculateYearlyValues(false);

            }else{
                // Default active - set default based on car size
                calculateYearlyValues(true);
                displayDefaultValues();
            }
        }
    }

    private void getRequiredValues() {
        // input values
        this.newCar = prefs.getBoolean("pref_key_car_owner", true);
        this.cost = Integer.parseInt(prefs.getString("pref_key_car_price", "300000"));
        this.distancePrYear = Integer.parseInt(prefs.getString("pref_key_car_distance","8000")); //change later
        this.yrsOwnCar = Integer.parseInt(prefs.getString("pref_key_car_ownership_period", "3"));

        switch (prefs.getString("pref_key_car_size", "0")){
            case "Small":
                this.carSize = 0;
                break;
            case "Medium":
                this.carSize = 1;
                break;
            case "Large":
                this.carSize = 2;
                break;
            default:
                this.carSize = 0;
        }


        if(preferenceChange){
            calculateYearlyValues(true);
            displayDefaultValues();
        }else{
            if (prefs.getBoolean("pref_car_key_advanced_default", false)) {
                // Custom active - set default and get preferences
                calculateYearlyValues(false);

            }else{
                // Default active - set default based on car size
                calculateYearlyValues(true);
                displayDefaultValues();
            }
        }
    }

    private void displayDefaultValues() {


        String pref_car_key_advanced_fuel_consumption = "" + (defaultFuelConsumptionPrKm[carSize] *100);
        String pref_car_key_advanced_fuel_price = "" + fuelCostPrLitre;
        String pref_car_key_advanced_Interest_cost = "0.04";
        String pref_car_key_advanced_yearly_fee = "2820";
        String pref_car_key_advanced_insurance_cost = "" + insurance;
        String pref_car_key_advanced_tire_cost = "" + tires;
        String pref_car_key_advanced_wash_accessories_cost = "" + wash;
        String pref_car_key_advanced_service_cost = "" + service;
        String pref_car_key_advanced_repair_cost = "" + repair;
        String pref_car_key_advanced_depreciation_yr1 = "0.20";
        String pref_car_key_advanced_depreciation_yr2 = "0.14";
        String pref_car_key_advanced_depreciation_yr3 = "0.13";
        String pref_car_key_advanced_depreciation_yr4 = "0.12";
        String pref_car_key_advanced_depreciation_yr5 = "0.11";
        String pref_car_key_advanced_depreciation_yr6 = "0.10";






        defaultValueMap.put("pref_car_key_advanced_fuel_consumption" , pref_car_key_advanced_fuel_consumption);
        defaultValueMap.put("pref_car_key_advanced_fuel_price",pref_car_key_advanced_fuel_price);
        defaultValueMap.put("pref_car_key_advanced_Interest_cost",pref_car_key_advanced_Interest_cost);
        defaultValueMap.put("pref_car_key_advanced_yearly_fee",pref_car_key_advanced_yearly_fee);
        defaultValueMap.put("pref_car_key_advanced_insurance_cost",pref_car_key_advanced_insurance_cost);
        defaultValueMap.put("pref_car_key_advanced_tire_cost",pref_car_key_advanced_tire_cost);
        defaultValueMap.put("pref_car_key_advanced_wash_accessories_cost",pref_car_key_advanced_wash_accessories_cost);
        defaultValueMap.put("pref_car_key_advanced_service_cost",pref_car_key_advanced_service_cost);
        defaultValueMap.put("pref_car_key_advanced_repair_cost",pref_car_key_advanced_repair_cost);
        defaultValueMap.put("pref_car_key_advanced_depreciation_yr1",pref_car_key_advanced_depreciation_yr1);
        defaultValueMap.put("pref_car_key_advanced_depreciation_yr2",pref_car_key_advanced_depreciation_yr2);
        defaultValueMap.put("pref_car_key_advanced_depreciation_yr3",pref_car_key_advanced_depreciation_yr3);
        defaultValueMap.put("pref_car_key_advanced_depreciation_yr4",pref_car_key_advanced_depreciation_yr4);
        defaultValueMap.put("pref_car_key_advanced_depreciation_yr5",pref_car_key_advanced_depreciation_yr5);
        defaultValueMap.put("pref_car_key_advanced_depreciation_yr6",pref_car_key_advanced_depreciation_yr6);
    }

    private void calculateYearlyValues(boolean bol) {


        calcCosts(bol);
    }

    private void setPostCalculationCustomValues() {
        if(prefs.contains("pref_car_key_advanced_fuel_consumption")){
            fuelConsumptionPrKm = Float.parseFloat(prefs.getString("pref_car_key_advanced_fuel_consumption","0f"));
            fuelConsumptionPrKm = fuelConsumptionPrKm/100f;
        }
        if(prefs.contains("pref_car_key_advanced_repair_cost")){
            repair = Float.parseFloat(prefs.getString("pref_car_key_advanced_repair_cost","0f"));
        }
        if(prefs.contains("pref_car_key_advanced_service_cost")){
            service = Float.parseFloat(prefs.getString("pref_car_key_advanced_service_cost","0f"));
        }
        if(prefs.contains("pref_car_key_advanced_wash_accessories_cost")){
            wash =Float.parseFloat(prefs.getString("pref_car_key_advanced_wash_accessories_cost","0f"));
        }
        if(prefs.contains("pref_car_key_advanced_tire_cost")){
            tires = Float.parseFloat(prefs.getString("pref_car_key_advanced_tire_cost","0f"));
        }
        if(prefs.contains("pref_car_key_advanced_insurance_cost")){
            insurance = Float.parseFloat(prefs.getString("pref_car_key_advanced_insurance_cost","0f"));
        }
        if(prefs.contains("pref_car_key_advanced_yearly_fee")){
            roadFee = Integer.parseInt(prefs.getString("pref_car_key_advanced_yearly_fee","0f"));
        }
        if(prefs.contains("pref_car_key_advanced_fuel_price")){
            fuelCostPrLitre = Float.parseFloat(prefs.getString("pref_car_key_advanced_fuel_price","0f"));
        }
    }

    private void setInitialCustomValues() {
        if(prefs.contains("pref_car_key_advanced_Interest_cost")){
//            interest = Float.parseFloat(prefs.getString("pref_car_key_advanced_Interest_cost","0.04f"));
        }
        if(prefs.contains("pref_car_key_advanced_depreciation_yr1")){
            valueLossRateYr1 = Float.parseFloat(prefs.getString("pref_car_key_advanced_depreciation_yr1","0.20f"));
        }
        if(prefs.contains("pref_car_key_advanced_depreciation_yr2")){
            valueLossRateYr2 = Float.parseFloat(prefs.getString("pref_car_key_advanced_depreciation_yr2","0.14f"));
        }
        if(prefs.contains("pref_car_key_advanced_depreciation_yr3")){
            valueLossRateYr3 = Float.parseFloat(prefs.getString("pref_car_key_advanced_depreciation_yr3","0.13f"));
        }
        if(prefs.contains("pref_car_key_advanced_depreciation_yr4")){
            valueLossRateYr4 = Float.parseFloat(prefs.getString("pref_car_key_advanced_depreciation_yr4","0.12f"));
        }
        if(prefs.contains("pref_car_key_advanced_depreciation_yr5")){
            valueLossRateYr5 = Float.parseFloat(prefs.getString("pref_car_key_advanced_depreciation_yr5","0.11f"));
        }
        if(prefs.contains("pref_car_key_advanced_depreciation_yr6")){
            valueLossRateYr6 = Float.parseFloat(prefs.getString("pref_car_key_advanced_depreciation_yr6","0.10f"));
        }
    }

    public  Map<String, String> getDefaultValueMap(boolean preferenceChange){
        this.preferenceChange = preferenceChange;
        return defaultValueMap;
    }
    protected float getAvgCurrentCarCost(){
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        float hourlyCost = (avgCostPrDay/24)*hour;
        return hourlyCost;
    }
    protected float getTotCost(){

        return totCost;
    }
    protected float getCost(TimeScale timeScale){

        switch (timeScale){
            case TODAY:
                return getCostToday();
            case WEEK:
                return getAvgCostPrDay();
            case MONTH:
                return getAvgCostPrDay();
            default:
                return 0f;
        }
    }

    protected float getAvgCostPrKm(){
        return avgCostPrKm;
    }
    protected float getMarginalCostPr1Km(){return marginalCostPr1Km; }

    protected float getMarginalCostFuel() {return marginalCostFuel;}

    private float getValueLoss(){return valueLoss;}

    private float getInterestLoss(){return interestLoss;}


    private float getAvgCostPrDay(){
        return avgCostPrDay;
    }

    private float getCostToday(){
        float costToday;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        float dist = db.getDrivingDistanceToday();
        costToday = ((totCostWithoutFuel/(24*365*yrsOwnCar))*hour)+ ((dist/1000f)*totFuelCostPrKm);

        String log = "costPrHour: " + totCostWithoutFuel/(24*365*yrsOwnCar) + "\ncostToday: " + costToday;
        Log.i(TAG, log);

        return costToday;
    }

    private void calcCosts(boolean onlyDefaultValues){


        if(newCar){
            //newCarCalc
            valueLossRateArray[0] = valueLossRateYr1 ;
            valueLossRateArray[1] = valueLossRateYr2;
            valueLossRateArray[2] = valueLossRateYr3;
            valueLossRateArray[3] = valueLossRateYr4;
            valueLossRateArray[4] = valueLossRateYr5;
            valueLossRateArray[5] = valueLossRateYr6;

            fuelConsumptionPrKm = defaultFuelConsumptionPrKm[carSize];

            if(onlyDefaultValues){

            }else{
                setInitialCustomValues();
            }

            calc();

            fuelCost = distancePrYear * fuelConsumptionPrKm * fuelCostPrLitre;
            insurance = calcInsurance();
            tires = calcTires(distancePrYear);
            wash = calcWash(distancePrYear);
            service = calcService(distancePrYear);
            repair = calcRepCost(distancePrYear, yrsOwnCar)/yrsOwnCar; // to get yearly value like the rest

            if(onlyDefaultValues){

            }else{
                setPostCalculationCustomValues();
            }
            totCost = ((fuelCost  + insurance + roadFee +
                    tires + wash + service + repair) * yrsOwnCar) + valueLoss + interestLoss;

            totCostWithoutFuel = ((insurance + roadFee +
                    tires + wash + service + repair) * yrsOwnCar) + valueLoss + interestLoss;

            totFuelCostPrKm = fuelConsumptionPrKm * fuelCostPrLitre;
            avgCostPrKm = totCost/(distancePrYear*yrsOwnCar);
            avgCostPrDay = totCost/(365*yrsOwnCar);
            calcMarginalCosts();

            String stringLog = "Total cost: " + totCost +"\nvalueLoss = " + valueLoss + "\ninterestCost =  "+ interestLoss +
                    "\nfuelCost = " + fuelCost +
                    "\ntotRoadFee = " + roadFee +
                    "\ntotInsurance = " + insurance +
                    "\ntotTireCost = " + tires +
                    "\ntotWash = " + wash +
                    "\ntotService = " + service +
                    "\ntotRepair = " + repair;
            Log.i(TAG, stringLog);

        }else{
            //oldCarCalc

        }

    }

    private float calcService(float dist){
        float service;
        switch (carSize){
            case 0:
                service = (serviceInit[0]+0.25f*dist);
                return service;
            case 1:
                service = (serviceInit[1]+0.175f*dist);
                return service;
            case 2:
                service = (serviceInit[2]+0.15f*dist);
                return service;
            default:
                return 0;
        }
    }

    private float calcWash(float dist){
        float wash;
        wash = (washArray[carSize] + (4f/80f)*dist);
        return wash;
    }

    private float calcInsurance(){
        float insurance;
        switch (carSize){
            case 0:
                insurance = (insuranceInit[carSize]+0.125f*distancePrYear);
                return insurance;
            case 1:
                insurance = (insuranceInit[carSize]+0.150f*distancePrYear);
                return insurance;
            case 2:
                insurance = (insuranceInit[carSize]+0.175f*distancePrYear);
                return insurance;
            default:
                return 0;
        }
    }

    private float calcTires(float dist){
        float tires;
        switch (carSize){
            case 0:
                tires = (tireInit[0] + 0.08f*dist);
                return tires;
            case 1:
                tires = (tireInit[1]+0.1f*dist);
                return tires;
            case 2:
                tires = (tireInit[2] + 0.12f*dist);
                return tires;
            default:
                return 0;
        }
    }

    private float calcRepCost(float dist, int yr){
        float repCost = 0;
        float accumDist = dist;

        for (int i=0; i<yr;i++){
            if(accumDist <= 20000f){
                repCost += repFactor[0][carSize]*dist;
            }else if(accumDist <= 30000f){
                repCost += repFactor[1][carSize]*dist;
            }else if(accumDist <= 40000f){
                repCost += repFactor[2][carSize]*dist;
            }else if(accumDist <= 50000f){
                repCost += repFactor[3][carSize]*dist;
            }else if(accumDist <= 60000f){
                repCost += repFactor[4][carSize]*dist;
            }else if(accumDist <= 70000f){
                repCost += repFactor[5][carSize]*dist;
            }else if(accumDist <= 80000f){
                repCost += repFactor[6][carSize]*dist;
            }else if(accumDist <= 90000f){
                repCost += repFactor[7][carSize]*dist;
            }else if(accumDist <= 100000f){
                repCost += repFactor[8][carSize]*dist;
            }else if(accumDist <= 110000f){
                repCost += repFactor[9][carSize]*dist;
            }else if(accumDist <= 120000f){
                repCost += repFactor[10][carSize]*dist;
            }else if(accumDist <= 130000f){
                repCost += repFactor[11][carSize]*dist;
            }else if(accumDist <= 140000f){
                repCost += repFactor[12][carSize]*dist;
            }else{
                repCost += repFactor[13][carSize]*dist;
            }
            String debug = "Distance acc.: " + accumDist + "\nrepCost: "+ repCost;
            Log.i(TAG,debug);
            accumDist += distancePrYear;

        }
        return repCost;
    }

    private float getServicePrKm(){
        switch (carSize){
            case 0:
                return 0.25f;
            case 1:
                return 0.175f;
            case 2:
                return 0.15f;
            default:
                return 0.25f;
        }
    }

    private float getTiresPrKm(){
        switch (carSize){
            case 0:
                return 0.08f;
            case 1:
                return 0.1f;
            case 2:
                return 0.12f;
            default:
                return 0.08f;
        }
    }
    private float getInsurancePrKm(){

        switch (carSize){
            case 0:
                return 0.125f;
            case 1:
                return 0.150f;
            case 2:
                return 0.175f;
            default:
                return 0.125f;
        }
    }

    private float getWashPrKm(){

        return (4f/80f);
    }

    private void calcMarginalCosts() {
        float marginalCostFuel = fuelConsumptionPrKm * fuelCostPrLitre;
        float marginalCostPr1Km = calcRepCost(1,1) + getServicePrKm() + getWashPrKm() +
                getTiresPrKm() + getInsurancePrKm() +
                marginalCostFuel;

        switch (carSize){
            case 0:
                marginalCostPr1Km += 0.01f*(cost/20000f);
                break;
            case 1:
                marginalCostPr1Km += 0.01f*(cost/20000f);
                break;
            case 2:
                marginalCostPr1Km += 0.01f*(cost/20000f);
                break;
            default:
                marginalCostPr1Km += 0.01f*(cost/20000f);
                break;
        }
        this.marginalCostPr1Km = marginalCostPr1Km;
        this.marginalCostFuel = marginalCostFuel;
    }


    private void calc(){

        // Variables related to normal value loss (no km correction)
        float normalValueLoss = 0;
        float totNormalValueLoss = 0;
        float normalCurrentValue = cost;

        // Variables related to km correction
        float correctedLossDiff =0;
        float totCorrectedLossDiff = 0;
        float correctedCurrentValue = cost;

        // Losses related to interest
        float totInterestLoss = 0;
        float interestLoss = 0;

        // initiating default values


        for(int i = 0; i < yrsOwnCar; i++){

            correctedLossDiff = (getDistanceFactor(i)*((distancePrYear/distancePrYear)-1f)*100f);
            totCorrectedLossDiff += correctedLossDiff;

            String distfac= "factor: " + getDistanceFactor(i) + "\ndiff: " + correctedLossDiff;
            Log.i(TAG, distfac);
            if(i>=5) {

                normalValueLoss = normalCurrentValue * valueLossRateArray[5];
                totNormalValueLoss += normalValueLoss;

            }else{
                normalValueLoss = normalCurrentValue * valueLossRateArray[i];
                totNormalValueLoss += normalValueLoss;
            }


            interestLoss = taxFactor*(correctedCurrentValue)*interest*
                    (1f-(((normalValueLoss+correctedLossDiff)/(correctedCurrentValue))/2f));
            totInterestLoss += interestLoss;

            normalCurrentValue = cost - totNormalValueLoss;
            correctedCurrentValue = cost - totNormalValueLoss - totCorrectedLossDiff;

            //----------------------------------------------------------------------
            //Only for debugging
            String log = ""+totNormalValueLoss;
            String log2 = ""+normalCurrentValue;
            String log3 = ""+totCorrectedLossDiff;
            Log.i(TAG,"totNormalValueLoss: "+log + "\nnormalCurrentValue: " + log2 +
                    "\ntotCorrectedLossDiff:" + log3);
            //-----------------------------------------------------------------------
        }

        valueLoss = totNormalValueLoss + totCorrectedLossDiff;
        this.interestLoss = totInterestLoss;
    }

    private float getDistanceFactor(int yr){
        if(yr >= 5){
            yr = 5;
        }
        return distFactor[yr][carSize];
    }
}
