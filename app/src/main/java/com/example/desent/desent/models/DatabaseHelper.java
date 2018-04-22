package com.example.desent.desent.models;

/**
 * Created by magnust on 04.07.2017.
 */


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.desent.desent.utils.SendMailTask;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private boolean firstRun = true;
    private static final String LOG = "DatabaseHelper";
    public static final String DATABASE_NAME = "ActivityLog.db";
    private float walkingDistance = 0;
    private float cyclingDistance = 0;
    private float drivingDistance = 0;

    // Check date
    private int checkDateCounter = 0;
    private final static int CHECK_DATE_INTERVAL = 5;
    private String date = checkTheDate();
    private boolean checkDate = true;
    private Cursor lastDateInRow;
    private String oldDate;

    // Submit data
    int submitInterval = 10080; //minutes (1 week)
    private SharedPreferences prefs;

    // Table names
    public static final String TABLE_NAME = "USERINPUT";
    public static final String TABLE_DISTANCE = "DISTANCETRACKER";
    public static final String TABLE_ENERGY = "ENERGY";
    public static final String TABLE_HOME = "HOME";
    public static final String TABLE_FORECAST = "FORECAST";
    public static final String TABLE_DESENT_DATA = "DESENT_DATA";
    public static final String TABLE_SUBMIT_DATA = "SUBMIT_DATA";

    // COL's for TABLE_DISTANCE
    public static final String D_COL_1 = "DATE";
    public static final String D_COL_2 = "WALKING";
    public static final String D_COL_3 = "CYCLING";
    public static final String D_COL_4 = "DRIVING";


    // COL's for TABLE_NAME
    // public static final String UI_COL_1 = "ID";
    public static final String UI_COL_2 = "NAME";
    public static final String UI_COL_3 = "SURNAME";
    public static final String UI_COL_4 = "WEIGHT";
    public static final String UI_COL_5 = "CAR_MAKE";
    public static final String UI_COL_6 = "YEARLY_ELECTRICITY_CONSUMPTION";

    // Table columns for TABLE_ENERGY
    private static final String EN_COL0 = "ID";
    private static final String EN_COL1 = "HOUR_OF_YEAR";
    private static final String EN_COL2 = "ELECTRICITY_PRICE";
    private static final String EN_COL3 = "PV_OUTPUT_1KW_CLEAR_SKY"; // used to scale with cloudiness to get actual profile
    private static final String EN_COL4 = "DEFAULT_ELECTRICITY_LOAD"; // electricity without heat
    // columns for actual load without pv
    private static final String EN_COL5 = "heat_load"; // calculated from real time weather values
    private static final String EN_COL6 = "heat_load_forecast"; // calculated from forecast weather values

    // COL's for TABLE_HOME
    public static final String H_COL_1 = "HOME_LAT";
    public static final String H_COL_2 = "HOME_LON";
    public static final String H_COL_3 = "TEMP";
    public static final String H_COL_4 = "COUNTRY";
    public static final String H_COL_5 = "CITY";

    // Forecast table columns
    private static final String FO_COL0 = "ID";
    private static final String FO_COL1 = "HOUR_OF_YEAR";
    private static final String FO_COL2 = "TEMPERATURE_FORECAST";
    private static final String FO_COL3 = "CLOUDS_FORECAST";
    private static final String FO_COL4 = "IRRADIANCE_FORECAST";

    // DESENT_DATA table columns
    private static final String DD_TIME = "TIME";
    private static final String DD_LONGITUDE = "LONGITUDE";
    private static final String DD_LATITUDE = "LATITUDE";
    private static final String DD_SPEED = "SPEED";
    private static final String DD_ACCELERATION_X = "ACCELERATION_X";
    private static final String DD_ACCELERATION_Y = "ACCELERATION_Y";
    private static final String DD_ACCELERATION_Z = "ACCELERATION_Z";
    private static final String DD_ACTIVITY = "ACTIVITY";

    // SUBMIT_DATA tabel columns
    private static final String SD_ID = "ID";
    private static final String SD_LAST_SUBMIT = "LAST_SUBMIT";
    private static final String SD_NEXT_SUBMIT = "NEXT_SUBMIT";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" + UI_COL_2 + " TEXT PRIMARY KEY," + UI_COL_3 + " TEXT," + UI_COL_4 + " TEXT," + UI_COL_5 + " TEXT, " + UI_COL_6 + " TEXT)");
        db.execSQL("create table " + TABLE_DISTANCE + " (" + D_COL_1 + " TEXT PRIMARY KEY," + D_COL_2 + " FLOAT," + D_COL_3 + " FLOAT," + D_COL_4 + " FLOAT)");
        db.execSQL("create table " + TABLE_HOME + " (" + H_COL_1 + " TEXT PRIMARY KEY," + H_COL_2 +
                " TEXT," + H_COL_3 + " TEXT,"+ H_COL_4 + " TEXT,"+ H_COL_5 + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_ENERGY + "("
                + EN_COL0  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EN_COL1  + " NUMERIC, "
                + EN_COL2  + " NUMERIC, "
                + EN_COL3  + " NUMERIC, "
                + EN_COL4  + " NUMERIC, "
                + EN_COL5  + " NUMERIC, "
                + EN_COL6  + " NUMERIC )");
        db.execSQL("CREATE TABLE " + TABLE_FORECAST + "("
                + FO_COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FO_COL1 + " NUMERIC, "
                + FO_COL2 + " NUMERIC, "
                + FO_COL3 + " NUMERIC, "
                + FO_COL4 + " NUMERIC )");
        db.execSQL("CREATE TABLE " + TABLE_DESENT_DATA + "("
                + DD_TIME + " INTEGER PRIMARY KEY , "
                + DD_LONGITUDE + " NUMERIC, "
                + DD_LATITUDE + " NUMERIC, "
                + DD_SPEED + " NUMERIC, "
                + DD_ACCELERATION_X + " NUMERIC, "
                + DD_ACCELERATION_Y + " NUMERIC, "
                + DD_ACCELERATION_Z + " NUMERIC, "
                + DD_ACTIVITY + " NUMERIC )");
        db.execSQL("CREATE TABLE " + TABLE_SUBMIT_DATA + "("
                + SD_ID + " INTEGER PRIMARY KEY , "
                + SD_LAST_SUBMIT + " INTEGER, "
                + SD_NEXT_SUBMIT + " INTEGER )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENERGY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORECAST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESENT_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBMIT_DATA);
        onCreate(db);
    }

    public boolean insertData(String name, String surname, String weight, String carMake, String elCons) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (checkIfEmpty(db, TABLE_NAME)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(UI_COL_2, name);
            contentValues.put(UI_COL_3, surname);
            contentValues.put(UI_COL_4, weight);
            contentValues.put(UI_COL_5, carMake);
            contentValues.put(UI_COL_6, elCons);
            Log.i(LOG, "Insert data ok, put values");
            long result = db.insert(TABLE_NAME, null, contentValues);
            if (result == -1)
                return false;
            else
                Log.i(LOG, "True returned");
            return true;
        } else {
            // the table is not empty and, it is not possible to insert a new row!
            return false;
        }
    }

    public boolean insertDistance(String activity, Float distance) {
        SQLiteDatabase db = this.getWritableDatabase();
        checkDate();
        // Check if it is necessary to check time
        if(checkDate){
            // Check date
            date = checkTheDate();
            checkDate = false;
            Log.i(LOG, "Current date: " + date);
            // Check if the date row exists
            lastDateInRow = db.rawQuery("select * from " + TABLE_DISTANCE + " WHERE DATE"  +
                    "='" + date + "'", null);
            int cursorHasResults = lastDateInRow.getCount();
            Log.i(LOG, "cursorHasResult = " + String.format("%.1f", (double)cursorHasResults));
            // Note: if the cursor doesn't have results, the method makes first entry and return
            if(cursorHasResults > 0){
                lastDateInRow.moveToLast();
                oldDate = lastDateInRow.getString(lastDateInRow.getColumnIndex("DATE"));
            }else {
                // This is the exception, only valid first time
                // Next time check date again to get correct oldDate and cursor object
                checkDate = true;

                // Create first row
                Log.i(LOG, "First time -> new insert");

                walkingDistance = 0;
                cyclingDistance = 0;
                drivingDistance = 0;

                // Have to check activity
                ContentValues contentValues = new ContentValues();

                if (activity.equals("WALKING")){
                    // The activity is walking
                    walkingDistance += distance;
                    contentValues.put(D_COL_1, date);
                    contentValues.put(D_COL_2, walkingDistance);
                    contentValues.put(D_COL_3, cyclingDistance);
                    contentValues.put(D_COL_4, drivingDistance);

                    long result = db.insert(TABLE_DISTANCE, null, contentValues);

                    if (result == -1) {
                        return false;
                    }
                    else {
                        Log.i(LOG, "First WALKING detected");
                        return true;
                    }
                } else if (activity.equals("CYCLING")){
                    // The activity is driving
                    cyclingDistance += distance;
                    contentValues.put(D_COL_1, date);
                    contentValues.put(D_COL_2, walkingDistance);
                    contentValues.put(D_COL_3, cyclingDistance);
                    contentValues.put(D_COL_4, drivingDistance);

                    long result = db.insert(TABLE_DISTANCE, null, contentValues);
                    if (result == -1) {
                        return false;
                    }
                    else {
                        Log.i(LOG, "First CYCLING detected");
                        return true;
                    }
                }else if (activity.equals("DRIVING")){
                    // The activity is driving

                    drivingDistance += distance;
                    contentValues.put(D_COL_1, date);
                    contentValues.put(D_COL_2, walkingDistance);
                    contentValues.put(D_COL_3, cyclingDistance);
                    contentValues.put(D_COL_4, drivingDistance);

                    long result = db.insert(TABLE_DISTANCE, null, contentValues);
                    if (result == -1) {

                        return false;
                    }
                    else {
                        Log.i(LOG, "First DRIVING detected");

                        return true;
                    }
                }
                return false;
            }
        }

        if(!oldDate.equals(date)){
            // New day
            Log.i(LOG, "(oldDate != date) -> new insert");
            walkingDistance = 0;
            cyclingDistance = 0;
            drivingDistance = 0;
            ContentValues contentValues = new ContentValues();

            // Create row of the day
            Log.i(LOG, "First time today -> new insert");

            // Have to check activity
            if (activity.equals("WALKING")){
                // The activity is walking
                walkingDistance += distance;
                contentValues.put(D_COL_1, date);
                contentValues.put(D_COL_2, walkingDistance);
                contentValues.put(D_COL_3, cyclingDistance);
                contentValues.put(D_COL_4, drivingDistance);
                long result = db.insert(TABLE_DISTANCE, null, contentValues);

                if (result == -1) {
                    return false;
                }
                else {
                    Log.i(LOG, "New day first WALKING detected");
                    return true;
                }

            } else if (activity.equals("CYCLING")){
                // The activity is driving
                cyclingDistance += distance;
                contentValues.put(D_COL_1, date);
                contentValues.put(D_COL_2, walkingDistance);
                contentValues.put(D_COL_3, cyclingDistance);
                contentValues.put(D_COL_4, drivingDistance);

                long result = db.insert(TABLE_DISTANCE, null, contentValues);

                if (result == -1) {
                    return false;
                }
                else {
                    Log.i(LOG, "New day first CYCLING detected");
                    return true;
                }
            }else if (activity.equals("DRIVING")){
                // The activity is driving
                drivingDistance += distance;
                contentValues.put(D_COL_1, date);
                contentValues.put(D_COL_2, walkingDistance);
                contentValues.put(D_COL_3, cyclingDistance);
                contentValues.put(D_COL_4, drivingDistance);

                long result = db.insert(TABLE_DISTANCE, null, contentValues);

                if (result == -1) {

                    return false;
                }
                else {
                    Log.i(LOG, "New day first DRIVING detected");

                    return true;
                }
            }

            return false;

        }else{
            if(firstRun){
                Log.i(LOG,"Inside firstRun");
                // Updating the values
                walkingDistance = lastDateInRow.getFloat(1);
                cyclingDistance = lastDateInRow.getFloat(2);
                drivingDistance = lastDateInRow.getFloat(3);
                firstRun = false;
            }
            // Date has not changed update the row
            Log.i(LOG, "oldDate == date -> update");

            // Check which activity is active
            if (activity.equals("WALKING")){
                // The activity is walking
                walkingDistance += distance;
                ContentValues contentValues = new ContentValues();
                contentValues.put(D_COL_2, walkingDistance);

                db.update(TABLE_DISTANCE, contentValues,"DATE = ?" , new String[]{date});
                return true;
            }
            else if (activity.equals("CYCLING")){
                // The activity is cycling
                cyclingDistance += distance;
                ContentValues contentValues = new ContentValues();
                contentValues.put(D_COL_3, cyclingDistance);

                db.update(TABLE_DISTANCE, contentValues,"DATE = ?" , new String[]{date});
                return true;
            }else if (activity.equals("DRIVING")){
                // The activity is driving
                drivingDistance += distance;
                ContentValues contentValues = new ContentValues();
                contentValues.put(D_COL_4, drivingDistance);

                db.update(TABLE_DISTANCE, contentValues,"DATE = ?" , new String[]{date});
                return true;
            }

            return false;
        }
    }

    public boolean insertEnergyData(double[] item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(EN_COL1, item[0]);
        contentValues.put(EN_COL2, item[1]);
        contentValues.put(EN_COL3, item[2]);
        contentValues.put(EN_COL4, item[3]);
        contentValues.put(EN_COL5, item[4]);
        contentValues.put(EN_COL6, item[5]);

        //Log.d(TAG, "addData: Adding " + item[2] + " to " + TABLE_NAME);

        long result = db.insert(TABLE_ENERGY, null, contentValues);
        db.close();

        return result != -1;
    }

    public boolean insertForecastData(double[] item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FO_COL1, item[0]);
        contentValues.put(FO_COL2, item[1]);
        contentValues.put(FO_COL3, item[2]);
        contentValues.put(FO_COL4, item[3]);

        //Log.d(TAG, "addData: Adding " + item[2] + " to " + TABLE_NAME);

        long result = db.insert(TABLE_FORECAST, null, contentValues);
        db.close();

        return result != -1;
    }

    public boolean insertDesentData(long time, double longitude, double latitude, float speed, float accX, float accY, float accZ, String activity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DD_TIME, time);
        contentValues.put(DD_LONGITUDE, longitude);
        contentValues.put(DD_LATITUDE, latitude);
        contentValues.put(DD_SPEED, speed);
        contentValues.put(DD_ACCELERATION_X, accX);
        contentValues.put(DD_ACCELERATION_Y, accY);
        contentValues.put(DD_ACCELERATION_Z, accZ);
        contentValues.put(DD_ACTIVITY, activity);

        Log.d("insertDesentData", "time: " + String.valueOf(time) +
                " longitude: " + String.valueOf(longitude) +
                " latitude: " + String.valueOf(latitude) +
                " speed: " + String.valueOf(speed) +
                " accX: " + String.valueOf(accX) +
                " accY: " + String.valueOf(accY) +
                " accZ: " + String.valueOf(accZ) +
                " Activity: " + activity);

        long result = db.insert(TABLE_DESENT_DATA, null, contentValues);
        db.close();

        return result != -1;
    }

    public Cursor getDesentData() {
        Cursor res;
        SQLiteDatabase db = this.getWritableDatabase();
        String tabName = TABLE_DESENT_DATA;
        if(checkIfEmpty(db, tabName)){
            //Table is empty
            res = null;

        }else{
            // Table is not empty
            res = tableToString(db, tabName);
        }

        return res;
    }

    public boolean submitDesentData(Activity activityContext) {
        SQLiteDatabase db = this.getWritableDatabase();
        prefs = PreferenceManager.getDefaultSharedPreferences(activityContext);
        boolean result;

        // First time
        if(checkIfEmpty(db, TABLE_SUBMIT_DATA)){
            //Add next submit date
            Calendar calendar = new GregorianCalendar();
            long currentTime = calendar.getTimeInMillis();
            calendar.add(GregorianCalendar.MINUTE, submitInterval);
            long nextSubmit = calendar.getTimeInMillis();

            ContentValues contentValues = new ContentValues();
            contentValues.put(SD_LAST_SUBMIT, currentTime);
            contentValues.put(SD_NEXT_SUBMIT, nextSubmit);

            long res = db.insert(TABLE_SUBMIT_DATA, null, contentValues);
            Log.i(LOG, "submitDesentData. First value inserted: " + String.valueOf(res));

            result = false;
            db.close();

        }else{
            // Table is not empty. Get current time.
            Calendar calendar = new GregorianCalendar();
            long currentTime = calendar.getTimeInMillis();

            // Fetch next submit date
            Cursor queryNextSubmit = db.rawQuery("select " + SD_NEXT_SUBMIT + " from " + TABLE_SUBMIT_DATA, null);
            queryNextSubmit.moveToFirst();
            long submit = queryNextSubmit.getLong(0);

            // Compare and check if it is time to send
            if(currentTime > submit){
                // Send data
                String fromEmail = "desent.data@gmail.com";
                String fromPassword = "datasubmitter";
                String emailSubject = prefs.getString("pref_key_personal_email", "Missing input");
                String emailBody = "";

                new SendMailTask(activityContext).execute(fromEmail,
                        fromPassword, emailSubject, emailBody, getCSV(activityContext));
                // Calculate and update new submit time
                calendar.add(GregorianCalendar.MINUTE, submitInterval);
                long nextSubmit = calendar.getTimeInMillis();

                ContentValues contentValues = new ContentValues();
                contentValues.put(SD_ID, 1);
                contentValues.put(SD_LAST_SUBMIT, currentTime);
                contentValues.put(SD_NEXT_SUBMIT, nextSubmit);
                int res = db.update(TABLE_SUBMIT_DATA, contentValues, "ID = ?", new String[]{"1"});
                Log.i(LOG, "submitDesentDataNext. submit updated: " + String.valueOf(res) + " Time: " + String.valueOf(nextSubmit));
                if(res > 0){
                    result = true;
                }else{
                    result = false;
                }
            }else{
                // Do nothing
                Log.i(LOG, "submitDesentData. Next submit not updated. currentTime < submit");
                result = false;
            }
        }

        return result;
    }

    public String getCSV(Activity activityContext) {
        String idEmail = prefs.getString("pref_key_personal_email", "Missing email from user");
        String csv = "Desent data collector\n\n";

        String heatType = prefs.getString("pref_key_heat_type", "Missing input");
        String heatEfficiency = prefs.getString("pref_key_heat_efficiency", "Missing input");
        String heatFuelCost = prefs.getString("pref_key_heat_fuel_cost", "Missing input");
        String heatLastYear = prefs.getString("pref_key_heat_prev_yr", "Missing input");

        String idRow = "ID," + idEmail + "\n\n";
        String heatHeader = "Heating system\nHeatType,Efficiency[%],FuelCost,ConsumptionLastYear\n";
        String heat = heatHeader + heatType + "," + heatEfficiency + "," + heatFuelCost + "," + heatLastYear +"\n";

        String firstRowRow = "\n\nData log:\nTime [ms],dd/MM/yyyy hh:mm:ss.SSS,Longitude,Latitude,Speed,AccelerationX,AccelerationY,AccelerationZ,activity\n";

        Cursor curCSV = getDesentData();

        csv = idRow + csv + heat + firstRowRow;

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();


        if(curCSV != null){
            while(curCSV.moveToNext()) {
                long time = curCSV.getLong(0);
                calendar.setTimeInMillis(time);
                String formattedDate = formatter.format(calendar.getTime());
                double longitude = curCSV.getDouble(1);
                double latitude = curCSV.getDouble(2);
                float speed = curCSV.getFloat(3);
                float accX = curCSV.getFloat(4);
                float accY = curCSV.getFloat(5);
                float accZ = curCSV.getFloat(6);
                String activity = curCSV.getString(7);

                String strRow = String.valueOf(time)+ "," + formattedDate + "," +String.valueOf(longitude)+ "," +String.valueOf(latitude)+ ","
                        +String.valueOf(speed)+ "," +String.valueOf(accX)+","
                        +String.valueOf(accY)+ "," +String.valueOf(accZ)+ ","
                        + activity+"\n";

                csv = csv + strRow;
            }
            curCSV.close();
        }

        return csv;
    }


    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }


    public Cursor getEnergyData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ENERGY;
        return db.rawQuery(query, null);
    }

    public Cursor getForecastData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FORECAST;
        return db.rawQuery(query, null);
    }

    public double getHeat(int hour) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + EN_COL5 + " FROM " + TABLE_ENERGY + " WHERE " + EN_COL1 + " = '" + String.valueOf(hour) + "'", null);
        cursor.moveToFirst();

        double heat = -100;
        if (cursor.getCount()>0) {
            cursor.moveToLast();
            heat = cursor.getFloat(0);
        }

        cursor.close();
        return heat;
    }

    public Cursor getDistance() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor distRes = db.rawQuery("select * from " + TABLE_DISTANCE, null);
        return distRes;
    }

    public float getWalkingDistanceToday() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select " + D_COL_2 + " from " + TABLE_DISTANCE + " where "+ D_COL_1 +" = '" + date + "'", null);
        cursor.moveToFirst();
        float walkedToday;

        if (cursor.getCount()>0){
            cursor.moveToLast();
            walkedToday = cursor.getFloat(0);
        }else{
            walkedToday = 0;
        }
        return walkedToday;
    }

    public float getWalkingDistance(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select " + D_COL_2 + " from " + TABLE_DISTANCE + " where "+ D_COL_1 +" = '" + date + "'", null);
        cursor.moveToFirst();
        float walkedToday;

        if (cursor.getCount()>0){
            cursor.moveToLast();
            walkedToday = cursor.getFloat(0);
        }else{
            walkedToday = 0;
        }
        return walkedToday;
    }

    public float[] getWeekWalkingDistance() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        int length = 7;

        float[] weekWalkingDistance = new float[length];
        weekWalkingDistance[length-1] = getWalkingDistance(df.format(calendar.getTime()));
        for (int i = 1; i<length; i++){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            weekWalkingDistance[length-i-1] = getWalkingDistance(df.format(calendar.getTime()));
        }

        return weekWalkingDistance;

    }

    public float getWeekAverageWalkingDistance() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        int length = calendar.get(Calendar.DAY_OF_WEEK);

        float weekWalkingDistance = getWalkingDistance(df.format(calendar.getTime()));
        for (int i = 1; i<length; i++){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            weekWalkingDistance += getDrivingDistance(df.format(calendar.getTime()));
        }

        return weekWalkingDistance/length;

    }

    public float getMonthAverageWalkingDistance() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        int length = calendar.get(Calendar.DAY_OF_MONTH);

        float monthWalkingDistance = getDrivingDistance(df.format(calendar.getTime()));
        for (int i = 1; i<length; i++){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            monthWalkingDistance += getWalkingDistance(df.format(calendar.getTime()));
        }

        return monthWalkingDistance/length;

    }

    public float getCyclingDistanceToday() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select " + D_COL_3 +" from " + TABLE_DISTANCE + " where "+ D_COL_1 +" = '" + date + "'", null);cursor.moveToFirst();
        float cycleToday;

        if (cursor.getCount()>0){
            cursor.moveToLast();
            cycleToday = cursor.getFloat(0);
        }else{
            cycleToday = 0;
        }
        return cycleToday;
    }

    public float getCyclingDistance(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select " + D_COL_3 +" from " + TABLE_DISTANCE + " where "+ D_COL_1 +" = '" + date + "'", null);
        cursor.moveToFirst();
        float cycleToday;

        if (cursor.getCount()>0){
            cursor.moveToLast();
            cycleToday = cursor.getFloat(0);
        }else{
            cycleToday = 0;
        }
        return cycleToday;
    }

    public float[] getWeekCyclingDistance() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        int length = 7;

        float[] weekCyclingDistance = new float[length];
        weekCyclingDistance[length-1] = getCyclingDistance(df.format(calendar.getTime()));
        for (int i = 1; i<length; i++){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            weekCyclingDistance[length-i-1] = getCyclingDistance(df.format(calendar.getTime()));
        }

        return weekCyclingDistance;

    }

    public float getWeekAverageCyclingDistance() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        int length = calendar.get(Calendar.DAY_OF_WEEK);

        float weekCyclingDistance = getDrivingDistance(df.format(calendar.getTime()));
        for (int i = 1; i<length; i++){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            weekCyclingDistance += getWalkingDistance(df.format(calendar.getTime()));
        }

        return weekCyclingDistance/length;

    }

    public float getMonthAverageCyclingDistance() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        int length = calendar.get(Calendar.DAY_OF_MONTH);

        float monthWalkingDistance = getDrivingDistance(df.format(calendar.getTime()));
        for (int i = 1; i<length; i++){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            monthWalkingDistance += getCyclingDistance(df.format(calendar.getTime()));
        }

        return monthWalkingDistance/length;

    }

    public float getDrivingDistanceToday() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select " + D_COL_4 +" from " + TABLE_DISTANCE + " where "+ D_COL_1 +" = '" + date + "'", null);
        float drivenToday;
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            cursor.moveToLast();
            drivenToday = cursor.getFloat(0);
        }else{
            drivenToday = 0;
        }
        return drivenToday;
    }

    public float getDrivingDistance(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select " + D_COL_4 +" from " + TABLE_DISTANCE + " where "+ D_COL_1 +" = '" + date + "'", null);
        float drivingDistance;
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            cursor.moveToLast();
            drivingDistance = cursor.getFloat(0);
        }else{
            drivingDistance = 0;
        }
        return drivingDistance;
    }

    public float[] getWeekDrivingDistance() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        int length = 7;

        float[] weekDrivingDistance = new float[length];
        weekDrivingDistance[length-1] = getDrivingDistance(df.format(calendar.getTime()));
        for (int i = 1; i<length; i++){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            weekDrivingDistance[length-i-1] = getDrivingDistance(df.format(calendar.getTime()));
        }

        return weekDrivingDistance;

    }

    public float getWeekAverageDrivingDistance() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        int length = calendar.get(Calendar.DAY_OF_WEEK);

        float weekDrivingDistance = getDrivingDistance(df.format(calendar.getTime()));
        for (int i = 1; i<length; i++){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            weekDrivingDistance += getDrivingDistance(df.format(calendar.getTime()));
        }

        return weekDrivingDistance/length;

    }

    public float getMonthAverageDrivingDistance() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        int length = calendar.get(Calendar.DAY_OF_MONTH);

        float weekDrivingDistance = getDrivingDistance(df.format(calendar.getTime()));
        for (int i = 1; i<length; i++){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            weekDrivingDistance += getDrivingDistance(df.format(calendar.getTime()));
        }

        return weekDrivingDistance/length;

    }

    public boolean updateData(String name, String surname, String weight, String carMake, String elCons) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UI_COL_2, name);
        contentValues.put(UI_COL_3, surname);
        contentValues.put(UI_COL_4, weight);
        contentValues.put(UI_COL_5, carMake);
        contentValues.put(UI_COL_6, elCons);
        db.update(TABLE_NAME, contentValues, "NAME = ?", new String[]{name});
        return true;
    }

    public boolean updateEnergyData(double[] item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(EN_COL1, item[0]);
        contentValues.put(EN_COL2, item[1]);
        contentValues.put(EN_COL3, item[2]);
        contentValues.put(EN_COL4, item[3]);
        contentValues.put(EN_COL5, item[4]);
        contentValues.put(EN_COL6, item[5]);

        long result = db.update(TABLE_ENERGY, contentValues, "ID=" +1, null);
        db.close();

        return result > 0;
    }

    public boolean updateForecastData(double[] item, int position) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FO_COL1, item[0]);
        contentValues.put(FO_COL2, item[1]);
        contentValues.put(FO_COL3, item[2]);
        contentValues.put(FO_COL4, item[3]);

        long result = db.update(TABLE_FORECAST, contentValues, "ID=" +position, null);
        db.close();

        return result > 0;
    }

    public Integer deleteData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "NAME = ?", new String[]{name});
    }

    public boolean checkIfEmpty(SQLiteDatabase db, String tableName){
        String count = "SELECT count(*) FROM " + tableName;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        mcursor.close();
        // Table is not empty
        if(icount>0){
            return false;
        }
        // Table is empty
        else{
            return true;
        }
    }

    private String checkTheDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    private void checkDate(){
        checkDateCounter += 1;
        if (checkDateCounter > CHECK_DATE_INTERVAL){
            checkDate = true;
            checkDateCounter = 0;
        }
    }

    // Weather info
    public String getWeatherLocation() {
        String res;
        SQLiteDatabase db = this.getWritableDatabase();
        if(checkIfEmpty(db,TABLE_HOME)){
            //Table is empty - give default Trondheim weather
            res = "lat=63.4&lon=10.4";

        }else{
            // Table is not empty
            Cursor cursor = db.rawQuery("select * from " + TABLE_HOME, null);
            cursor.moveToFirst();
            res = "lat=" + cursor.getString(0) + "&lon=" + cursor.getString(1);
            cursor.close();
        }

        return res;
    }

    public boolean cityEqualsHomeTown(String city){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_HOME, null);
        cursor.moveToFirst();
        String homeTown = cursor.getString(4).trim();
        Log.i(LOG, "hometown = " + homeTown);
        Log.i(LOG, "current city = " + city);
        cursor.close();
        if(homeTown.equals(city)){
            return true;
        }else{
            return false;
        }
    }

    public boolean isNorway(){
        SQLiteDatabase db = this.getWritableDatabase();
        String country;
        boolean isNorway = true;
        Cursor cursor = db.rawQuery("select * from " + TABLE_HOME, null);

        if(checkIfEmpty(db, TABLE_HOME) ){
            country = "Norway";
        }else{
            cursor.moveToFirst();
            country = cursor.getString(3).trim();
        }

        if(country.equals("Norway")){
            isNorway = true;
        }else{
            isNorway = false;
        }
        cursor.close();
        return isNorway;
    }

    public boolean insertWeatherLocation(String lat, String lon, String temp, String country, String city) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (checkIfEmpty(db, TABLE_HOME)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(H_COL_1, lat);
            contentValues.put(H_COL_2, lon);
            contentValues.put(H_COL_3, temp);
            contentValues.put(H_COL_4, country);
            contentValues.put(H_COL_5, city);
            Log.i(LOG, "WeatherLocation inserted");
            long result = db.insert(TABLE_HOME, null, contentValues);
            if (result == -1)
                return false;
            else
                Log.i(LOG, "True returned");
            return true;
        } else {
            // the table is not empty and, it is not possible to insert a new row!
            return false;
        }
    }

    public boolean updateWeatherData(String lat, String lon, String country, String city) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(H_COL_1, lat);
        contentValues.put(H_COL_2, lon);
        contentValues.put(H_COL_3, "NO"); //Prevents app from updating again
        contentValues.put(H_COL_4, country);
        contentValues.put(H_COL_5, city);
        long result = db.update(TABLE_HOME, contentValues, "TEMP = ?", new String[]{"YES"});
        if (result == -1){
            Log.i(LOG, "False, table not updated");
            return false;
        }else{
            Log.i(LOG, "True, table updated");
            return true;
        }
    }

    public boolean existingWeatherData(){
        SQLiteDatabase db = this.getWritableDatabase();
        if (checkIfEmpty(db, TABLE_HOME)) {
            //empty table
            return false;
        } else {
            //Not empty table
            return true;
        }
    }

    public Cursor tableToString(SQLiteDatabase db, String tableName) {
        Log.d("","tableToString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);

        //tableString += cursorToString(allRows);
        //return tableString;
        return allRows;
    }

    public String cursorToString(Cursor cursor){
        String cursorString = "";
        if (cursor.moveToFirst() ){
            String[] columnNames = cursor.getColumnNames();
            for (String name: columnNames)
                cursorString += String.format("%s ][ ", name);
            cursorString += "\n";
            do {
                for (String name: columnNames) {
                    cursorString += String.format("%s ][ ",
                            cursor.getString(cursor.getColumnIndex(name)));
                }
                cursorString += "\n";
            } while (cursor.moveToNext());
        }
        return cursorString;
    }


}