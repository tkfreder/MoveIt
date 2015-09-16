package com.tinakit.moveit.model;

/**
 * Created by Tina on 9/15/2015.
 */
public enum ActivityType {

    WALKING (4.6f),//1995 world record, walking speed meters/second
    HIKING (4.6f),//same as walking
    RUNNING (12.4f),//2009 world record, running speed meters/second
    SCOOTER (5.0f), //estimate based on 20 km/h, world record doesn't exist
    CYCLING (74.7f),//1985 world record, cycling speed meters/second
    SWIMMING (2.3f);//1990 world record, swimming speed meters/second

    private final float mMaxSpeed; //meters per second

    ActivityType(float maxSpeed){

        mMaxSpeed = maxSpeed;
    }

    public float getMaxSpeed(){
        return mMaxSpeed;
    }
}
