package com.example.desent.desent.models;

/**
 * Created by magnust on 06.07.2017.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by magnust on 04.07.2017.
 */

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

    // Table names
    public static final String TABLE_NAME = "USERINPUT";
    public static final String TABLE_DISTANCE = "DISTANCETRACKER";

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


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" + UI_COL_2 + " TEXT PRIMARY KEY," + UI_COL_3 + " TEXT," + UI_COL_4 + " TEXT," + UI_COL_5 + " TEXT, " + UI_COL_6 + " TEXT)");
        db.execSQL("create table " + TABLE_DISTANCE + " (" + D_COL_1 + " TEXT PRIMARY KEY," + D_COL_2 + " FLOAT," + D_COL_3 + " FLOAT," + D_COL_4 + " FLOAT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
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

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
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

    public Integer deleteData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "NAME = ?", new String[]{name});
    }

    private boolean checkIfEmpty(SQLiteDatabase db, String tableName){
        String count = "SELECT count(*) FROM " + tableName;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);

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
}