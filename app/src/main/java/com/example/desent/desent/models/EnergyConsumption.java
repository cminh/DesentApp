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

    public EnergyConsumption(String name, String unit, String explanation, Energy energy) {
        super(name, unit, explanation, energy);
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
            case WALKING:
                averageValues[1] = (float) energy.calculateEnergyConsumption(timeScale);
                break;
            case CYCLING:
                averageValues[1] = (float) energy.calculateEnergyConsumption(timeScale);
                break;
            case ELECTRIC_CAR:
                averageValues[1] = (float) energy.calculateEnergyConsumption(timeScale);
                break;

        }

    }
}
