package com.tinakit.moveit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.ActivityDetailRecyclerAdapter;
import com.tinakit.moveit.adapter.EditActivityRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.UnitSplitCalorie;
import com.tinakit.moveit.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tina on 9/22/2015.
 */
public class EditActivity extends AppCompatActivity {

    //UI Widgets
    private Button mRewardButton;


    private ActivityDetail mActivityDetail;
    private EditActivityRecyclerAdapter mEditActivityRecyclerAdapter;
    private TextView mUserList;
    private int mActivityId;
    private TextView mPoints;
    private TextView mDistance;
    private RecyclerView mRecyclerView;
    private int mTotalCoins = 0;
    private User mUser;

    FitnessDBHelper mDatabaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.edit_activity);

        if (getIntent() != null && getIntent().getExtras().containsKey("activityId")) {

            //save activityId
            mActivityId = getIntent().getExtras().getInt("activityId");

            //TODO:  get Activity Detail history from DB
            mDatabaseHelper = FitnessDBHelper.getInstance(this);
            mActivityDetail = mDatabaseHelper.getActivityDetail(mActivityId);

            if (mActivityDetail != null){

                mPoints = (TextView) findViewById(R.id.points);
                mPoints.setText(String.valueOf(mActivityDetail.getPointsEarned()));

                mDistance = (TextView) findViewById(R.id.distanceInFeet);
                mDistance.setText(String.valueOf(mActivityDetail.getDistanceInFeet()));
            }

            //TODO: create DB table Activity_Users
            mUserList = (TextView) findViewById(R.id.userList);


            //RecyclerView
            // Initialize recycler view
            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

            List<UnitSplitCalorie> unitSplitList = new ArrayList<>();
            unitSplitList = mDatabaseHelper.getActivityLocationData(mActivityId);


            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mEditActivityRecyclerAdapter = new EditActivityRecyclerAdapter(this, unitSplitList, this);
            mRecyclerView.setAdapter(mEditActivityRecyclerAdapter);

            //button
            mRewardButton = (Button) findViewById(R.id.rewardButton);
            mRewardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), RewardView.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("total_coins", mTotalCoins);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        else {

            startActivity(new Intent(this, ActivityHistory.class));
        }

    }

}
