package com.example.desent.desent.models;

import android.content.Context;

import com.example.desent.desent.R;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by celine on 29/06/17.
 */

public class Expenses extends Indicator {

    public Expenses(Context context, Energy energy, InputStream inputStream, ArrayList<String> columnNames) {
        super(inputStream,
                context.getResources().getString(R.string.expenses_name),
                context.getResources().getString(R.string.expenses_unit),
                columnNames);
        this.energy = energy;
    }

    //TODO: optimize
    public float calculateSavings() {
        float savings = 0;

        switch (estimationType) {
            case SOLAR_INSTALLATION:
                savings = (float) (energy.calculateElectricityCost(timeScale) - energy.calculateElectricityCost(timeScale, pvSystemSize));
        }

        return (savings>0) ? savings : 0;
    }

    @Override
    public void calculateValues() {

        switch (estimationType) {

            case NONE:
                estimateDailyValues(columnNames.get(0), 0);
                averageValues[1] = (float) energy.calculateElectricityCost(timeScale);
                break;
            case SOLAR_INSTALLATION:
                estimateDailyValues(columnNames.get(0), 0);
                averageValues[1] = (float) energy.calculateElectricityCost(timeScale, pvSystemSize);
                break;
            case WALKING:
                estimateDailyValues("Walking", 0);
                averageValues[1] = (float) energy.calculateElectricityCost(timeScale);
                break;
            case CYCLING:
                estimateDailyValues("Cycling", 0);
                averageValues[1] = (float) energy.calculateElectricityCost(timeScale);
                break;
            case ELECTRIC_CAR:
                estimateDailyValues("Electric car", 0);
                averageValues[1] = (float) energy.calculateElectricityCost(timeScale);
                break;

        }

    }

}
