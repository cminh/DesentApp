package com.example.desent.desent.models;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.desent.desent.utils.TimeScale;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 * Created by celine on 29/06/17.
 */

public class Energy {

    private static final String TAG = "Energy";
    private DatabaseHelper db;

    public double heatingConstant = 0.015;
    public double CO2Factor = 0.137; //  kgCO2/kWh of electricity
    public double electricityPrice = 0.95;

    private double Irradiance = 0.6;
    private double PVoutput;

    // Network tariff: the effekttariff applies to the average of the highest three peaks of the net load
    //public double PeakLoad = float(mean(sorted(self.load)[len(self.load) - 10:]));
    // EFFEKT_LEDD = [0, 930]       # kr/kW
    // ENERGI_TARIFF = [0.44, 0.05]  # kr/kWh
    public double EnergyTariff = 0.44;
    public float EffektLedd;

    // TODO save original load, prices and pv hourly profiles to SQLite database
    // TODO retrieve original profiles from SQLite database
    // TODO save results to SQLite

    public Energy(Context context) {
        db = new DatabaseHelper(context);
        PVoutput = Irradiance *0.86; //TODO: calculate somewhere else
    }

    public double calculateDailyAverage(double value, int start, int stop) {
        return ((stop > 24 + start) ? value / (1 + (stop-start)/24) : value);
    }

    //TODO: check
    public int[] indexesFromTimeScale(TimeScale timeScale) {

        int[] indexes = new int[]{0,0};
        Calendar calendar = Calendar.getInstance();

        switch (timeScale) {
            case TODAY:
                indexes[0] = (calendar.get(Calendar.DAY_OF_YEAR)-1) * 24;
                indexes[1] = indexes[0] + calendar.get(Calendar.HOUR_OF_DAY);
                break;
            case LAST_24_HOURS:
                indexes[0] = (calendar.get(Calendar.DAY_OF_YEAR)-2) * 24 + calendar.get(Calendar.HOUR_OF_DAY);
                indexes[1] = indexes[0] + 24;
                break;
            case WEEK:
                indexes[0] = ((calendar.get(Calendar.WEEK_OF_YEAR)-1) * 7 - 1) * 24;
                indexes[1] = indexes[0] + (calendar.get(Calendar.DAY_OF_WEEK)) * 24 + calendar.get(Calendar.HOUR_OF_DAY);
                break;
            case MONTH:
                indexes[1] = (calendar.get(Calendar.DAY_OF_YEAR)-1)*24 + calendar.get(Calendar.HOUR_OF_DAY);
                indexes[0] = indexes[1] - (calendar.get(Calendar.DAY_OF_MONTH)-1)*24 - calendar.get(Calendar.HOUR_OF_DAY);
                break;
        }

        return indexes;
    }

    public double calculateElectricityCost(TimeScale timeScale) {
        double electricityCost = 0;
        int[] indexes = indexesFromTimeScale(timeScale);

        for(int i = indexes[0]; i<=indexes[1]; i++) {
            electricityCost += (db.getHeat(i) > -100) ? db.getHeat(i) * electricityPrice : 0;
        }
        // TODO make it a synchronous task - hourly or when user reopens the app
        return calculateDailyAverage(electricityCost, indexes[0], indexes[1]);
    }

    public double calculateElectricityCost(TimeScale timeScale, float pvSystemSize) {
        double electricityCost = 0;
        int[] indexes = indexesFromTimeScale(timeScale);

        for(int i = indexes[0]; i<=indexes[1]; i++) {
            electricityCost += (db.getHeat(i) > -100) ? ( db.getHeat(i) - PVoutput * pvSystemSize ) * electricityPrice : 0;
        }
        // TODO make it a synchronous task - hourly or when user reopens the app
        return calculateDailyAverage(electricityCost, indexes[0], indexes[1]);

    }

    public double calculateCO2FromElectricity(TimeScale timeScale) {
        double co2 = 0;
        int[] indexes = indexesFromTimeScale(timeScale);

        for(int i = indexes[0]; i<=indexes[1]; i++) {
            co2 += (db.getHeat(i) > -100) ?  db.getHeat(i) * CO2Factor : 0;
        }

        co2 = calculateDailyAverage(co2, indexes[0], indexes[1]);
        // TODO make it a synchronous task - hourly or when user reopens the app
        return co2;
    }

    public double calculateCO2FromElectricity(TimeScale timeScale, float pvSystemSize) {
        double co2 = 0;
        int[] indexes = indexesFromTimeScale(timeScale);

        for(int i = indexes[0]; i<=indexes[1]; i++) {
            co2 += (db.getHeat(i) > -100) ? ( db.getHeat(i) - PVoutput * pvSystemSize ) * CO2Factor : 0;
        }
        // TODO make it a synchronous task - hourly or when user reopens the app
        return calculateDailyAverage(co2, indexes[0], indexes[1]);
    }

    //TODO: check
    public double calculateEnergyConsumption(TimeScale timeScale) {
        double load = 0;
        int[] indexes = indexesFromTimeScale(timeScale);

        for(int i = indexes[0]; i<=indexes[1]; i++) {
            load += (db.getHeat(i) > -100) ? db.getHeat(i) : 0;
        }
        // TODO make it a synchronous task - hourly or when user reopens the app
        return calculateDailyAverage(load, indexes[0], indexes[1]);

    }

    //TODO: check
    public double calculateEnergyConsumption(TimeScale timeScale, float pvSystemSize) {
        double load = 0;
        int[] indexes = indexesFromTimeScale(timeScale);

        for (int i=indexes[0]; i<=indexes[1]; i++)
            load += (db.getHeat(i) > -100) ? db.getHeat(i) - PVoutput * pvSystemSize : 0;

        // TODO make it a synchronous task - hourly or when user reopens the app
        return calculateDailyAverage(load, indexes[0], indexes[1]);

    }

    //TODO: check
    public float[] generateArrayWeekEnergyConsumption() {
        float[] weekEnergyConsumption = new float[7];
        Calendar calendar = Calendar.getInstance();

        int start = (calendar.get(Calendar.DAY_OF_YEAR)-7) * 24;
        int stop = start +24*6 + calendar.get(Calendar.HOUR_OF_DAY);

        for (int i = start; i <= stop; i++){
            int j = (i-start) / 24;
            weekEnergyConsumption[j] += (db.getHeat(i) > -100) ? db.getHeat(i) : 0;
        }

        return weekEnergyConsumption;
    }
    public float[] generateArrayWeekCarbonFootprint() {

        float[] weekEnergyConsumption = generateArrayWeekEnergyConsumption();

        int length = weekEnergyConsumption.length;
        float[] weekCarbonFootprint = new float[length];

        for (int i = 0; i < length; i++){
            weekCarbonFootprint[i] = ((float) CO2Factor)*weekEnergyConsumption[i];
        }

        return weekCarbonFootprint;
    }
}
