package com.example.desent.desent.utils;

import android.database.Cursor;
import android.util.Log;

import com.example.desent.desent.models.DatabaseHelper;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by magnu on 19.03.2018.
 */


public class PostProcessData {

    private DatabaseHelper db;
    private File csv;

    public PostProcessData(DatabaseHelper db) {
        this.db = db;
    }

    public String getCSV() {
        Cursor curCSV = db.getDesentData();
        String csv = "Desent data collector\n";
        String firstRowRow = "Time,Longitude,Latitude,Speed,AccelerationX,AccelerationY,AccelerationZ,activity\n";
        csv = csv + firstRowRow;

        while(curCSV.moveToNext()) {
            long time = curCSV.getLong(0);
            double longitude = curCSV.getDouble(1);
            double latitude = curCSV.getDouble(2);
            float speed = curCSV.getFloat(3);
            float accX = curCSV.getFloat(4);
            float accY = curCSV.getFloat(5);
            float accZ = curCSV.getFloat(6);
            String activity = curCSV.getString(7);

            String strRow = String.valueOf(time)+ "," +String.valueOf(longitude)+ "," +String.valueOf(latitude)+ ","
                    +String.valueOf(speed)+ "," +String.valueOf(accX)+","
                    +String.valueOf(accY)+ "," +String.valueOf(accZ)+ ","
                    + activity+"\n";


            csv = csv + strRow;
        }

        curCSV.close();


        return csv;
    }
}
