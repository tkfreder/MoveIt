package com.tinakit.moveit.api;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tinakit.moveit.LifeCycle;
import com.tinakit.moveit.activity.ActivityTracker;

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
    private boolean mIsPollingData = false;

    //LocationRequest settings
    private LocationRequest mLocationRequest;
    private Location mBestReading;
    private static long POLLING_FREQUENCY = 2 * 1000; // in milliseconds, standard is 5 seconds
    private static long FASTEST_POLLING_FREQUENCY = 5 * 1000; // in milliseconds
    private static final long LOCATION_ACCURACY = 4; //within # meter accuracy, standard is 20 meters
    private boolean mIsTimeLimit = false;


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

    public boolean isPollingData(){

        return mIsPollingData;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (DEBUG) Log.d(LOG, "onLocationChanged");

        if (DEBUG) Log.d(LOG, "Accuracy: " + location.getAccuracy());

        // set flag, when start receiving location data
        mIsPollingData = true;

        // save location if meets minimum accuracy
        if (isAccurate(location)){

            // save the current location
            mLocation = location;

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

}
