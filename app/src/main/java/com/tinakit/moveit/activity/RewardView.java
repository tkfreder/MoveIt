package com.tinakit.moveit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.RewardRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.fragment.UserStatsMain;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 9/23/2015.
 */
public class RewardView extends Fragment {

    // CONSTANTS
    public static final String REWARD_VIEW_TAG= "REWARD_VIEW_TAG";
    public static final String REWARD_VIEW_USER="REWARD_VIEW_USER";

    @Inject
    FitnessDBHelper mDatabaseHelper;

    // CACHE
    private User mUser;

    //UI Widgets
    private RecyclerView mRecyclerView;
    private ImageView mAvatar;
    private TextView mTotalCoins_textview;
    private RewardRecyclerAdapter mRewardRecyclerAdapter;
    protected FragmentActivity mFragmentActivity;
    private View rootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.reward_view, container, false);
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Dagger 2 injection
        ((CustomApplication)getActivity().getApplication()).getAppComponent().inject(this);

        initializeUI();

        fetchData();

        return rootView;
    }

    private void initializeUI(){

        //wire up UI components
        mAvatar = (ImageView)rootView.findViewById(R.id.avatar);
        mTotalCoins_textview = (TextView)rootView.findViewById(R.id.coinTotal);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
    }

    private void fetchData() {


        //Bundle bundle = getIntent().getExtras();

        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(REWARD_VIEW_USER)) {

            mUser = bundle.getParcelable(REWARD_VIEW_USER);

            // if this is the first time, there will be no data in the bundle
            if (mUser == null) {

                // redirect to UserStats screen
                //Intent intent = new Intent(this, UserStats.class);

                // check if UserStats is already displayed
                UserStatsMain userStatsMain = (UserStatsMain) getActivity().getSupportFragmentManager().findFragmentByTag(UserStatsMain.USER_STATS_TAG);
                if (userStatsMain == null) {

                    userStatsMain = new UserStatsMain();
                    //replace current fragment with Rewards fragment
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, userStatsMain).commit();

                }
            } else {

                displayRewards();
            }
        }
    }
/*
        if (bundle.containsKey("user")){

            mUser = bundle.getParcelable("user");

            // if this is the first time, there will be data in the bundle
            if (mUser == null){

                // redirect to UserStats screen
                //Intent intent = new Intent(this, UserStats.class);

                UserStats userStats = (UserStats)getActivity().getSupportFragmentManager().findFragmentByTag(UserStats.USER_STATS_TAG);
                if (userStats == null) {

                    userStats = new UserStats();
                    //replace current fragment with Rewards fragment
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, userStats).commit();

                }
            }
            else {

                displayRewards();
            }
        }
*/


    private void displayRewards(){

        mAvatar.setImageResource(getResources().getIdentifier(mUser.getAvatarFileName(), "drawable", getActivity().getPackageName()));

        //TODO: check points are rounding in a consistent way throughout code, including updating DB
        mTotalCoins_textview.setText(String.format("%d", mUser.getPoints()));

        List<Reward> rewardList = mDatabaseHelper.getAllRewards();

        //RecyclerView
        // Initialize recycler view
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRewardRecyclerAdapter = new RewardRecyclerAdapter(mUser, getActivity());
        mRecyclerView.setAdapter(mRewardRecyclerAdapter);

    }

}