package com.tinakit.moveit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.UserStatsRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;

import java.util.List;

/**
 * Created by Tina on 12/29/2015.
 */
public class UserProfile extends Fragment {

    // constants
    public static final String USER_PROFILE_TAG = "USER_PROFILE_TAG";

    // cache
    protected FragmentActivity mFragmentActivity;
    private View rootView;
    List<User> mUserList;

    //database
    FitnessDBHelper mDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.recycler_view, container, false);

        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //get databaseHelper instance
        mDatabaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);

        // fetch directly from the database
        mUserList = mDatabaseHelper.getUsers();


        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mFragmentActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        UserStatsRecyclerAdapter mUserStatsRecyclerAdapter = new UserStatsRecyclerAdapter(getActivity(), mFragmentActivity, mUserList);
        mRecyclerView.setAdapter(mUserStatsRecyclerAdapter);


        return rootView;
    }
}
