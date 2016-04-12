package com.tinakit.moveit.fragment;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.charts.SeriesLabel;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.UserStatsRecyclerAdapter;
import com.tinakit.moveit.adapter.ViewPagerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;
import com.tinakit.moveit.utility.CheatSheet;
import com.tinakit.moveit.utility.DateUtility;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 1/24/2016.
 */
public class UserStats extends Fragment {

    // CONSTANTS
    public static final String USER_STATS_TAG = "USER_STATS_TAG";
    public static final String USER_STATS_LIST_KEY = "USER_STATS_LIST";
    public static final String USER_STATS_ARG_USER = "USER_STATS_ARG_USER";
    private static final float ARC_LINE_WIDTH = 64f;

    @Inject
    FitnessDBHelper mDatabaseHelper;

    // local cache
    protected FragmentActivity mFragmentActivity;
    private View rootView;
    protected List<Integer> mColorList;
    protected User mUser;
    private int mRewardPoints;

    // UI COMPONENTS
    protected TextView textPercentage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentActivity  = super.getActivity();
        rootView = inflater.inflate(R.layout.user_stats, container, false);

        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Dagger 2 injection
        ((CustomApplication)getActivity().getApplication()).getAppComponent().inject(this);

        initializeUI();

        return rootView;
    }

    private void initializeUI(){

        Bundle args = getArguments();
        if(args != null && args.containsKey(USER_STATS_ARG_USER)){

            mUser = args.getParcelable(USER_STATS_ARG_USER);

            // get rewards earned for this user
            List<Reward> rewardList = mDatabaseHelper.getRewardsEarned(mUser);
            List<Reward> rewardListToFulfill = new ArrayList<>();

            if (rewardList.size() > 0){


                for (Reward reward : rewardList){

                    if(reward.getDateFulfilled() == null){

                        rewardListToFulfill.add(reward);

                    }
                    // display ribbons for rewards already fulfilled
                    else{

                        TextView rewardsEarned = (TextView)rootView.findViewById(R.id.rewardsEarned);
                        rewardsEarned.setVisibility(View.VISIBLE);

                        ImageView certificate = new ImageView(mFragmentActivity);
                        certificate.setImageResource(getResources().getIdentifier("ribbon", "drawable", mFragmentActivity.getPackageName()));
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100,100);
                        certificate.setLayoutParams(layoutParams);
                        CheatSheet.setup(certificate,DateUtility.getDateFormattedRecent(reward.getDateEarned(), 7));
                        LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.rewardLayout);
                        layout.addView(certificate);
                    }
                }
            }


            // if reward is earned and has not been fulfilled yet, display ribbon
            if ( rewardListToFulfill.size() > 0 ){

                RelativeLayout ribbonLayout = (RelativeLayout)rootView.findViewById(R.id.ribbonLayout);
                ribbonLayout.setVisibility(View.VISIBLE);

                TextView textPercentage = (TextView)rootView.findViewById(R.id.textPercentage);
                textPercentage.setVisibility(View.GONE);

            }


            mColorList = new ArrayList<>();

            mColorList.add(Color.argb(255, 76, 175, 80)); // green
            mColorList.add(Color.argb(255,194,24,91)); // violet
            mColorList.add(Color.argb(255,25,118,210)); //blue
            mColorList.add(Color.argb(255,255,87,34)); // red
            mColorList.add(Color.argb(255,255,133,17)); // orange
            mColorList.add(Color.argb(255,93,64,55));
            mColorList.add(Color.argb(255,211,47,47));

            textPercentage = (TextView) rootView.findViewById(R.id.textPercentage);
            mRewardPoints = mUser.getReward().getPoints();

            TextView userName = (TextView)rootView.findViewById(R.id.userName);
            userName.setText(mUser.getUserName());

            TextView points = (TextView)rootView.findViewById(R.id.points);
            points.setText(String.valueOf(mUser.getPoints()));

            TextView rewardName = (TextView)rootView.findViewById(R.id.rewardName);
            rewardName.setText(mUser.getReward().getName());

            DecoView decoView = (DecoView) rootView.findViewById(R.id.dynamicArcView);

            // background arc
            SeriesItem backgroundSeries = new SeriesItem.Builder(Color.argb(255,211,211,211))
                    .setRange(0, 100, 0)
                    .setLineWidth(ARC_LINE_WIDTH)
                    .build();

            int backIndex = decoView.addSeries(backgroundSeries);

            decoView.addEvent(new DecoEvent.Builder(100)
                    .setIndex(backIndex)
                    .build());

            SeriesItem seriesItem2 = new SeriesItem.Builder(mColorList.get(mUser.getUserId() % mColorList.size()))
                    .setRange(0, 100, 0)
                    .setLineWidth(ARC_LINE_WIDTH)
                    .setSeriesLabel(new SeriesLabel.Builder(" %.0f%%")
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
                    int pointsRemaining = mRewardPoints >= mUser.getPoints() ? mRewardPoints - mUser.getPoints() : mRewardPoints;
                    textPercentage.setText(String.format("%d more " + (pointsRemaining == 1 ? "coin" : "coins") + " to go!", pointsRemaining));
                }

                @Override
                public void onSeriesItemDisplayProgress(float percentComplete) {

                    textPercentage.setText("");
                }
            });

            int percentage = rewardListToFulfill.size() == 0 ? Math.round(100 * (mUser.getPoints() % mRewardPoints) / mRewardPoints) :  100;

            decoView.addEvent(new DecoEvent.Builder(percentage)
                    .setIndex(series1Index)
                    .setDelay(2000)
                    .build());

            }


            }


        }

