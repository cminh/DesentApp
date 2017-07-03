package com.example.desent.desent.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.desent.desent.models.CarbonFootprint;
import com.example.desent.desent.models.Energy;
import com.example.desent.desent.models.Expenses;
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

    Spinner timeSpinner;
    ActiveEstimation activeEstimation;

    //Fragments
    CircleFragment carbonFootprintCircleFragment;
    private CategoryFragment transportationDashboardFragment;
    private CategoryFragment housingDashboardFragment;
    private IndicatorsBarFragment indicatorsBarFragment;

    private View categoriesBar;

    //Estimations parameters
    private CyclingDistanceFragment cyclingDistanceFragment;
    private SolarPanelSizeFragment solarPanelSizeFragment;

    //Indicators
    protected ArrayList<Indicator> indicators = new ArrayList<>();
    protected Indicator calories;
    protected Expenses expenses;
    protected CarbonFootprint carbonFootprint;
    protected Indicator transportation;
    protected Indicator housing;
    protected Energy energy;

    //Information views
    protected View informationCO2Left;
    protected View informationSavings;
    protected View informationDaysLeftSolarPanel;
    protected View informationOwnEnergy;
    protected View informationSeparator; //TODO: not treat it in the activity?

    //Drawer
    private DrawerLayout drawer;
    private  Toolbar toolbar;
    private NavigationView navigationView;

    public Expenses getExpenses() {
        return expenses;
    }

    public void setExpenses(Expenses expenses) {
        this.expenses = expenses;
    }

    public CarbonFootprint getCarbonFootprint() {
        return carbonFootprint;
    }

    public void setCarbonFootprint(CarbonFootprint carbonFootprint) {
        this.carbonFootprint = carbonFootprint;
    }

    public ActiveEstimation getActiveEstimation() {
        return activeEstimation;
    }

    public void setActiveEstimation(ActiveEstimation activeEstimation) {
        this.activeEstimation = activeEstimation;
    }

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

    public enum ActiveEstimation {
        NONE,
        SOLAR_INSTALLATION,
        WALKING,
        CYCLING,
        ELECTRIC_CAR
    }

    @Override
    public void onBackPressed() {
        timeSpinner.setSelection(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Drawer "hamburger"
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        //Navigation drawer
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Spinners
        timeSpinner = (Spinner) findViewById(R.id.time_spinner);
        //timeSpinner = new Spinner(getSupportActionBar().getThemedContext());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);

        //Bottom navigation

        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        findViewById(R.id.navigation_none).setVisibility(GONE);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        FragmentTransaction ft = getFragmentManager().beginTransaction();

                        switch (item.getItemId()) {

                            case R.id.navigation_none:

                                activeEstimation = ActiveEstimation.NONE;

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

                                bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.selector_bottom_navigation_blue));
                                bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(getApplicationContext(),R.color.selector_bottom_navigation_blue));

                                //enableEstimation(getResources().getString(R.string.estimation_solar_panel_title), 1);
                                activeEstimation = ActiveEstimation.SOLAR_INSTALLATION;
                                solarPanelSizeFragment.selectFirstButton(); //TODO: ugly way to do it

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

                                bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.selector_bottom_navigation_green));
                                bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(getApplicationContext(),R.color.selector_bottom_navigation_green));

                                activeEstimation = ActiveEstimation.WALKING;
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

                                bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.selector_bottom_navigation_green));
                                bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(getApplicationContext(),R.color.selector_bottom_navigation_green));

                                activeEstimation = ActiveEstimation.CYCLING;
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

                                bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.selector_bottom_navigation_green));
                                bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(getApplicationContext(),R.color.selector_bottom_navigation_green));

                                activeEstimation = ActiveEstimation.ELECTRIC_CAR;
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
    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:


                        // launch new intent instead of loading fragment
                        /*
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                         */
                        drawer.closeDrawers();

                        return true;
                    case R.id.nav_settings:


                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, Settings.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_about_us:


                        // launch new intent instead of loading fragment
                        /*
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                         */
                        drawer.closeDrawers();

                        return true;
                    default:

                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    AdapterView.OnItemSelectedListener timeSpinnerActivity = new AdapterView.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

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
        ((TextView) findViewById(R.id.text_view_information_co2_left)).setText(String.format(getResources().getString(R.string.information_co2_left), Utility.doubleToStringNDecimals(carbonFootprint.getLimitValue()-carbonFootprint.getDailyValue(), carbonFootprint.getDecimalsNumber())));
    }

    private void updateSavings() {
        //TODO: implement
        ((TextView) findViewById(R.id.text_view_information_daily_savings)).setText(String.format(getResources().getString(R.string.information_savings), "20kr"));
    }

    public void refreshAll(){
        //TODO: finish to implement
        carbonFootprintCircleFragment.refresh();
        housingDashboardFragment.refresh();
        transportationDashboardFragment.refresh();
        indicatorsBarFragment.refresh();
    }

    public void enableEstimation(String estimationTitle, int categoryIndex) {

        clearEstimations();

        for (Indicator indicator : indicators) {
            indicator.estimateValues(estimationTitle, categoryIndex);
        }

        //TODO: test
        //if (estimationTitle == getResources().getString(R.string.estimation_solar_panel_title))
            //carbonFootprint.estimateTodaysValueWithSolarPanel(3);

        carbonFootprintCircleFragment.refresh();
        housingDashboardFragment.refresh();
        transportationDashboardFragment.refresh();
        updateCO2left();

    }

    public void clearEstimations() {

        //TODO: code duplication
        for (Indicator indicator : indicators) {
            indicator.readValues();
        }

        carbonFootprintCircleFragment.refresh();
        housingDashboardFragment.refresh();
        transportationDashboardFragment.refresh();
        indicatorsBarFragment.refresh();
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

        //Indicators
        energy = new Energy(getApplicationContext());

        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("Transportation");
        columnNames.add("Housing"); //Prevent errors for estimations
        indicators.add(carbonFootprint = new CarbonFootprint(getApplicationContext(), energy, inputStream, columnNames));
        indicators.add(calories = new Indicator(inputStream, "Calories", "kCal", columnNames));
        indicators.add(expenses = new Expenses(getApplicationContext(), energy, inputStream, columnNames));

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
            indicator.readTodaysValues();
            indicator.readWeeklyValues();
            indicator.readMonthlyValues();
        }

        transportation.readTodaysValues();
        housing.readTodaysValues();

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

        //solarPanelSizeFragment.addButton("3 kW");
        //solarPanelSizeFragment.addButton("4 kW");
        //solarPanelSizeFragment.addButton("5 kW");
        //solarPanelSizeFragment.addButton("6 kW");
        int[] pvSystemSizes = {3,4,5,6};
        solarPanelSizeFragment.addButtons(pvSystemSizes);

        updateCO2left();
        updateSavings();
        setUpNavigationView();
    }
}
