package com.example.desent.desent.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.desent.desent.R;
import com.example.desent.desent.fragments.CategoryFragment;
import com.example.desent.desent.fragments.CircleFragment;
import com.example.desent.desent.fragments.CyclingDistanceFragment;
import com.example.desent.desent.fragments.IndicatorsBarFragment;
import com.example.desent.desent.fragments.SolarPanelSizeFragment;
import com.example.desent.desent.fragments.WalkingDistanceFragment;
import com.example.desent.desent.models.Calories;
import com.example.desent.desent.models.CarbonFootprint;
import com.example.desent.desent.models.DrivingDistance;
import com.example.desent.desent.models.Energy;
import com.example.desent.desent.models.EnergyConsumption;
import com.example.desent.desent.models.Expenses;
import com.example.desent.desent.models.Indicator;
import com.example.desent.desent.models.Transportation;
import com.example.desent.desent.models.VehicleCost;
import com.example.desent.desent.utils.EstimationType;
import com.example.desent.desent.utils.TimeScale;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by celine on 02/08/17.
 */

public class AsyncMainSetup extends AsyncTask {

    MainActivity activity;
    ProgressBar progress;

    private Energy energy;
    private Transportation transport;
    private VehicleCost vehicleCost;

    private ArrayList<Indicator> indicators;
    private Calories calories;
    private Expenses expenses;
    private CarbonFootprint carbonFootprint;
    private DrivingDistance drivingDistance;
    private EnergyConsumption energyConsumption;

    private CircleFragment carbonFootprintCircleFragment;
    private IndicatorsBarFragment indicatorsBarFragment;
    private CategoryFragment transportationDashboardFragment;
    private CategoryFragment housingDashboardFragment;

    //Estimations parameters
    private WalkingDistanceFragment walkingDistanceFragment;
    private CyclingDistanceFragment cyclingDistanceFragment;
    private SolarPanelSizeFragment solarPanelSizeFragment;

    public AsyncMainSetup(MainActivity activity,
                          ProgressBar progress,
                          ArrayList<Indicator> indicators,
                          CircleFragment circleFragment,
                          IndicatorsBarFragment indicatorsBarFragment,
                          CategoryFragment transportationFragment,
                          CategoryFragment housingFragment,
                          WalkingDistanceFragment walkingDistanceFragment,
                          CyclingDistanceFragment cyclingDistanceFragment,
                          SolarPanelSizeFragment solarPanelSizeFragment) {
        this.activity = activity;
        this.progress = progress;
        this.indicators = indicators;
        this.carbonFootprintCircleFragment = circleFragment;
        this.indicatorsBarFragment = indicatorsBarFragment;
        this.transportationDashboardFragment = transportationFragment;
        this.housingDashboardFragment = housingFragment;
        this.walkingDistanceFragment = walkingDistanceFragment;
        this.cyclingDistanceFragment = cyclingDistanceFragment;
        this.solarPanelSizeFragment = solarPanelSizeFragment;
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        setUp();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        Toast.makeText(activity, "Calculations updated",
                Toast.LENGTH_LONG).show();

        progress.setVisibility(View.GONE);

        //carbonFootprintCircleFragment.updateViews();

        indicatorsBarFragment.setUp();

        activity.setCarbonFootprint(carbonFootprint);
        activity.setCalories(calories);
        activity.setExpenses(expenses);
        activity.setDrivingDistance(drivingDistance);
        activity.setEnergyConsumption(energyConsumption);

        walkingDistanceFragment.setUp();
        cyclingDistanceFragment.setUp();
        solarPanelSizeFragment.setUp();

        float[] pvSystemSizes = {3,4,5,6};
        solarPanelSizeFragment.addButtons(pvSystemSizes);

        activity.initTimeSpinner();
        activity.setFirstDisplay(false);
        activity.refreshAll();

    }

    protected void setUp(){

        //Limit values
        int limitCarbonFootprint = 4;//Data
        InputStream inputStream = activity.getResources().openRawResource(R.raw.data);

        //Indicators
        energy = new Energy(activity);
        transport = new Transportation(activity);
        vehicleCost = new VehicleCost(activity);

        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("Transportation");
        columnNames.add("Housing");
        indicators.add(carbonFootprint = new CarbonFootprint(activity,transport, energy, inputStream, columnNames));
        indicators.add(calories = new Calories(activity, transport, inputStream, columnNames));
        indicators.add(expenses = new Expenses(activity, vehicleCost, energy, inputStream, columnNames));
        indicators.add(drivingDistance = new DrivingDistance(activity, transport, inputStream, columnNames));
        indicators.add(energyConsumption = new EnergyConsumption(activity, energy, inputStream, columnNames));

        carbonFootprint.setMaxValue(2 * limitCarbonFootprint);
        carbonFootprint.setLimitValue(limitCarbonFootprint);

        calories.setDecimalsNumber(0);
        expenses.setDecimalsNumber(0);
        carbonFootprint.setDecimalsNumber(1);
        drivingDistance.setDecimalsNumber(1);
        energyConsumption.setDecimalsNumber(1);

        for (Indicator indicator: indicators) {
            indicator.setTimeScale(TimeScale.TODAY);
            indicator.setEstimationType(EstimationType.NONE);
        }

        indicatorsBarFragment.setLength(4);
        indicatorsBarFragment.addIndicator(calories);
        indicatorsBarFragment.addIndicator(expenses);
        indicatorsBarFragment.addIndicator(drivingDistance);
        indicatorsBarFragment.addIndicator(energyConsumption);

        carbonFootprintCircleFragment.setImgName("earth");
        carbonFootprintCircleFragment.setNumberOfStates(5);

        carbonFootprintCircleFragment.setIndicator(carbonFootprint);
        carbonFootprintCircleFragment.setUp();

        transportationDashboardFragment.setCategoryIndex(0);
        transportationDashboardFragment.setIndicator(carbonFootprint);
        transportationDashboardFragment.setUp();

        housingDashboardFragment.setCategoryIndex(1);
        housingDashboardFragment.setIndicator(carbonFootprint);
        housingDashboardFragment.setUp();

    }


}
