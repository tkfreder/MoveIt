package com.tinakit.moveit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.tinakit.moveit.api.GoogleApi;
import com.tinakit.moveit.fragment.ActivityChooser;
import com.tinakit.moveit.fragment.ActivityHistory;
import com.tinakit.moveit.fragment.UserProfile;
import com.tinakit.moveit.fragment.UserStats;
import com.tinakit.moveit.model.ActivityDetail;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.view.MenuItem;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 10/26/2015.
 */

public class MainActivity extends AppCompatActivity {

    private static final String LOG = "MAINACTIVITY";
    private static final boolean DEBUG = true;
    public static final String MAIN_ACTIVITY_BROADCAST_RECEIVER = "MAIN_ACTIVITY_BROADCAST_RECEIVER";

    // UI widgets
    private DrawerLayout mDrawerLayout;
    private Menu mMenu;


    @Inject
    GoogleApi mGoogleApi;

    @Inject
    FitnessDBHelper mDatabaseHelper;

    //cache
    ArrayList<ActivityDetail> mActivityDetailList;
    ArrayList<User> mUserList;
    //Bundle mSavedInstanceState;

    //**********************************************************************************************
    //  onCreate()
    //**********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // DI
        ((CustomApplication)getApplication()).getAppComponent().inject(this);

        // save state
        //mSavedInstanceState = savedInstanceState;

        //end the activity if Google Play Services is not present
        //redirect user to Google Play Services
        //mGoogleApi = new GoogleApi();

        if (!mGoogleApi.servicesAvailable(this))
            finish();
        else
            mGoogleApi.buildGoogleApiClient(this);

        // instantiate databaseHelper
        //mDatabaseHelper = FitnessDBHelper.getInstance(this);

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
        actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Navigation Drawer
        initializeNavigationDrawer();

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

             case R.id.nav_rewards:

                getSupportActionBar().setTitle(getResources().getString(R.string.rewards));

                UserStats userStats = (UserStats)getSupportFragmentManager().findFragmentByTag(UserStats.USER_STATS_TAG);
                if (userStats == null) {

                    userStats = new UserStats();
                    //replace current fragment with Rewards fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, userStats).commit();

                }

                break;

            case R.id.nav_history:

                getSupportActionBar().setTitle(getResources().getString(R.string.history));

                ActivityHistory activityHistory = (ActivityHistory)getSupportFragmentManager().findFragmentByTag(ActivityHistory.ACTIVITY_HISTORY_TAG);
                if (activityHistory == null) {

                    activityHistory = new ActivityHistory();
                    //replace current fragment with Rewards fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, activityHistory).commit();

                }

                break;

            case R.id.nav_user_profiles:

                getSupportActionBar().setTitle(getResources().getString(R.string.user_profiles));

                // check whether UserProfile does not exist
                UserProfile userProfile = (UserProfile)getSupportFragmentManager().findFragmentByTag(UserProfile.USER_PROFILE_TAG);
                if (userProfile == null){

                    userProfile= new UserProfile ();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, userProfile).commit();

                }

                break;

            case R.id.nav_start:

                getSupportActionBar().setTitle(getResources().getString(R.string.start));
                //TODO: redirect to ActivityTracker, change ActivityTracker to a fragment

                // check whether UserProfile is already visible
                ActivityChooser activityChooser = (ActivityChooser)getSupportFragmentManager().findFragmentByTag(ActivityChooser.ACTIVITY_CHOOSER_TAG);
                if (activityChooser == null){

                    activityChooser= new ActivityChooser ();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, activityChooser).commit();
                }


                break;

            case R.id.nav_settings:
            case R.id.action_settings:

                getSupportActionBar().setTitle(getResources().getString(R.string.settings));
                // TODO: redirect to admin screen

            break;

        }
    }

    //**********************************************************************************************
    //  onStart()
    //**********************************************************************************************

    @Override
    protected void onStart() {
        if (DEBUG) Log.d(LOG, "onStart");
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(ActivityTracker.ACTIVITY_TRACKER_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(GoogleApi.GOOGLE_API_INTENT));

    }

    //**********************************************************************************************
    //  onPause()
    //**********************************************************************************************

    @Override
    protected void onPause() {
        if (DEBUG) Log.d(LOG, "onPause");

        //TODO:  should all Listeners be unregistered here?
        super.onPause();
    }

    //**********************************************************************************************
    //  onResume()
    //**********************************************************************************************

    @Override
    protected void onResume() {
        if (DEBUG) Log.d(LOG, "onResume");

        //TODO:  should all Listeners be registered here?
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // save menu
        mMenu = menu;

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
    //  BroadcastReceiver mMessageReceiver
    //**********************************************************************************************

    // Handler for received Intents sent by various Fragments and Activities from the app
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(MAIN_ACTIVITY_BROADCAST_RECEIVER);
            if (DEBUG) Log.d(LOG, "BroadcastReceiver - onReceive(): message: " + message);

            if(message != null && message.equals(ActivityTracker.ACTIVITY_TRACKER_INTENT)){

                // when Tracker has started, close this Activity
                finish();
            }

            //TODO: change key to more generic name
            // message to indicate Google API Client connection
            message = intent.getStringExtra(ActivityTracker.ACTIVITY_TRACKER_BROADCAST_RECEIVER);

            if(message != null && message.equals(GoogleApi.GOOGLE_API_INTENT)){

                //put a Fragment in the FragmentManager, so just need to call replace when click on nav items
                // display ActivityChooser screen first
                ActivityChooser activityChooser = new ActivityChooser();
                getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, activityChooser).commit();

                //TODO:  should the Message listener for GoogleApi be unregistered at this point
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);

            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // get current fragment in container
        UserProfile userProfile = (UserProfile)getSupportFragmentManager().findFragmentByTag(UserProfile.USER_PROFILE_TAG);
        userProfile.onActivityResult(requestCode, resultCode, data);
    }

}