package com.tinakit.moveit.fragment;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.charts.SeriesLabel;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.UserStatsRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 12/19/2015.
 */
public class UserStats extends Fragment{

    // CONSTANTS
    public static final String USER_STATS_TAG = "USER_STATS_TAG";
    public static final String USER_STATS_LIST_KEY = "USER_STATS_LIST";

    @Inject
    FitnessDBHelper mDatabaseHelper;

    // local cache
    protected static List<User> mUserList;
    protected FragmentActivity mFragmentActivity;
    private View rootView;

    // UI COMPONENTS
    protected RecyclerView mRecyclerView;
    protected UserStatsRecyclerAdapter mUserStatsRecyclerAdapter;
    protected SeriesItem seriesItem2;
    protected TextView textPercentage;
    List<SeriesItem> mSeriesItemList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity  = (FragmentActivity)super.getActivity();
        //rootView = inflater.inflate(R.layout.recycler_view, container, false);
        rootView = inflater.inflate(R.layout.user_stats, container, false);

        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Dagger 2 injection
        ((CustomApplication)getActivity().getApplication()).getAppComponent().inject(this);

        //get databaseHelper instance
        //mDatabaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);

        initializeUI();

        //fetchData(inflater);

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
            user.setChildItemList(mDatabaseHelper.getUserRewards(user));
        }

        //mUserStatsRecyclerAdapter = new UserStatsRecyclerAdapter(layoutInflater.getContext(), mFragmentActivity, mUserList);
        //mRecyclerView.setAdapter(mUserStatsRecyclerAdapter);
    }

    private void initializeUI(){

        textPercentage = (TextView) rootView.findViewById(R.id.textPercentage);

        List<User> userList = new ArrayList<User>();
        User user = new User();

        user.setPoints(250);
        user.setUserName("Laura");
        userList.add(user);

        user = new User();
        user.setPoints(280);
        user.setUserName("Lucy");
        userList.add(user);

        user = new User();
        user.setPoints(20);
        user.setUserName("Alec");
        userList.add(user);

        user = new User();
        user.setPoints(50);
        user.setUserName("Tina");
        userList.add(user);

        List<Reward> rewardList = new ArrayList<>();
        Reward reward = new Reward();
        reward.setName("Animal Jam 5 Diamonds");
        reward.setPoints(350);
        reward.setUserId(1);
        rewardList.add(reward);
        userList.get(0).setChildItemList(rewardList);


        reward = new Reward();
        reward.setName("Chocolate Chip Pancake Dinner");
        reward.setPoints(300);
        reward.setUserId(2);
        rewardList = new ArrayList<>();
        rewardList.add(reward);
        userList.get(1).setChildItemList(rewardList);

        reward = new Reward();
        reward.setName("Arclight Movie Night");
        reward.setPoints(200);
        reward.setUserId(3);
        rewardList = new ArrayList<>();
        rewardList.add(reward);
        userList.get(2).setChildItemList(rewardList);

        reward = new Reward();
        reward.setName("Dinner Out");
        reward.setPoints(200);
        reward.setUserId(4);
        rewardList = new ArrayList<>();
        rewardList.add(reward);
        userList.get(3).setChildItemList(rewardList);


        // sample data
        List<Integer> percentageList = new ArrayList<>();

        for (int i = 0; i < userList.size(); i++){

            int percentage = Math.round(100 * userList.get(i).getPoints() / userList.get(i).getChildItemList().get(0).getPoints());
            percentageList.add(percentage);
        }

        DecoView decoView = (DecoView) rootView.findViewById(R.id.dynamicArcView);

        // background arc
        SeriesItem backgroundSeries = new SeriesItem.Builder(Color.argb(255,211,211,211))
                .setRange(0, 100, 0)
                .build();

        int backIndex = decoView.addSeries(backgroundSeries);

        decoView.addEvent(new DecoEvent.Builder(100)
                .setIndex(backIndex)
                .build());

        // populate color array
        List<Integer> mColorList = new ArrayList<>();

        mColorList.add(Color.argb(255,255,133,17));
        mColorList.add(Color.argb(255,194,24,91));
        mColorList.add(Color.argb(255,255,87,34));
        mColorList.add(Color.argb(255,25,118,210));
        mColorList.add(Color.argb(255,76,175,80));
        mColorList.add(Color.argb(255,93,64,55));
        mColorList.add(Color.argb(255,211,47,47));


        // add item series for each user
        for (int i=0; i < userList.size(); i++){

            // first arc
            SeriesItem seriesItem2 = new SeriesItem.Builder(mColorList.get(i%7))
                    .setRange(0, 100, 0)
                    .setLineWidth(32f)
                    .setInset(new PointF((float)(i-1)*32, (float)(i-1)*32))
                    .setSeriesLabel(new SeriesLabel.Builder(userList.get(i).getUserName() + " %.0f%%")
                            .setVisible(true)
                            .setColorBack(Color.argb(150, 0, 0, 0))
                            .setColorText(Color.argb(255, 255, 255, 255))
                                    //.setTypeface(customTypeface) //Load the font from your Android assets folder
                            .build())
                    .build();

            int series1Index = decoView.addSeries(seriesItem2);

            seriesItem2.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
                @Override
                public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {

                    float percentFilled = ((currentPosition) / 100);
                    textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
                }

                @Override
                public void onSeriesItemDisplayProgress(float percentComplete) {

                }
            });


            decoView.addEvent(new DecoEvent.Builder(percentageList.get(i))
                    .setIndex(series1Index)
                    .setDelay(5000 * i)
                    .build());

        }



        /*
        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.

        //GridLayoutManager gridLayoutManager = new GridLayoutManager(mFragmentActivity, 2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mFragmentActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        */
    }

}
