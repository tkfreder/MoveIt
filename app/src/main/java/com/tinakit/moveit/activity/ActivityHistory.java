package com.tinakit.moveit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.ActivityHistoryRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.UnitSplitCalorie;

import java.util.List;

/**
 * Created by Tina on 10/19/2015.
 */
public class ActivityHistory  extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ActivityHistoryRecyclerAdapter mActivityHistoryRecyclerAdapter;
    private List<UnitSplitCalorie> unitList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_history);

        //TODO:  get Activity Detail history from DB
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(this);
        unitList = databaseHelper.getFirstLocationPoints();

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mActivityHistoryRecyclerAdapter = new ActivityHistoryRecyclerAdapter(this, unitList);
        mRecyclerView.setAdapter(mActivityHistoryRecyclerAdapter);
    }
}
