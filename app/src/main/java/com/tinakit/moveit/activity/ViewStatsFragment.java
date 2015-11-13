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

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.ViewStatsRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;

/**
 * Created by Tina on 10/26/2015.
 */
public class ViewStatsFragment extends Fragment {

    FragmentActivity mFragmentActivity;

    //UI Widgets
    private RecyclerView mRecyclerView;
    private ViewStatsRecyclerAdapter mViewStatsRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentActivity  = (FragmentActivity)    super.getActivity();
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View rootView = inflater.inflate(R.layout.view_stats, container, false);

        //fix the orientation to portrait
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Get singleton instance of database
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);


        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.


        // The number of Columns
        mRecyclerView.setLayoutManager(new GridLayoutManager(mFragmentActivity, 2));
        mViewStatsRecyclerAdapter = new ViewStatsRecyclerAdapter(mFragmentActivity, databaseHelper.getUsers());
        mRecyclerView.setAdapter(mViewStatsRecyclerAdapter);

        return rootView;
    }
}
