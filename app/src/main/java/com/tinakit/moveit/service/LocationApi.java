package com.tinakit.moveit.service;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Tina on 12/12/2015.
 */
public class LocationApi implements LocationListener {

    // DEBUG
    private static final boolean DEBUG = false;
    private static final String LOG = "LOCATION_API";

    // CONSTANTS
    public static final String LOCATION_API_INTENT = "LOCATION_API_INTENT";

    // instance variables
    private FragmentActivity mFragmentActivity;
    private Location mLocation;

    //LocationRequest settings
    private LocationRequest mLocationRequest;
    private Location mBestReading;
    private static long POLLING_FREQUENCY = 5 * 1000; //10 seconds
    private static long FASTEST_POLLING_FREQUENCY = 5 * 1000; //5 second
    private static long DISPLACEMENT = 1; //meters //displacement takes precedent over interval/fastestInterval
    private static long STOP_SERVICE_TIME_LIMIT = 30 * 60 * 1000 * 60; // 30 minutes in seconds
    private static final long LOCATION_ACCURACY = 10; //within # meter accuracy //TODO: change this for better accuracy
    private boolean mIsTimeLimit = false;


    public LocationApi(FragmentActivity fragmentActivity){

        mFragmentActivity = fragmentActivity;
    }


    public LocationRequest locationRequest(){

        return mLocationRequest;
    }

    public Location location(){

        return mLocation;
    }

    //this method should be called once at start of run
    //while stopServices should be called once at end of run
    //do not call buildLocationRequest() and stopServices multiple times, difficult to trach asynchronous process
    //use flag mSaveLocationData to determine whether to save data.  during Pause, set mSaveLocationData to false
    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(POLLING_FREQUENCY);//get location updates every x seconds
        mLocationRequest.setFastestInterval(FASTEST_POLLING_FREQUENCY);//not to exceed location updates every x seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void requestLocationUpdates(GoogleApiClient googleApiClient){

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);

    }

    public void removeLocationUpdates(GoogleApiClient googleApiClient){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (DEBUG) Log.d(LOG, "onLocationChanged");

        if (DEBUG) Log.d(LOG, "Accuracy: " + location.getAccuracy());

        // save location if meets minimum accuracy
        if (isAccurate(location)){

            // save the current location
            mLocation = location;

            // send message to indicate there is new location data
            Intent intent = new Intent(LOCATION_API_INTENT);
            intent.putExtra(LOCATION_API_INTENT, LOCATION_API_INTENT);
            LocalBroadcastManager.getInstance(mFragmentActivity).sendBroadcast(intent);
        }
    }

    private boolean isAccurate(Location location){

        if (location.getAccuracy() < LOCATION_ACCURACY)
            return true;
        else
            return false;
    }

}
