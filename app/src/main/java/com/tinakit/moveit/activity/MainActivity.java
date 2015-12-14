package com.tinakit.moveit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.BinderServiceConnection;

/**
 * Created by Tina on 10/26/2015.
 */
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.view.MenuItem;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.fragment.EditRewardFragment;
import com.tinakit.moveit.fragment.RewardViewFragment;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.service.TrackerService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String LOG = "MAINACTIVITY";
    private static final boolean DEBUG = true;

    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    protected ViewPagerAdapter mViewPagerAdapter;
    protected int mUserCount;
    protected int mCurrentTab;

    //BinderServiceConnection
    BinderServiceConnection mConnection;

    //LocalBroadcaseManager
    LocalBroadcastManager mLocalBroadcastManager;

    //TrackerService
    TrackerService mTrackerService;

    //cache
    ActivityDetail mActivityDetail;
    List<User> mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


/*
        Toolbar toolbar = (Toolbar)findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);


        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        NavigationView navView = (NavigationView) findViewById(R.id.navigation_view);
        if (navView != null){
            setupDrawerContent(navView);
        }
        */

        //ViewPager
        viewPager = (ViewPager)findViewById(R.id.tab_viewpager);
        if (viewPager != null){
            setupViewPager(viewPager);
        }

        //TabLayout
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        //TODO: once TrackerService is implemented, we may not need this part. maybe the current index persists with a notifydatasetchanged
        //set tab index if this is redirected
        //int defaultValue = 0;
        if(getIntent().hasExtra("tab_index")){

            viewPager.setCurrentItem((int)getIntent().getExtras().get("tab_index"));
        }


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

    }

   /*
    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.drawer_labels:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.drawer_fab:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.drawer_snackbar:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.drawer_coordinator:
                        viewPager.setCurrentItem(3);
                        break;
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
    }
*/

    //**********************************************************************************************
    //  onStart()
    //**********************************************************************************************

    @Override
    protected void onStart() {
        if (DEBUG) Log.d(LOG, "onStart");
        super.onStart();
        mConnection = BinderServiceConnection.getInstance();
        mConnection.doBindService(this, new Intent(MainActivity.this, TrackerService.class));

        // Register to receive Intents with actions.
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(
                mMessageReceiver, new IntentFilter(TrackerService.TRACKER_SERVICE_INTENT));
    }

    //**********************************************************************************************
    //  onPause()
    //**********************************************************************************************

    @Override
    protected void onPause() {
        if (DEBUG) Log.d(LOG, "onPause");

        // Unregister since the activity is paused.
        mLocalBroadcastManager.unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    //**********************************************************************************************
    //  onResume()
    //**********************************************************************************************

    @Override
    protected void onResume() {
        if (DEBUG) Log.d(LOG, "onResume");

        super.onResume();
        // Register to receive Intents with actions named DATA_SERVICE_INTENT.
        mLocalBroadcastManager.registerReceiver(mMessageReceiver, new IntentFilter(TrackerService.TRACKER_SERVICE_INTENT));
    }

    //**********************************************************************************************
    //  onDestroy()
    //**********************************************************************************************

    @Override
    protected void onDestroy() {
        if (DEBUG) Log.d(LOG, "onDestroy");

        mConnection.doUnbindService(this);
        super.onDestroy();
    }

    //**********************************************************************************************
    //  BroadcastReceiver mMessageReceiver
    //**********************************************************************************************

    // Handler for received Intents. This will be called whenever an Intent
    // with an action named DATA_SERVICE_INTENT is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");

            if (DEBUG) Log.d(LOG, "BroadcastReceiver - onReceive(): message: " + message);


            //if this message came from DataService, get the stocklist and previous prices from the Service
            if(message.equals(TrackerService.TRACKER_SERVICE_UPDATE)){

                if(mConnection.isBound()){

                    mActivityDetail = mConnection.mBoundService.getActivityDetail();
                    mUserList = mConnection.mBoundService.getUserList();

                    mViewPagerAdapter.notifyDataSetChanged();
                }
            }
        }
    };


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

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFrag(new ActivityTracker(), "START");

        //add user screens
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(this);
        List<User> userList = databaseHelper.getUsers();

        //refresh user count
        mUserCount = userList.size();

        for (User user : userList){

            Fragment fragment = new RewardViewFragment();
            Bundle args = new Bundle();
            args.putParcelable("user", user);
            args.putInt("tab_index", mCurrentTab);
            fragment.setArguments(args);

            mViewPagerAdapter.addFrag(fragment, user.getUserName());
        }

        mViewPagerAdapter.addFrag(new EditRewardFragment(), "REWARDS");
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
}