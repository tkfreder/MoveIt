package com.tinakit.moveit.model;

/**
 * Created by Tina on 7/2/2015.
 */
public class StatInfo {

    private int mUserId;
    private String mUserName;
    private int mCoinTotal;

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public int getCoinTotal() {
        return mCoinTotal;
    }

    public void setCoinTotal(int coinTotal) {
        mCoinTotal = coinTotal;
    }
}
