package com.tinakit.moveit.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.RewardView;
import com.tinakit.moveit.adapter.UserStatsExpandableAdapter;
import com.tinakit.moveit.adapter.view_holder.RewardChildViewHolder;
import com.tinakit.moveit.adapter.view_holder.RewardParentViewHolder;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.RewardStatusType;
import com.tinakit.moveit.model.User;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Tina on 12/19/2015.
 */
public class UserStats extends Fragment{

    // CONSTANTS
    public static final String USER_STATS_LIST = "USER_STATS_LIST";

    // local cache
    protected static List<User> mUserList;
    protected FragmentActivity mFragmentActivity;
    private View rootView;

    // UI COMPONENTS
    protected RecyclerView mRecyclerView;
    public static UserStatsExpandableAdapter mUserStatsExpandableAdapter;

    //database
    FitnessDBHelper mDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.user_stats_list, container, false);

        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //get databaseHelper instance
        mDatabaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);

        initializeUI();

        fetchData();

        return rootView;
    }

    private void fetchData(){

        // get UserActivityList from intent

        Bundle bundle = this.getArguments();
        if (bundle.containsKey(USER_STATS_LIST)){

            mUserList = bundle.getParcelableArrayList(USER_STATS_LIST);

            // if this is the first time, there will be data in the bundle
            if (mUserList == null){

                // fetch directly from the database
                mUserList = mDatabaseHelper.getUsers();
            }
        }

        mUserStatsExpandableAdapter = new UserStatsExpandableAdapter(getContext(), mFragmentActivity, mUserList);
        mRecyclerView.setAdapter(mUserStatsExpandableAdapter);
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
