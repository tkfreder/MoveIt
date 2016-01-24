package com.tinakit.moveit.fragment;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.UserStatsRecyclerAdapter;
import com.tinakit.moveit.adapter.ViewPagerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

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

    @Inject
    FitnessDBHelper mDatabaseHelper;

    // local cache
    protected FragmentActivity mFragmentActivity;
    private View rootView;
    List<Integer> mColorList;
    List<User> mUserList;
    User mUser;

    // UI COMPONENTS
    protected RecyclerView mRecyclerView;
    protected UserStatsRecyclerAdapter mUserStatsRecyclerAdapter;
    protected SeriesItem seriesItem2;
    protected TextView textPercentage;
    List<SeriesItem> mSeriesItemList;
    protected ViewPager mViewPager;
    protected ViewPagerAdapter mViewPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentActivity  = (FragmentActivity)super.getActivity();
        //rootView = inflater.inflate(R.layout.recycler_view, container, false);
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

            mUser = (User)args.getParcelable(USER_STATS_ARG_USER);
        }

        mColorList = new ArrayList<>();

        mColorList.add(Color.argb(255, 76, 175, 80)); // green
        mColorList.add(Color.argb(255,194,24,91)); // violet
        mColorList.add(Color.argb(255,25,118,210)); //blue
        mColorList.add(Color.argb(255,255,87,34)); // red
        mColorList.add(Color.argb(255,255,133,17)); // orange
        mColorList.add(Color.argb(255,93,64,55));
        mColorList.add(Color.argb(255,211,47,47));

        // add textview for each user
        LinearLayout userLayout = (LinearLayout)rootView.findViewById(R.id.userLayout);

        mUserList = mDatabaseHelper.getUsers();

        for (int i = 0; i < mUserList.size(); i++){

            TextView textView = new TextView(mFragmentActivity);
            textView.setId(mUserList.get(i).getUserId());
            textView.setTextColor(mColorList.get(i % mColorList.size()));
            textView.setTextSize(16);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText(mUserList.get(i).getUserName() + " " + String.valueOf(mUserList.get(i).getPoints()) + "(" + 100 * mUserList.get(i).getPoints()/mUserList.get(i).getChildItemList().get(0).getPoints() + "%)" + " " + mUserList.get(i).getChildItemList().get(0).getName());
            userLayout.addView(textView);

        }

        textPercentage = (TextView) rootView.findViewById(R.id.textPercentage);

        List<Integer> percentageList = new ArrayList<>();

        for (int i = 0; i < mUserList.size(); i++){

            int percentage = Math.round(100 * mUserList.get(i).getPoints() / mUserList.get(i).getChildItemList().get(0).getPoints());
            percentageList.add(percentage);
        }

        DecoView decoView = (DecoView) rootView.findViewById(R.id.dynamicArcView);

        // background arc
        SeriesItem backgroundSeries = new SeriesItem.Builder(Color.argb(255,211,211,211))
                .setRange(0, 100, 0)
                .setLineWidth((float) mUserList.size() * 32)
                .build();

        int backIndex = decoView.addSeries(backgroundSeries);

        decoView.addEvent(new DecoEvent.Builder(100)
                .setIndex(backIndex)
                .build());

        // add item series for each user
        for (int i=0; i < mUserList.size(); i++){

            // first arc
            SeriesItem seriesItem2 = new SeriesItem.Builder(mColorList.get(i % mColorList.size()))
                    .setRange(0, 100, 0)
                    .setLineWidth(32f)
                    .setInset(new PointF((float) ((i - 1) * 32) - 16, (float) ((i - 1) * 32) - 16))
                    /*.setSeriesLabel(new SeriesLabel.Builder(mUserList.get(i).getUserName() + " %.0f%%")
                            .setVisible(true)
                            .setColorBack(Color.argb(150, 0, 0, 0))
                            .setColorText(Color.argb(255, 255, 255, 255))
                                    //.setTypeface(customTypeface) //Load the font from your Android assets folder
                            .build())
                            */
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

                    textPercentage.setText("");
                }
            });


            decoView.addEvent(new DecoEvent.Builder(percentageList.get(i))
                    .setIndex(series1Index)
                    .setDelay(2000 * i)
                    .build());

        }
    }
}
