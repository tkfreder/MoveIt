package com.tinakit.moveit.model;

/**
 * Created by Tina on 7/2/2015.
 */
public class UserInfo {

    private String mUserName;
    private String mPassword;
    private boolean mIsModerator;
    private int mWeight;

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public boolean isModerator() {
        return mIsModerator;
    }

    public void setIsModerator(boolean isModerator) {
        mIsModerator = isModerator;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        mWeight = weight;
    }
}
