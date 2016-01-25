package com.tinakit.moveit.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Tina on 9/23/2015.
 */
public class Reward implements Parcelable {

    private int mRewardId;
    private String mName;
    private int mPoints;
    private int mUserId;
    private Date mDateEarned;
    private Date mDateFulfilled;

    public Reward (){}

    public int getRewardId() {
        return mRewardId;
    }

    public void setRewardId(int rewardId) {
        mRewardId = rewardId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public Date getDateEarned() {
        return mDateEarned;
    }

    public void setDateEarned(Date dateEarned) {
        mDateEarned = dateEarned;
    }

    public Date getDateFulfilled() {
        return mDateFulfilled;
    }

    public void setDateFulfilled(Date dateFulfilled) {
        mDateFulfilled = dateFulfilled;
    }

    protected Reward(Parcel in) {
        mRewardId = in.readInt();
        mName = in.readString();
        mPoints = in.readInt();
        mUserId = in.readInt();
        long tmpMDateEarned = in.readLong();
        mDateEarned = tmpMDateEarned != -1 ? new Date(tmpMDateEarned) : null;
        long tmpMDateFulfilled = in.readLong();
        mDateFulfilled = tmpMDateFulfilled != -1 ? new Date(tmpMDateFulfilled) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mRewardId);
        dest.writeString(mName);
        dest.writeInt(mPoints);
        dest.writeInt(mUserId);
        dest.writeLong(mDateEarned != null ? mDateEarned.getTime() : -1L);
        dest.writeLong(mDateFulfilled != null ? mDateFulfilled.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Reward> CREATOR = new Parcelable.Creator<Reward>() {
        @Override
        public Reward createFromParcel(Parcel in) {
            return new Reward(in);
        }

        @Override
        public Reward[] newArray(int size) {
            return new Reward[size];
        }
    };
}