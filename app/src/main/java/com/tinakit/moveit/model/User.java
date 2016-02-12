package com.tinakit.moveit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tina on 7/2/2015.
 */
public class User  implements Parcelable {

    private int mUserId;
    private String mUserName;
    private int mIsAdmin;
    private String mEmail;
    private String mPassword;
    private int mWeight;
    private String mAvatarFileName;
    private int mPoints;
    private boolean mIsEnabled;
    //private List<Reward> mRewardList;
    private Reward mReward;
    private boolean mInitiallyExpanded;


    public User(){}

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

    public boolean isAdmin() {
        return mIsAdmin == 1 ? true : false;
    }

    public void setIsAdmin(boolean isAdmin) {
        mIsAdmin = isAdmin ? 1 : 0;
    }

    public void setEmail(String email) { mEmail = email;}

    public void setPassword(String password){ mPassword = password;}

    public String getPassword(){ return mPassword;}

    public String getEmail () { return mEmail;}

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

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public boolean isEnabled() {
        return mIsEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        mIsEnabled = isEnabled;
    }

    public boolean hasSameProfile(User user){

        if(this.getAvatarFileName().equals(user.getAvatarFileName())
                && this.getUserName().equals(user.getUserName())
                && this.isAdmin() == user.isAdmin()
                && this.getWeight() == user.getWeight()){

            return true;
        }
        else
            return false;
    }

    public Reward getReward() {
        return mReward;
    }

    public void setReward(Reward reward) {
        mReward = reward;
    }

    /*
    @Override
    public List<Reward> getChildItemList() {
        return mRewardList;
    }

    public void setChildItemList(List<Reward> childItemList) {
        mRewardList = childItemList;
    }
*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return !(mUserName != null ? !mUserName.equals(user.mUserName) : user.mUserName != null);

    }

    @Override
    public int hashCode() {
        return mUserName != null ? mUserName.hashCode() : 0;}

        protected User(Parcel in) {
            mUserId = in.readInt();
            mUserName = in.readString();
            mIsAdmin = in.readInt();
            mEmail = in.readString();
            mPassword = in.readString();
            mWeight = in.readInt();
            mAvatarFileName = in.readString();
            mPoints = in.readInt();
            mIsEnabled = in.readByte() != 0x00;
            mReward = (Reward) in.readValue(Reward.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mUserId);
            dest.writeString(mUserName);
            dest.writeInt(mIsAdmin);
            dest.writeString(mEmail);
            dest.writeString(mPassword);
            dest.writeInt(mWeight);
            dest.writeString(mAvatarFileName);
            dest.writeInt(mPoints);
            dest.writeByte((byte) (mIsEnabled ? 0x01 : 0x00));
            dest.writeValue(mReward);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
            @Override
            public User createFromParcel(Parcel in) {
                return new User(in);
            }

            @Override
            public User[] newArray(int size) {
                return new User[size];
            }
        };
    }