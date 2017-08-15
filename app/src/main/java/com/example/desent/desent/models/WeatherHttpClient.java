package com.example.desent.desent.models;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by petera on 07.07.2017.
 */

public class WeatherHttpClient {

    static final String TAG = "WeatherHttpClient";

    private static String BASE_URL_CURRENT_WEATHER = "http://api.openweathermap.org/data/2.5/weather?";
    private static String BASE_URL_5_DAY_FORECAST = "http://api.openweathermap.org/data/2.5/forecast?";
    private static String APIID = "&appid=";
    private static String API_KEY = "1ca3cae9d5a2df24e80d5f73767d77ad";

    // Hourly values read from OpenWeatherMap int string "Result"
    private String Result;

    public double[] getCurrentWeather(String location) {

        Result = getWeatherData(location, BASE_URL_CURRENT_WEATHER);
        double cloudsCurrent = readCurrentClouds(Result);
        double temperatureCurrent = readCurrentTemperature(Result);

        double args[] = new double[2]; args[0] = cloudsCurrent; args[1] = temperatureCurrent;
        return args;

    }

    public double[][] get5DayForecast(String location) {

        Result = getWeatherData(location, BASE_URL_5_DAY_FORECAST);
        double[] CloudsForecast = readCloudsForecast(Result);
        double[] TemperatureForecast = readTemperatureForecast(Result);

        double[][] args = new double[2][4*24/3]; args[0] = CloudsForecast; args[1] = TemperatureForecast;
        return args;
    }

    private double readCurrentTemperature(String string) {

        double temperature;

        // read temperature from string Result.
        int indexOfTemperature = string.indexOf("temp");

        temperature = Character.getNumericValue(string.charAt(indexOfTemperature+6))*100
                + Character.getNumericValue(string.charAt(indexOfTemperature+7))*10
                + Character.getNumericValue(string.charAt(indexOfTemperature+8))
                + ((double) Character.getNumericValue(string.charAt(indexOfTemperature+10)))/10
                + ((double) Character.getNumericValue(string.charAt(indexOfTemperature+11)))/100;

        Log.i(TAG,"Temp.: " + Double.toString(temperature-273.15));

        return  temperature-273.15;
    }

    private double readCurrentClouds(String string) {

        double clouds;

        // read cloudiness percentage.
        // Given in 2 digits or if cloudiness <10% in 1 digit
        int indexOfClouds = string.indexOf("all");

        if  (string.charAt(indexOfClouds+6)=='}') {
            clouds = Character.getNumericValue(string.charAt(indexOfClouds+5));
        } else {
            clouds = Character.getNumericValue(string.charAt(indexOfClouds+5))*10
                    + Character.getNumericValue(string.charAt(indexOfClouds+6));
        }

        Log.i(TAG,"Clouds: " + Double.toString(clouds));

        return clouds;
    }

    private double[] readTemperatureForecast(String weatherFeed) {

        double[] Temperature = new double[4*24/3];

        // weatherFeed contains 5 days worth of weather forecast in 3 hour increments
        String string; int substringStart = 0;
        int indexOfTemperature;

        for (int i = 0; i<4*24/3; i++) {

            string = weatherFeed.substring(substringStart);
            indexOfTemperature = string.indexOf("temp");

            Temperature[i] = Character.getNumericValue(string.charAt(indexOfTemperature+6))*100
                    + Character.getNumericValue(string.charAt(indexOfTemperature+7))*10
                    + Character.getNumericValue(string.charAt(indexOfTemperature+8))
                    + ((double) Character.getNumericValue(string.charAt(indexOfTemperature+10)))/10
                    + ((double) Character.getNumericValue(string.charAt(indexOfTemperature+11)))/100;
            substringStart = substringStart + string.indexOf("dt_txt") + 15;
            Temperature[i] -= 273.15;
            //Log.d(TAG, "Temperature: " + String.format("%.2f", Temperature[i]));
        }
        return Temperature;
    }

    private double[] readCloudsForecast(String weatherFeed) {

        double[] Clouds = new double[4*24/3];

        // weatherFeed contains 5 days worth of weather forecast in 3 hour increments
        String string; int substringStart = 0;
        int indexOfClouds;

        for (int i = 0; i<4*24/3; i++) {

            string = weatherFeed.substring(substringStart);
            indexOfClouds = string.indexOf("all");

            if  (string.charAt(indexOfClouds+6)=='}') {
                Clouds[i] = Character.getNumericValue(string.charAt(indexOfClouds+5));
            } else {
                Clouds[i] = Character.getNumericValue(string.charAt(indexOfClouds+5))*10
                        + Character.getNumericValue(string.charAt(indexOfClouds+6));
            }
            substringStart = substringStart + indexOfClouds + 6;
            //Log.d(TAG, "Clouds: " + String.format("%.2f", Clouds[i]));
        }
        return Clouds;
    }

    public String getWeatherData(String location, String BASE_URL) {
        HttpURLConnection con = null;
        InputStream is = null;

        try {

            con = (HttpURLConnection) (new URL(BASE_URL + location + APIID + API_KEY)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setReadTimeout(3000);
            Log.i(TAG, "Response code: " + Integer.toString(con.getResponseCode()));
            con.connect();

            int responseCode = con.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            // read response
            StringBuffer stringBuffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null)
                stringBuffer.append(line + "rn");

            is.close();
            con.disconnect();
            return stringBuffer.toString();

        } catch (Throwable t) {
            // t.printStackTrace();
            Log.i(TAG, t.getMessage());
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
                Log.i(TAG, t.getMessage());
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        }
        return null;
    }

}