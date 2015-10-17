package com.tinakit.moveit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.RewardRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tina on 9/23/2015.
 */
public class RewardView extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TextView mMessage;

    //TODO: dummy data
    int mTotalCoins = 0;
    int mUserId = 1;

    //UI Widgets
    private TextView mTotalCoins_textview;

    //RecyclerView
    private RewardRecyclerAdapter mRewardRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.reward_view);

        //get total coins out of intent
        mTotalCoins_textview = (TextView)findViewById(R.id.coinTotal);
        if(getIntent() != null) {

            if  (getIntent().getExtras().containsKey("total_coins")) {
                mTotalCoins = getIntent().getExtras().getInt("total_coins");
                mTotalCoins_textview.setText(String.valueOf(mTotalCoins));
            }
        } else {

            mTotalCoins_textview.setText("0");
        }

        //display message if not enough coins to redeem reward
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(getApplicationContext());
        List<Reward> rewardList = databaseHelper.getAllRewards();
        if (rewardList.size() != 0){
            int minCoins = Integer.parseInt(mTotalCoins_textview.getText().toString());
            if (rewardList.get(0).getPoints() > minCoins){
                mMessage = (TextView)findViewById(R.id.message);
                mMessage.setText("You need " + String.valueOf(minCoins - rewardList.get(0).getPoints()) + " to get a reward.");
            }
        }

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRewardRecyclerAdapter = new RewardRecyclerAdapter(this, mTotalCoins, mUserId);
        mRecyclerView.setAdapter(mRewardRecyclerAdapter);
    }
}