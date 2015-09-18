package com.tinakit.moveit.model;

/**
 * Created by Tina on 7/2/2015.
 */
public class User {

    private String mUserName;
    private String mPassword;
    private boolean mIsAdmin;
    private int mWeight;
    private String mAvatarFileName;

    public User(String userName, String password, boolean isAdmin, int weight, String avatarFileName){

        mUserName = userName;
        mPassword = password;
        mIsAdmin = isAdmin;
        mWeight = weight;
        mAvatarFileName = avatarFileName;
    }

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

    public boolean isAdmin() {
        return mIsAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        mIsAdmin = isAdmin;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        mWeight = weight;
    }

    public String getAvatarFileName() {
        return mAvatarFileName;
    }

    public void setAvatarFileName(String avatarFileName) {
        mAvatarFileName = avatarFileName;
    }
}
