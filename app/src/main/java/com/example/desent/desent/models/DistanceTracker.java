package com.example.desent.desent.models;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by magnust on 06.07.2017.
 */


import com.example.desent.desent.activities.MainActivity;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by magnust on 04.07.2017.
 */

public class DistanceTracker extends MainActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{


    private MyFenceReceiver fenceReceiver;
    private PendingIntent mFencePendingIntent;
    private GoogleApiClient mGoogleApiClientAware;
    private static final String FENCE_RECEIVER_ACTION = "FENCE_RECEIVE";
    private static final String TAG = "Awareness";
    private HomeTown homeTown;

    // Current state
    private String currentActivity;
    boolean activeAccelerometer = false;
    private float accelerationX = 0;
    private float accelerationY = 0;
    private float accelerationZ = 0;
    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private final String LOG="DistanceTracking";
    private GoogleApiClient mGoogleApiClientLoc;
    private LocationRequest mLocationRequest;
    private static final int DELAY = 3000;
    private static final int SNAP_DELAY = 10000;
    private Location mLastLocation;
    private String activity;
    private DatabaseHelper myDb;
    private Context context;
    private Activity activityContext;
    private boolean isStill = true;
    private boolean activeGps = false;
    private Handler handler = new Handler();
    private boolean noFence = true;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
           Awareness.SnapshotApi.getDetectedActivity(mGoogleApiClientAware)
                    .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                        @Override
                        public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                            ActivityRecognitionResult result = detectedActivityResult.getActivityRecognitionResult();
                                Log.i(TAG, "time: " + result.getTime());
                                Log.i(TAG, "elapsed time: " + result.getElapsedRealtimeMillis());
                                String stringBuffer ="";
                                for( DetectedActivity activity : result.getProbableActivities() ) {
                                    Log.i(TAG, "Activity num.: " + activity.getType() + " Activity: " + translateActivity(activity.getType()) +
                                            " Likelihood: " + activity.getConfidence() );
                                    stringBuffer = stringBuffer + "\nActivity num.: " + activity.getType() +
                                            "\nActivity: " + translateActivity(activity.getType()) +
                                            "\nLikelihood: " + activity.getConfidence() + "\n";
                                }
                                Log.i(TAG,stringBuffer);
                            myDb.submitDesentData(activityContext);
                        }
                    });

      // Repeater
            handler.postDelayed(this, SNAP_DELAY);
        }
    };
    private String translateActivity(int number){
        String stringActivity;

        switch (number){
            case 0:
                stringActivity = "IN_VEHICLE";
                break;
            case 1:
                stringActivity = "ON_BICYCLE";
                break;
            case 2:
                stringActivity = "ON_FOOT";
                break;
            case 3:
                stringActivity = "STILL";
                break;
            case 4:
                stringActivity = "UNKNOWN";
                break;
            case 5:
                stringActivity = "5 - DOES NOT EXIST?";
                break;
            case 6:
                stringActivity = "6 - DOES NOT EXIST?";
                break;
            case 7:
                stringActivity = "WALKING";
                break;
            case 8:
                stringActivity = "RUNNING";
                break;
            default:
                stringActivity = "default";
                break;
        }

        return stringActivity;
    }


    public DistanceTracker(SensorManager mSensorManager, Sensor mAccelerometer, Activity activityContext, Context context){
        this.mSensorManager = mSensorManager;
        this.mAccelerometer = mAccelerometer;
        this.activityContext = activityContext;
        this.context = context;
        Log.i(LOG,"constructor");

        onCreate();
        String locationProvider = LocationManager.GPS_PROVIDER;
        Log.i(LOG,locationProvider);
        //startActivity(new Intent(android.provider.SettingsActivity.ACTION_LOCATION_SOURCE_SETTINGS))
    }


    protected void onCreate() {


        // Distance tracking
        Log.i(LOG, "Distance is initiated");
        if(noFence){
            mGoogleApiClientLoc = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();


            myDb = new DatabaseHelper(context);

            //Create a GoogleApiClient instance
            mGoogleApiClientAware = new GoogleApiClient.Builder(context)
                    .addApi(Awareness.API)
                    .build();
            mGoogleApiClientAware.connect();
            fenceReceiver = new MyFenceReceiver();
            Intent intent = new Intent(FENCE_RECEIVER_ACTION);
            mFencePendingIntent = PendingIntent.getBroadcast(context,
                    10001,
                    intent,
                    0);

            Log.i(TAG, "mFencePendingIntent finished");
        }else{
            Log.i(TAG, "Fence exists");
        }





    }

    public void start() {
        Log.e("OnSTART", "Before");
        if (!mGoogleApiClientLoc.isConnected()) {
            mGoogleApiClientLoc.connect();
        }
        Log.e("OnSTART", "Location is connected");
        if(noFence){
            registerFences();
        }

        // registerReceiver(fenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
    }


    public void stop() {
        if (mGoogleApiClientLoc.isConnected()) {
            mGoogleApiClientLoc.disconnect();
            handler.removeCallbacks(runnable);

        }
    }



    public MyFenceReceiver getFenceReceiver(){
        return fenceReceiver;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(DELAY);
        mLocationRequest.setFastestInterval(DELAY);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(LOG, "Request location");
            ActivityCompat.requestPermissions(activityContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            askForGps();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // check if accelerometer is activated or activate
        if(!activeAccelerometer){
            // Acceleration sensor
            //mInitialized = false;
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            activeAccelerometer = true;

        }
        double distance;
        if(mLastLocation == null){
            mLastLocation = location;
            distance = 0;
            Log.i(LOG, "mLastLocation == null");
        }else{
            homeTown = new HomeTown(activityContext, location);
            Log.i(TAG, homeTown.getWeatherLocation());

            Log.i(LOG, mLastLocation.toString());
            distance = mLastLocation.distanceTo(location)/1000; //meters to kilometers
            mLastLocation = location;
            activeGps = true;
            if(activity.equals("STILL")){
                disableGPS();
                //Turn off accelerometer if still and accelerometer is active
                if(activeAccelerometer){
                    mSensorManager.unregisterListener(this);
                    activeAccelerometer = false;
                }
                Log.i(LOG, "You are still");
            }else{
                if (!mGoogleApiClientLoc.isConnected()) {
                    Log.i(TAG, "Enable GPS");
                }
                boolean isInserted = myDb.insertDistance(activity, (float)distance);
                // Start datalog for DESENT
                long time = System.currentTimeMillis();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                float speed = location.getSpeed();


                boolean desentLog = myDb.insertDesentData(time, longitude, latitude, speed, accelerationX, accelerationY, accelerationZ, activity);
                if(isInserted == true){
                    String distToast = "Walking: " + String.format("%.1f", myDb.getWalkingDistanceToday()) + " Cycling: " + String.format("%.1f", myDb.getCyclingDistanceToday()) + " Driving: " +  String.format("%.1f", myDb.getDrivingDistanceToday());
                    Log.i(TAG, "Data inserted. " + distToast);
                    //Toast.makeText(context,"Data inserted. " + distToast,Toast.LENGTH_LONG).show();
                }
                else{
                    //Toast.makeText(context,"Data not inserted",Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG, "GoogleApiClient connection has been suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOG, "GoogleApiClient connection has failed");
        Toast.makeText(context, "ConnectionFailed", Toast.LENGTH_LONG).show();
    }

    public void setActivity(String activity){
        this.activity = activity;
        if (!mGoogleApiClientLoc.isConnected()) {
            Log.e(TAG, "Connecting location from setActivity");
            //mGoogleApiClientLoc.connect();
        }

    }

    public void initiateLocation(){
        Log.i(LOG, "Textview should be changed");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClientLoc, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClientLoc);


    }

    private void registerFences() {

        //Create fences
        AwarenessFence onFootFence = DetectedActivityFence.during(DetectedActivityFence.ON_FOOT);
        AwarenessFence stillFence = DetectedActivityFence.during(DetectedActivityFence.STILL);
        AwarenessFence cyclingFence = DetectedActivityFence.during(DetectedActivityFence.ON_BICYCLE);
        AwarenessFence drivingFence = DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE);


        Awareness.FenceApi.updateFences(
        mGoogleApiClientAware,
                new FenceUpdateRequest.Builder()
                .addFence("onFootFence", onFootFence, mFencePendingIntent)
                .addFence("stillFence", stillFence, mFencePendingIntent)
                .addFence("cyclingFence", cyclingFence, mFencePendingIntent)
                .addFence("drivingFence", drivingFence, mFencePendingIntent)
                .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.e(TAG, "Fence was successfully registered.");
                            noFence = false;
                        } else {
                            Log.e(TAG, "Fence could not be registered: " + status);
                        }
                    }
                });

    }

    private void unregisterFences() {
        unregisterFence("onFootFence");
        unregisterFence("runningFence");
        unregisterFence("unknownFence");
        unregisterFence("stillFence");
        unregisterFence("walkingFence");
        unregisterFence("cyclingFence");
        unregisterFence("drivingFence");
    }
    protected void enableGPS(){
        if(!activeGps){
            //mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            activeGps = true;
            handler.removeCallbacks(runnable);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClientLoc, mLocationRequest, this);
        }
    }

    public void askForGps(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClientLoc, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                Log.i(LOG, "onResult LocationSettingsResult");
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(LOG, "SUCCESS");
                        initiateLocation();
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(LOG, "RESOLUTION_REQUIRED");
                        initiateLocation();
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult( activityContext, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(LOG, "SETTINGS_CHANGE_UNAVAILABLE");
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }
    protected void disableGPS(){
        if (mGoogleApiClientLoc.isConnected() && activeGps) {
            Log.i(TAG, "Tried to shut down Location API");

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClientLoc, this);
            activeGps = false;
            handler.postDelayed(runnable, 1000);
        }
    }
    @Override
    public void onDestroy(){

        try{
            if(fenceReceiver!=null)
                //mSensorManager.unregisterListener(this);
                stop();
                unregisterReceiver(fenceReceiver);
                Log.i(TAG, "Everything is destroyed");
        }catch(Exception e)
        {

        }

        super.onDestroy();
    }

    private void unregisterFence(final String fenceKey) {
        Awareness.FenceApi.updateFences(
                mGoogleApiClientAware,
                new FenceUpdateRequest.Builder()
                        .removeFence(fenceKey)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.i(TAG, "Fence " + fenceKey + " successfully removed.");
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.i(TAG, "Fence " + fenceKey + " could NOT be removed.");
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        accelerationX = sensorEvent.values[0];
        accelerationY = sensorEvent.values[1];
        accelerationZ = sensorEvent.values[2];

        /*
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        if (!mInitialized){
            mLastX = x;
            mLastY = y;
            mLastZ = z;

            mInitialized = true;
        }else{
            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);

            if (deltaX < NOISE) deltaX = (float)0.0;
            if (deltaY < NOISE) deltaY = (float)0.0;
            if (deltaZ < NOISE) deltaZ = (float)0.0;

            mLastX = x;
            mLastY = y;
            mLastZ = z;
        }
        */
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    // Handle the callback on the Intent.
    public class MyFenceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);
            Log.i(TAG, "onReceive");
            isStill = false;

            switch(fenceState.getFenceKey()) {
                case "onFootFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "WALKING";
                        setActivity(currentActivity);
                        enableGPS();
                        Log.i(TAG, "onFootFence");
                    }
                     break;
                case "runningFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "WALKING";
                        setActivity(currentActivity);
                        Log.i(TAG, "runningFence");
                    }
                    break;
                case "unknownFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "STILL";
                        isStill = true;
                        setActivity(currentActivity);
                        Log.i(TAG, "unknownFence");
                    }
                    break;
                case "stillFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "STILL";
                        isStill = true;
                        disableGPS();
                        setActivity(currentActivity);
                        Log.i(TAG, "stillFence");
                    }
                    break;
                case "walkingFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "WALKING";
                        setActivity(currentActivity);
                        Log.i(TAG, "walkingFence");
                    }
                    break;
                case "cyclingFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "CYCLING";
                        setActivity(currentActivity);
                        Log.i(TAG, "cyclingFence");
                    }
                    break;
                case "drivingFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "DRIVING";
                        setActivity(currentActivity);
                        Log.i(TAG, "drivingFence");
                    }
                    break;
                default:
                    currentActivity = "STILL";
                    isStill = true;
                    setActivity(currentActivity);
                    Log.i(TAG, "stillFence initiated from default");
            }
        }


    }
}

