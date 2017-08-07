package com.example.desent.desent.utils;

import android.database.Cursor;
import android.util.Log;

import com.example.desent.desent.models.VehicleCost;
import com.jjoe64.graphview.series.DataPoint;

/**
 * Created by magnust on 09.06.2017.
 */

public class GraphPoints {
    private static String LOGG = "GraphPoints";
    private Cursor curs;
    private double maxY = 0;
    private DataPoint[] pointsToGraph;
    private boolean firstRun = true;

    // Constructor
    public GraphPoints(Cursor newCurs){
        // Get cursor from db
        curs = newCurs;
        Log.i(LOGG, "gp created" );


        DataPoint[] values = new DataPoint[curs.getCount()+1];
        Log.i(LOGG, "The number of data points " + String.format("%.1f", (double)curs.getCount()));
        int i = 0;

        // prepare the cursor by placing in it in the initial pos.
        //curs.moveToFirst();

        // get points and calculate the maximum Y-value
        while (curs.moveToNext()) {

            if(firstRun && !curs.isFirst()) {
                curs.moveToFirst();
            }
            firstRun = false;

            Log.i(LOGG, "Inside WHILE");
            double x = (double) curs.getFloat(1);
            double y = (double) curs.getFloat(2);
            double testY = (double) curs.getFloat(2);
            DataPoint dp = new DataPoint(x, y);

            Log.i(LOGG, "dataPoint: " + String.format("%.1f", x) + " " + String.format("%.1f", y));
            values[i] = dp;

            Log.i(LOGG, "Iteration nr.:" + i + " in WHILE");
            i++;

            // Spare the highest Y-value
            if (testY >= maxY) {
                maxY = testY;
            }

        }

        values[i] = new DataPoint(i+1, 0.0);
        firstRun = true;
        maxY += 1;
        this.pointsToGraph = values;
        Log.i(LOGG, "Points generated and the max Y-value calculated" );

    }


    public double getMaxY(){


        return maxY;
    }


    public DataPoint[] generatePoints() {


        return this.pointsToGraph;
    }
}
