package com.tinakit.moveit.module;

import android.app.Application;


/**
 * Created by Tina on 1/7/2016.
 */
public class CustomApplication extends Application {

    AppComponent mAppComponent;

    @Override
    public void onCreate(){

        super.onCreate();

        mAppComponent = DaggerAppComponent
                .builder()
                .storageModule(new StorageModule(this))
                .apiModule(new ApiModule())
                .build();

    }

    public AppComponent getAppComponent(){ return mAppComponent;};



}
