package com.tinakit.moveit.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.tinakit.moveit.R;
import com.tinakit.moveit.api.GoogleApi;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.fragment.ActivityChooser;
import com.tinakit.moveit.fragment.ActivityHistory;
import com.tinakit.moveit.fragment.Admin;
import com.tinakit.moveit.fragment.BackHandledFragment;
import com.tinakit.moveit.fragment.EditUser;
import com.tinakit.moveit.fragment.UserProfile;
import com.tinakit.moveit.fragment.UserStatsMain;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

/**
 * Created by Tina on 10/26/2015.
 */

public class MainActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface {

    private static final String LOG = "MAINACTIVITY";
    private static final boolean DEBUG = true;
    public static final String MAIN_ACTIVITY_BROADCAST_RECEIVER = "MAIN_ACTIVITY_BROADCAST_RECEIVER";

    // UI widgets
    public static DrawerLayout mDrawerLayout;
    private Menu mMenu;


    @Inject
    GoogleApi mGoogleApi;

    @Inject
    FitnessDBHelper mDatabaseHelper;

    //cache
    ArrayList<ActivityDetail> mActivityDetailList;
    ArrayList<User> mUserList;
    private BackHandledFragment selectedFragment;


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

        //end the activity if Google Play Services is not present
        //redirect user to Google Play Services

        if (!mGoogleApi.servicesAvailable(this))
            finish();
        else
            mGoogleApi.buildGoogleApiClient(this);

        // get data before initializing UI, need data to pass to ViewPager
        fetchData();

        initializeUI();

