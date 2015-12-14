package com.tinakit.moveit.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.fragment.MapFragment;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.ActivityType;
import com.tinakit.moveit.model.UnitSplit;
import com.tinakit.moveit.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tinakit.moveit.R;
import com.tinakit.moveit.model.UserActivity;
import com.tinakit.moveit.service.Accelerometer;
import com.tinakit.moveit.service.GoogleApi;
import com.tinakit.moveit.service.LocationApi;
import com.tinakit.moveit.utility.CalorieCalculator;
import com.tinakit.moveit.utility.ChronometerUtility;
import com.tinakit.moveit.utility.DialogUtility;
import com.tinakit.moveit.utility.Map;
import com.tinakit.moveit.utility.UnitConverter;

public class ActivityTracker extends Fragment {

    //DEBUG
    private static final String LOG = "MAIN_ACTIVITY";
    private static final boolean DEBUG = true;

    //CONSTANTS
    private static final float FEET_COIN_CONVERSION = 0.5f;  //2 feet = 1 coin
    private static final float USERNAME_FONT_SIZE = 20f;

    //save all location points during location updates
    private List<UnitSplit> mUnitSplitList = new ArrayList<>();
    private int mTotalPoints = 0;

    protected static boolean mRequestedService = false;
    long mTimeElapsed = 0; //in seconds
    private static long STOP_SERVICE_TIME_LIMIT = 30 * 60 * 1000 * 60; // 30 minutes in seconds
    private boolean mIsTimeLimit = false;

    // APIs
    private LocationApi mLocationApi;
    private Accelerometer mAccelerometer;
    private MapFragment mMapFragment;

    //GOOGLE PLAY SERVICES
    private GoogleApi mGoogleApi;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private boolean mResolvingError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    //UI widgets
    protected LinearLayout mCounterLayout;
    private static Button mStartButton;
    private static Button mStopButton;
    private static Button mPauseButton;
    private static Button mSaveButton;
    private static Button mResumeButton;
    private static Button mCancelButton;
    private static LinearLayout mButtonLinearLayout;
    private static Chronometer mChronometer;
    private static ChronometerUtility mChronometerUtility;
    private TextView mDistance;
    private TextView mCoins;
    private TextView mFeetPerMinute;
    private TextView mMessage;
    //protected static RecyclerView mRecyclerView;
    //public static MultiChooserRecyclerAdapter mRecyclerViewAdapter;
    private View rootView;
    private ViewGroup mContainer;

    // INSTANCE FIELDS
    private FragmentActivity mFragmentActivity;
    public static ActivityDetail mActivityDetail = new ActivityDetail();
    //protected static List<ActivityType> mActivityTypeList;
    private long mTimeWhenPaused;
    private boolean mSaveLocationData = false;
    private static Bundle mBundle;
    //private static int mSelectedActivityTypeIndex;

    //database
    FitnessDBHelper mDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (DEBUG) Log.d(LOG, "onCreateView()");

        mFragmentActivity  = (FragmentActivity)super.getActivity();

        // save inflator and container for MapFragment
        rootView = inflater.inflate(R.layout.activity_tracker, container, false);
        mContainer = container;

        //fix the orientation to portrait
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //end the activity if Google Play Services is not present
        //redirect user to Google Play Services
        mGoogleApi = new GoogleApi(mFragmentActivity);

        if (!mGoogleApi.servicesAvailable())
            mFragmentActivity.finish();
        else
            mGoogleApi.buildGoogleApiClient();

