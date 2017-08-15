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
    private double[] cloudsForecast = new double[4*24];
    private double[] temperatureForecast = new double[4*24];
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

    public int getCurrentHour() {
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        return currentHour = (dayOfYear-1) * 24 + hourOfDay;
    }

    public void UpdateCurrentValues() {

        currentHour = getCurrentHour();

        Cursor cursor = mDatabaseHelper.getEnergyData();

        if(!CurrentValuesInDatabase(cursor)) {

            temperature = -100;
            if ( !mDatabaseHelper.checkIfEmpty(mDatabaseHelper.getWritableDatabase(), "FORECAST") ) { //!PullCurrentWeather(location)
                Cursor cursor_forecast = mDatabaseHelper.getForecastData();
                cursor_forecast.moveToPosition(ForecastValuesInDatabase());
                temperature = cursor_forecast.getDouble(cursor_forecast.getColumnIndex("TEMPERATURE_FORECAST"));
                clouds = cursor_forecast.getDouble(cursor_forecast.getColumnIndex("CLOUDS_FORECAST"));
            /*
            if ( !useForecast() ) {
                // If forecast unavailable consumption = 0.
                temperature = roomTemperature;
            }*/
            }
            prepareValues();
            writeCurrentToDatabase();
        } else {
            Log.i(TAG, "Values for current hour already in database");
        }

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

    private boolean PullCurrentWeather(String location) {

        double[] cloudsAndTemp = mWeatherHttpClient.getCurrentWeather(location);
        clouds = cloudsAndTemp[0]; temperature = cloudsAndTemp[1];
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

                double hour_of_last_entry = cursor.getDouble(cursor.getColumnIndex("HOUR_OF_YEAR"));
                inDatabase = (int) hour_of_last_entry == currentHour;
                Log.i(TAG, "Last hour: " + Double.toString(cursor.getDouble(cursor.getColumnIndex("HOUR_OF_YEAR"))) + " Current hour: "  + Integer.toString(currentHour));

            } finally {
                if (cursor != null && !cursor.isClosed())
                    cursor.close();
            }
        }

        return inDatabase;
    }

    /** ********* METHODS RELATED TO FORECAST ******** */

    public void printHours() {
        Cursor cursor = mDatabaseHelper.getForecastData();
        cursor.moveToFirst();
        Log.d(TAG, "cursor length v printHours: " + Integer.toString(cursor.getCount()));
        try {
            for (int i=0; i<cursor.getCount(); i++) {
                Log.d(TAG, "i v printHours: " + Integer.toString(i));
                Log.d(TAG, "HOURS IN DATABASE: " + Integer.toString((int) cursor.getDouble(cursor.getColumnIndex("HOUR_OF_YEAR"))));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
    }

    public void UpdateForecast() {

        currentHour = getCurrentHour();
        PullWeatherForecast(location);

        Log.d(TAG, "PRINT HOURS BEFORE");
        printHours();

        int position = ForecastValuesInDatabase();
        if (position<0) {

            writeForecastToDatabase(temperatureForecast.length);

            Log.d(TAG, "PRINT HOURS AFTER THEY ARE NOT IN DATABASE");
            printHours();

        } else {

            Cursor cursor = mDatabaseHelper.getForecastData();

            try {

                cursor.moveToLast();
                int hour_of_last_entry = (int) cursor.getDouble(cursor.getColumnIndex("HOUR_OF_YEAR"));
                //Log.i(TAG, "Last hour: " + Double.toString(cursor.getDouble(cursor.getColumnIndex("hour_of_year")))
                //       + " Current hour: "  + Integer.toString(currentHour));

                //Log.d(TAG, "Hour of last entry: " + Integer.toString(hour_of_last_entry));

                cursor.moveToPosition(position);
                int Position = position+1; // Row ID in database at position 0 is 1 (is it?)

                // update existing values
                int number_of_values_to_update = hour_of_last_entry - currentHour + 1;
                double[] row = new double[4];

                for (int i = 0; i < number_of_values_to_update; i++) {
                    row[0] = currentHour + i;
                    row[1] = temperatureForecast[i];
                    row[2] = cloudsForecast[i];
                    mDatabaseHelper.updateForecastData(row, Position+i);
                    //Log.d(TAG, " ****** updated hour: " + Integer.toString(currentHour + i));
                }

                //Log.d(TAG, "Print hours after UPDATE");
                //printHours();

                // write the new entries
                int number_of_new_entries = temperatureForecast.length-number_of_values_to_update;
                writeForecastToDatabase(number_of_new_entries);

                //Log.d(TAG, "Print hours after WRITING");
                //printHours();

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (cursor != null && !cursor.isClosed())
                    cursor.close();
            }
        }
    }


    private boolean PullWeatherForecast(String location) {

        double[][] CloudsAndTemps = mWeatherHttpClient.get5DayForecast(location);
        // Interpolate between two adjacent hours - forecast comes in three hour increments -
        int I = CloudsAndTemps[0].length-1;
        for (int i=0; i < I+1; ++i) {
            for (int k=0; k<3; ++k) {
                cloudsForecast[3*i+k] =
                        CloudsAndTemps[0][i] + (double) k /3*(CloudsAndTemps[0][(i+1<I) ? i+1 : I]-CloudsAndTemps[0][i]);
                temperatureForecast[3*i+k] =
                        CloudsAndTemps[1][i] + (double) k /3*(CloudsAndTemps[1][(i+1<I) ? i+1 : I]-CloudsAndTemps[1][i]);
            }
        }
        cloudsForecast[24*4-1] = CloudsAndTemps[0][4*8-1]; temperatureForecast[24*4-1] = CloudsAndTemps[1][4*8-1];

        // TODO get irradiance forecast
        return temperatureForecast[0] >= -100;

    }

    private int ForecastValuesInDatabase() {

        Cursor cursor = mDatabaseHelper.getForecastData();
        int inDatabase = -1;

        if(cursor.getCount()>0) {

            cursor.moveToLast();

            try {
                // hour of last entry in forecast is last hour in database -(4*24-1)
                int hour_of_last_entry = (int) cursor.getDouble(cursor.getColumnIndex("HOUR_OF_YEAR"));
                //Log.i(TAG, "Last hour: " + Double.toString(cursor.getDouble(cursor.getColumnIndex("hour_of_year"))) + " Current hour: "  + Integer.toString(currentHour));
                cursor.move(currentHour - hour_of_last_entry);
                int HOUR = (int) cursor.getDouble(cursor.getColumnIndex("HOUR_OF_YEAR"));
                Log.i(TAG, "Current hour: " + Integer.toString(currentHour) + " In database we find: " + Integer.toString(HOUR) + " Position: " + Integer.toString(cursor.getPosition()));
                inDatabase = cursor.getPosition();
            } finally {
                if (cursor != null && !cursor.isClosed())
                    cursor.close();
            }
        }

        return inDatabase;
    }

    private void writeForecastToDatabase(int number_of_new_entries) {

        double[] row = new double[4];

        try {
            Cursor cursor = mDatabaseHelper.getForecastData();

            Log.d(TAG, "Cursor length before writing: " + Integer.toString(cursor.getCount()));

            for (int i = temperatureForecast.length-number_of_new_entries; i < temperatureForecast.length; i++) {
                row[0] = currentHour + i;
                row[1] = temperatureForecast[i];
                row[2] = cloudsForecast[i];
                //Log.d(TAG, " ****** WROTE hour: " + Integer.toString(currentHour + i) + " i: " + Integer.toString(i));
                mDatabaseHelper.insertForecastData(row);
            }

            cursor.close();

        } catch (Exception e) { Log.e(TAG, e.getMessage()); }
    }
}
