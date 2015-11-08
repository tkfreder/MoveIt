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
    private List<UserActivity> mUserActivityList;
    private LatLng mStartLocation;
    private Date mStartDate;  //"YYYY-MM-DD HH:MM:SS.SSS"
    private Date mEndDate;  //"YYYY-MM-DD HH:MM:SS.SSS"
    private float mDistanceInFeet;

    private float mPointsEarned;
    private float mBearing;

    public ActivityDetail(){

        mUserActivityList = new ArrayList<>();
    }

    public int getActivityId() {
        return mActivityId;
    }

    public void setActivityId(int activityId) {
        mActivityId = activityId;
    }

    public boolean addUserActivity(UserActivity userActivity){

        if (!mUserActivityList.contains(userActivity.getUser())){
            mUserActivityList.add(userActivity);
            return true;
        }
        else
            return false;

    }

    public boolean removeUserActivity(UserActivity userActivity){

        if (mUserActivityList.contains(userActivity.getUser())){
            mUserActivityList.remove(userActivity);
            return true;
        }
        else
            return false;
    }

    public List<UserActivity> getUserActivityList(){

        return mUserActivityList;
    }

    public void setUserActivityList(List<UserActivity> userActivityList){

        mUserActivityList = userActivityList;
    }

    public boolean setUserCalorie(User user, int calorie){

        for (UserActivity userActivity : mUserActivityList) {

            if (user.equals(userActivity.getUser())) {

                userActivity.setCalories(calorie);
                return true;
            }
        }

        return false;
    }

    public int getUserCalorie(User user){

        for (UserActivity userActivity : mUserActivityList) {

            if (user.equals(userActivity.getUser())) {

                return userActivity.getCalories();

            }
        }

        return -1;
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
            mUserActivityList = new ArrayList<UserActivity>();
            in.readList(mUserActivityList, UserActivity.class.getClassLoader());
        } else {
            mUserActivityList = null;
        }
        mStartLocation = (LatLng) in.readValue(LatLng.class.getClassLoader());
        long tmpMStartDate = in.readLong();
        mStartDate = tmpMStartDate != -1 ? new Date(tmpMStartDate) : null;
        long tmpMEndDate = in.readLong();
        mEndDate = tmpMEndDate != -1 ? new Date(tmpMEndDate) : null;
        mDistanceInFeet = in.readFloat();
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
        if (mUserActivityList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mUserActivityList);
        }
        dest.writeValue(mStartLocation);
        dest.writeLong(mStartDate != null ? mStartDate.getTime() : -1L);
        dest.writeLong(mEndDate != null ? mEndDate.getTime() : -1L);
        dest.writeFloat(mDistanceInFeet);
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