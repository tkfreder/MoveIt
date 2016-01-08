package com.tinakit.moveit.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.RewardRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.fragment.UserStats;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.RewardStatusType;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 9/23/2015.
 */
public class RewardView  extends AppCompatActivity {

    @Inject
    FitnessDBHelper mDatabaseHelper;

    // CACHE
    private User mUser;

    //UI Widgets
    private RecyclerView mRecyclerView;
    private ImageView mAvatar;
    private TextView mMessage;
    private TextView mTotalCoins_textview;
    private RewardRecyclerAdapter mRewardRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward_view);
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Dagger 2 injection
        ((CustomApplication)getApplication()).getAppComponent().inject(this);

        initializeUI();

        fetchData();
    }


    private void initializeUI(){

        //wire up UI components
        mAvatar = (ImageView)findViewById(R.id.avatar);
        mTotalCoins_textview = (TextView)findViewById(R.id.coinTotal);
        mMessage = (TextView)findViewById(R.id.message);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
    }

    private void fetchData(){


        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("user")){

            mUser = bundle.getParcelable("user");

            // if this is the first time, there will be data in the bundle
            if (mUser == null){

                // redirect to UserStats screen
                Intent intent = new Intent(this, UserStats.class);
            }
            else {

                displayRewards();
            }
        }

    }

    private void displayRewards(){

        mAvatar.setImageResource(getResources().getIdentifier(mUser.getAvatarFileName(), "drawable", getPackageName()));

        //TODO: check points are rounding in a consistent way throughout code, including updating DB
        mTotalCoins_textview.setText(String.format("%d", mUser.getPoints()));

        List<Reward> rewardList = mDatabaseHelper.getAllRewards();

        if (rewardList.size() != 0){

            //display message if not enough coins to redeem reward
            if (rewardList.get(0).getPoints() > mUser.getPoints()){

                mMessage.setVisibility(View.VISIBLE);
                mMessage.setText("You need " + String.valueOf(rewardList.get(0).getPoints() - mUser.getPoints()) + " more coins to get a reward.");
            }
        }

        //RecyclerView
        // Initialize recycler view
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRewardRecyclerAdapter = new RewardRecyclerAdapter(getApplicationContext(), mUser, this);
        mRecyclerView.setAdapter(mRewardRecyclerAdapter);

    }

}