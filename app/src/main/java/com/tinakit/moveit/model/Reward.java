package com.tinakit.moveit.model;

/**
 * Created by Tina on 9/23/2015.
 */
public class Reward {

    private int mRewardId;
    private String mName;
    private int mPoints;
    private String mDescription;
    private boolean mEnabled;
    private int mUserStatus;

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

    public int getUserStatus() {
        return mUserStatus;
    }

    public void setUserStatus(int userStatus) {
        mUserStatus = userStatus;
    }
}
