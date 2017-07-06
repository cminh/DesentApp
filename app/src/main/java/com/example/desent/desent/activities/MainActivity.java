package com.example.desent.desent.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.desent.desent.R;
import com.example.desent.desent.fragments.CategoryFragment;
import com.example.desent.desent.fragments.CircleFragment;
import com.example.desent.desent.fragments.CyclingDistanceFragment;
import com.example.desent.desent.fragments.IndicatorsBarFragment;
import com.example.desent.desent.fragments.SolarPanelSizeFragment;
import com.example.desent.desent.models.CarbonFootprint;
import com.example.desent.desent.models.DistanceTracker;
import com.example.desent.desent.models.Energy;
import com.example.desent.desent.models.EnergyConsumption;
import com.example.desent.desent.models.Expenses;
import com.example.desent.desent.models.Indicator;
import com.example.desent.desent.utils.EstimationType;
import com.example.desent.desent.utils.TimeScale;
import com.example.desent.desent.utils.Utility;

import java.io.InputStream;
import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Spinner timeSpinner;

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
    protected EnergyConsumption energyConsumption;
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

    //DistanceTracker
    private String activity = "STILL";
    private DistanceTracker distanceTracking;
    private boolean gpsFlag;
    private static final String FENCE_RECEIVER_ACTION = "FENCE_RECEIVE";


    public ArrayList<Indicator> getIndicators() {
        return indicators;
    }

    public void setIndicators(ArrayList<Indicator> indicators) {
        this.indicators = indicators;
    }

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
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

                                for (Indicator indicator:indicators)
                                    indicator.setEstimationType(EstimationType.NONE);

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
                                for (Indicator indicator:indicators)
                                    indicator.setEstimationType(EstimationType.SOLAR_INSTALLATION);

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

                                for (Indicator indicator:indicators)
                                    indicator.setEstimationType(EstimationType.WALKING);

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

                                for (Indicator indicator:indicators)
                                    indicator.setEstimationType(EstimationType.CYCLING);

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

                                for (Indicator indicator:indicators)
                                    indicator.setEstimationType(EstimationType.ELECTRIC_CAR);

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
                        refreshAll();
                        return true;
                    }
                });

        bottomNavigationView.setOnNavigationItemReselectedListener(
                new BottomNavigationView.OnNavigationItemReselectedListener() {
                    @Override
                    public void onNavigationItemReselected(@NonNull MenuItem item) {
                        findViewById(R.id.navigation_none).performClick();
                    }
                });


        setUp();

        timeSpinner.setOnItemSelectedListener(timeSpinnerActivity);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(cyclingDistanceFragment);
        ft.hide(solarPanelSizeFragment);
        ft.commit();

        //DistanceTracker
        Activity activityContext = (Activity) this;
        distanceTracking = new DistanceTracker(activityContext, this);
        distanceTracking.setActivity(activity);
        gpsFlag = checkGpsStatus();
        if (gpsFlag) {

        } else {
            alertbox();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        distanceTracking.start();
        registerReceiver(distanceTracking.getFenceReceiver(), new IntentFilter(FENCE_RECEIVER_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        distanceTracking.stop();
        unregisterReceiver(distanceTracking.getFenceReceiver());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2: { if ((grantResults.length > 0)
                    && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                distanceTracking.initiateLocation();
            } else {
                Toast.makeText(this, "The app needs to enable 'location' to do the calculations.",
                        Toast.LENGTH_LONG).show();
            }
                return;
            }
        }
    }

    protected void alertbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disabled. Activate?")
                .setCancelable(false)
                .setTitle("Gps Status")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected boolean checkGpsStatus() {
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        boolean gpsStatus = android.provider.Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
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
                    informationCO2Left.setVisibility(VISIBLE); //TODO: nope
                    for (Indicator indicator: indicators)
                        indicator.setTimeScale(TimeScale.TODAY);
                    break;
                case 1:
                    informationCO2Left.setVisibility(GONE);
                    for (Indicator indicator: indicators)
                        indicator.setTimeScale(TimeScale.LAST_24_HOURS);
                    break;
                case 2:
                    informationCO2Left.setVisibility(GONE);
                    for (Indicator indicator: indicators)
                        indicator.setTimeScale(TimeScale.WEEK);
                    break;
                case 3:
                    informationCO2Left.setVisibility(GONE);
                    for (Indicator indicator: indicators)
                        indicator.setTimeScale(TimeScale.MONTH);
            }

            refreshAll();

        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

    };

    public void updateCO2left(){
        //TODO: make another message when the amount of CO2 is exceeded?
        ((TextView) findViewById(R.id.text_view_information_co2_left)).setText(String.format(getResources().getString(R.string.information_co2_left), Utility.doubleToStringNDecimals(carbonFootprint.getLimitValue()-carbonFootprint.getDailyValue(), carbonFootprint.getDecimalsNumber())));
    }

    private void updateSavings() {
        ((TextView) findViewById(R.id.text_view_information_daily_savings)).setText(String.format(getResources().getString(R.string.information_savings), Utility.floatToStringNDecimals(expenses.calculateSavings(), expenses.getDecimalsNumber()) + expenses.getUnit()));
    }

    public void updateOwnEnergy(){
        ((TextView) findViewById(R.id.text_view_information_own_energy)).setText(String.format(getResources().getString(R.string.information_own_energy), String.valueOf(energyConsumption.calculatePercentageSelfConsumption()) + "%"));
    }

    public void refreshAll(){
        //TODO: finish to implement

        for (Indicator indicator: indicators)
            indicator.calculateValues();

        carbonFootprintCircleFragment.refresh();
        housingDashboardFragment.refresh();
        transportationDashboardFragment.refresh();
        indicatorsBarFragment.refresh();
        updateSavings();
        updateOwnEnergy();
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
        int mGreen = ContextCompat.getColor(getApplicationContext(), R.color.green);
        int mBlue = ContextCompat.getColor(getApplicationContext(), R.color.blue);

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
        columnNames.add("Housing");
        indicators.add(carbonFootprint = new CarbonFootprint(getApplicationContext(), energy, inputStream, columnNames));
        indicators.add(calories = new Indicator(inputStream, "Calories", "kCal", columnNames));
        indicators.add(expenses = new Expenses(getApplicationContext(), energy, inputStream, columnNames));
        indicators.add(transportation = new Indicator(inputStream, "Transportation", "km", "Distance"));
        indicators.add(energyConsumption = new EnergyConsumption(getApplicationContext(), energy, inputStream, columnNames));

        indicatorsBarFragment.addIndicator(calories);
        indicatorsBarFragment.addIndicator(expenses);
        indicatorsBarFragment.addIndicator(transportation);
        indicatorsBarFragment.addIndicator(energyConsumption);

        carbonFootprintCircleFragment.setStartAngle(135);
        carbonFootprintCircleFragment.setSweepAngle(270);
        carbonFootprintCircleFragment.setImgName("earth");
        carbonFootprintCircleFragment.setNumberOfStates(5);

        ArrayList<Integer> energyTransportationColors = new ArrayList<>();
        energyTransportationColors.add(mGreen);
        energyTransportationColors.add(mBlue);
        carbonFootprint.setColors(energyTransportationColors);
        carbonFootprint.setLimitColor(mRed);

        for (Indicator indicator : indicators) {
            indicator.setTimeScale(TimeScale.TODAY); //TODO: SharedPreferences?
            indicator.setEstimationType(EstimationType.NONE);
        }

        carbonFootprint.setMaxValue(2 * limitCarbonFootprint);
        carbonFootprint.setLimitValue(limitCarbonFootprint);

        calories.setDecimalsNumber(0);
        expenses.setDecimalsNumber(0);
        carbonFootprint.setDecimalsNumber(1);
        transportation.setDecimalsNumber(1);
        energyConsumption.setDecimalsNumber(1);

        carbonFootprintCircleFragment.setIndicator(carbonFootprint);
        carbonFootprintCircleFragment.setUp();

        transportationDashboardFragment.setCategoryName("Transportation");
        transportationDashboardFragment.setCategoryIndex(0);
        transportationDashboardFragment.setIndicator(carbonFootprint);
        transportationDashboardFragment.setUp();

        housingDashboardFragment.setCategoryName("Housing");
        housingDashboardFragment.setCategoryIndex(1);
        housingDashboardFragment.setIndicator(carbonFootprint);
        housingDashboardFragment.setUp();

        indicatorsBarFragment.setUp();
        cyclingDistanceFragment.setUp();
        solarPanelSizeFragment.setUp();

        int[] pvSystemSizes = {3,4,5,6};
        solarPanelSizeFragment.addButtons(pvSystemSizes);

        setUpNavigationView();
    }
}
