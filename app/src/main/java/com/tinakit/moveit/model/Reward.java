package com.tinakit.moveit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tina on 9/23/2015.
 */
public class Reward implements Parcelable {

    private int mRewardId;
    private String mName;
    private int mPoints;
    private String mDescription;
    private boolean mEnabled;
    private RewardStatusType mRewardStatusType;
    private int mUserId;

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

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public RewardStatusType getRewardStatusType() {
        return mRewardStatusType;
    }

    public void setRewardStatusType(RewardStatusType rewardStatusType) {
        mRewardStatusType = rewardStatusType;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    protected Reward(Parcel in) {
        mRewardId = in.readInt();
        mName = in.readString();
        mPoints = in.readInt();
        mDescription = in.readString();
        mEnabled = in.readByte() != 0x00;
        mRewardStatusType = (RewardStatusType) in.readValue(RewardStatusType.class.getClassLoader());
        mUserId = in.readInt();
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
        dest.writeString(mDescription);
        dest.writeByte((byte) (mEnabled ? 0x01 : 0x00));
        dest.writeValue(mRewardStatusType);
        dest.writeInt(mUserId);
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