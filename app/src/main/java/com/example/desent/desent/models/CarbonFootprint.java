package com.example.desent.desent.models;

import android.content.Context;

import com.example.desent.desent.R;
import com.example.desent.desent.utils.EstimationType;
import com.example.desent.desent.utils.TimeScale;
import com.example.desent.desent.utils.Utility;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by celine on 29/06/17.
 */

public class CarbonFootprint extends Indicator {

    public CarbonFootprint(Context context, Transportation transport, Energy energy, InputStream inputStream, ArrayList<String> columnNames) {
        super(inputStream,
                context.getResources().getString(R.string.carbon_footprint_name),
                context.getResources().getString(R.string.carbon_footprint_unit),
                columnNames);
        this.energy = energy; //TODO: move
        this.transport = transport; //TODO: move
    }

    @Override
    public void calculateValues() {

        switch (estimationType) {

            case NONE:
                averageValues[0] = transport.calculateKgCo2FromDriving(transport.getDrivingDistance(timeScale));
                averageValues[1] = (float) energy.calculateCO2FromElectricity(timeScale);
                break;
            case SOLAR_INSTALLATION:
                averageValues[0] = transport.calculateKgCo2FromDriving(transport.getDrivingDistance(timeScale));
                averageValues[1] = (float) energy.calculateCO2FromElectricity(timeScale, pvSystemSize);
                break;
            case WALKING:
                averageValues[0] = transport.calculateKgCo2FromDriving(this.drivingDistance);
                averageValues[1] = (float) energy.calculateCO2FromElectricity(timeScale);
                break;
            case CYCLING:
                averageValues[0] = transport.calculateKgCo2FromDriving(this.drivingDistance);
                averageValues[1] = (float) energy.calculateCO2FromElectricity(timeScale);
                break;
            case ELECTRIC_CAR:
                averageValues[0] = transport.calculateKgCo2FromDriving(transport.getDrivingDistance(timeScale), transport.getEmissionsPrLitre("Electricity"));
                averageValues[1] = (float) energy.calculateCO2FromElectricity(timeScale);
                break;

        }

    }
}
