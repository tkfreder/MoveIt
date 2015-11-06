package com.tinakit.moveit.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Parcelable;
import android.os.Parcel;

/**
 * Created by Tina on 9/22/2015.
 */
public class ActivityDetail implements Parcelable {

    private int mActivityId;
    private List<User> mUserList;
    private List<ActivityType> mActivityTypeList;
    private LatLng mStartLocation;
    private Date mStartDate;  //"YYYY-MM-DD HH:MM:SS.SSS"
    private Date mEndDate;  //"YYYY-MM-DD HH:MM:SS.SSS"
    private float mDistanceInFeet;
    private float mCalories;
    private float mPointsEarned;
    private float mBearing;

    public ActivityDetail(){

        mUserList = new ArrayList<>();
        mActivityTypeList = new ArrayList<>();
    }

    public int getActivityId() {
        return mActivityId;
    }

    public void setActivityId(int activityId) {
        mActivityId = activityId;
    }

    public boolean addUserActivity(User user, ActivityType activityType){

        if (!mUserList.contains(user)){
            mUserList.add(user);
            mActivityTypeList.add(activityType);
            return true;
        }
        else
            return false;

    }

    public boolean removeUserActivity(User user){

        if (mUserList.contains(user)){
            int index = mUserList.indexOf(user);
            mUserList.remove(user);

            mActivityTypeList.remove(index);
            return true;
        }
        else
            return false;
    }

    public List<User> getUserList(){

        return mUserList;
    }

    public boolean hasUser(User user){

        return mUserList.contains(user);
    }

    public void setUserList(List<User> userList){

        mUserList = userList;
    }


    public void setActivityTypeList(List<ActivityType> activityTypeList){
        mActivityTypeList = activityTypeList;
    }

    public List<ActivityType> getActivityTypeList(){
        return mActivityTypeList;
    }

    public LatLng getStartLocation() {
        return mStartLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        mStartLocation = startLocation;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date endDate) {
        mEndDate = endDate;
    }

    public float getDistanceInFeet() {
        return mDistanceInFeet;
    }

    public void setDistanceInFeet(float distanceInFeet) {
        mDistanceInFeet = distanceInFeet;
    }

    public float getCalories() {
        return mCalories;
    }

    public void setCalories(float calories) {
        mCalories = calories;
    }

    public float getPointsEarned() {
        return mPointsEarned;
    }

    public void setPointsEarned(float pointsEarned) {
        mPointsEarned = pointsEarned;
    }

    public float getBearing() {
        return mBearing;
    }

    public void setBearing(float bearing) {
        mBearing = bearing;
    }

    protected ActivityDetail(Parcel in) {
        mActivityId = in.readInt();
        if (in.readByte() == 0x01) {
            mUserList = new ArrayList<User>();
            in.readList(mUserList, User.class.getClassLoader());
        } else {
            mUserList = null;
        }
        if (in.readByte() == 0x01) {
            mActivityTypeList = new ArrayList<ActivityType>();
            in.readList(mActivityTypeList, ActivityType.class.getClassLoader());
        } else {
            mActivityTypeList = null;
        }
        mStartLocation = (LatLng) in.readValue(LatLng.class.getClassLoader());
        long tmpMStartDate = in.readLong();
        mStartDate = tmpMStartDate != -1 ? new Date(tmpMStartDate) : null;
        long tmpMEndDate = in.readLong();
        mEndDate = tmpMEndDate != -1 ? new Date(tmpMEndDate) : null;
        mDistanceInFeet = in.readFloat();
        mCalories = in.readFloat();
        mPointsEarned = in.readFloat();
        mBearing = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mActivityId);
        if (mUserList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mUserList);
        }
        if (mActivityTypeList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mActivityTypeList);
        }
        dest.writeValue(mStartLocation);
        dest.writeLong(mStartDate != null ? mStartDate.getTime() : -1L);
        dest.writeLong(mEndDate != null ? mEndDate.getTime() : -1L);
        dest.writeFloat(mDistanceInFeet);
        dest.writeFloat(mCalories);
        dest.writeFloat(mPointsEarned);
        dest.writeFloat(mBearing);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ActivityDetail> CREATOR = new Parcelable.Creator<ActivityDetail>() {
        @Override
        public ActivityDetail createFromParcel(Parcel in) {
            return new ActivityDetail(in);
        }

        @Override
        public ActivityDetail[] newArray(int size) {
            return new ActivityDetail[size];
        }
    };
}