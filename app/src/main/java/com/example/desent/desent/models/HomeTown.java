package com.example.desent.desent.models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by magnust on 09.08.2017.
 */

public class HomeTown {
    private final static String TAG = "HomeTown";
    private DatabaseHelper db;
    private String city;
    private String homeTown;
    private String country;
    private String lat;
    private String lon;
    private String weatherLocation = "";
    private boolean exist = false;
    private Context context;
    private Location location;


    public HomeTown(Context context, Location location){
        this.db = new DatabaseHelper(context);
        this.context = context;
        this.location = location;
        init();
    }

    public HomeTown(Context context){
        this.db = new DatabaseHelper(context);
        this.context = context;
    }

    public String getWeatherLocation(){
        weatherLocation = db.getWeatherLocation(); //Will return weather location for Trondheim as default
        return weatherLocation;
    }

    private void init(){
        exist = db.existingWeatherData();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.ENGLISH);
        Log.e(TAG, "Before geocoder");
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            country = addresses.get(0).getCountryName();
            city = addresses.get(0).getLocality();
            lat = String.format(Locale.ENGLISH,"%.3f", (float)location.getLatitude());
            lon = String.format(Locale.ENGLISH,"%.3f", (float)location.getLongitude());

            Log.e(TAG, country );
            Log.e(TAG, "Lon: " + String.format(Locale.ENGLISH,"%.3f", (float)location.getLongitude()) +"\nLat: " + String.format(Locale.ENGLISH,
                    "%.3f", (float)location.getLatitude()));

        } catch (IOException e) {
            Log.e(TAG, "Inside catch error geocoder");
            e.printStackTrace();
            city = "null";
            country = "null";
        }

        if(country.equals("null") || city.equals("null")){
            //Do nothing
            Log.e(TAG, "country.equals(null) || city.equals(null)");
        }else{
            if(exist){
                //WeatherLocation exist
                if(db.cityEqualsHomeTown(city)){
                    // HER ER DET NOE FEIL
                    //City equals home town
                    //Do nothing
                }else{
                    //City does not equal home town
                    //askIfHomeTown();
                }
            }else{
                //WeatherLocation do not exist
                askIfHomeTown();
            }
        }

    }

    private boolean getExist(){return exist;}

    private void askIfHomeTown(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("In order to make the calculations we need to know your hometown. " +
                "\n\nCountry: " + country + "\nCity: " +city+
                "\n\nIs this information correct?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'YES' Button
                        homeTown = city;
                        if(getExist()){
                            //Existing data, but this is the hometown
                            db.updateWeatherData(lat, lon, country, homeTown);
                            Log.e(TAG, "db.updateWeatherData(lat, lon, country, homeTown);");
                        }else{
                            //Doesn't exist, but it is the hometown (temp = NO)
                            db.insertWeatherLocation(lat, lon, "NO", country, homeTown);
                            Log.e(TAG, "db.insertWeatherLocation(lat, lon, \"NO\", country, homeTown);");
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        if(getExist()){
                            //Exists data and this is not the home town. Do nothing.
                        }else{
                            //Doesn't exist data and this is not the home town, but need to store data
                            //for initial calculations (temp = YES)
                            homeTown = city;
                            db.insertWeatherLocation(lat, lon, "YES", country, homeTown);
                            Log.e(TAG, "db.insertWeatherLocation(lat, lon, \"YES\", country, homeTown);");
                        }
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Personal information");
        alert.show();

    }

}
