package com.tinakit.moveit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tina on 9/30/2015.
 */
public class ActivityType implements Parcelable {

    private int mActivityTypeId;
    private float mMaxSpeed;
    private String mMaxSpeedNotes;
    private String mIconFileName;
    private String mActivityName;
    private int mPriority;

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

    public String getMaxSpeedNotes() {
        return mMaxSpeedNotes;
    }

    public void setMaxSpeedNotes(String maxSpeedNotes) {
        mMaxSpeedNotes = maxSpeedNotes;
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

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int priority) {
        mPriority = priority;
    }

    protected ActivityType(Parcel in) {
        mActivityTypeId = in.readInt();
        mMaxSpeed = in.readFloat();
        mMaxSpeedNotes = in.readString();
        mIconFileName = in.readString();
        mActivityName = in.readString();
        mPriority = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mActivityTypeId);
        dest.writeFloat(mMaxSpeed);
        dest.writeString(mMaxSpeedNotes);
        dest.writeString(mIconFileName);
        dest.writeString(mActivityName);
        dest.writeInt(mPriority);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ActivityType> CREATOR = new Parcelable.Creator<ActivityType>() {
        @Override
        public ActivityType createFromParcel(Parcel in) {
            return new ActivityType(in);
        }

        @Override
        public ActivityType[] newArray(int size) {
            return new ActivityType[size];
        }
    };
}