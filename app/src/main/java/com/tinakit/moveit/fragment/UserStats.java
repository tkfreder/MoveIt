package com.tinakit.moveit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.UserStatsRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 12/19/2015.
 */
public class UserStats extends Fragment{

    // CONSTANTS
    public static final String USER_STATS_TAG = "USER_STATS_TAG";
    public static final String USER_STATS_LIST_KEY = "USER_STATS_LIST";

    // local cache
    protected static List<User> mUserList;
    protected FragmentActivity mFragmentActivity;
    private View rootView;

    // UI COMPONENTS
    protected RecyclerView mRecyclerView;
    protected UserStatsRecyclerAdapter mUserStatsRecyclerAdapter;

    @Inject
    FitnessDBHelper mDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.recycler_view, container, false);

        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // inject FitnessDBHelper
        ((CustomApplication)getActivity().getApplication()).getStorageComponent().inject(this);

        //get databaseHelper instance
        //mDatabaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);

        initializeUI();

        fetchData(inflater);

        return rootView;
    }

    private void fetchData(LayoutInflater layoutInflater){

        // get UserActivityList from intent

        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(USER_STATS_LIST_KEY)){

            mUserList = bundle.getParcelableArrayList(USER_STATS_LIST_KEY);
        }
        else{
            // fetch directly from the database
            mUserList = mDatabaseHelper.getUsers();
        }

        //set RewardList for each user
        for (User user : mUserList){

            //set the reward list for each user
            user.setChildItemList(mDatabaseHelper.getUserRewards(user.getUserId()));
        }

        mUserStatsRecyclerAdapter = new UserStatsRecyclerAdapter(layoutInflater.getContext(), mFragmentActivity, mUserList);
        mRecyclerView.setAdapter(mUserStatsRecyclerAdapter);
    }

    private void initializeUI(){

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mFragmentActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

}
