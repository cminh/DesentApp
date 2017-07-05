package com.example.desent.desent.models;

import android.content.Context;

import com.example.desent.desent.R;
import com.example.desent.desent.utils.EstimationType;
import com.example.desent.desent.utils.TimeScale;
import com.example.desent.desent.utils.Utility;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by celine on 29/06/17.
 */

public class CarbonFootprint extends Indicator {

    public CarbonFootprint(Context context, Energy energy, InputStream inputStream, ArrayList<String> columnNames) {
        super(inputStream,
                context.getResources().getString(R.string.carbon_footprint_name),
                context.getResources().getString(R.string.carbon_footprint_unit),
                columnNames);
        this.energy = energy; //TODO: move
    }

    @Override
    public void calculateValues() {

        switch (estimationType) {

            case NONE:
                estimateDailyValues(columnNames.get(0), 0);
                averageValues[1] = (float) energy.calculateCO2FromElectricity(timeScale);
                break;
            case SOLAR_INSTALLATION:
                estimateDailyValues(columnNames.get(0), 0);
                averageValues[1] = (float) energy.calculateCO2FromElectricity(timeScale, pvSystemSize);
                break;
            case WALKING:
                estimateDailyValues("Walking", 0);
                averageValues[1] = (float) energy.calculateCO2FromElectricity(timeScale);
                break;
            case CYCLING:
                estimateDailyValues("Cycling", 0);
                averageValues[1] = (float) energy.calculateCO2FromElectricity(timeScale);
                break;
            case ELECTRIC_CAR:
                estimateDailyValues("Electric car", 0);
                averageValues[1] = (float) energy.calculateCO2FromElectricity(timeScale);
                break;

        }

    }
}