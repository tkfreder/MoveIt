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

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.EditActivityRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.UnitSplit;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.utility.DateUtility;
import com.tinakit.moveit.utility.Map;

import java.util.ArrayList;
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
    private TextView mStartDate;
    private TextView mStreetName;
    private TextView mPoints;
    private TextView mDistance;
    private SupportStreetViewPanoramaFragment streetViewPanoramaFragment;
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

                mStartDate = (TextView)findViewById(R.id.startDate);
                mStartDate.setText(DateUtility.getDateFormattedRecent(mActivityDetail.getStartDate(), 7));

                mStreetName = (TextView)findViewById(R.id.streetName);
                mStreetName.setText(Map.getStreetName(EditActivity.this, mActivityDetail.getStartLocation()));

                mPoints = (TextView) findViewById(R.id.points);
                //mPoints.setText(String.format("%.0f", mActivityDetail.getPointsEarned()));

                mDistance = (TextView) findViewById(R.id.distanceInFeet);
                mDistance.setText(String.format("%.0f", mActivityDetail.getDistanceInFeet()));
            }

            //TODO: create DB table Activity_Users
            mUserList = (TextView) findViewById(R.id.userList);


            StreetViewPanoramaOrientation.Builder builder = StreetViewPanoramaOrientation.builder().tilt(-10);

            //streetview panorama
            SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                    (SupportStreetViewPanoramaFragment)
                            getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);
            streetViewPanoramaFragment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
                @Override
                public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                    streetViewPanorama.setPosition(new LatLng(mActivityDetail.getStartLocation().latitude, mActivityDetail.getStartLocation().longitude));
                 }
            });


            //RecyclerView
            // Initialize recycler view
            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

            List<UnitSplit> unitSplitList = new ArrayList<>();
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
                    startActivity(intent);
                }
            });

        }

        else {

            startActivity(new Intent(this, ActivityHistory.class));
        }

    }

}
