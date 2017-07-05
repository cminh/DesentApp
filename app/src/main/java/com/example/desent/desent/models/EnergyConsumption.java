package com.example.desent.desent.models;

import android.content.Context;

import com.example.desent.desent.R;
import com.example.desent.desent.utils.EstimationType;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by celine on 30/06/17.
 */

public class EnergyConsumption extends Indicator {

    public EnergyConsumption(Context context, Energy energy, InputStream inputStream, ArrayList<String> columnNames) {
        super(inputStream,
                context.getResources().getString(R.string.energy_consumption_name),
                context.getResources().getString(R.string.energy_consumption_unit),
                columnNames);
        this.energy = energy;
    }

    public int calculatePercentageSelfConsumption() {

        int selfConsumption = 0;

        if (estimationType == EstimationType.SOLAR_INSTALLATION)
            selfConsumption = (int) (((energy.calculateEnergyConsumption(timeScale) - energy.calculateEnergyConsumption(timeScale,pvSystemSize))*100) / energy.calculateEnergyConsumption(timeScale));

        return selfConsumption;
    }

    @Override
    public void calculateValues() {

        switch (estimationType) {

            case NONE:
                averageValues[1] = (float) energy.calculateEnergyConsumption(timeScale);
                break;
            case SOLAR_INSTALLATION:
                averageValues[1] = (float) energy.calculateEnergyConsumption(timeScale, pvSystemSize);
                break;

        }

    }
}
