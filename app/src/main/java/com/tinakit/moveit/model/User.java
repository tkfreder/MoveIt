package com.tinakit.moveit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tina on 7/2/2015.
 */
public class User  implements Parcelable {

    private int mUserId;
    private String mUserName;
    private int mIsAdmin;
    private float mWeight;
    private String mAvatarFileName;
    private int mPoints;
    private boolean mIsParticipant;

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
