package com.example.desent.desent.models;

import android.content.Context;

import com.example.desent.desent.R;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by celine on 31/07/17.
 */

public class DrivingDistance extends Indicator {

    public DrivingDistance(String name, String unit, String explanation, Transportation transport) {
        super(name, unit, explanation, transport);
    }


    @Override
    public void calculateValues() {

        switch (estimationType) {

            case NONE:
                averageValues[0] = transport.getDrivingDistance(timeScale);
                break;
            case SOLAR_INSTALLATION:
                averageValues[0] = transport.getDrivingDistance(timeScale);
                break;
            case WALKING:
                averageValues[0] = this.drivingDistance;
                break;
            case CYCLING:
                averageValues[0] = this.drivingDistance;
                break;
            case ELECTRIC_CAR:
                averageValues[0] = transport.getDrivingDistance(timeScale);
                break;

        }
    }
}
