package com.tinakit.moveit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tinakit.moveit.R;

/**
 * Created by Tina on 6/23/2015.
 */
public class ViewRewards  extends AppCompatActivity {

    //DEBUG
    private static final String LOG = "VIEW_REWARDS";
    private static final boolean DEBUG = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(LOG, "onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_rewards);

    }
}