        // this should be called once, so defined in onCreate and not in onResume
        LocalBroadcastManager.getInstance(this).registerReceiver(mGoogleApiReceiver, new IntentFilter(GoogleApi.GOOGLE_API_INTENT));

    }

    //**********************************************************************************************
    //  fetchData()
    //**********************************************************************************************

    private void fetchData(){

        //data for ActivityHistory
        mActivityDetailList = mDatabaseHelper.getActivityDetailList(ActivityHistory.DAYS_AGO);

        // get user data
        mUserList = mDatabaseHelper.getUsers();

    }

    //**********************************************************************************************
    //  initializeUI()
    //**********************************************************************************************

    private void initializeUI(){

        // Toolbar
        setSupportActionBar((Toolbar)findViewById(R.id.toolBar));

        // set title
        getSupportActionBar().setTitle(getString(R.string.nav_menu_start));


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
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        if (navigationView != null){

            setupDrawerContent(navigationView);
        }

        //lock Navigation Drawer, until we gain connection to GoogleApi
        MainActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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

        // set random background image
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(8);


        NavigationView navView = (NavigationView)findViewById(R.id.navigation_view);
        View header = navView.getHeaderView(0);

        //RelativeLayout drawerBackgroundLayout = (RelativeLayout)findViewById(R.id.navigation_view).findViewById(R.id.drawer_background);

        //header.setBackground(ContextCompat.getDrawable(this, getResources().getIdentifier("background_" + String.valueOf(randomInt), "drawable", getPackageName())));

        int resourceId = -1;

        try{
            resourceId = R.drawable.class.getField("background_" + String.valueOf(randomInt)).getInt(null);
            header.setBackground(ContextCompat.getDrawable(this, resourceId));

        } catch(NoSuchFieldException nsfe){
            nsfe.printStackTrace();
        } catch(IllegalAccessException iae){
            iae.printStackTrace();
        }


    }

    private void callMenuItemAction(int id){

        switch(id){

            case R.id.nav_start:

                displayStartScreen();

                break;

             case R.id.nav_coins:

                getSupportActionBar().setTitle(getString(R.string.nav_menu_coins));

                UserStatsMain userStatsMain = (UserStatsMain)getSupportFragmentManager().findFragmentByTag(UserStatsMain.USER_STATS_TAG);
                if (userStatsMain == null) {

                    userStatsMain = new UserStatsMain();

                }
                //replace current fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, userStatsMain, UserStatsMain.USER_STATS_TAG)
                        .addToBackStack(UserStatsMain.USER_STATS_BACKSTACK_NAME)
                        .commit();

                break;

             /*
            case R.id.nav_charts:

                getSupportActionBar().setTitle(getString(R.string.nav_menu_charts));

                break;
*/
            case R.id.nav_admin:
            case R.id.action_settings:

                getSupportActionBar().setTitle(getString(R.string.nav_menu_admin));

                Admin admin = (Admin)getSupportFragmentManager().findFragmentByTag(Admin.ADMIN_TAG);
                if (admin == null) {

                    admin = new Admin();

                }

                //replace current fragment with Admin fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, admin, Admin.ADMIN_TAG)
                        .addToBackStack(Admin.ADMIN_BACKSTACK_NAME)
                        .commit();

                break;

            /*
            case R.id.nav_history:

                getSupportActionBar().setTitle(getString(R.string.history));

                ActivityHistory activityHistory = (ActivityHistory)getSupportFragmentManager().findFragmentByTag(ActivityHistory.ACTIVITY_HISTORY_TAG);
                if (activityHistory == null) {

                    activityHistory = new ActivityHistory();
                    //replace current fragment with Rewards fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, activityHistory).commit();

                }

                break;

            case R.id.nav_user_profiles:

                getSupportActionBar().setTitle(getString(R.string.user_profiles));

                // check whether UserProfile does not exist
                UserProfile userProfile = (UserProfile)getSupportFragmentManager().findFragmentByTag(UserProfile.USER_PROFILE_TAG);
                if (userProfile == null){

                    userProfile= new UserProfile ();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, userProfile).commit();

                }

                break;

*/

        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.selectedFragment = selectedFragment;
    }

    @Override
    public void onBackPressed() {

        if(selectedFragment == null || !selectedFragment.onBackPressed()) {
            // Selected fragment did not consume the back press event.

            int count = getSupportFragmentManager().getBackStackEntryCount();

            if (count == 0 || count == 1)
                finish();

            else{

                getSupportFragmentManager().popBackStack();

                int size = getSupportFragmentManager().getBackStackEntryCount();
                getSupportActionBar().setTitle(getSupportFragmentManager().getBackStackEntryAt(size - 2).getName());

            }
        }


    }

    private void displayStartScreen(){

        getSupportActionBar().setTitle(getString(R.string.nav_menu_start));

        // check whether UserProfile is already visible
        ActivityChooser activityChooser = (ActivityChooser)getSupportFragmentManager().findFragmentByTag(ActivityChooser.ACTIVITY_CHOOSER_TAG);
        if (activityChooser == null) {
            activityChooser = new ActivityChooser();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, activityChooser, ActivityChooser.ACTIVITY_CHOOSER_TAG)
                .addToBackStack(ActivityChooser.ACTIVITY_CHOOSER_BACKSTACK_TAG)
                .commit();

    }

    //**********************************************************************************************
    //  onStart()
    //**********************************************************************************************

    @Override
    protected void onStart() {
        if (DEBUG) Log.d(LOG, "onStart");
        super.onStart();

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
    //  onPause()
    //**********************************************************************************************

    @Override
    protected void onPause() {
        if (DEBUG) Log.d(LOG, "onPause");
        super.onPause();
    }

    //**********************************************************************************************
    //  onStop()
    //**********************************************************************************************

    @Override
    protected void onStop() {
        if (DEBUG) Log.d(LOG, "onStop");
        super.onStop();

        // destroy this activity, in the case where home button is pressed, this will force restart of MainActivity/fragments
        // otherwise, there will be an extra ActivityChooser fragment
        //finish();

    }

//**********************************************************************************************
    //  onDestroy()
    //**********************************************************************************************

    @Override
    protected void onDestroy() {
        if (DEBUG) Log.d(LOG, "onDestroy");
        super.onDestroy();

        // unregister receiver
        //LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);

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
    private BroadcastReceiver mGoogleApiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // message to indicate Google API Client connection
            String message = intent.getStringExtra(ActivityTracker.ACTIVITY_TRACKER_BROADCAST_RECEIVER);
            if (DEBUG) Log.d(LOG, "BroadcastReceiver - onReceive(): message: " + message);

            // message to indicate Google API Client connection
            message = intent.getStringExtra(ActivityTracker.ACTIVITY_TRACKER_BROADCAST_RECEIVER);

            if(message != null && message.equals(GoogleApi.GOOGLE_API_INTENT)){


                ActivityChooser activityChooser = (ActivityChooser)getSupportFragmentManager().findFragmentByTag(ActivityChooser.ACTIVITY_CHOOSER_TAG);
                if (activityChooser == null) {

                    //put a Fragment in the FragmentManager, so just need to call replace when click on nav items
                    // display ActivityChooser screen first
                    activityChooser = new ActivityChooser();

                    // display ActivityChooser only after SaveInstanceState
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragmentContainer, activityChooser, ActivityChooser.ACTIVITY_CHOOSER_TAG)
                                .addToBackStack(null)
                                .commit();
                    else
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragmentContainer, activityChooser, ActivityChooser.ACTIVITY_CHOOSER_TAG)
                                .addToBackStack(null)
                                .commit();

                    //unlock Navigation Drawer, originally locked when first launching MainActivity
                    MainActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                    //Message listener for GoogleApi to be unregistered at this point
                    LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mGoogleApiReceiver);
                }
                else
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, activityChooser, ActivityChooser.ACTIVITY_CHOOSER_TAG)
                            .addToBackStack(null)
                            .commit();

            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == EditUser.PICK_AVATAR_REQUEST){

            if (resultCode == Activity.RESULT_OK){

                EditUser editUser = (EditUser)getSupportFragmentManager().findFragmentByTag(EditUser.EDIT_USER_TAG);
                editUser.onActivityResult(requestCode, resultCode, data);
            }
        }
        else if (requestCode == ActivityChooser.PICK_AVATAR_REQUEST){

            if (resultCode == Activity.RESULT_OK){

                ActivityChooser activityChooser = (ActivityChooser)getSupportFragmentManager().findFragmentByTag(ActivityChooser.ACTIVITY_CHOOSER_TAG);
                activityChooser.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

}