package com.tinakit.moveit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tina on 7/2/2015.
 */
public class User  implements Parcelable, ParentObject {

    private int mUserId;
    private String mUserName;
    private int mIsAdmin;
    private float mWeight;
    private String mAvatarFileName;
    private int mPoints;
    private boolean mIsParticipant;
    private List<Object> mChildrenList;

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

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public boolean isParticipant() {
        return mIsParticipant;
    }

    public void setIsParticipant(boolean isParticipant) {
        mIsParticipant = isParticipant;
    }

    @Override
    public List<Object> getChildObjectList() {
        return null;
    }

    @Override
    public void setChildObjectList(List<Object> list) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return !(mUserName != null ? !mUserName.equals(user.mUserName) : user.mUserName != null);

    }

    @Override
    public int hashCode() {
        return mUserName != null ? mUserName.hashCode() : 0;
    }

        protected User(Parcel in) {
            mUserId = in.readInt();
            mUserName = in.readString();
            mIsAdmin = in.readInt();
            mWeight = in.readFloat();
            mAvatarFileName = in.readString();
            mPoints = in.readInt();
            mIsParticipant = in.readByte() != 0x00;
            if (in.readByte() == 0x01) {
                mChildrenList = new ArrayList<Object>();
                in.readList(mChildrenList, Object.class.getClassLoader());
            } else {
                mChildrenList = null;
            }
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
            dest.writeFloat(mWeight);
            dest.writeString(mAvatarFileName);
            dest.writeInt(mPoints);
            dest.writeByte((byte) (mIsParticipant ? 0x01 : 0x00));
            if (mChildrenList == null) {
                dest.writeByte((byte) (0x00));
            } else {
                dest.writeByte((byte) (0x01));
                dest.writeList(mChildrenList);
            }
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