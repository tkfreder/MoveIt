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
    private float mSpeed; //miles per hour
    private float mCalories;
    private float mAccuracy;
    private int mActivityId;
    private float mBearing;//need subsequent location data point to calculate bearing

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

    public float getAccuracy() {
        return mAccuracy;
    }

    public void setAccuracy(float accuracy) {
        mAccuracy = accuracy;
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
