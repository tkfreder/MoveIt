package com.tinakit.moveit.model;

import android.location.Location;

import java.util.Date;

/**
 * Created by Tina on 6/18/2015.
 */
public class UnitSplit {

    private Location mLocation; //contains latitude, longitude, altitude, accuracy, timestamp
    private float mSpeed; //miles per hour
    private int mActivityId;
    private float mBearing;//need subsequent location data point to calculate bearing

    public UnitSplit(Location location){
        mLocation = location;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    public int getActivityId() {
        return mActivityId;
    }

    public void setActivityId(int activityId) {
        mActivityId = activityId;
    }

    public float getBearing() {
        return mBearing;
    }

    public void setBearing(float bearing) {
        mBearing = bearing;
    }
}
