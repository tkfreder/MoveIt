package com.tinakit.moveit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.model.UserListObservable;
import com.tinakit.moveit.module.CustomApplication;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

/**
 * Created by Tina on 12/19/2015.
 */
public class UserStatsMain extends Fragment implements Observer {

    // CONSTANTS
    public static final String USER_STATS_TAG = "USER_STATS_TAG";
    public static final String USER_STATS_BACKSTACK_NAME = "Coins";
    public static final String USER_STATS_LIST_KEY = "USER_STATS_LIST";

    @Inject
    FitnessDBHelper mDatabaseHelper;

    // local cache
    protected FragmentActivity mFragmentActivity;
    private View rootView;
    List<Integer> mColorList;
    protected static List<User> mUserList;

    // UI COMPONENTS
    protected RecyclerView mRecyclerView;
    protected SeriesItem seriesItem2;
    protected TextView textPercentage;
    List<SeriesItem> mSeriesItemList;
    protected UserStatsPagerAdapter mUserStatsPagerAdapter;
    protected ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.user_stats_main, container, false);
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Dagger 2 injection
        ((CustomApplication)getActivity().getApplication()).getAppComponent().inject(this);
        // add UserListObservable
        CustomApplication app = ((CustomApplication)mFragmentActivity.getApplication());
        UserListObservable mUserListObservable = app.getUserListObservable();
        mUserListObservable.addObserver(this);
        initializeUI();
        return rootView;
    }

    @Override
    public void update(Observable observable, Object data) {
        //mViewPager.setAdapter(mUserStatsPagerAdapter);
        mUserStatsPagerAdapter.notifyDataSetChanged();
    }

    private void initializeUI(){
        // get user data
        //mUserList = mDatabaseHelper.getUsers();
        CustomApplication app = ((CustomApplication)mFragmentActivity.getApplication());
        mUserList = app.getUserListObservable().getValue();
        mViewPager = (ViewPager)rootView.findViewById(R.id.viewpager_user_stats);
        mViewPager.setOffscreenPageLimit(0);
        if (mViewPager != null){
            setupViewPager(mViewPager);
        }

    }

    //**********************************************************************************************
    //  setUpViewPager()
    //**********************************************************************************************
    private void setupViewPager(ViewPager viewPager){

        mUserStatsPagerAdapter = new UserStatsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(mUserStatsPagerAdapter);

    }

    public static class UserStatsPagerAdapter extends FragmentPagerAdapter {

        public UserStatsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            Fragment fragment = new UserStats();
            Bundle args = new Bundle();
            args.putParcelable(UserStats.USER_STATS_ARG_USER, mUserList.get(i));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return mUserList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mUserList.get(position).getUserName();
        }
    }

}
