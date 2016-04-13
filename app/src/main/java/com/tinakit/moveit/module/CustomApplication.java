package com.tinakit.moveit.module;

import android.app.Application;

import com.tinakit.moveit.model.RewardListObservable;
import com.tinakit.moveit.model.UserListObservable;


/**
 * Created by Tina on 1/7/2016.
 */
public class CustomApplication extends Application {

    AppComponent mAppComponent;
    UserListObservable mUserListObservable;
    RewardListObservable mRewardListObservable;

    @Override
    public void onCreate(){

        super.onCreate();

        mAppComponent = DaggerAppComponent
                .builder()
                .storageModule(new StorageModule(this))
                .apiModule(new ApiModule())
                .build();

        mUserListObservable = new UserListObservable();
        mRewardListObservable = new RewardListObservable();
    }

    public AppComponent getAppComponent(){ return mAppComponent;};
    public UserListObservable getUserListObservable(){
        return mUserListObservable;
    }
    public RewardListObservable getRewardListObservable(){
        return mRewardListObservable;
    }


}
