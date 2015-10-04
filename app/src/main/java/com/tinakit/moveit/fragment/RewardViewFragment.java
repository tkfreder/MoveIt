package com.tinakit.moveit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.RewardRecyclerAdapter;
import com.tinakit.moveit.model.Reward;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tina on 9/23/2015.
 */
public class RewardViewFragment extends Fragment {


    private RecyclerView mRecyclerView;

    //TODO: dummy data
    int mTotalCoins = 0;
    int mUserId = 1;

    //UI Widgets
    private TextView mTotalCoins_textview;

    //RecyclerView
    private List<Reward> mRewardList;
    private RewardRecyclerAdapter mRewardRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward_view, container, false);

        //get total coins out of intent
        mTotalCoins_textview = (TextView)view.findViewById(R.id.coinTotal);
        if(getActivity().getIntent() != null) {

            if  (getActivity().getIntent().getExtras().containsKey("total_coins")) {
                mTotalCoins = getActivity().getIntent().getExtras().getInt("total_coins");
                mTotalCoins_textview.setText(String.valueOf(mTotalCoins));
            }
        }

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRewardRecyclerAdapter = new RewardRecyclerAdapter(getActivity(), mTotalCoins, mUserId);
        mRecyclerView.setAdapter(mRewardRecyclerAdapter);

        return view;
    }

}

