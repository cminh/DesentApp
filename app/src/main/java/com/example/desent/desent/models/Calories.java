package com.example.desent.desent.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.desent.desent.R;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by celine on 24/07/17.
 */

public class Calories extends Indicator {

    private SharedPreferences prefs;
    protected String gender;
    protected float weight;
    protected int age;

    public Calories(String name, String unit, String explanation, Transportation transport, Context context) {
        super(name, unit, explanation, transport);

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        gender = prefs.getString("pref_key_gender", "Female");
        weight = Float.parseFloat(prefs.getString("pref_key_personal_weight", "70"));
        age = Integer.parseInt(prefs.getString("pref_key_personal_age", "25"));
    }

    protected float calculateCaloriesFromWalking(float distance) {
        return 7*distance; //TODO: accurate formula
    }

    protected float calculateCaloriesFromCycling(float distance) {
        return 2*distance; //TODO: accurate formula
    }

    @Override
    public void calculateValues() {

        switch (estimationType) {

            case NONE:
                averageValues[0] = calculateCaloriesFromWalking(transport.getWalkingDistance(timeScale)) + calculateCaloriesFromCycling(transport.getCyclingDistance(timeScale)/1000);
                break;
            case SOLAR_INSTALLATION:
                averageValues[0] = calculateCaloriesFromWalking(transport.getWalkingDistance(timeScale)) + calculateCaloriesFromCycling(transport.getCyclingDistance(timeScale)/1000);
                break;
            case WALKING:
                averageValues[0] = calculateCaloriesFromWalking(this.walkingDistance) + calculateCaloriesFromCycling(transport.getCyclingDistance(timeScale)/1000);
                break;
            case CYCLING:
                averageValues[0] = calculateCaloriesFromWalking(transport.getWalkingDistance(timeScale)) + calculateCaloriesFromCycling(this.cyclingDistance);
                break;
            case ELECTRIC_CAR:
                averageValues[0] = calculateCaloriesFromWalking(transport.getWalkingDistance(timeScale)) + calculateCaloriesFromCycling(transport.getCyclingDistance(timeScale)/1000);
                break;

        }
    }
}