        //get databaseHelper instance
        mDatabaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);

        //connect to Google Play Services
        //buildLocationRequest();

        mLocationApi = new LocationApi(mFragmentActivity, mGoogleApi.client());
        mLocationApi.createLocationRequest();

        // accelerometer
        mAccelerometer = new Accelerometer(mFragmentActivity);

        //check  savedInstanceState not null
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        //wire up UI widgets
        mCounterLayout = (LinearLayout)rootView.findViewById(R.id.counterLayout);
        mStartButton = (Button) rootView.findViewById(R.id.startButton);
        mStopButton = (Button) rootView.findViewById(R.id.stopButton);
        mPauseButton = (Button) rootView.findViewById(R.id.pauseButton);
        mSaveButton = (Button) rootView.findViewById(R.id.saveButton);
        mResumeButton = (Button) rootView.findViewById(R.id.resumeButton);
        mCancelButton = (Button)rootView.findViewById(R.id.cancelButton);
        mButtonLinearLayout = (LinearLayout)rootView.findViewById(R.id.buttonLayout);
        setButtonOnClickListeners();

        mChronometer = (Chronometer) rootView.findViewById(R.id.chronometer);
        mChronometerUtility = new ChronometerUtility (mChronometer);
        mDistance = (TextView) rootView.findViewById(R.id.distance);
        mCoins = (TextView) rootView.findViewById(R.id.coins);
        mFeetPerMinute = (TextView) rootView.findViewById(R.id.feetPerMinute);
        mMessage = (TextView) rootView.findViewById(R.id.message);

        //recycler view
        //initializeRecyclerView(rootView);

        return rootView;

    }

    protected void setButtonOnClickListeners(){

        //**********************************************************************************************
        //  onClickListeners
        //**********************************************************************************************

        mStartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //set flag to save location data
                mSaveLocationData = true;

                //get timestamp of start
                mActivityDetail.setStartDate(new Date());

                //set visibility
                mMapFragment.setVisibility(View.GONE);

                //Restart
                mCancelButton.setVisibility(View.GONE);

                //clear out error message
                mMessage.setText("");

                mStartButton.setVisibility(View.GONE);
                mStopButton.setVisibility(View.VISIBLE);
                mPauseButton.setVisibility(View.VISIBLE);

                startRun();
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //get timestamp of end
                mActivityDetail.setEndDate(new Date());

                //set button visibility
                mStopButton.setVisibility(View.GONE);
                mPauseButton.setVisibility(View.GONE);
                mResumeButton.setVisibility(View.GONE);

                stopRun();

                //save Activity Detail data
                if (mUnitSplitList.size() > 1) {

                    mCancelButton.setVisibility(View.VISIBLE);
                    mSaveButton.setVisibility(View.VISIBLE);

                    //display number of coins
                    displayResults();

                } else {

                    //not enough data
                    mStartButton.setVisibility(View.VISIBLE);
                    mStartButton.setText(getResources().getString(R.string.restart));

                    mCancelButton.setVisibility(View.VISIBLE);

                    //message:  no data to display
                    mMessage.setText("No location data was collected. " + getResources().getString(R.string.restart) + "?");
                    playSound();

                }
            }
        });

        mPauseButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                pauseTracking();

                //set button visibility
                mPauseButton.setVisibility(View.GONE);
                mResumeButton.setVisibility(View.VISIBLE);
                mStopButton.setVisibility(View.VISIBLE);
                mCancelButton.setVisibility(View.GONE);

            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //save activity data to database on separate background thread
                new SaveToDB().run();
                doStartState(mFragmentActivity.getString(R.string.activity_saved));

            }

        });

        mResumeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                resumeTracking();

                //set button visibility
                mResumeButton.setVisibility(View.GONE);
                mStopButton.setVisibility(View.VISIBLE);
                mPauseButton.setVisibility(View.VISIBLE);

            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: replace with state pattern
                doStartState(mFragmentActivity.getString(R.string.activity_cancelled));

                //display restart
                hideAllButtons();
                mStartButton.setVisibility(View.VISIBLE);
                mStartButton.setText(mFragmentActivity.getString(R.string.restart));
            }
        });
    }

    private void doStartState(String message){

        //clear out user activity list and unitsplit data
        mActivityDetail = new ActivityDetail();
        mUnitSplitList = new ArrayList<>();
        //mRecyclerViewAdapter.notifyDataSetChanged();

        //make map visible
        mMapFragment.makeMap();

        //make start button visible
        hideAllButtons();

        //counter UI
        mCounterLayout.setVisibility(View.GONE);
        resetFields();

        Toast.makeText(mFragmentActivity, message, Toast.LENGTH_SHORT).show();
    }

    private static void hideAllButtons(){

        mStartButton.setVisibility(View.GONE);
        mStopButton.setVisibility(View.GONE);
        mPauseButton.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.GONE);
        mResumeButton.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);

    }

    private void pauseTracking(){

        //set the flag to not save location data
        mSaveLocationData = false;

        //stop timer
        mChronometerUtility.stop();

        //save current time
        mTimeWhenPaused = mChronometerUtility.elapsedTime();
    }

    private void resumeTracking(){

        //set flag to save location data
        mSaveLocationData = true;

        //start accelerometer listener, after a delay of ACCELEROMETER_DELAY
        mAccelerometer.registerAccelerometer();

        //reset time to the time when paused
        mChronometerUtility.resetTime();

        //start timer
        mChronometerUtility.start();    }

