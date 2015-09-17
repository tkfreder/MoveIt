package com.tinakit.moveit.model;

/**
 * Created by Tina on 9/15/2015.
 */
public enum ActivityType {

    WALKING ("walk", 4.6f),//1995 world record, walking speed meters/second
    SCOOTER ("scooter",5.0f), //estimate based on 20 km/h, world record doesn't exist
    CYCLING ("bike", 74.7f),//1985 world record, cycling speed meters/second
    HIKING ("hike", 4.6f),//1995 world record, walking speed meters/second
    RUNNING ("run", 12.4f),//2009 world record, running speed meters/second
    SWIMMING ("swim", 2.3f);//1990 world record, swimming speed meters/second

    private final String mActivityName;
    private final float mMaxSpeed; //meters per second


    ActivityType(String activityName,float maxSpeed){

        mActivityName = activityName;
        mMaxSpeed = maxSpeed;

    }

    public String getName(){
        return mActivityName;
    }
    public float getMaxSpeed(){
        return mMaxSpeed;
    }

}
