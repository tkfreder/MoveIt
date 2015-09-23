package com.tinakit.moveit.model;

import java.util.Date;

/**
 * Created by Tina on 9/22/2015.
 */
public class ActivityDetail {

    private int mActivityId;
    private Date mStartDate;
    private Date mEndDate;
    private float mMinutesElapsed;
    private float mCoinsEarned;
    private int mRouteId;

    public ActivityDetail(int activityId, Date startDate, Date endDate, float minutesElapsed, float coinsEarned){

        mActivityId = activityId;
        mStartDate = startDate;
        mEndDate = endDate;
        mMinutesElapsed = minutesElapsed;
        mCoinsEarned = coinsEarned;
    }

    public int getActivityId() {
        return mActivityId;
    }

    public void setActivityId(int activityId) {
        mActivityId = activityId;
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

    public int getRouteId() {
        return mRouteId;
    }

    public void setRouteId(int routeId) {
        mRouteId = routeId;
    }

    public float getMinutesElapsed() {
        return mMinutesElapsed;
    }

    public void setMinutesElapsed(int minutesElapsed) {
        mMinutesElapsed = minutesElapsed;
    }

    public float getCoinsEarned() {
        return mCoinsEarned;
    }

    public void setCoinsEarned(int coinsEarned) {
        mCoinsEarned = coinsEarned;
    }
}
