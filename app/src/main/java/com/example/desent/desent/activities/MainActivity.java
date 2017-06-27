package com.example.desent.desent.activities;

import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.desent.desent.R;
import com.example.desent.desent.fragments.CategoryFragment;
import com.example.desent.desent.fragments.CircleFragment;
import com.example.desent.desent.fragments.CyclingDistanceFragment;
import com.example.desent.desent.fragments.IndicatorsBarFragment;
import com.example.desent.desent.fragments.SolarPanelSizeFragment;
import com.example.desent.desent.models.Indicator;
import com.example.desent.desent.utils.Utility;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Date date = null;
    Spinner timeSpinner;

    //Fragments
    CircleFragment carbonFootprintCircleFragment;
    private CategoryFragment transportationDashboardFragment;
    private CategoryFragment housingDashboardFragment;
    private IndicatorsBarFragment indicatorsBarFragment;

    private View categoriesBar;

    private CyclingDistanceFragment cyclingDistanceFragment;
    private SolarPanelSizeFragment solarPanelSizeFragment;

    //Indicators
    protected ArrayList<Indicator> indicators = new ArrayList<>();
    protected Indicator calories;
    protected Indicator expenses;
    protected Indicator carbonFootprint;
    protected Indicator transportation;
    protected Indicator housing;

    //Information views
    protected View informationCO2Left;
    protected View informationSavings;
    protected View informationDaysLeftSolarPanel;
    protected View informationOwnEnergy;
    protected View informationSeparator; //TODO: not treat it in the activity?

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public enum ActiveView {
        TODAY,
        LAST_24_HOURS,
        WEEK,
        MONTH
    }

    @Override
    public void onBackPressed() {
        timeSpinner.setSelection(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Navigation drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Bottom navigation

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        findViewById(R.id.navigation_none).setVisibility(GONE);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        FragmentTransaction ft = getFragmentManager().beginTransaction();

                        switch (item.getItemId()) {

                            case R.id.navigation_none:

                                if (timeSpinner.getSelectedItemPosition() == 0)
                                    informationCO2Left.setVisibility(View.VISIBLE);

                                informationSavings.setVisibility(View.GONE);
                                informationDaysLeftSolarPanel.setVisibility(View.GONE);
                                informationSeparator.setVisibility(GONE);
                                informationOwnEnergy.setVisibility(GONE);

                                ft.hide(cyclingDistanceFragment);
                                ft.hide(solarPanelSizeFragment);
                                ft.commit();


                                break;

                            case R.id.navigation_solar_installation:

                                enableEstimation(getResources().getString(R.string.estimation_solar_panel_title), 1);

                                informationCO2Left.setVisibility(View.GONE);
                                informationSavings.setVisibility(View.VISIBLE);
                                informationDaysLeftSolarPanel.setVisibility(View.GONE);
                                informationSeparator.setVisibility(VISIBLE);
                                informationOwnEnergy.setVisibility(VISIBLE);

                                ft.hide(cyclingDistanceFragment);
                                ft.show(solarPanelSizeFragment);
                                ft.commit();

                                break;
                            case R.id.navigation_walking:
                                enableEstimation(getResources().getString(R.string.estimation_walking_title), 0);

                                informationCO2Left.setVisibility(View.GONE);
                                informationSavings.setVisibility(View.VISIBLE);
                                informationDaysLeftSolarPanel.setVisibility(View.VISIBLE);
                                informationOwnEnergy.setVisibility(GONE);
                                informationSeparator.setVisibility(VISIBLE);

                                ft.hide(cyclingDistanceFragment);
                                ft.hide(solarPanelSizeFragment);
                                ft.commit();

                                break;
                            case R.id.navigation_cycling:

                                enableEstimation(getResources().getString(R.string.estimation_cycling_title), 0);

                                informationCO2Left.setVisibility(View.GONE);
                                informationSavings.setVisibility(View.VISIBLE);
                                informationDaysLeftSolarPanel.setVisibility(View.VISIBLE);
                                informationOwnEnergy.setVisibility(GONE);
                                informationSeparator.setVisibility(VISIBLE);

                                ft.show(cyclingDistanceFragment);
                                ft.hide(solarPanelSizeFragment);
                                ft.commit();

                                break;
                            case R.id.navigation_electric_car:
                                enableEstimation(getResources().getString(R.string.estimation_electric_car_title), 0);

                                informationCO2Left.setVisibility(View.GONE);
                                informationSavings.setVisibility(View.VISIBLE);
                                informationDaysLeftSolarPanel.setVisibility(View.VISIBLE);
                                informationOwnEnergy.setVisibility(GONE);
                                informationSeparator.setVisibility(VISIBLE);

                                ft.hide(cyclingDistanceFragment);
                                ft.hide(solarPanelSizeFragment);
                                ft.commit();

                                break;

                        }
                        return true;
                    }
                });

        bottomNavigationView.setOnNavigationItemReselectedListener(
                new BottomNavigationView.OnNavigationItemReselectedListener() {
                    @Override
                    public void onNavigationItemReselected(@NonNull MenuItem item) {
                        findViewById(R.id.navigation_none).performClick();
                        clearEstimations();
                    }
                });


        setUp();

        timeSpinner.setOnItemSelectedListener(timeSpinnerActivity);

        //TODO: move
        carbonFootprintCircleFragment.setActiveView(ActiveView.TODAY);
        transportationDashboardFragment.setActiveView(ActiveView.TODAY);
        housingDashboardFragment.setActiveView(ActiveView.TODAY);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(cyclingDistanceFragment);
        ft.hide(solarPanelSizeFragment);
        ft.commit();
    }

    AdapterView.OnItemSelectedListener timeSpinnerActivity = new AdapterView.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            switch (pos) {
                case 0:
                    informationCO2Left.setVisibility(VISIBLE);
                    carbonFootprintCircleFragment.setActiveView(ActiveView.TODAY);
                    transportationDashboardFragment.setActiveView(ActiveView.TODAY);
                    housingDashboardFragment.setActiveView(ActiveView.TODAY);
                    break;
                case 1:
                    informationCO2Left.setVisibility(GONE);
                    carbonFootprintCircleFragment.setActiveView(ActiveView.TODAY);
                    transportationDashboardFragment.setActiveView(ActiveView.TODAY);
                    housingDashboardFragment.setActiveView(ActiveView.TODAY);
                    break;
                case 2:
                    informationCO2Left.setVisibility(GONE);
                    carbonFootprintCircleFragment.setActiveView(ActiveView.WEEK);
                    transportationDashboardFragment.setActiveView(ActiveView.WEEK);
                    housingDashboardFragment.setActiveView(ActiveView.WEEK);
                    break;
                case 3:
                    informationCO2Left.setVisibility(GONE);
                    carbonFootprintCircleFragment.setActiveView(ActiveView.MONTH);
                    transportationDashboardFragment.setActiveView(ActiveView.MONTH);
                    housingDashboardFragment.setActiveView(ActiveView.MONTH);
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

    };

    public void updateCO2left(){
        //TODO: make another message when the amount of CO2 is exceeded?
        ((TextView) findViewById(R.id.text_view_information_co2_left)).setText(String.format(getResources().getString(R.string.information_co2_left), Utility.floatToStringNDecimals(carbonFootprint.getLimitValue()-carbonFootprint.getDailyValue(), carbonFootprint.getDecimalsNumber())));
    }

    private void updateSavings() {
        //TODO: implement
        ((TextView) findViewById(R.id.text_view_information_daily_savings)).setText(String.format(getResources().getString(R.string.information_savings), "20kr"));
    }

    public void enableEstimation(String estimationTitle, int categoryIndex) {

        clearEstimations();

        for (Indicator indicator : indicators) {
            indicator.estimateValues(date, estimationTitle, categoryIndex);
        }

        carbonFootprintCircleFragment.refresh();
        housingDashboardFragment.refresh();
        transportationDashboardFragment.refresh();
        updateCO2left();

    }

    public void clearEstimations() {

        //TODO: code duplication
        for (Indicator indicator : indicators) {
            indicator.readValues(date);
        }

        carbonFootprintCircleFragment.refresh();
        housingDashboardFragment.refresh();
        transportationDashboardFragment.refresh();
        updateCO2left();

    }

    protected void setUp() {

        //Information text views
        informationCO2Left = findViewById(R.id.information_co2_left);
        informationSavings = findViewById(R.id.information_daily_savings);
        informationDaysLeftSolarPanel = findViewById(R.id.information_days_left_solar_panel);
        informationOwnEnergy = findViewById(R.id.information_own_energy);
        informationSeparator = findViewById(R.id.separator_information);

        cyclingDistanceFragment = (CyclingDistanceFragment) getFragmentManager().findFragmentById(R.id.cycling_distance);
        solarPanelSizeFragment = (SolarPanelSizeFragment) getFragmentManager().findFragmentById(R.id.solar_panel_size);

        categoriesBar = findViewById(R.id.categories_bar);

        //Colors
        int mRed = ContextCompat.getColor(getApplicationContext(), R.color.red);
        int mOrange = ContextCompat.getColor(getApplicationContext(), R.color.orange);
        int mGreen = ContextCompat.getColor(getApplicationContext(), R.color.green);
        int mBlue = ContextCompat.getColor(getApplicationContext(), R.color.blue);
        int mDarkGrey = ContextCompat.getColor(getApplicationContext(), R.color.dark_grey);
        int mLightGrey = ContextCompat.getColor(getApplicationContext(), R.color.light_grey);

        //Limit values
        int targetCalories = 1700;
        int limitExpenses = 400;
        int limitCarbonFootprint = 4;

        //Fragments

        carbonFootprintCircleFragment = (CircleFragment) getFragmentManager().findFragmentById(R.id.dailyCarbonFootprint);

        transportationDashboardFragment = (CategoryFragment) getFragmentManager().findFragmentById(R.id.transportation_dashboard_fragment);
        housingDashboardFragment = (CategoryFragment) getFragmentManager().findFragmentById(R.id.housing_dashboard_fragment);

        indicatorsBarFragment = (IndicatorsBarFragment) getFragmentManager().findFragmentById(R.id.indicators_bar);

        //Date
        InputStream inputStream = getResources().openRawResource(R.raw.data);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = dateFormat.parse("2017-04-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Spinners
        timeSpinner = (Spinner) findViewById(R.id.time_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);

        //Indicators
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("Transportation");
        columnNames.add("Housing"); //Prevent errors for estimations
        indicators.add(carbonFootprint = new Indicator(inputStream, "Carbon footprint", "kgCO2", columnNames));
        indicators.add(calories = new Indicator(inputStream, "Calories", "kCal", columnNames));
        indicators.add(expenses = new Indicator(inputStream, "Expenses", "kr", columnNames));

        transportation = new Indicator(inputStream, "Transportation", "km", "Distance");
        housing = new Indicator(inputStream, "Housing", "kWh", "Energy consumption");

        indicatorsBarFragment.addIndicator(calories);
        indicatorsBarFragment.addIndicator(expenses);
        indicatorsBarFragment.addIndicator(transportation);
        indicatorsBarFragment.addIndicator(housing);

        carbonFootprintCircleFragment.setStartAngle(135);
        carbonFootprintCircleFragment.setSweepAngle(270);
        carbonFootprintCircleFragment.setImgName("earth");
        carbonFootprintCircleFragment.setNumberOfStates(5);

        calories.setColor(mOrange);
        calories.setLimitColor(mLightGrey);

        ArrayList<Integer> energyTransportationColors = new ArrayList<>();
        energyTransportationColors.add(mGreen);
        energyTransportationColors.add(mBlue);
        carbonFootprint.setColors(energyTransportationColors);
        carbonFootprint.setLimitColor(mRed);
        expenses.setColors(energyTransportationColors);
        expenses.setLimitColor(mRed);

        transportation.setColor(mGreen);
        transportation.setDecimalsNumber(1);
        housing.setColor(mBlue);
        housing.setDecimalsNumber(1);

        for (Indicator indicator : indicators) {
            indicator.readDailyValues(date);
            indicator.readWeeklyValues(date);
            indicator.readMonthlyValues(date);
        }

        transportation.readDailyValues(date);
        housing.readDailyValues(date);

        calories.setMaxValue(targetCalories);
        calories.setLimitValue(targetCalories);
        expenses.setMaxValue(limitExpenses);
        expenses.setLimitValue(limitExpenses);
        carbonFootprint.setMaxValue(2 * limitCarbonFootprint);
        carbonFootprint.setLimitValue(limitCarbonFootprint);

        calories.setDecimalsNumber(0);
        expenses.setDecimalsNumber(0);
        carbonFootprint.setDecimalsNumber(1);

        carbonFootprintCircleFragment.setIndicator(carbonFootprint);
        carbonFootprintCircleFragment.setUp();

        transportationDashboardFragment.setCategory(transportation);
        transportationDashboardFragment.setCategoryIndex(0);
        transportationDashboardFragment.setIndicator(carbonFootprint);
        transportationDashboardFragment.setUp();

        housingDashboardFragment.setCategory(housing);
        housingDashboardFragment.setCategoryIndex(1);
        housingDashboardFragment.setIndicator(carbonFootprint);
        housingDashboardFragment.setUp();

        indicatorsBarFragment.setUp();
        cyclingDistanceFragment.setUp();
        solarPanelSizeFragment.setUp();

        //TODO: test
        solarPanelSizeFragment.addButton("3 kW");
        solarPanelSizeFragment.addButton("4 kW");
        solarPanelSizeFragment.addButton("5 kW");
        solarPanelSizeFragment.addButton("6 kW");

        updateCO2left();
        updateSavings();
    }
}
