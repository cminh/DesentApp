package com.example.desent.desent.models;

import android.content.Context;

import com.example.desent.desent.R;

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
}
