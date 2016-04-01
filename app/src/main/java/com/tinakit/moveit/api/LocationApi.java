package com.tinakit.moveit.api;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.tinakit.moveit.LifeCycle;
import com.tinakit.moveit.activity.ActivityTracker;
import com.tinakit.moveit.fragment.ActivityChooser;

/**
 * Created by Tina on 12/12/2015.
 */
public class LocationApi implements LocationListener, LifeCycle {

    // DEBUG
    private static final boolean DEBUG = false;
    private static final String LOG = "LOCATION_API";

    // CONSTANTS
    public static final String LOCATION_API_INTENT = "LOCATION_API_INTENT";

    // instance variables
    private FragmentActivity mFragmentActivity;
    private Location mLocation;
    private GoogleApiClient mGoogleApiClient;
    private int mLocationDataCount = 0;

    //LocationRequest settings
    private LocationRequest mLocationRequest;
    private Location mBestReading;
    private static long POLLING_FREQUENCY = 3 * 1000; // in milliseconds, standard is 5 seconds
    private static long FASTEST_POLLING_FREQUENCY = 5 * 1000; // in milliseconds
    private static final long LOCATION_ACCURACY = 10; //within # meter accuracy, standard is 20 meters
    private boolean mIsTimeLimit = false;
    private boolean mFirstData = true;
    private long lastTimeStamp = System.currentTimeMillis();
    private static long MIN_INTERVAL_TRACKING = 10 * 1000; // in milliseconds


    public LocationApi(FragmentActivity fragmentActivity, GoogleApiClient googleApiClient){

        mFragmentActivity = fragmentActivity;
        mGoogleApiClient = googleApiClient;

        initialize();
    }


    public LocationRequest locationRequest(){

        return mLocationRequest;
    }

    public Location location(){

        return mLocation;
    }

    public boolean isPollingData (int count){

        return mLocationDataCount >= count;
    }

    @Override
    public void onLocationChanged(Location location) {
        // save location if meets minimum accuracy
        if (isAccurate(location) && (mFirstData || System.currentTimeMillis() - lastTimeStamp >= MIN_INTERVAL_TRACKING)){
            mFirstData = false;
            // set flag, when start receiving location data that meets accuracy requirement
            mLocationDataCount++;
            // save the current location
            mLocation = location;
            // timestamp
            lastTimeStamp = System.currentTimeMillis();
            // send message to indicate there is new location data
            Intent intent = new Intent(LOCATION_API_INTENT);
            intent.putExtra(ActivityTracker.ACTIVITY_TRACKER_BROADCAST_RECEIVER, LOCATION_API_INTENT);
            LocalBroadcastManager.getInstance(mFragmentActivity).sendBroadcast(intent);
        }
    }

    private boolean isAccurate(Location location){

        if (location.getAccuracy() < LOCATION_ACCURACY)
            return true;
        else
            return false;
    }

    public void initialize() {

        //this method should be called once at start of run
        //while stopServices should be called once at end of run
        //do not call buildLocationRequest() and stopServices multiple times, difficult to trach asynchronous process
        //use flag mSaveLocationData to determine whether to save data.  during Pause, set mSaveLocationData to false
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(POLLING_FREQUENCY);//get location updates every x seconds
        mLocationRequest.setFastestInterval(FASTEST_POLLING_FREQUENCY);//not to exceed location updates every x seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void start() {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void pause() {

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    @Override
    public void resume() {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void stop() {

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    //TODO:  may be able to utilize this to get a more accurate first data point
    //reference:  http://www.adavis.info/2014/09/android-location-updates-with.html?m=1
    private Location bestLastKnownLocation(float minAccuracy, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Get the best most recent location currently available
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null) {
            float accuracy = mCurrentLocation.getAccuracy();
            long time = mCurrentLocation.getTime();

            if (accuracy < bestAccuracy) {
                bestResult = mCurrentLocation;
                bestAccuracy = accuracy;
                bestTime = time;
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy || bestTime < minTime) {
            return null;
        }
        else {
            return bestResult;
        }
    }


    public boolean hasLocationService(){

        final LocationManager manager = (LocationManager) mFragmentActivity.getSystemService(Context.LOCATION_SERVICE);

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();

            return false;
        }

        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mFragmentActivity);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        mFragmentActivity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), ActivityChooser.ENABLE_GPS);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

}
