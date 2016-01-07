package com.tinakit.moveit.module;

import android.app.Application;

import com.tinakit.moveit.component.DaggerStorageComponent;
import com.tinakit.moveit.component.StorageComponent;

/**
 * Created by Tina on 1/7/2016.
 */
public class CustomApplication extends Application {

    StorageComponent storageComponent;

    @Override
    public void onCreate(){

        super.onCreate();

        storageComponent = DaggerStorageComponent
                .builder()
                .storageModule(new StorageModule(this))
                .build();


    }

    public StorageComponent getStorageComponent(){ return storageComponent;};
}
