package com.tinakit.moveit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.tinakit.moveit.fragment.ActivityChooser;
import com.tinakit.moveit.fragment.ActivityHistory;
import com.tinakit.moveit.fragment.PickAvatar;
import com.tinakit.moveit.fragment.UserProfile;
import com.tinakit.moveit.fragment.UserStats;
import com.tinakit.moveit.model.ActivityDetail;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.tab.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tina on 10/26/2015.
 */

public class MainActivity extends AppCompatActivity {

    private static final String LOG = "MAINACTIVITY";
    private static final boolean DEBUG = true;
    public static final String MAIN_ACTIVITY_BROADCAST_RECEIVER = "MAIN_ACTIVITY_BROADCAST_RECEIVER";

    // Navigation Drawer
    private DrawerLayout mDrawerLayout;

    private ViewPager mViewPager;
    protected ViewPagerAdapter mViewPagerAdapter;
    SlidingTabLayout mSlidingTabLayout;

    private FitnessDBHelper mDatabaseHelper;

    //cache
    ArrayList<ActivityDetail> mActivityDetailList;
    ArrayList<User> mUserList;
    Bundle mSavedInstanceState;

    //**********************************************************************************************
    //  onCreate()
    //**********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // save state
        mSavedInstanceState = savedInstanceState;

        // instantiate databaseHelper
        mDatabaseHelper = FitnessDBHelper.getInstance(this);

        // get data before initializing UI, need data to pass to ViewPager
        fetchData();

        initializeUI();

    }

    //**********************************************************************************************
    //  fetchData()
    //**********************************************************************************************

    private void fetchData(){

        //data for ActivityHistory
        mActivityDetailList = mDatabaseHelper.getActivityDetailList();

        // get user data
        mUserList = mDatabaseHelper.getUsers();

    }

    //**********************************************************************************************
    //  initializeUI()
    //**********************************************************************************************

    private void initializeUI(){

        // Toolbar
        setSupportActionBar((Toolbar)findViewById(R.id.toolBar));

        // Actionbar
        final ActionBar actionBar = getSupportActionBar();

        // enable ActionBar app icon to behave as action to toggle nav drawer
        actionBar.setHomeAsUpIndicator(R.drawable.hamburger_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // SlidingTabLayout
        mSlidingTabLayout = (SlidingTabLayout)findViewById(R.id.tabLayout);

        // Navigation Drawer
        initializeNavigationDrawer();

        //ViewPager
        mViewPager = (ViewPager)findViewById(R.id.tab_viewpager);
        if (mViewPager != null){
            setupViewPager(mViewPager);
        }

        //TabLayout
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout)findViewById(R.id.tabLayout);
        slidingTabLayout.setViewPager(mViewPager);

        //set tab index if this is redirected
        if(getIntent().hasExtra("tab_index")){

            mViewPager.setCurrentItem((int)getIntent().getExtras().get("tab_index"));
        }
    }

    private void initializeNavigationDrawer(){

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        if (navigationView != null){

            setupDrawerContent(navigationView);
        }


    }

    private void setupDrawerContent(final NavigationView navigationView){

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        callMenuItemAction(menuItem.getItemId());
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void callMenuItemAction(int id){

        switch(id){

            case R.id.action_user_profiles:
            case R.id.nav_user_profiles:

                //TODO: replace SlidingTabLayout and ViewPager with UserProfile fragment
                //http://stackoverflow.com/questions/30518710/slidingtablayout-replace-with-fragment

                //make sure you set title first, as the closeFragments depends on the desired screen
                getSupportActionBar().setTitle(getResources().getString(R.string.user_profiles));
                closeFragments();
                displayTabLayout(View.GONE);

                // check whether UserProfile is already visible
                UserProfile userProfile = (UserProfile)getSupportFragmentManager().findFragmentByTag(UserProfile.USER_PROFILE_TAG);
                if (userProfile == null){

                    userProfile= new UserProfile ();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.fragmentContainer, userProfile, UserProfile.USER_PROFILE_TAG);
                    transaction.commit();


                }

                //Intent intent = new Intent(this, UserProfile.class);
                //startActivity(intent);
                break;

            case R.id.nav_start:

                //make sure you set title first, as the closeFragments depends on the desired screen
                getSupportActionBar().setTitle(getResources().getString(R.string.start));

                closeFragments();

                displayTabLayout(View.VISIBLE);
                mViewPager.setCurrentItem(0);




                break;

            case R.id.nav_settings:
            case R.id.action_settings:

                //make sure you set title first, as the closeFragments depends on the desired screen
                getSupportActionBar().setTitle(getResources().getString(R.string.settings));

                displayTabLayout(View.GONE);

                closeFragments();



        }
    }

    @Override
    public void onBackPressed() {

        closeFragments();
    }

    private void closeFragments(){

        // if the desired screen is User Profiles, close other fragments that might be currently displayed
        if (getSupportActionBar().getTitle().equals(getResources().getString(R.string.user_profiles))){

            /*
            EditReward editReward = (EditReward)getSupportFragmentManager().findFragmentByTag(EditReward.EDIT_REWARD_TAG);
            if (editReward!= null && editReward.isVisible()) {
                getSupportFragmentManager().beginTransaction().remove(editReward).commit();
            }
            */
        }

        else if (getSupportActionBar().getTitle().equals(getResources().getString(R.string.rewards))){

            UserProfile userProfile = (UserProfile)getSupportFragmentManager().findFragmentByTag(UserProfile.USER_PROFILE_TAG);
            if (userProfile!= null && userProfile.isVisible()) {
                getSupportFragmentManager().beginTransaction().remove(userProfile).commit();
            }
        }

        else if (getSupportActionBar().getTitle().equals(getResources().getString(R.string.start))){

            /*
            EditReward editReward = (EditReward)getSupportFragmentManager().findFragmentByTag(EditReward.EDIT_REWARD_TAG);
            if (editReward!= null && editReward.isVisible()) {
                getSupportFragmentManager().beginTransaction().remove(editReward).commit();
            }
*/
            UserProfile userProfile = (UserProfile)getSupportFragmentManager().findFragmentByTag(UserProfile.USER_PROFILE_TAG);
            if (userProfile!= null && userProfile.isVisible()) {
                getSupportFragmentManager().beginTransaction().remove(userProfile).commit();
            }
        }


    }

    private void displayTabLayout(int visibility){

        mSlidingTabLayout.setVisibility(visibility);
        mViewPager.setVisibility(visibility);

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

    //**********************************************************************************************
    //  ViewPagerAdapter
    //**********************************************************************************************

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        callMenuItemAction(item.getItemId());

        return super.onOptionsItemSelected(item);
    }

    //**********************************************************************************************
    //  setUpViewPager()
    //**********************************************************************************************
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
        mViewPagerAdapter.addFrag(UserStatsFragment, "REWARDS");

        viewPager.setAdapter(mViewPagerAdapter);

    }

    //**********************************************************************************************
    //  BroadcastReceiver mMessageReceiver
    //**********************************************************************************************

    // Handler for received Intents sent by various Fragments and Activities from the app
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get current fragment in container
        UserProfile userProfile = (UserProfile)getSupportFragmentManager().findFragmentByTag(UserProfile.USER_PROFILE_TAG);
        userProfile.onActivityResult(requestCode, resultCode, data);
    }

}