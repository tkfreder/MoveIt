package com.tinakit.moveit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.tinakit.moveit.api.Accelerometer;
import com.tinakit.moveit.api.GoogleApi;
import com.tinakit.moveit.api.LocationApi;
import com.tinakit.moveit.fragment.ActivityChooser;
import com.tinakit.moveit.fragment.ActivityHistory;
import com.tinakit.moveit.fragment.MapFragment;
import com.tinakit.moveit.fragment.UserStats;
import com.tinakit.moveit.model.ActivityDetail;

/**
 * Created by Tina on 10/26/2015.
 */
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.view.MenuItem;
import android.view.View;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.fragment.EditRewardFragment;
import com.tinakit.moveit.fragment.RewardViewFragment;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.tab.SlidingTabLayout;
import com.tinakit.moveit.utility.DialogUtility;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String LOG = "MAINACTIVITY";
    private static final boolean DEBUG = true;
    public static final String MAIN_ACTIVITY_BROADCAST_RECEIVER = "MAIN_ACTIVITY_BROADCAST_RECEIVER";

    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    protected ViewPagerAdapter mViewPagerAdapter;
    protected int mUserCount;
    protected int mCurrentTab;
    private FitnessDBHelper mDatabaseHelper;

    //cache
    ArrayList<ActivityDetail> mActivityDetailList;
    ArrayList<User> mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // instantiate databaseHelper
        mDatabaseHelper = FitnessDBHelper.getInstance(this);

        // get data before initializing UI, need data to pass to ViewPager
        fetchData();

        initializeUI();

    }

    private void fetchData(){

        //FitnessDBHelper mDatabaseHelper = FitnessDBHelper.getInstance(this);

        //data for ActivityHistory
        mActivityDetailList = mDatabaseHelper.getActivityDetailList();

        // get user data
        mUserList = mDatabaseHelper.getUsers();

    }

    private void initializeUI(){

        //ViewPager
        viewPager = (ViewPager)findViewById(R.id.tab_viewpager);
        if (viewPager != null){
            setupViewPager(viewPager);
        }

        //TabLayout
        //TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        //tabLayout.setupWithViewPager(viewPager);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout)findViewById(R.id.tabLayout);
        slidingTabLayout.setViewPager(viewPager);

        //set tab index if this is redirected
        //int defaultValue = 0;
        if(getIntent().hasExtra("tab_index")){

            viewPager.setCurrentItem((int)getIntent().getExtras().get("tab_index"));
        }

/*
        //OnTabSelected Listener
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                //save index of current tab, save in Bundle
                mCurrentTab = tab.getPosition();

                //TODO: may not need this if block, after TrackerService is implemented
                //if User tab is selected, index of User tabs start at index = 1
                if (tab.getPosition() > 0 && tab.getPosition() <= mUserCount) {

                    //refresh user data
                    setupViewPager(viewPager);
                }

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
*/
    }

    //**********************************************************************************************
    //  onStart()
    //**********************************************************************************************

    @Override
    protected void onStart() {
        if (DEBUG) Log.d(LOG, "onStart");
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(ActivityTracker.ACTIVITY_TRACKER_INTENT));


    }

    //**********************************************************************************************
    //  onPause()
    //**********************************************************************************************

    @Override
    protected void onPause() {
        if (DEBUG) Log.d(LOG, "onPause");

        super.onPause();
    }

    //**********************************************************************************************
    //  onResume()
    //**********************************************************************************************

    @Override
    protected void onResume() {
        if (DEBUG) Log.d(LOG, "onResume");

        super.onResume();

    }

    //**********************************************************************************************
    //  onDestroy()
    //**********************************************************************************************

    @Override
    protected void onDestroy() {
        if (DEBUG) Log.d(LOG, "onDestroy");

        super.onDestroy();
    }


    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) { return mFragmentList.get(position);}

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }

    }

    private void setupViewPager(ViewPager viewPager){

        // FIRST TAB
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFrag(new ActivityChooser(), "START");

        // SECOND TAB
        Fragment ActivityHistoryFragment = new ActivityHistory();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ActivityHistory.ACTIVITY_HISTORY, mActivityDetailList);
        ActivityHistoryFragment.setArguments(bundle);
        mViewPagerAdapter.addFrag(ActivityHistoryFragment, "HISTORY");

        // THIRD TAB
        Fragment UserStatsFragment = new UserStats();
        bundle = new Bundle();
        bundle.putParcelableArrayList(UserStats.USER_STATS_LIST, mUserList);
        UserStatsFragment.setArguments(bundle);
        mViewPagerAdapter.addFrag(UserStatsFragment, "USERS");

        // FOURTH TAB
        //mViewPagerAdapter.addFrag(new EditRewardFragment(), "REWARDS");
        viewPager.setAdapter(mViewPagerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (id){
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //**********************************************************************************************
    //  BroadcastReceiver mMessageReceiver
    //**********************************************************************************************

    // Handler for received Intents. This will be called whenever an Intent
    // with an action named GOOGLE_API_INTENT is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(MAIN_ACTIVITY_BROADCAST_RECEIVER);
            if (DEBUG) Log.d(LOG, "BroadcastReceiver - onReceive(): message: " + message);

            if(message.equals(ActivityTracker.ACTIVITY_TRACKER_INTENT)){

                // when Tracker has started, close this Activity
                finish();
            }

        }
    };

}