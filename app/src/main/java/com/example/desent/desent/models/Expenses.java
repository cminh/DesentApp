package com.example.desent.desent.models;

import android.content.Context;
import android.util.Log;

import com.example.desent.desent.R;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by celine on 29/06/17.
 */

public class Expenses extends Indicator {

    //TODO: make it accurate
    final static int SOLAR_PANEL_PRICE = 100000;
    float savings;

    public Expenses(String name, String unit, String explanation, Energy energy, Transportation transport, VehicleCost vehicleCost, Context context) {
        super(name, unit, explanation, energy, transport, vehicleCost);
        this.explanation = context.getResources().getString(R.string.expenses_explanation);
    }

    public float calculateSavings() {
        savings = 0;

        switch (estimationType) {
            case SOLAR_INSTALLATION:
                savings = (float) (energy.calculateElectricityCost(timeScale) - energy.calculateElectricityCost(timeScale, pvSystemSize));
                break;
            case WALKING:
                savings = (transport.getDrivingDistance(timeScale) - this.drivingDistance)*vehicleCost.getAvgCostPrKm();
                break;
            case CYCLING:
                savings = (transport.getDrivingDistance(timeScale) - this.drivingDistance)*vehicleCost.getAvgCostPrKm();
                break;
        }

        return (savings>0) ? savings : 0;
    }

    public int calculateDaysLeftForSolarRoof() {
        return ((int) savings>0) ? SOLAR_PANEL_PRICE / (int) savings : -1;
    }

    @Override
    public void calculateValues() {

        switch (estimationType) {

            case NONE:
                averageValues[0] = (transport.isCarOwner()) ? vehicleCost.getCost(timeScale) : 0;
                averageValues[1] = (float) energy.calculateElectricityCost(timeScale);
                break;
            case SOLAR_INSTALLATION:
                averageValues[0] = (transport.isCarOwner()) ? vehicleCost.getCost(timeScale) : 0;
                averageValues[1] = (float) energy.calculateElectricityCost(timeScale, pvSystemSize);
                break;
            case WALKING:
                averageValues[0] = vehicleCost.getCost(timeScale) - (transport.getDrivingDistance(timeScale) - this.drivingDistance)*vehicleCost.getAvgCostPrKm();
                averageValues[1] = (float) energy.calculateElectricityCost(timeScale);
                break;
            case CYCLING:
                averageValues[0] = vehicleCost.getCost(timeScale) - (transport.getDrivingDistance(timeScale) - this.drivingDistance)*vehicleCost.getAvgCostPrKm();
                averageValues[1] = (float) energy.calculateElectricityCost(timeScale);
                break;
            case ELECTRIC_CAR:
                averageValues[0] = vehicleCost.getCost(timeScale) - transport.getDrivingDistance(timeScale)*vehicleCost.getAvgCostPrKm();
                averageValues[1] = (float) energy.calculateElectricityCost(timeScale);
                break;

        }

    }

}
