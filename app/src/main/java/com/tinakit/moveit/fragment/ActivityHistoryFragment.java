package com.tinakit.moveit.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.RewardView;
import com.tinakit.moveit.activity.TrackerActivity;
import com.tinakit.moveit.adapter.ActivityDetailRecyclerAdapter;
import com.tinakit.moveit.adapter.ChooserRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tina on 9/22/2015.
 */
public class ActivityHistoryFragment extends Fragment {

    //UI Widgets
    private TextView mTotalCoins_textview;
    private RecyclerView mRecyclerView;
    private Button mRewardButton;


    private List<ActivityDetail> mActivityDetailList;
    private ActivityDetailRecyclerAdapter mActivityDetailRecyclerAdapter;
    private int mTotalCoins = 0;
    private User mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_history, container, false);

        //TODO: replace this with DB call or Application Preferences
        mUser = new User();
        mUser.setUserId(1);
        mUser.setUserName("Lucy");
        mUser.setIsAdmin(false);
        mUser.setWeight(40);
        mUser.setAvatarFileName("tiger");

        //TODO:  get Activity Detail history from DB
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(getActivity());
        mActivityDetailList = databaseHelper.getActivityDetailList(mUser.getUserId());

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActivityDetailRecyclerAdapter = new ActivityDetailRecyclerAdapter(getActivity(), mActivityDetailList);
        mRecyclerView.setAdapter(mActivityDetailRecyclerAdapter);

        //display coin total
        mTotalCoins_textview = (TextView)view.findViewById(R.id.coinTotal);
        mTotalCoins = getCoinTotal();
        mTotalCoins_textview.setText(String.valueOf(mTotalCoins));

        //button
        mRewardButton = (Button)view.findViewById(R.id.rewardButton);
        mRewardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), RewardView.class);
                Bundle bundle = new Bundle();
                bundle.putInt("total_coins", mTotalCoins);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }

    private int getCoinTotal(){

        float totalCoins = 0;

        for (ActivityDetail activityDetail : mActivityDetailList){

            totalCoins += activityDetail.getPointsEarned();
        }

        return Math.round(totalCoins);
    }
}
