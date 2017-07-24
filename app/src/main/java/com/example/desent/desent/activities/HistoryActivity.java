package com.example.desent.desent.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.example.desent.desent.R;
import com.example.desent.desent.models.DatabaseHelper;
import com.example.desent.desent.models.Indicator;
import com.example.desent.desent.utils.ChartData;
import com.example.desent.desent.utils.EstimationType;
import com.example.desent.desent.utils.GraphPoints;
import com.example.desent.desent.utils.TimeScale;
import com.example.desent.desent.views.StackBarChart;
import com.example.desent.desent.views.StackedBarLabel;
import com.example.desent.desent.views.Yaxis;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by celine on 20/07/17.
 */

public class HistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Spinner spinner;
    final static String LOGG = "HistoryPage";
    DatabaseHelper myDb;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        spinner = (Spinner) findViewById(R.id.history_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.history_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(spinnerHandler);

        myDb = new DatabaseHelper(this);
        displayGraph();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(HistoryActivity.this, MainActivity.class));
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(HistoryActivity.this, Settings.class));
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_about) {
            drawer.closeDrawer(GravityCompat.START);

        }

        return true;
    }

    AdapterView.OnItemSelectedListener spinnerHandler = new AdapterView.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

            //TODO: implement
            switch (pos) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

    };

    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    public void displayGraph() {

        Cursor dist = myDb.getDistance();
        if (dist.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        // Getting the datapoints for the graph
        GraphPoints gp = new GraphPoints(dist);
        double maxY = gp.getMaxY();
        DataPoint[] dp = gp.generatePoints();

        StackBarChart stackBarChart = (StackBarChart) findViewById(R.id.chart);


        List<ChartData> value = new ArrayList<>();

        Float[] value1 = {2f, 3f, 6f, 5f, 4f, 4f, 6f};
        Float[] value2 = {1f, 1f, 1f, 1f, 1f, 1f, 9f};
        Float[] value3 = {3f, 5f, 7f, 9f, 4f, 4f, 6f};

        String barColor1 = "#00ff00";
        String barColor2 = "#4f8714";
        String barColor3 = "#875c14";

        String labelText1 = "Walking";
        String labelText2 = "Cycling";
        String labelText3 = "Driving";


        value.add(new ChartData(value1, labelText1, barColor1));
        value.add(new ChartData(value2, labelText2, barColor2));
        value.add(new ChartData(value3, labelText3, barColor3));

        List<String> h_lables = new ArrayList<>();
        h_lables.add("sun");
        h_lables.add("mon");
        h_lables.add("tue");
        h_lables.add("wed");
        h_lables.add("thurs");
        h_lables.add("fri");
        h_lables.add("sat");

        stackBarChart.setHorizontal_label(h_lables);
        stackBarChart.setBarIndent(50);
        Log.i(LOGG, "før setData");

        stackBarChart.setData(value);

        // stackBarChart.setDescription("Travel distance");


        StackedBarLabel labelOrganizer = (StackedBarLabel) findViewById(R.id.labelStackedBar);
        // Set color on labels
        labelOrganizer.setColorLabels(barColor1);
        labelOrganizer.setColorLabels(barColor2);
        labelOrganizer.setColorLabels(barColor3);


        // Set label text
        labelOrganizer.setLabelText(labelText1);
        labelOrganizer.setLabelText(labelText2);
        labelOrganizer.setLabelText(labelText3);

        Yaxis yaxis = (Yaxis) findViewById(R.id.y_axis);
        yaxis.setBorder(60);
        yaxis.setFirstValueSet(value);
    }
}
