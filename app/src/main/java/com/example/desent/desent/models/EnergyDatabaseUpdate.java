package com.example.desent.desent.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by petera on 07.08.2017.
 */

public class EnergyDatabaseUpdate {

    private static final String TAG = "DatabaseManager";
    public String location = "Trondheim";


    private double clouds;
    private double temperature;
    private double currentHeat;
    private double currentCost;
    private double currentCO2;
    private double Irradiance = 0.6;
    private double PVoutput;

    public double heatingConstant = 0.015;
    public double CO2factor = 0.137;
    public double electricityPrice = 0.95;

    public double roomTemperature  = 22;

    private int currentHour;

    private WeatherHttpClient mWeatherHttpClient;
    private DatabaseHelper mDatabaseHelper;


    public EnergyDatabaseUpdate(Context context) {

        mDatabaseHelper = new DatabaseHelper(context);
        mWeatherHttpClient = new WeatherHttpClient();
        HomeTown homeTown = new HomeTown(context);
        location = homeTown.getWeatherLocation();

    }

    public void UpdateCurrentValues() {

        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        currentHour = (dayOfYear-1) * 24 + hourOfDay;

        Cursor cursor = mDatabaseHelper.getEnergyData();

        if(!CurrentValuesInDatabase(cursor)) {

            temperature = -100;
            if ( !CurrentWeather(location) ) {
                // If current weather unavailable use stored forecast.
            /*
            if ( !useForecast() ) {
                // If forecast unavailable consumption = 0.
                temperature = roomTemperature;
            }*/
            }


        } else {
            Log.i(TAG, "Values for current hour already in database");
        }
        prepareValues();
        writeCurrentToDatabase();
        // TODO update all missing values available in stored forecast
    }

    private boolean CurrentWeather(String location) {

        double[] cloudsAndTemp = mWeatherHttpClient.getCurrentWeather(location);
        clouds = cloudsAndTemp[0];
        temperature = cloudsAndTemp[1];
        // get irradiance from API
        Irradiance = 0.72;
        return temperature >= -100;

    }

    private void prepareValues() {

        currentHeat = (roomTemperature-temperature) * heatingConstant;
        currentCost = currentHeat * electricityPrice;
        currentCO2 = currentHeat * CO2factor;

        PVoutput = Irradiance *0.86; // factor 0.86 to poorly approximate output

    }

    private void writeCurrentToDatabase() {

        double[] row = new double[20];

        row[0] = currentHour;
        row[1] = electricityPrice;
        row[2] = 3;
        row[3] = 0.4;
        row[4] = currentHeat;
        row[5] = currentHeat;

        Log.d(TAG, Arrays.toString(row));

        mDatabaseHelper.insertEnergyData(row);

    }

    private boolean CurrentValuesInDatabase(Cursor cursor) {

        boolean inDatabase = false;

        if(cursor.getCount()>0) {

            cursor.moveToLast();

            try {

                double hour_of_last_entry = cursor.getDouble(cursor.getColumnIndex("hour_of_year"));
                inDatabase = (int) hour_of_last_entry == currentHour;
                Log.i(TAG, "Last hour: " + Double.toString(cursor.getDouble(cursor.getColumnIndex("hour_of_year"))) + " Current hour: "  + Integer.toString(currentHour));

            } finally {
                if (cursor != null && !cursor.isClosed())
                    cursor.close();
            }
        }

        return inDatabase;
    }
}
