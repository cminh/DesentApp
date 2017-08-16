package com.example.desent.desent.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.desent.desent.R;
import com.example.desent.desent.models.DatabaseHelper;
import com.example.desent.desent.models.Energy;
import com.example.desent.desent.models.Transportation;
import com.example.desent.desent.utils.ChartData;
import com.example.desent.desent.utils.GraphPoints;
import com.example.desent.desent.utils.Utility;
import com.example.desent.desent.views.StackBarChart;
import com.example.desent.desent.views.StackedBarLabel;
import com.example.desent.desent.views.Yaxis;
import com.jjoe64.graphview.series.DataPoint;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by celine on 20/07/17.
 */

public class HistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Spinner spinner;
    final static String LOGG = "HistoryPage";
    DatabaseHelper myDb;
    Energy energy;
    Transportation transportation;
    StackBarChart stackBarChart;
    StackedBarLabel labelOrganizer;
    Yaxis yaxis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setUpNavigationView();

        spinner = (Spinner) findViewById(R.id.history_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.history_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        myDb = new DatabaseHelper(this);
        myDb.getWeekDrivingDistance();

        stackBarChart = (StackBarChart) findViewById(R.id.chart);
        labelOrganizer = (StackedBarLabel) findViewById(R.id.labelStackedBar);
        yaxis = (Yaxis) findViewById(R.id.y_axis);
        //displayDistanceGraph();
    }


    @Override
    protected void onStart() {
        super.onStart();
        spinner.setSelection(0);

        AsyncHistorySetup asyncHistorySetup = new AsyncHistorySetup(this,
                stackBarChart,
                labelOrganizer,
                yaxis);

        asyncHistorySetup.execute();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(HistoryActivity.this, MainActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_history) {
            drawer.closeDrawers();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(HistoryActivity.this, SettingsActivity.class));
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_about_us) {
            drawer.closeDrawer(GravityCompat.START);

        }

        return true;
    }

    public void initSpinner(){
        spinner.setOnItemSelectedListener(spinnerHandler);
    }

    AdapterView.OnItemSelectedListener spinnerHandler = new AdapterView.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            switch (pos) {
                case 0:
                    displayCarbonFootprintGraph();
                    break;
                case 1:
                    displayDistanceGraph();
                    break;
                case 2:
                    displayEnergyConsumptionGraph();
                    break;
            }

            stackBarChart.invalidate();
            labelOrganizer.invalidate();
            yaxis.invalidate();

        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

    };

    protected void setUpNavigationView(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.title_nav_header)).setText(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_key_personal_name", ""));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.subtitle_nav_header)).setText(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_key_personal_email", ""));
        ImageView profilePicture = navigationView.getHeaderView(0).findViewById(R.id.imageView);

        Uri imageUri = null;
        try {
            imageUri = Uri.parse(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_key_profile_picture", "android.resource://com.example.desent.desent/drawable/earth"));
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(imageUri));
            profilePicture.setImageBitmap(Utility.getCroppedBitmap(bitmap));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ArrayList<String> getWeekLabels(){
        ArrayList<String> weekLabels = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        String[] ref = getResources().getStringArray(R.array.week_days);

        for (int i=7; i>0; i--){
            weekLabels.add(ref[(today-i)>=0 ? today-i : today-i+7]);
        }

        return weekLabels;
    }

    public void displayCarbonFootprintGraph() {

        List<ChartData> value = new ArrayList<>();

        float[] carbonFootprintTransportation = transportation.getWeekCarbonFootprint();
        float[] carbonFootprintEnergy = energy.generateArrayWeekCarbonFootprint();

        Float[] value1 = new Float[7];
        Float[] value2 = new Float[7];
        for (int i=0; i<7; i++) {
            value1[i] = carbonFootprintEnergy[i];
            value2[i] = carbonFootprintTransportation[i];
        }

        String barColor1 = "#03a9f4";
        String barColor2 = "#64dd17";

        String labelText1 = "Energy";
        String labelText2 = "Transportation"; //TODO: string


        value.add(new ChartData(value1, labelText1, barColor1));
        value.add(new ChartData(value2, labelText2, barColor2));

        List<String> h_labels = getWeekLabels();

        stackBarChart.setHorizontal_label(h_labels);
        stackBarChart.setBarIndent(50);
        stackBarChart.setDecimalsNumber(1);
        Log.i(LOGG, "før setData");

        stackBarChart.setData(value);

        labelOrganizer.clear();

        labelOrganizer = (StackedBarLabel) findViewById(R.id.labelStackedBar);
        // Set color on labels
        labelOrganizer.addColorLabels(barColor1);
        labelOrganizer.addColorLabels(barColor2);

        // Set label text
        labelOrganizer.addLabelText(labelText1);
        labelOrganizer.addLabelText(labelText2);

        yaxis = (Yaxis) findViewById(R.id.y_axis);
        yaxis.setBorder(60);
        yaxis.setFirstValueSet(value);

    }

    public void displayEnergyConsumptionGraph() {

        List<ChartData> value = new ArrayList<>();

        float[] weekEnergyConsumption = energy.generateArrayWeekEnergyConsumption();
        Float[] value1 = new Float[7];
        for (int i=0; i<7; i++)
            value1[i] = weekEnergyConsumption[i];

        String barColor1 = "#FF53BCD6";

        String labelText1 = "Energy consumption (kWh)";


        value.add(new ChartData(value1, labelText1, barColor1));

        List<String> h_labels = getWeekLabels();

        stackBarChart.setHorizontal_label(h_labels);
        stackBarChart.setBarIndent(50);
        Log.i(LOGG, "før setData");

        stackBarChart.setData(value);
        stackBarChart.setDecimalsNumber(1);

        labelOrganizer.clear();

        labelOrganizer = (StackedBarLabel) findViewById(R.id.labelStackedBar);
        // Set color on labels
        labelOrganizer.addColorLabels(barColor1);

        // Set label text
        labelOrganizer.addLabelText(labelText1);

        yaxis = (Yaxis) findViewById(R.id.y_axis);
        yaxis.setBorder(60);
        yaxis.setFirstValueSet(value);

    }

    public void displayDistanceGraph() {

        List<ChartData> value = new ArrayList<>();

        float[] walkingDistance = myDb.getWeekWalkingDistance();
        float[] cyclingDistance = myDb.getWeekCyclingDistance();
        float[] drivingDistance = myDb.getWeekDrivingDistance();

        Float[] value1 = new Float[7];
        Float[] value2 = new Float[7];
        Float[] value3 = new Float[7];

        for (int i=0; i<7; i++) {
            value1[i] = walkingDistance[i];
            value2[i] = cyclingDistance[i];
            value3[i] = drivingDistance[i];
        }

        String barColor1 = "#00ff00";
        String barColor2 = "#4f8714";
        String barColor3 = "#875c14";

        String labelText1 = "Walking";
        String labelText2 = "Cycling";
        String labelText3 = "Driving";


        value.add(new ChartData(value1, labelText1, barColor1));
        value.add(new ChartData(value2, labelText2, barColor2));
        value.add(new ChartData(value3, labelText3, barColor3));

        List<String> h_labels = getWeekLabels();

        stackBarChart.setHorizontal_label(h_labels);
        stackBarChart.setBarIndent(50);
        Log.i(LOGG, "før setData");

        stackBarChart.setData(value);
        stackBarChart.setDecimalsNumber(1);

        // stackBarChart.setDescription("Travel distance");

        labelOrganizer.clear();

        // Set color on labels
        labelOrganizer.addColorLabels(barColor1);
        labelOrganizer.addColorLabels(barColor2);
        labelOrganizer.addColorLabels(barColor3);


        // Set label text
        labelOrganizer.addLabelText(labelText1);
        labelOrganizer.addLabelText(labelText2);
        labelOrganizer.addLabelText(labelText3);

        yaxis.setBorder(60);
        yaxis.setFirstValueSet(value);
    }

    public Energy getEnergy() {
        return energy;
    }

    public void setEnergy(Energy energy) {
        this.energy = energy;
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }
}
