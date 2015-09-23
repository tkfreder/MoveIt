package com.tinakit.moveit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.TrackerActivity;
import com.tinakit.moveit.adapter.ActivityDetailListAdapter;
import com.tinakit.moveit.adapter.RewardListAdapter;
import com.tinakit.moveit.model.Reward;

import java.util.ArrayList;

/**
 * Created by Tina on 9/23/2015.
 */
public class RewardViewFragment extends Fragment {

    private ArrayList<Reward> mRewardList;
    private RewardListAdapter mRewardListAdapter;

    //TODO: dummy data
    int mTotalCoins = 60;
    int mUserId = 1;

    //UI Widgets
    private ListView mRewardListView;
    private TextView mTotalCoins_textview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward_view, container, false);



        mRewardListView = (ListView)view.findViewById(R.id.rewardListView);
        mRewardListAdapter = new RewardListAdapter(getActivity(), mTotalCoins, mUserId );
        mRewardListView.setAdapter(mRewardListAdapter);

        //set data to list adapter
        //TODO: get this data from the database, for now using local method to populate dummy data
        //get Reward data for this user
        mRewardListAdapter.setList(getRewardList());

        //TODO: temp
        mTotalCoins_textview = (TextView)view.findViewById(R.id.coinTotal);
        mTotalCoins_textview.setText(String.valueOf(mTotalCoins));

    return view;
    }

    private ArrayList<Reward> getRewardList(){

        ArrayList<Reward> rewardList = new ArrayList<>();

        rewardList.add(new Reward("popsicle", 25, "popsicle dinner dessert"));
        rewardList.add(new Reward("park playdate", 50, "bring a friend to the park of your choice"));
        rewardList.add(new Reward("movie buddy", 75, "invite friend for movie night"));
        rewardList.add(new Reward("Jumpin' Jammin'", 100, "invite friend to Jumpin' Jammin'"));
        rewardList.add(new Reward("Pizza Party for 5", 150, "invite 4 friends for Pizza Party"));
        rewardList.add(new Reward("Family Roadtrip", 500, "family votes on a road trip"));

        return rewardList;
    }
}

