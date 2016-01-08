package com.tinakit.moveit.module;

import android.app.Application;
import android.support.v4.app.FragmentActivity;


/**
 * Created by Tina on 1/7/2016.
 */
public class CustomApplication extends Application {

    StorageComponent mStorageComponent;

    @Override
    public void onCreate(){

        super.onCreate();

        mStorageComponent = DaggerStorageComponent
                .builder()
                .storageModule(new StorageModule(this))
                .build();

    }

    public StorageComponent getStorageComponent(){ return mStorageComponent;};



}
