package com.tinakit.moveit.model;

/**
 * Created by Tina on 9/30/2015.
 */
public class ActivityType {

    private int mActivityTypeId;
    private float mMaxSpeed;
    private String mIconFileName;
    private String mActivityName;

    public ActivityType(){}

    public int getActivityTypeId() {
        return mActivityTypeId;
    }

    public void setActivityTypeId(int activityTypeId) {
        mActivityTypeId = activityTypeId;
    }

    public float getMaxSpeed() {
        return mMaxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        mMaxSpeed = maxSpeed;
    }

    public String getIconFileName() {
        return mIconFileName;
    }

    public void setIconFileName(String iconFileName) {
        mIconFileName = iconFileName;
    }

    public String getActivityName() {
        return mActivityName;
    }

    public void setActivityName(String activityName) {
        mActivityName = activityName;
    }
}
