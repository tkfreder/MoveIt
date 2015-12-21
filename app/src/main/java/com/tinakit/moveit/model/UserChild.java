package com.tinakit.moveit.model;

/**
 * Created by Tina on 12/20/2015.
 */
public class UserChild {

    private int mPoints;
    private String mRewardName;
    private String status

    public UserChild(int points, String rewardName){

        mPoints = points;
        mRewardName = getRewardName();

    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public String getRewardName() {
        return mRewardName;
    }

    public void setRewardName(String rewardName) {
        mRewardName = rewardName;
    }
}
