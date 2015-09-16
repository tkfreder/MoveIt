package com.tinakit.moveit.model;

import android.location.Location;

import java.util.Date;

/**
 * Created by Tina on 6/18/2015.
 */
public class LocationTime {

    private Date mTimeStamp;
    private Location mLocation;

    public LocationTime (Date timeStamp, Location location){

        mTimeStamp = timeStamp;
        mLocation = location;

    }

    public Date getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        mTimeStamp = timeStamp;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }
}
