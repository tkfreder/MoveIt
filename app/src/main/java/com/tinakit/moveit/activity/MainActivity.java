package com.tinakit.moveit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.tinakit.moveit.fragment.UserStats;
import com.tinakit.moveit.model.ActivityDetail;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.tab.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Arrays;
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
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mDrawerMenuItems;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private NavigationView mNavigationView;

    private ViewPager viewPager;
    protected ViewPagerAdapter mViewPagerAdapter;
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

        // Navigation Drawer
        initializeNavigationDrawer();

        //ViewPager
        viewPager = (ViewPager)findViewById(R.id.tab_viewpager);
        if (viewPager != null){
            setupViewPager(viewPager);
        }

        //TabLayout
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout)findViewById(R.id.tabLayout);
        slidingTabLayout.setViewPager(viewPager);

        //set tab index if this is redirected
        //int defaultValue = 0;
        if(getIntent().hasExtra("tab_index")){

            viewPager.setCurrentItem((int)getIntent().getExtras().get("tab_index"));
        }
    }

    private void initializeNavigationDrawer(){

        mTitle = mDrawerTitle = getTitle();
        mDrawerMenuItems = getResources().getStringArray(R.array.navigation_drawer_menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mDrawerList = (ListView) findViewById(R.id.list_view_inside_nav);
        mNavigationView = (NavigationView)findViewById(R.id.navigation_view);

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Handle menu item clicks here.
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        // add menu items to Navigation Drawer
        Menu m = mNavigationView.getMenu();

        // copy menu items from string resource
        String[] myResArray = getResources().getStringArray(R.array.navigation_drawer_menu_array);
        List<String> menuItemList = Arrays.asList(myResArray);

        SubMenu userProfileMenu = m.addSubMenu(getResources().getString(R.string.user_profiles));

        for (String menuName : menuItemList){

            userProfileMenu.add(menuName);
        }

        // set a custom shadow that overlays the main content when the drawer opens
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        //mDrawerList.setAdapter(new ArrayAdapter<String>(this,
        //        R.layout.drawer_list_item, mDrawerMenuItems));
        //mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger_icon);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                new Toolbar(this),  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (mSavedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {

        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new UserProfile();

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        //mDrawerList.setItemChecked(position, true);
        setTitle(mDrawerMenuItems[position]);
        //mDrawerLayout.closeDrawer(mDrawerList);
        mDrawerLayout.closeDrawers();

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
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
    //  onCreateOptionsMenu()
    //**********************************************************************************************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SubMenu subMenu = menu.findItem(R.id.action_user_profiles).getSubMenu();

        //delete placeholder submenu
        subMenu.clear();

        for (User user : mUserList){

            subMenu.addSubMenu(user.getUserName());
        }
        return true;
    }

    //**********************************************************************************************
    //  onOptionsItemSelected()
    //**********************************************************************************************

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // user profile menus
        if (id == R.id.action_user_profiles) {

            for (int i = 0; i < mUserList.size(); i++){

                if(mUserList.get(i).getUserName().equals(item.getTitle().toString())){

                    intent = new Intent(this, UserProfile.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", mUserList.get(i));
                    intent.putExtra("user", bundle);
                    startActivity(intent);

                    break;

                }
            }
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (id){
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }

                return true;
        }

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

            if(message.equals(ActivityTracker.ACTIVITY_TRACKER_INTENT)){

                // when Tracker has started, close this Activity
                finish();
            }

        }
    };

}