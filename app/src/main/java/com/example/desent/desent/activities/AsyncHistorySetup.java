package com.example.desent.desent.activities;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.desent.desent.R;
import com.example.desent.desent.models.Energy;
import com.example.desent.desent.models.Transportation;
import com.example.desent.desent.utils.ChartData;
import com.example.desent.desent.views.StackBarChart;
import com.example.desent.desent.views.StackedBarLabel;
import com.example.desent.desent.views.Yaxis;

import java.util.ArrayList;
import java.util.List;

import static com.example.desent.desent.activities.HistoryActivity.LOGG;

/**
 * Created by celine on 04/08/17.
 */

public class AsyncHistorySetup extends AsyncTask {

    protected HistoryActivity historyActivity;
    View progressBar;

    protected Transportation transportation;
    protected Energy energy;

    protected StackBarChart stackBarChart;
    protected StackedBarLabel labelOrganizer;
    protected Yaxis yaxis;

    private List<String> h_labels;
    private String barColor1;
    private String barColor2;
    private List<ChartData> value;
    private String labelText1;
    private String labelText2;

    public AsyncHistorySetup(HistoryActivity historyActivity,
                             StackBarChart stackBarChart,
                             StackedBarLabel labelOrganizer,
                             Yaxis yaxis) {
        this.historyActivity = historyActivity;
        this.stackBarChart = stackBarChart;
        this.labelOrganizer = labelOrganizer;
        this.yaxis = yaxis;
    }

    @Override
    protected Object doInBackground(Object[] objects) {


        progressBar = historyActivity.findViewById(R.id.progress_bar);

        energy = new Energy(historyActivity);
        transportation = new Transportation(historyActivity);

        value = new ArrayList<>();

        float[] carbonFootprintTransportation = transportation.getWeekCarbonFootprint();
        float[] carbonFootprintEnergy = energy.generateArrayWeekCarbonFootprint();

        Float[] value1 = new Float[7];
        Float[] value2 = new Float[7];
        for (int i=0; i<7; i++) {
            value1[i] = carbonFootprintEnergy[i];
            value2[i] = carbonFootprintTransportation[i];
        }

        barColor1 = "#03a9f4";
        barColor2 = "#64dd17";

        labelText1 = "Energy";
        labelText2 = "Transportation"; //TODO: string


        value.add(new ChartData(value1, labelText1, barColor1));
        value.add(new ChartData(value2, labelText2, barColor2));

        h_labels = historyActivity.getWeekLabels();

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        progressBar.setVisibility(View.GONE);

        stackBarChart.setHorizontal_label(h_labels);
        stackBarChart.setBarIndent(50);
        stackBarChart.setDecimalsNumber(1);
        Log.i(LOGG, "før setData");

        stackBarChart.setData(value);
        stackBarChart.setVisibility(View.VISIBLE);

        labelOrganizer.clear();
        // Set color on labels
        labelOrganizer.addColorLabels(barColor1);
        labelOrganizer.addColorLabels(barColor2);

        // Set label text
        labelOrganizer.addLabelText(labelText1);
        labelOrganizer.addLabelText(labelText2);
        labelOrganizer.setVisibility(View.VISIBLE);

        yaxis.setBorder(60);
        yaxis.setFirstValueSet(value);
        yaxis.setVisibility(View.VISIBLE);

        historyActivity.setEnergy(energy);
        historyActivity.setTransportation(transportation);

        historyActivity.initSpinner();

    }

}
