package com.tinakit.moveit.service;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationListener;

/**
 * Created by Tina on 12/12/2015.
 */
public class LocationApi implements LocationListener {

    // DEBUG
    private static final boolean DEBUG = false;
    private static final String LOG = "LOCATION_API";

    @Override
    public void onLocationChanged(Location location) {
        if (DEBUG) Log.d(LOG, "onLocationChanged");

        if (DEBUG) Log.d(LOG, "Accuracy: " + location.getAccuracy());

/*
        //only track data when it has high level of accuracy && not Pause mode
        if (isAccurate(location) && mSaveLocationData){
            //update cache
            updateCache(location);

            refreshData();
        }

        //TODO: do we still want a time limit?
        if(getSecondsFromChronometer() > STOP_SERVICE_TIME_LIMIT && !mIsTimeLimit){
            mIsTimeLimit = true;
            reachedTimeLimit();
            stopRun();
        }
*/
    }

}
