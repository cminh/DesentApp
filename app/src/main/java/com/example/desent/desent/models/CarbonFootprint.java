package com.example.desent.desent.models;

import android.content.Context;

import com.example.desent.desent.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

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
    public void calculateTodaysEnergyValue() {
        dailyValues[1] = (float) energy.calculateTodaysCO2FromElectricity();
    }

    @Override
    public void estimateTodaysValueWithSolarPanel(int pvSystemSize) {
        dailyValues[1] = (float) energy.calculateTodaysCO2FromElectricity(pvSystemSize); //TODO: change to float
    }
}
