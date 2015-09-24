package com.tinakit.moveit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.TrackerActivity;
import com.tinakit.moveit.adapter.ActivityDetailListAdapter;
import com.tinakit.moveit.adapter.ActivityDetailRecyclerAdapter;
import com.tinakit.moveit.adapter.ChooserRecyclerAdapter;
import com.tinakit.moveit.model.ActivityDetail;

import java.util.ArrayList;

/**
 * Created by Tina on 9/22/2015.
 */
public class ActivityHistoryFragment extends Fragment {

    //UI Widgets
    //private ListView mActivityDetailListView;
    private TextView mTotalCoins_textview;

    //TODO: delete
    //private ActivityDetailListAdapter mActivityDetailListAdapter;
    //private ArrayList<ActivityDetail> mActivityDetailList = new ArrayList<>();

    private ArrayList<ActivityDetail> mActivityDetailList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ActivityDetailRecyclerAdapter mActivityDetailRecyclerAdapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_history, container, false);

        //TODO: delete
        /*
        mActivityDetailListView = (ListView)view.findViewById(R.id.activityDetailListView);
        mActivityDetailListAdapter = new ActivityDetailListAdapter(getActivity());
        mActivityDetailListView.setAdapter(mActivityDetailListAdapter);

        //get list data
        mActivityDetailList = TrackerActivity.mActivityDetailList;

        //set data to list adapter
        mActivityDetailListAdapter.setList(mActivityDetailList);
        */

        mActivityDetailList = TrackerActivity.mActivityDetailList;

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActivityDetailRecyclerAdapter = new ActivityDetailRecyclerAdapter(getActivity(), mActivityDetailList);
        mRecyclerView.setAdapter(mActivityDetailRecyclerAdapter);

        //display coin total
        mTotalCoins_textview = (TextView)view.findViewById(R.id.coinTotal);
        mTotalCoins_textview.setText(String.valueOf(getCoinTotal()));

        //mActivityDetailListAdapter.notifyDataSetChanged();


        return view;
    }

    private int getCoinTotal(){

        float totalCoins = 0;

        for (ActivityDetail activityDetail : mActivityDetailList){

            totalCoins += activityDetail.getCoinsEarned();
        }

        return Math.round(totalCoins);
    }
}
