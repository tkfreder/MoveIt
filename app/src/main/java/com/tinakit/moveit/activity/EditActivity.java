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
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;

import java.util.List;

/**
 * Created by Tina on 9/22/2015.
 */
public class EditActivity extends AppCompatActivity {

    //UI Widgets
    private TextView mTotalCoins_textview;
    private RecyclerView mRecyclerView;
    private Button mRewardButton;


    private List<com.tinakit.moveit.model.ActivityDetail> mActivityDetailList;
    private ActivityDetailRecyclerAdapter mActivityDetailRecyclerAdapter;
    private int mTotalCoins = 0;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.edit_activity);

        //TODO: replace this with DB call or Application Preferences
        mUser = new User();
        mUser.setUserId(1);
        mUser.setUserName("Lucy");
        mUser.setIsAdmin(false);
        mUser.setWeight(40);
        mUser.setAvatarFileName("tiger");

        //TODO:  get Activity Detail history from DB
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(this);
        mActivityDetailList = databaseHelper.getActivityDetailList(mUser.getUserId());

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mActivityDetailRecyclerAdapter = new ActivityDetailRecyclerAdapter(this, mActivityDetailList);
        mRecyclerView.setAdapter(mActivityDetailRecyclerAdapter);

        //display coin total
        mTotalCoins_textview = (TextView)findViewById(R.id.coinTotal);
        mTotalCoins = getCoinTotal();
        mTotalCoins_textview.setText(String.valueOf(mTotalCoins));

        //button
        mRewardButton = (Button)findViewById(R.id.rewardButton);
        mRewardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),RewardView.class);
                Bundle bundle = new Bundle();
                bundle.putInt("total_coins", mTotalCoins);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }

    private int getCoinTotal(){

        float totalCoins = 0;

        for (com.tinakit.moveit.model.ActivityDetail activityDetail : mActivityDetailList){

            totalCoins += activityDetail.getPointsEarned();
        }

        return Math.round(totalCoins);
    }
}
