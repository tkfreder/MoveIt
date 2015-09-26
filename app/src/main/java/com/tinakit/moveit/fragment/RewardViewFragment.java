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
            //TODO: to delete
            if (getActivity().getIntent().getExtras().containsKey("reward_list")){

                mRewardList = RewardRecyclerAdapter.mRewardList;

            } else{

                //TODO: get this data from the database, for now using local method to populate dummy data
                //get Reward data for this user
                mRewardList = getRewardList();
            }
        }



        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRewardRecyclerAdapter = new RewardRecyclerAdapter(getActivity(), mRewardList, mTotalCoins, mUserId);
        mRecyclerView.setAdapter(mRewardRecyclerAdapter);

        return view;
    }

    private ArrayList<Reward> getRewardList(){

        ArrayList<Reward> rewardList = new ArrayList<>();

        rewardList.add(new Reward("popsicle", 1, "popsicle dinner dessert"));
        rewardList.add(new Reward("park playdate", 2, "bring a friend to the park of your choice"));
        rewardList.add(new Reward("movie buddy", 5, "invite friend for movie night"));
        rewardList.add(new Reward("Jumpin' Jammin'", 10, "invite friend to Jumpin' Jammin'"));
        rewardList.add(new Reward("Pizza Party for 5", 12, "invite 4 friends for Pizza Party"));
        rewardList.add(new Reward("Family Roadtrip", 20, "family votes on a road trip"));

        return rewardList;
    }
}

