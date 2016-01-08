package com.tinakit.moveit.module;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.tinakit.moveit.api.GoogleApi;
import com.tinakit.moveit.db.FitnessDBHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tina on 1/7/2016.
 */



@Module public class StorageModule {

    private final Context mContext;

    public StorageModule(Context context){

        mContext = context;
    }

    @Provides @Singleton FitnessDBHelper provideDBHelper(){

        return FitnessDBHelper.getInstance(mContext);
    }

    /*
    @Provides
    @Singleton
    GoogleApi provideGoogleApi(){

        return new GoogleApi();
    }

*/
}
