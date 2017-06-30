package com.example.desent.desent.models;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 * Created by celine on 29/06/17.
 */

public class Energy {

    private static final String TAG = "Energy";
    // Monthly electricity consumption used to scale the hourly load according to user input
    private double[] monthlyEnergyValues = new double[12];
    // hourly electricity load profile
    private double[] load = new double[8760];
    //  kgCO2/kWh of electricity
    public double CO2Factor = 0.137;
    public double[] pv = new double[8760];
    public double[] prices = new double[8760];

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
        this.monthlyEnergyValues = readFromCSVSources(context, "monthlyenergyvalues.csv", monthlyEnergyValues, 1);
        /* Initialize hourly load, solar output and prices profiles */
        this.load = readFromCSVSources(context, "PricesLoadPV.csv", load, 1);
        this.pv = readFromCSVSources(context, "PricesLoadPV.csv", pv, 2);
        this.prices = readFromCSVSources(context, "PricesLoadPV.csv", prices, 3);
    }

    /* method to read the stored CSV files containing monthly and hourly values */
    public double[] readFromCSVSources(Context context, String fileName, double[] values, int column) {
        // this.context = context;
        try {
            AssetManager assetManager = context.getAssets();
            InputStream csvStream = assetManager.open(fileName);
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);

            // save header - actually not needed
            String[] header = csvReader.readNext();

            String[] line;
            int i = 0;
            while ((line = csvReader.readNext()) != null) {
                values[i] = Float.parseFloat(line[column]);
                i++;
            }

        } catch (Exception ee) {
            Log.e(TAG, "readFromCSVSources: " + ee.getMessage());
        }
        return values;
    }

    /* this method calibrates the yearly electricity consumption profile to the monthly value provided by the user */
    public void CalibrateElectricityConsumption(double monthlyConsumption, int month) {
        double calibrationFactor = monthlyConsumption / monthlyEnergyValues[month-1];
        for (int i=0; i<this.load.length; i++) {
            this.load[i] = load[i] * calibrationFactor;
        }

        // TODO calibration with more than one monthly consumption value
    }

    public double[] calculateLoadWithPV(int start, int stop, double pvSystemSize) {
        double[] loadWithPV = new double[pv.length];
        for (int i=start; i<stop; i++)
            loadWithPV[i] = this.pv[i] * pvSystemSize;
        return loadWithPV;
    }

    private double[] calculateHourlyElectricityCost(int start, int stop) {

        double[] hourlyElectricityCost = new double[load.length]; //TODO: handle errors
        for(int i=start; i<stop; i++) {
            // first energy cost
            hourlyElectricityCost[i] = this.prices[i] * this.load[i] + this.EnergyTariff * this.load[i];
        }
        // TODO include demand charges in cost
        Log.d(TAG, "Calculated hourly electricity cost.");
        return hourlyElectricityCost;

    }

    public double[] calculateHourlyElectricityCost(int start, int stop, double pvSystemSize) {
        double[] hourlyElectricityCost = new double[load.length];
        for(int i = start; i<stop; i++) //TODO: handle errors
            hourlyElectricityCost[i] = (this.load[i] - this.pv[i] * pvSystemSize) * (this.prices[i] + this.EnergyTariff);
        // TODO include demand charges in cost
        Log.d(TAG, "Calculated hourly electricity with PV.");
        return hourlyElectricityCost;

    }

    public double[] calculateHourlyCO2(int start, int stop) {
        double[] hourlyCO2 = new double[load.length];
        for(int i=start; i<stop; i++) {
            hourlyCO2[i] = this.load[i] * this.CO2Factor;
        }
        Log.d(TAG, "Calculated hourly electricity related CO2.");
        return hourlyCO2;

    }

    public double[] calculateHourlyCO2(int start, int stop, double pvSystemSize) {
        double [] hourlyCO2 = new double[load.length];
        for(int i=start; i<stop; i++) {
            hourlyCO2[i] = (this.load[i] - this.pv[i] * pvSystemSize) * this.prices[i];
        }
        Log.d(TAG, "Calculated hourly CO2 with PV.");
        return hourlyCO2;
    }

    public double calculateTodaysElectricityCost() {

        Calendar calendar = Calendar.getInstance();
        int start = (calendar.get(Calendar.DAY_OF_YEAR)-1) * 24;
        int stop = start + calendar.get(Calendar.HOUR_OF_DAY);
        double[] hourlyCost;
        double todaysCost = 0;

        hourlyCost = calculateHourlyElectricityCost(start, stop); //TODO: calculate directly in this method?

        for(int i = start; i<stop; i++) {
            todaysCost += hourlyCost[i];
        }
        // TODO make it a synchronous task - hourly or when user reopens the app
        return todaysCost;

    }

    public double calculateTodaysElectricityCost(int pvSystemSize) {

        Calendar calendar = Calendar.getInstance();
        int start = (calendar.get(Calendar.DAY_OF_YEAR)-1) * 24;
        int stop = start + calendar.get(Calendar.HOUR_OF_DAY);
        double[] hourlyCost;
        double todaysCost = 0;

        hourlyCost = calculateHourlyElectricityCost(start, stop, pvSystemSize); //TODO: calculate directly in this method?

        for(int i = start; i<stop; i++) {
            todaysCost += hourlyCost[i];
        }
        // TODO make it a synchronous task - hourly or when user reopens the app
        return todaysCost;

    }

    public double calculateTodaysCO2FromElectricity() {
        Calendar calendar = Calendar.getInstance();
        int start = (calendar.get(Calendar.DAY_OF_YEAR)-1) * 24;
        int stop = start + calendar.get(Calendar.HOUR_OF_DAY);
        double todaysLoad = 0;


        for(int i = start; i<stop; i++) {
            todaysLoad += load[i];
        }

        // TODO make it a synchronous task - hourly or when user reopens the app
        return CO2Factor*todaysLoad; // round to one decimal
    }

    public double calculateTodaysCO2FromElectricity(int pvSystemSize) {
        Calendar calendar = Calendar.getInstance();
        int start = (calendar.get(Calendar.DAY_OF_YEAR)-1) * 24;
        int stop = start + calendar.get(Calendar.HOUR_OF_DAY);
        double todaysCO2 = 0;
        double[] hourlyCO2;

        hourlyCO2 = calculateHourlyCO2(start, stop, pvSystemSize);

        for(int i = start; i<stop; i++) {
            todaysCO2 += hourlyCO2[i];
        }

        // TODO make it a synchronous task - hourly or when user reopens the app
        return todaysCO2; // round to one decimal
    }
}
