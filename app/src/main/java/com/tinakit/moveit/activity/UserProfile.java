package com.tinakit.moveit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.UserStatsRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;

import java.util.List;

/**
 * Created by Tina on 12/29/2015.
 */
public class UserProfile extends AppCompatActivity {


    // cache
    List<User> mUserList;

    //database
    FitnessDBHelper mDatabaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.recycler_view);

        mDatabaseHelper = FitnessDBHelper.getInstance(this);

        // fetch directly from the database
        mUserList = mDatabaseHelper.getUsers();


        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        UserStatsRecyclerAdapter mUserStatsRecyclerAdapter = new UserStatsRecyclerAdapter(this, this, mUserList);
        mRecyclerView.setAdapter(mUserStatsRecyclerAdapter);

        }
}