/*
   private void initializeRecyclerView(View rootView){

        mBundle = new Bundle();

        // Get userlist
        List<User> userList = mDatabaseHelper.getUsers();
        mActivityTypeList = mDatabaseHelper.getActivityTypes();

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.

        // The number of Columns
        //mRecyclerView.setLayoutManager(new GridLayoutManager(mFragmentActivity, 2));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mFragmentActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerViewAdapter = new MultiChooserRecyclerAdapter(userList, mActivityTypeList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

    }
*/
    private void createUserActivityList(){

        //get user list
        List<User> userList = new ArrayList<>();
        userList = mDatabaseHelper.getUsers();


        //get userActivityList out of bundle
        //Bundle bundle  = getIntent().getExtras();
        if (mBundle.containsKey("userActivityList")) {

            mActivityDetail.setUserActivityList(new ArrayList(mBundle.getParcelableArrayList("userActivityList")));


            //add user check boxes
            for (int i = 0; i < mActivityDetail.getUserActivityList().size(); i++) {

                LinearLayout linearLayout = new LinearLayout(mFragmentActivity);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

                //username
                TextView textView = new TextView(mFragmentActivity);
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, USERNAME_FONT_SIZE);
                textView.setText(mActivityDetail.getUserActivityList().get(i).getUser().getUserName());

                //activity type icon
                ImageView imageView = new ImageView(mFragmentActivity);
                imageView.setImageResource(getResources().getIdentifier(mActivityDetail.getUserActivityList().get(i).getActivityType().getActivityName() + "_icon_small", "drawable", mFragmentActivity.getPackageName()));
                imageView.setLayoutParams(new LinearLayout.LayoutParams(100, 100));

                //add checkbox and textview to linear layout
                linearLayout.addView(textView);
                linearLayout.addView(imageView);

                //add linear layout to parent linear layout
                //mUserCheckBoxLayout.addView(linearLayout);


            }
        }
    }

    private void resetFields(){

        mCoins.setText("0");
        mDistance.setText("0");
        mFeetPerMinute.setText("0");
        mChronometerUtility.resetTime();
    }

    // call this when user clicks Start button
    public void doStartTracker() {



    }

   //**********************************************************************************************
    //  service helper methods
    //**********************************************************************************************

    protected void startServices(GoogleApiClient googleApiClient) {

        mLocationApi.requestLocationUpdates(googleApiClient);
        mAccelerometer.registerAccelerometer();
    }

    protected void stopServices(GoogleApiClient googleApiClient) {

         mLocationApi.removeLocationUpdates(googleApiClient);
         mAccelerometer.unregisterAccelerometer();

    }

    //**********************************************************************************************
    //  Control methods
    //**********************************************************************************************

    private void startRun(){
        mRequestedService = true;

        startServices(mGoogleApi.client());

        //chronometer settings, set base time right before starting the chronometer
        mChronometerUtility.resume();

        //display counters
        mCounterLayout.setVisibility(View.VISIBLE);

        //TODO:  this isn't always accurate so not sure if it should be used
        //get the starting point
        //updateCache(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));

        //display map of starting point
        mMapFragment.displayStartMap();

    }

    private void stopRun(){

        stopServices(mGoogleApi.client());

        //stop chronometer
        mChronometerUtility.stop();

        //save elapsed time
        mTimeElapsed = mChronometerUtility.getTimeByUnits(mChronometer.getText().toString(), 0);

    }

    private void playSound(){

        MediaPlayer mp;
        mp = MediaPlayer.create(mFragmentActivity, R.raw.cat_meow);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.reset();
                mp.release();
                mp=null;
            }

        });
        mp.start();
    }

    //**********************************************************************************************
    //  Data methods
    //**********************************************************************************************

    private void saveToDB(FitnessDBHelper databaseHelper, List<UnitSplit> unitSplitList, ActivityDetail activityDetail, int totalPoints, float distance){

        /*
        //save to cache
        ActivityDetail activityDetail = new ActivityDetail();
        activityDetail.setStartLocation(new LatLng(mUnitSplitCalorieList.get(0).getLocation().getLatitude(),mUnitSplitCalorieList.get(0).getLocation().getLongitude()));
        activityDetail.setDistanceInFeet(getDistance(1));
        activityDetail.setBearing(mUnitSplitCalorieList.size() > 1 ? mUnitSplitCalorieList.get(0).getBearing() : 0);

        mActivityDetailList.add(activityDetail);
*/

        //save Activity Detail (overall stats)
        long activityId = databaseHelper.insertActivity((float) unitSplitList.get(0).getLocation().getLatitude()
                , (float) unitSplitList.get(0).getLocation().getLongitude()
                , activityDetail.getStartDate()
                , activityDetail.getEndDate()
                , distance //TODO:replace with Enum type
                , unitSplitList.size() > 1 ? unitSplitList.get(0).getBearing() : 0);

        if (activityId != -1){

            //track participants for this activity: save userIds for this activityId
            int rowsAffected = databaseHelper.insertActivityUsers(activityId, activityDetail.getUserActivityList());

            for ( int i = 0; i < unitSplitList.size(); i++) {

                databaseHelper.insertActivityLocationData(activityId
                        , activityDetail.getStartDate()
                        , unitSplitList.get(i).getLocation().getLatitude()
                        , unitSplitList.get(i).getLocation().getLongitude()
                        , unitSplitList.get(i).getLocation().getAltitude()
                        , unitSplitList.get(i).getLocation().getAccuracy()
                        , unitSplitList.get(i).getBearing()
                        , unitSplitList.get(i).getSpeed());
            }
        }

        //update points for each user
        for (UserActivity userActivity : activityDetail.getUserActivityList()){

            User user = userActivity.getUser();
            user.setPoints(totalPoints + user.getPoints());
            databaseHelper.updateUser(user);
        }

    }

    //**********************************************************************************************
    //  updateCache()   /* saves data to cache*/
    //**********************************************************************************************

    private void  updateCache(Location location) {
        if (DEBUG) Log.d(LOG, "updateCache()");

        UnitSplit unitSplit = new UnitSplit(location);

        mUnitSplitList.add(unitSplit);

        //save time elapsed
        //get time from Chronometer
        mTimeElapsed = mChronometerUtility.getTimeByUnits(mChronometer.getText().toString(), 0);
    }


    private void displayResults(){

        mMapFragment.displayMap(mUnitSplitList, getDistance(1));

        //TODO:  why does sound get truncated?
        playSound();

    }

    //**********************************************************************************************
    //  onStart()
    //**********************************************************************************************

    @Override
    public void onStart() {
        if (DEBUG) Log.d(LOG, "onStart");
        super.onStart();

        LocalBroadcastManager.getInstance(mFragmentActivity).registerReceiver(mMessageReceiver, new IntentFilter(GoogleApi.GOOGLE_API_INTENT));


    }

    //**********************************************************************************************
    //  onStop()
    //**********************************************************************************************
    @Override
    public void onStop() {
        if (DEBUG) Log.d(LOG, "onStop");
        super.onStop();
    }

    //**********************************************************************************************
    //  BroadcastReceiver mMessageReceiver
    //**********************************************************************************************

    // Handler for received Intents. This will be called whenever an Intent
    // with an action named GOOGLE_API_INTENT is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(GoogleApi.GOOGLE_API_INTENT);

            if (DEBUG) Log.d(LOG, "BroadcastReceiver - onReceive(): message: " + message);

            // message to indicate Google API Client connection
            if(message.equals(GoogleApi.GOOGLE_API_INTENT)){


                //TODO: pending tracker state
                //show Start button
                mStartButton.setVisibility(View.VISIBLE);
                //add map
                mMapFragment = new MapFragment(getChildFragmentManager(), mGoogleApi);
                mMapFragment.addMap(rootView, mContainer);

            }
            else if (message.equals(LocationApi.LOCATION_API_INTENT)){

                //only track data when it has high level of accuracy && not Pause mode
                if (mSaveLocationData){
                    //update cache
                    updateCache(mLocationApi.location());

                    refreshData();
                }

                //TODO: do we still want a time limit?
                if(mChronometerUtility.getTimeByUnits(mChronometer.getText().toString(), 0) > STOP_SERVICE_TIME_LIMIT && !mIsTimeLimit){
                    mIsTimeLimit = true;
                    reachedTimeLimit();
                    stopRun();
                }
            }
            else if (message.equals(Accelerometer.ACCELEROMETER_INTENT)){

                playSound();

                pauseTracking();

                //disable accelerometer listener
                mAccelerometer.unregisterAccelerometer();

                //display warning message that no movement has been detected
                DialogUtility.displayAlertDialog(mFragmentActivity, getResources().getString(R.string.warning), getResources().getString(R.string.no_movement), getResources().getString(R.string.ok));
            }
        }
    };

    //**********************************************************************************************
    //  onPause() - Activity is partially obscured by another app but still partially visible and not the activity in focus
    //**********************************************************************************************

    @Override
    public void onPause() {
        if (DEBUG) Log.d(LOG, "onPause");

        //do nothing, we want to continue to collect location data until user clicks Stop button
        //other apps may run concurrently, such as music player

        LocalBroadcastManager.getInstance(mFragmentActivity).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    //**********************************************************************************************
    //  onResume()
    //**********************************************************************************************

    @Override
    public void onResume() {
        if (DEBUG) Log.d(LOG, "onResume");
        super.onResume();

        LocalBroadcastManager.getInstance(mFragmentActivity).registerReceiver(mMessageReceiver, new IntentFilter(GoogleApi.GOOGLE_API_INTENT));

        //ensures that if the user returns to the running app through some other means,
        //such as through the back button, the check is still performed.
        //end the activity if Google Play Services is not present
        //redirect user to Google Play Services
        if (!mGoogleApi.servicesAvailable()) {
            mFragmentActivity.finish();
        }

    }

    private void refreshData(){


        mTimeElapsed = mChronometerUtility.getTimeByUnits(mChronometer.getText().toString(), 0);

        //save location data in mLocationList
        displayCurrent();
    }

    //**********************************************************************************************
    //  onDestroy()
    //**********************************************************************************************

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(LOG, "onDestroy");
        super.onDestroy();

        mAccelerometer.unregisterAccelerometer();
    }

    //**********************************************************************************************
    //  Message methods
    //**********************************************************************************************

    private void reachedTimeLimit(){

        displayAlertDialog(getString(R.string.time_limit), getString(R.string.reached_time_limit_30_minutes));
        stopRun();

        //display number of coins earned
        displayResults();
    }

    private void displayAlertDialog(String title, String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mFragmentActivity);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                })
                ;

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    private void displayCurrent(){

        //TODO: replace references of mLocationList with mLocationTimeList
        //if (DEBUG) Log.d(LOG, "displayCurrent: intervalCount" + mLocationList.size());
        if (DEBUG) Log.d(LOG, "displayCurrent: intervalCount" + mUnitSplitList.size());

        if (mUnitSplitList.size() > 1){

            //update distance textview
            float distanceFeet = getDistance(1);
            mDistance.setText(String.format("%d", (int)distanceFeet));

            //update distance in cache
            mActivityDetail.setDistanceInFeet(Math.round(distanceFeet));

            //update speed feet/minute
            //float elapsedMinutes = (float)(SystemClock.elapsedRealtime() - mChronometer.getBase())/(1000 * 60);
            int elapsedMinutes = mChronometerUtility.getTimeByUnits(mChronometer.getText().toString(), 1);
            mFeetPerMinute.setText(String.format("%.0f", (float)distanceFeet/elapsedMinutes));

            //TODO: move this somewhere else, where business rules are updated, not UI update
            //update the UnitSplitCalorie list with calorie and speed values
            refreshUnitSplitAndTotalCalorie();

            //number of coins earned based on distance traveled
            int totalPoints =  Math.round(mActivityDetail.getDistanceInFeet() * FEET_COIN_CONVERSION);

            //compare previous totalCoins to current one
            //TODO:  for now, the points earned is based on distance, so each user will earn the same amount of coins
            //for now, use points earned of first user to represent the points earned for each user
            float delta = totalPoints - mTotalPoints;

            if(delta > 0 ){
                playSound();
            }

            //update coins
            mCoins.setText(String.format("%d", totalPoints));

            //save latest total number of coins
           mTotalPoints = totalPoints;

        }
    }


    private void refreshUnitSplitAndTotalCalorie(){

        //TODO: how to handle the first split, first data point is captured up to 4 seconds after the run starts.

        for ( int i = 0 ; i < mUnitSplitList.size() - 1; i++ ){

            float minutesElapsed = (mUnitSplitList.get(i+1).getLocation().getTime() - mUnitSplitList.get(i).getLocation().getTime()) / (1000f * 60f) ;
            float miles = UnitConverter.convertMetersToMiles(mUnitSplitList.get(i + 1).getLocation().distanceTo(mUnitSplitList.get(i).getLocation()));
            float hoursElapsed = minutesElapsed/60f;
            float milesPerHour = miles / hoursElapsed;

            //calculate calorie for each participant for their specific activity
            //update their total calorie count and points for this activity
            for (int j = 0; j < mActivityDetail.getUserActivityList().size(); j++){

                User user = mActivityDetail.getUserActivityList().get(j).getUser();
                float currentCalorie = mActivityDetail.getUserActivityList().get(j).getCalories();
                mActivityDetail.getUserActivityList().get(j).setCalories(currentCalorie + getCalorieByActivity(user.getWeight(), minutesElapsed, milesPerHour, mActivityDetail.getUserActivityList().get(j).getActivityType().getActivityTypeId()));
                mActivityDetail.getUserActivityList().get(j).setPoints(mTotalPoints);
            }
            //calculate bearing
            float bearing = mUnitSplitList.get(i).getLocation().bearingTo(mUnitSplitList.get(i+1).getLocation());

            //save calorie, speed, bearing in list
            //mUnitSplitCalorieList.get(i).setCalories(calorie);
            mUnitSplitList.get(i).setSpeed(milesPerHour);
            mUnitSplitList.get(i).setBearing(bearing);

        }
    }



    private float getDistance(int units){

        float[] intervalDistance = new float[3];
        float totalDistance = 0.0f;

        //DEBUG
        //StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0 ; i < mUnitSplitList.size() - 1 ; i++){
            Location.distanceBetween(mUnitSplitList.get(i).getLocation().getLatitude(), mUnitSplitList.get(i).getLocation().getLongitude(), mUnitSplitList.get(i+1).getLocation().getLatitude(), mUnitSplitList.get(i + 1).getLocation().getLongitude(), intervalDistance);
            totalDistance += Math.abs(intervalDistance[0]);
            //DEBUG
            //stringBuilder.append("\n" + i + ": " + intervalDistance[0] + " meters");

        }

        switch(units){
            case 0:
                //convert meters to miles
                totalDistance = UnitConverter.convertMetersToMiles(totalDistance);
                break;
            case 1:
                //convert to feet
                totalDistance = UnitConverter.convertMetersToFeet(totalDistance);
                break;

            default:
                //do nothing, units are in meters already
                break;

        }

        return totalDistance;
    }

    private float getCalorieByActivity(float weight, float minutes, float speed, int activityId){

        float calorie = 0f;

        switch (activityId){

            case 1:
                calorie = CalorieCalculator.getCalorieByRun(weight, minutes, speed);
                break;

            case 2:
                calorie = CalorieCalculator.getCalorieByScooter(weight, minutes);
                break;

            case 3:
                calorie = CalorieCalculator.getCalorieByBike(weight, minutes, speed);
                break;

            case 4:
                calorie = CalorieCalculator.getCalorieByRun(weight, minutes, speed);
                break;

            default:
                calorie = CalorieCalculator.getCalorieByRun(weight, minutes, speed);
                break;
        }

        return calorie;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == mFragmentActivity.RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApi.client().isConnecting() &&
                        !mGoogleApi.client().isConnected()) {
                    mGoogleApi.client().connect();
                }
            }
        }
    }


    public static int getSecondsFromChronometer(){

        String string = mChronometer.getText().toString();

        String [] parts = string.split(":");

        // Wrong format, no value for you.
        if(parts.length < 2 || parts.length > 3)
            return 0;

        int seconds = 0, minutes = 0, hours = 0;

        if(parts.length == 2){
            seconds = Integer.parseInt(parts[1]);
            minutes = Integer.parseInt(parts[0]);
        }
        else if(parts.length == 3){
            seconds = Integer.parseInt(parts[2]);
            minutes = Integer.parseInt(parts[1]);
            hours = Integer.parseInt(parts[1]);
        }

        return seconds + (minutes*60) + (hours*3600);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_rewards:
                EditRewards();
                return true;
            case R.id.action_settings:
                //TODO:  openSettings();
                return true;

            default:
        }

        return super.onOptionsItemSelected(item);

    }

    private void EditRewards(){

        //TODO: replace this with a call to EditRewardFragment
        //Intent intent = new Intent(mFragmentActivity, EditReward.class);
        //startActivity(intent);
    }



    private static void refreshDisplay(){

        hideAllButtons();

        //enable or disable buttons if there are participants or not
        //enable button if there is at least one user participating
        if (mActivityDetail.getUserActivityList().size() > 0)
            mStartButton.setVisibility(View.VISIBLE);
        else
            mStartButton.setVisibility(View.GONE);

        //fresh the icons in case user list has changed
        //mRecyclerViewAdapter.notifyDataSetChanged();

    }





    private class SaveToDB implements Runnable {

        public void run() {

            saveToDB(mDatabaseHelper, mUnitSplitList, mActivityDetail, mTotalPoints, getDistance(1));
        }
    }

}
