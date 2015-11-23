package com.tinakit.moveit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.RewardRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.User;

import java.util.List;

/**
 * Created by Tina on 9/23/2015.
 */
public class RewardView extends AppCompatActivity {

    //UI Widgets
    private TextView mUserName;
    private RecyclerView mRecyclerView;
    private ImageView mAvatar;
    private TextView mMessage;
    private TextView mTotalCoins_textview;
    private RewardRecyclerAdapter mRewardRecyclerAdapter;

    FitnessDBHelper mDatabaseHelper;
    User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.reward_view);

        //wire up UI components
        initialize();

        //if this is a refresh of the screen, get the userId
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("user")) {
                mUser = getIntent().getExtras().getParcelable("user");

                displayRewards();

            }
        }

    }

    private void initialize(){

        mUserName = (TextView)findViewById(R.id.username);
        mAvatar = (ImageView)findViewById(R.id.avatar);
        mTotalCoins_textview = (TextView)findViewById(R.id.coinTotal);
        mMessage = (TextView)findViewById(R.id.message);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mDatabaseHelper = FitnessDBHelper.getInstance(getApplicationContext());
    }

    private void displayRewards(){

        mUserName.setText(mUser.getUserName());
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRewardRecyclerAdapter = new RewardRecyclerAdapter(this, mUser);
        mRecyclerView.setAdapter(mRewardRecyclerAdapter);

    }

}