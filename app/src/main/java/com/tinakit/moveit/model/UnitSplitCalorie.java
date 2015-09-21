package com.tinakit.moveit.model;

import android.location.Location;

import java.util.Date;

/**
 * Created by Tina on 6/18/2015.
 */
public class UnitSplitCalorie {

    //TODO:  create DB table Splits(userId, timeStart, timeEnd, activityId, weight, minutes, speed, calories)
    //TODO: create DB table ActivityStats(activityId, userId, weight, minutes, speed, calories)
    private Date mTimeStamp;
    private Location mLocation;
    private float mSpeed;
    private float mCalories;

    public UnitSplitCalorie(Date timeStamp, Location location){

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

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    public float getCalories() {
        return mCalories;
    }

    public void setCalories(float calories) {
        mCalories = calories;
    }
}
