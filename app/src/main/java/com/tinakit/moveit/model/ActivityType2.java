package com.tinakit.moveit.model;

/**
 * Created by Tina on 9/15/2015.
 */
public enum ActivityType2 {

    //WALKING (0, "walk", 4.6f),//1995 world record, walking speed meters/second
    RUNNING (1, "run", 12.4f),//2009 world record, running speed meters/second
    SCOOTER (2, "scooter",5.0f), //estimate based on 20 km/h, world record doesn't exist
    CYCLING (3, "bike", 74.7f),//1985 world record, cycling speed meters/second
    HIKING (4, "hike", 4.6f);//1995 world record, walking speed meters/second
    //only supported for FitBit fitness wrist trackers
    //SWIMMING (6, "swim", 2.3f);//1990 world record, swimming speed meters/second

    private final int mActivityId;
    private final String mActivityName;
    private final float mMaxSpeed; //meters per second


    ActivityType2(int activityId, String activityName, float maxSpeed){

        mActivityId = activityId;
        mActivityName = activityName;
        mMaxSpeed = maxSpeed;

    }

    public int getActivityId(){ return mActivityId; }
    public String getName(){
        return mActivityName;
    }
    public float getMaxSpeed(){
        return mMaxSpeed;
    }

    public static int getIndexById(int activityId){

        for (int i = 0; i < ActivityType2.values().length; i++ ){

            if (ActivityType2.values()[i].getActivityId() == activityId){
                return i;
            }

        }

        return -1;
    }

}
