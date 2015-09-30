package com.tinakit.moveit.model;

/**
 * Created by Tina on 7/2/2015.
 */
public class User {

    private long mUserId;
    private String mUserName;
    private int mIsAdmin;
    private float mWeight;
    private String mAvatarFileName;

    public User(){}

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long userId) {
        mUserId = userId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public boolean isAdmin() {
        return mIsAdmin == 1 ? true : false;
    }

    public void setIsAdmin(boolean isAdmin) {
        mIsAdmin = isAdmin ? 1 : 0;
    }

    public float getWeight() {
        return mWeight;
    }

    public void setWeight(float weight) {
        mWeight = weight;
    }

    public String getAvatarFileName() {
        return mAvatarFileName;
    }

    public void setAvatarFileName(String avatarFileName) {
        mAvatarFileName = avatarFileName;
    }
}
