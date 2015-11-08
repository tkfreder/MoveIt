package com.tinakit.moveit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.tinakit.moveit.utility.AndroidDatabaseManager;
import com.tinakit.moveit.adapter.ActivityHistoryRecyclerAdapter;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.User;

import java.util.List;

/**
 * Created by Tina on 10/19/2015.
 */
public class ActivityHistory  extends AppCompatActivity {

    private static final boolean DEBUG = true;

    private RecyclerView mRecyclerView;
    private ActivityHistoryRecyclerAdapter mActivityHistoryRecyclerAdapter;
    private List<ActivityDetail> activityList;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_history);

        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().containsKey("user")) {
                mUser = (User)getIntent().getExtras().getParcelable("user");

                //TODO:  get Activity Detail history from DB
                FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(this);
                activityList = databaseHelper.getFirstLocationPoints(mUser);

                //RecyclerView
                // Initialize recycler view
                mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mActivityHistoryRecyclerAdapter = new ActivityHistoryRecyclerAdapter(this, activityList);
                mRecyclerView.setAdapter(mActivityHistoryRecyclerAdapter);


            }
        }
        */

        if (DEBUG)
            displayDatabaseManager();
    }

    private void displayDatabaseManager(){

            Intent dbmanager = new Intent(this,AndroidDatabaseManager.class);
            startActivity(dbmanager);

    }
}
