package com.tinakit.moveit.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tina on 9/22/2015.
 */
public class ActivityDetail {

    private int mActivityId;
    private List<User> mUserList;
    private int mActivityTypeId;
    private LatLng mStartLocation;
    private Date mStartDate;  //"YYYY-MM-DD HH:MM:SS.SSS"
    private Date mEndDate;  //"YYYY-MM-DD HH:MM:SS.SSS"
    private float mDistanceInFeet;
    private float mCalories;
    private float mPointsEarned;

    public ActivityDetail(){

        mUserList = new ArrayList<>();
    }

    public int getActivityId() {
        return mActivityId;
    }

    public void setActivityId(int activityId) {
        mActivityId = activityId;
    }

    public int getActivityTypeId() {
        return mActivityTypeId;
    }

    public void addUser(User user){

        mUserList.add(user);
    }

    public void removeUser(User user){

        mUserList.remove(user);
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

    public void setActivityTypeId(int activityTypeId) {
        mActivityTypeId = activityTypeId;
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
}
