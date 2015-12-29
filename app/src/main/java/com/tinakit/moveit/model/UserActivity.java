package com.tinakit.moveit.model;

import android.os.Parcelable;
import android.os.Parcel;

/**
 * Created by Tina on 11/7/2015.
 */
public class UserActivity implements Parcelable, Comparable<UserActivity> {

    private User mUser;
    private ActivityType mActivityType;
    private float mCalories;
    private int mPoints;

    public UserActivity(User user){

        mUser = user;
        mCalories = 0;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public ActivityType getActivityType() {
        return mActivityType;
    }

    public void setActivityType(ActivityType activityType) {
        mActivityType = activityType;
    }

    public float getCalories() {
        return mCalories;
    }

    public void setCalories(float calories) {
        mCalories = calories;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserActivity userActivity = (UserActivity) o;

        return !(mUser.getUserName() != null ? !mUser.getUserName().equals(userActivity.getUser().getUserName()) : userActivity.getUser().getUserName() != null);

    }

    @Override
    public int hashCode() {
        return mUser != null ? mUser.hashCode() : 0;
    }

    public int compareTo(UserActivity userActivity) {
        int userId = this.getUser().getUserId();
        return (userId - userActivity.getUser().getUserId());
    }


    protected UserActivity(Parcel in) {
        mUser = (User) in.readValue(User.class.getClassLoader());
        mActivityType = (ActivityType) in.readValue(ActivityType.class.getClassLoader());
        mCalories = in.readFloat();
        mPoints = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mUser);
        dest.writeValue(mActivityType);
        dest.writeFloat(mCalories);
        dest.writeInt(mPoints);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserActivity> CREATOR = new Parcelable.Creator<UserActivity>() {
        @Override
        public UserActivity createFromParcel(Parcel in) {
            return new UserActivity(in);
        }

        @Override
        public UserActivity[] newArray(int size) {
            return new UserActivity[size];
        }
    };
}