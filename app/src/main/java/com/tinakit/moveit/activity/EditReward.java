package com.tinakit.moveit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.EditRewardRecyclerAdapter;

/**
 * Created by Tina on 10/4/2015.
 */
public class EditReward extends AppCompatActivity {

    //RecyclerView
    private RecyclerView mRecyclerView;
    private EditRewardRecyclerAdapter mEditRewardRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.edit_reward);

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mEditRewardRecyclerAdapter = new EditRewardRecyclerAdapter(this);
        mRecyclerView.setAdapter(mEditRewardRecyclerAdapter);
    }
}
