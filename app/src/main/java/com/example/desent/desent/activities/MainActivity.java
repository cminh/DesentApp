package com.example.desent.desent.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
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
import com.example.desent.desent.fragments.IndicatorsBarFragment;
import com.example.desent.desent.fragments.MonthFragment;
import com.example.desent.desent.fragments.WeekFragment;
import com.example.desent.desent.models.Indicator;
import com.example.desent.desent.utils.Utility;
import com.example.desent.desent.views.CircularIndicator;

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
    private ArrayList<CircleFragment> circleFragments = new ArrayList<>();
    private WeekFragment weekFragment;
    private MonthFragment monthFragment;
    private CategoryFragment transportationDashboardFragment;
    private CategoryFragment housingDashboardFragment;
    private IndicatorsBarFragment indicatorsBarFragment;

    //Indicators
    protected ArrayList<Indicator> indicators = new ArrayList<>();
    protected Indicator calories;
    protected Indicator expenses;
    protected Indicator carbonFootprint;
    protected Indicator transportation;
    protected Indicator housing;

    protected View informationCO2left;

    // Drawer
    private DrawerLayout drawer;
    private  Toolbar toolbar;
    private NavigationView navigationView;



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public enum ActiveView {
        DAY,
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

        //Bottom navigation

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        findViewById(R.id.navigation_none).setVisibility(GONE);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_solar_installation:
                                enableEstimation(getResources().getString(R.string.estimation_solar_panel_title), 1);
                                break;
                            case R.id.navigation_walking:
                                enableEstimation(getResources().getString(R.string.estimation_walking_title), 0);
                                break;
                            case R.id.navigation_cycling:
                                enableEstimation(getResources().getString(R.string.estimation_cycling_title), 0);
                                break;
                            case R.id.navigation_electric_car:
                                enableEstimation(getResources().getString(R.string.estimation_electric_car_title), 0);
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

        goToDailyView();

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
                        drawer.closeDrawers();
                        */
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
            switch (pos) {
                case 0:
                    goToDailyView();
                    break;
                case 1:
                    goToWeeklyView();
                    break;
                case 2:
                    goToMonthlyView();
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

    };

    public void updateCO2left(){
        //TODO: make another message when the amount of CO2 is exceeded?
        ((TextView) findViewById(R.id.text_view_information_co2_left)).setText(String.format(getResources().getString(R.string.information_co2_left), Utility.floatToStringNDecimals(carbonFootprint.getLimitValue()-carbonFootprint.getDailyValue(), carbonFootprint.getDecimalsNumber())));
    }

    public void enableEstimation(String estimationTitle, int categoryIndex) {

        clearEstimations();

        for (Indicator indicator : indicators) {
            indicator.estimateValues(date, estimationTitle, categoryIndex);
        }

        for (CircleFragment circleFragment : circleFragments) {
            circleFragment.refresh();
        }

        housingDashboardFragment.refresh();
        transportationDashboardFragment.refresh();
        weekFragment.refresh();
        monthFragment.refresh();
        updateCO2left();

    }

    public void clearEstimations() {

        //TODO: code duplication
        for (Indicator indicator : indicators) {
            indicator.readValues(date);
        }

        for (CircleFragment circleFragment : circleFragments) {
            circleFragment.refresh();
        }

        housingDashboardFragment.refresh();
        transportationDashboardFragment.refresh();
        weekFragment.refresh();
        monthFragment.refresh();
        updateCO2left();

    }

    protected void goToIndicatorView(Indicator indicator) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        for (CircleFragment circleFragment : circleFragments) {
            ft.hide(circleFragment);
        }

        ft.commit();


        ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        for (CircleFragment circleFragment : circleFragments) {

            circleFragment.saveInitialHeight();

            if (indicator.getName().equals(circleFragment.getIndicator().getName()))
                ft.show(circleFragment);

        }

        weekFragment.setIndicator(indicator);
        weekFragment.refresh();
        monthFragment.setIndicator(indicator);
        monthFragment.refresh();

        if (indicator != calories) {
            transportationDashboardFragment.setIndicator(indicator);
            housingDashboardFragment.setIndicator(indicator);
            ft.show(transportationDashboardFragment);
            ft.show(housingDashboardFragment);
        } else {
            ft.hide(transportationDashboardFragment);
            ft.hide(housingDashboardFragment);
        }

        ft.commit();

    }

    protected void goToDailyView() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.hide(weekFragment);
        ft.hide(monthFragment);
        ft.commit();

        informationCO2left.setVisibility(VISIBLE);

        for (CircleFragment circleFragment : circleFragments) {
            circleFragment.setActiveView(ActiveView.DAY);
        }

        transportationDashboardFragment.setActiveView(ActiveView.DAY);
        housingDashboardFragment.setActiveView(ActiveView.DAY);
    }

    protected void goToWeeklyView() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        ft.show(weekFragment);
        ft.hide(monthFragment);
        ft.commit();

        informationCO2left.setVisibility(GONE);

        for (CircleFragment circleFragment : circleFragments)
            circleFragment.setActiveView(ActiveView.WEEK);

        transportationDashboardFragment.setActiveView(ActiveView.WEEK);
        housingDashboardFragment.setActiveView(ActiveView.WEEK);

    }

    protected void goToMonthlyView() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //ft.addToBackStack("month");

        ft.hide(weekFragment);
        ft.show(monthFragment);
        ft.commit();

        informationCO2left.setVisibility(GONE);

        for (CircleFragment circleFragment : circleFragments)
            circleFragment.setActiveView(ActiveView.MONTH);

        transportationDashboardFragment.setActiveView(ActiveView.MONTH);
        housingDashboardFragment.setActiveView(ActiveView.MONTH);

    }

    protected void setUp() {

        //Text
        informationCO2left = findViewById(R.id.information_co2_left);

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
        CircleFragment caloriesCircleFragment;
        CircleFragment expensesCircleFragment;
        CircleFragment carbonFootprintCircleFragment;

        circleFragments.add(carbonFootprintCircleFragment = (CircleFragment) getFragmentManager().findFragmentById(R.id.dailyCarbonFootprint));

        weekFragment = (WeekFragment) getFragmentManager().findFragmentById(R.id.weeklyData);
        monthFragment = (MonthFragment) getFragmentManager().findFragmentById(R.id.monthlyData);

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
        carbonFootprintCircleFragment.setFormat(CircularIndicator.Format.CIRCLE_IMG_TEXT);
        carbonFootprintCircleFragment.setUp();

        weekFragment.setIndicator(carbonFootprint);
        weekFragment.setUp();

        monthFragment.setIndicator(carbonFootprint);
        monthFragment.setUp();

        transportationDashboardFragment.setCategory(transportation);
        transportationDashboardFragment.setCategoryIndex(0);
        transportationDashboardFragment.setIndicator(carbonFootprint);
        transportationDashboardFragment.setUp();

        housingDashboardFragment.setCategory(housing);
        housingDashboardFragment.setCategoryIndex(1);
        housingDashboardFragment.setIndicator(carbonFootprint);
        housingDashboardFragment.setUp();

        indicatorsBarFragment.setUp();

        updateCO2left();

        // Drawer setup
        setUpNavigationView();
    }
}
