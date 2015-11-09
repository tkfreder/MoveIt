package com.tinakit.moveit.model;

import android.os.Parcelable;
import android.os.Parcel;

/**
 * Created by Tina on 11/7/2015.
 */
public class UserActivity implements Parcelable {

    private User mUser;
    private ActivityType mActivityType;
    private Integer mCalories;

    public UserActivity(User user){

        mUser = user;
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

    public Integer getCalories() {
        return mCalories;
    }

    public void setCalories(Integer calories) {
        mCalories = calories;
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


    protected UserActivity(Parcel in) {
        mUser = (User) in.readValue(User.class.getClassLoader());
        mActivityType = (ActivityType) in.readValue(ActivityType.class.getClassLoader());
        mCalories = in.readByte() == 0x00 ? null : in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mUser);
        dest.writeValue(mActivityType);
        if (mCalories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mCalories);
        }
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