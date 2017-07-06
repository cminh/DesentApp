package com.example.desent.desent.models;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by magnust on 04.07.2017.
 */

public class DistanceTracker extends MainActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private MyFenceReceiver fenceReceiver;
    private PendingIntent mFencePendingIntent;
    private GoogleApiClient mGoogleApiClientAware;
    private static final String FENCE_RECEIVER_ACTION = "FENCE_RECEIVE";
    private static final String TAG = "Awareness";

    // Current state
    private String currentActivity;

    private final String LOG="DistanceTracking";
    private GoogleApiClient mGoogleApiClientLoc;
    private LocationRequest mLocationRequest;
    private static final int DELAY = 6000;
    private Location mLastLocation;
    private String activity;
    private String probableActivity;
    private DatabaseHelper myDb;
    private Context context;
    private Activity activityContext;
    private LocationManager service;
    private boolean enabled;
    private boolean update = false;


    public DistanceTracker(Activity activityContext, Context context){

        this.activityContext = activityContext;
        this.context = context;
        Log.i(LOG,"constructor");

        onCreate();
        String locationProvider = LocationManager.GPS_PROVIDER;
        Log.i(LOG,locationProvider);
        //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }


    protected void onCreate() {

        Log.i(LOG, "Distance is initiated");

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
    }

    public void start() {
        Log.e("OnSTART", "Before");
        mGoogleApiClientLoc.connect();
        Log.e("OnSTART", "Location is connected");
        registerFences();
        // registerReceiver(fenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
    }


    public void stop() {
        if (mGoogleApiClientLoc.isConnected()) {
            mGoogleApiClientLoc.disconnect();
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
            initiateLocation();

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double distance;
        if(mLastLocation == null){
            mLastLocation = location;
            distance = 0;
            Log.i(LOG, "mLastLocation == null");
        }else{
            Log.i(LOG, mLastLocation.toString());
            distance = mLastLocation.distanceTo(location);
            mLastLocation = location;

            // The code is tricked to think that it knows activity
            if(activity.equals("STILL")){

                if (mGoogleApiClientLoc.isConnected() && !update) {
                    mGoogleApiClientLoc.disconnect();
                }

                Log.i(LOG, "You are still");
            }else{
                mGoogleApiClientLoc.connect();
                boolean isInserted = myDb.insertDistance(activity, (float)distance);

                if(isInserted == true){
                    Toast.makeText(context,"Data inserted",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(context,"Data not Inserted",Toast.LENGTH_LONG).show();
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
            mGoogleApiClientLoc.connect();
        }

    }

    public void initiateLocation(){
        Log.i(LOG, "Textview should be changed");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClientLoc, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClientLoc);
    }

    private void registerFences() {
        Calendar cal = Calendar.getInstance();
        //Create fences
        AwarenessFence onFootFence = DetectedActivityFence.during(DetectedActivityFence.ON_FOOT);
        AwarenessFence runningFence = DetectedActivityFence.during(DetectedActivityFence.RUNNING);
        AwarenessFence unknownFence = DetectedActivityFence.during(DetectedActivityFence.UNKNOWN);
        AwarenessFence stillFence = DetectedActivityFence.during(DetectedActivityFence.STILL);
        AwarenessFence walkingFence = DetectedActivityFence.during(DetectedActivityFence.WALKING);
        AwarenessFence cyclingFence = DetectedActivityFence.during(DetectedActivityFence.ON_BICYCLE);
        AwarenessFence drivingFence = DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE);

        Awareness.FenceApi.updateFences(
                mGoogleApiClientAware,
                new FenceUpdateRequest.Builder()
                        .addFence("onFootFence", onFootFence, mFencePendingIntent)
                        .addFence("runningFence", runningFence, mFencePendingIntent)
                        .addFence("unknownFence", unknownFence, mFencePendingIntent)
                        .addFence("stillFence", stillFence, mFencePendingIntent)
                        .addFence("walkingFence", walkingFence, mFencePendingIntent)
                        .addFence("cyclingFence", cyclingFence, mFencePendingIntent)
                        .addFence("drivingFence", drivingFence, mFencePendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.e(TAG, "Fence was successfully registered.");
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

    public void updateActivity(boolean update){
        this.update = update;
        mGoogleApiClientLoc.connect();
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


    // Handle the callback on the Intent.
    public class MyFenceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);
            Log.i(TAG, "onReceive");
            switch(fenceState.getFenceKey()) {
                case "onFootFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "WALKING";
                        setActivity(currentActivity);
                    }
                    break;
                case "runningFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "WALKING";
                        setActivity(currentActivity);
                    }
                    break;
                case "unknownFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "STILL";
                        setActivity(currentActivity);
                    }
                    break;
                case "stillFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "STILL";
                        setActivity(currentActivity);
                    }
                    break;
                case "walkingFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "WALKING";
                        setActivity(currentActivity);
                    }
                    break;
                case "cyclingFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "CYCLING";
                        setActivity(currentActivity);
                    }
                    break;
                case "drivingFence":
                    if(fenceState.getCurrentState() == FenceState.TRUE) {
                        currentActivity = "DRIVING";
                        setActivity(currentActivity);
                    }
                    break;
            }
        }
    }
}

