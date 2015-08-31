package com.tinakit.moveit.model;

import android.content.SharedPreferences;
import android.location.Location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Tina on 6/16/2015.
 */
public class LocationInfo {
    private Location mLocation;
    private long mSecondsElapsed;

    public LocationInfo(Location location, long secondsElapsed){
        mLocation = location;
        mSecondsElapsed = secondsElapsed;
    }



    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public long getSecondsElapsed() {
        return mSecondsElapsed;
    }

    public void setSecondsElapsed(long secondsElapsed) {
        mSecondsElapsed = secondsElapsed;
    }
}
