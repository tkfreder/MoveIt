package com.tinakit.moveit.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.ActivityType;
import com.tinakit.moveit.model.UnitSplit;
import com.tinakit.moveit.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

import com.tinakit.moveit.R;
import com.tinakit.moveit.model.UserActivity;
import com.tinakit.moveit.service.GoogleApi;
import com.tinakit.moveit.utility.CalorieCalculator;
import com.tinakit.moveit.utility.DialogUtility;
import com.tinakit.moveit.utility.Map;
import com.tinakit.moveit.utility.UnitConverter;

public class ActivityTracker extends Fragment implements
        LocationListener, OnMapReadyCallback,
        SensorEventListener {

    //DEBUG
    private static final String LOG = "MAIN_ACTIVITY";
    private static final boolean DEBUG = true;

    //CONSTANTS
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final float FEET_COIN_CONVERSION = 0.5f;  //2 feet = 1 coin
    private static final float USERNAME_FONT_SIZE = 20f;

    //save all location points during location updates
    private List<UnitSplit> mUnitSplitList = new ArrayList<>();
    private int mTotalPoints = 0;

    protected static boolean mRequestedService = false;
    long mTimeElapsed = 0; //in seconds

    //state flags
    private boolean mIsStatView = false;

    //LocationRequest settings
    private LocationRequest mLocationRequest;
    private Location mBestReading;
    private static long POLLING_FREQUENCY = 5 * 1000; //10 seconds
    private static long FASTEST_POLLING_FREQUENCY = 5 * 1000; //5 second
    private static long DISPLACEMENT = 1; //meters //displacement takes precedent over interval/fastestInterval
    private static long STOP_SERVICE_TIME_LIMIT = 30 * 60 * 1000 * 60; // 30 minutes in seconds
    private static final long LOCATION_ACCURACY = 10; //within # meter accuracy //TODO: change this for better accuracy
    private boolean mIsTimeLimit = false;

    //GOOGLE PLAY SERVICES
    private GoogleApi mGoogleApi;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
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
    private TextView mDistance;
    private TextView mCoins;
    private TextView mFeetPerMinute;
    private TextView mMessage;
    protected static RecyclerView mRecyclerView;
    public static MultiChooserRecyclerAdapter mRecyclerViewAdapter;

    //local cache
    private FragmentActivity mFragmentActivity;
    public static ActivityDetail mActivityDetail = new ActivityDetail();
    protected static List<ActivityType> mActivityTypeList;
    private long mTimeWhenPaused;
    private boolean mSaveLocationData = false;
    private static Bundle mBundle;
    private static int mSelectedActivityTypeIndex;

    private static final float ZOOM_STREET_ROUTE = 15.0f;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;

    //database
    FitnessDBHelper mDatabaseHelper;

    //ACCELEROMETER
    private SensorManager mSensorManager;
    private Sensor sensorAccelerometer;
    private int ACCELEROMETER_DELAY = 60 * 30; //in seconds
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final float SHAKE_THRESHOLD = 0.5f;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (DEBUG) Log.d(LOG, "onCreateView()");

        mFragmentActivity  = (FragmentActivity)super.getActivity();
        View rootView = inflater.inflate(R.layout.activity_tracker, container, false);

        //fix the orientation to portrait
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        //end the activity if Google Play Services is not present
        //redirect user to Google Play Services
        mGoogleApi = new GoogleApi(mFragmentActivity);

        if (!mGoogleApi.servicesAvailable()) {
            mFragmentActivity.finish();
        }

        //get databaseHelper instance
        mDatabaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);

        //connect to Google Play Services
        buildLocationRequest();

        //check  savedInstanceState not null
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);


        //TODO:  how to handle admin page?
        /*
        //display admin menu, if user is admin
        if (mUser.isAdmin()) {
            findViewById(R.id.action_rewards).setVisibility(View.VISIBLE);
        }
        */

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
        mDistance = (TextView) rootView.findViewById(R.id.distance);
        mCoins = (TextView) rootView.findViewById(R.id.coins);
        mFeetPerMinute = (TextView) rootView.findViewById(R.id.feetPerMinute);
        mMessage = (TextView) rootView.findViewById(R.id.message);

        //recycler view
        initializeRecyclerView(rootView);

        //add map
        addMap(inflater, container);

        return rootView;

    }


    protected void addMap(LayoutInflater inflater, ViewGroup container){

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.map_container, mMapFragment).commit();
        mMapFragment.getMapAsync(this);
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
                mMapFragment.getView().setVisibility(View.GONE);

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
        mRecyclerViewAdapter.notifyDataSetChanged();

        //make map visible
        mMapFragment.getView().setVisibility(View.VISIBLE);
        mMapFragment.getMapAsync(this);

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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                //check for inactivity, below shake threshold
                if (speed < SHAKE_THRESHOLD) {

                    playSound();

                    pauseTracking();

                    //disable accelerometer listener until user clicks on confirm dialog
                    //unregisterAccelerometer();

                    //display warning message that no movement has been detected
                    DialogUtility.displayAlertDialog(mFragmentActivity, getResources().getString(R.string.warning), getResources().getString(R.string.no_movement), getResources().getString(R.string.ok));

                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    private void pauseTracking(){

        //set the flag to not save location data
        mSaveLocationData = false;

        //stop timer
        mChronometer.stop();

        //save current time
        mTimeWhenPaused = mChronometer.getBase() - SystemClock.elapsedRealtime();
    }

    private void resumeTracking(){

        //set flag to save location data
        mSaveLocationData = true;

        //start accelerometer listener, after a delay of ACCELEROMETER_DELAY
        registerAccelerometer();

        //reset time to the time when paused
        mChronometer.setBase(SystemClock.elapsedRealtime() + mTimeWhenPaused);

        //start timer
        mChronometer.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onMapReady(GoogleMap map) {

        mGoogleMap = map;

        displayStartMap();

    }

    private boolean isMapReady(){

        return mGoogleMap != null;
    }

    /*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

  */

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
        mChronometer.setBase(SystemClock.elapsedRealtime());

    }


    //TODO: call this from BroadcastReceiver, when receive message from GoogleApi
    public void doStartTracker() {

        //TODO:  this isn't always accurate so not sure if it should be used
        //get the starting point
        //updateCache(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));

        //display map of starting point
        displayStartMap();

        //start getting location data after there is a connection
        startServices(mGoogleApi.client(), mLocationRequest, this);

    }

    //**********************************************************************************************
    //  Location API overridden methods
    //**********************************************************************************************

    //this method should be called once at start of run
    //while stopServices should be called once at end of run
    //do not call buildLocationRequest() and stopServices multiple times, difficult to trach asynchronous process
    //use flag mSaveLocationData to determine whether to save data.  during Pause, set mSaveLocationData to false
    private void buildLocationRequest() {

        //create instance of LocationRequest
        createLocationRequest();

        //if connection doesn't exist
        if (!mGoogleApi.isConnectedToGoogle()) {
            //create instance of Google Play Services API client
            mGoogleApi = new GoogleApi(mFragmentActivity);
            mGoogleApi.buildGoogleApiClient();
        }

    }

    private void displayStartMap(){

        if (isMapReady() && mGoogleApi.isConnectedToGoogle()){

            mGoogleMap.setContentDescription("Starting point");
            Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApi.client());
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_STREET_ROUTE));

            //start marker
            mGoogleMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .position(latLng)
                            .title("start")
            );
        }
    }

    //TODO:  may be able to utilize this to get a more accurate first data point
    //reference:  http://www.adavis.info/2014/09/android-location-updates-with.html?m=1
    private Location bestLastKnownLocation(float minAccuracy, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Get the best most recent location currently available
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApi.client());

        if (mCurrentLocation != null) {
            float accuracy = mCurrentLocation.getAccuracy();
            long time = mCurrentLocation.getTime();

            if (accuracy < bestAccuracy) {
                bestResult = mCurrentLocation;
                bestAccuracy = accuracy;
                bestTime = time;
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy || bestTime < minTime) {
            return null;
        }
        else {
            return bestResult;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if (DEBUG) Log.d(LOG, "onLocationChanged");

        if (DEBUG) Log.d(LOG, "Accuracy: " + location.getAccuracy());


        //only track data when it has high level of accuracy && not Pause mode
        if (isAccurate(location) && mSaveLocationData){
            //update cache
            updateCache(location);

            refreshData();
        }

        //TODO: do we still want a time limit?
        if(getSecondsFromChronometer() > STOP_SERVICE_TIME_LIMIT && !mIsTimeLimit){
            mIsTimeLimit = true;
            reachedTimeLimit();
            stopRun();
        }

    }

    //**********************************************************************************************
    //  Location helper methods
    //**********************************************************************************************

    private boolean isAccurate(Location location){

        if (location.getAccuracy() < LOCATION_ACCURACY)
            return true;
        else
            return false;
    }

    //PERIODIC LOCATION UPDATES
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(POLLING_FREQUENCY);//get location updates every x seconds
        mLocationRequest.setFastestInterval(FASTEST_POLLING_FREQUENCY);//not to exceed location updates every x seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /*
    protected synchronized GoogleApiClient buildGoogleApiClient(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();

        return googleApiClient;
    }
*/
    //PERIODIC LOCATION UPDATES
    protected void startServices(GoogleApiClient googleApiClient, LocationRequest locationRequest, LocationListener locationListener ) {

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
        registerAccelerometer();
    }

    private boolean hasAccelerometer(){

        mSensorManager = (SensorManager) mFragmentActivity.getSystemService(Context.SENSOR_SERVICE);
        return mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
    }

    //TODO: rename this method to reflect both operations
    protected void stopServices(GoogleApiClient googleApiClient, LocationListener locationListener) {

         LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
         unregisterAccelerometer();

    }

    private void registerAccelerometer(){

        if (hasAccelerometer()) {
            // success! we have an accelerometer

            executor.schedule(new Runnable(){

                @Override
                public void run(){

                    //mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    sensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    mSensorManager.registerListener(ActivityTracker.this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }, ACCELEROMETER_DELAY, TimeUnit.SECONDS);

        }

    }

    private void unregisterAccelerometer(){

        //unregister accelerometer
        if(sensorAccelerometer != null){
            mSensorManager.unregisterListener(this);

            if (!executor.isTerminated())
                executor.shutdownNow();

        }

    }

    //**********************************************************************************************
    //  Control methods
    //**********************************************************************************************

    private void startRun(){
        mRequestedService = true;

        //buildLocationRequest();

        startServices(mGoogleApi.client(), mLocationRequest, this);

        //chronometer settings, set base time right before starting the chronometer
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

        //display counters
        mCounterLayout.setVisibility(View.VISIBLE);

    }

    private void stopRun(){

        stopServices(mGoogleApi.client(), this);

        //stop chronometer
        mChronometer.stop();

        //save elapsed time
        mTimeElapsed = getSecondsFromChronometer();

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
        mTimeElapsed = getSecondsFromChronometer();
    }


    private void displayResults(){

        displayMap(mUnitSplitList);

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
    //  onPause() - Activity is partially obscured by another app but still partially visible and not the activity in focus
    //**********************************************************************************************

    @Override
    public void onPause() {
        if (DEBUG) Log.d(LOG, "onPause");

        //do nothing, we want to continue to collect location data until user clicks Stop button
        //other apps may run concurrently, such as music player

        super.onPause();
    }

    //**********************************************************************************************
    //  onResume()
    //**********************************************************************************************

    @Override
    public void onResume() {
        if (DEBUG) Log.d(LOG, "onResume");
        super.onResume();

        //ensures that if the user returns to the running app through some other means,
        //such as through the back button, the check is still performed.
        //end the activity if Google Play Services is not present
        //redirect user to Google Play Services
        if (!mGoogleApi.servicesAvailable()) {
            mFragmentActivity.finish();
        }

    }

    private void refreshData(){


        mTimeElapsed = getSecondsFromChronometer();

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

        unregisterAccelerometer();
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
            float elapsedMinutes = (float)(SystemClock.elapsedRealtime() - mChronometer.getBase())/(1000 * 60);
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

    private void displayMap(List<UnitSplit> unitSplitList){

        if (mGoogleMap != null){

            //ensure map is visible
            mMapFragment.getView().setVisibility(View.VISIBLE);

            //clear out existing markers if any
            mGoogleMap.clear();

            // Override the default content description on the view, for accessibility mode.
            // Ideally this string would be localised.
            mGoogleMap.setContentDescription("Google Map with polylines.");

            ArrayList<LatLng> locationList = new ArrayList<>();
            for ( UnitSplit unitSplit : unitSplitList) {
                locationList.add(new LatLng(unitSplit.getLocation().getLatitude(), unitSplit.getLocation().getLongitude()));
            }

            mGoogleMap.addPolyline((new PolylineOptions().addAll(locationList).color(Color.BLUE)));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(locationList.get(0).latitude, locationList.get(0).longitude), Map.getZoomByDistance(getDistance(1))));

            //render markers
            addMarkersToMap(locationList.get(0), locationList.get(locationList.size() - 1));
        }

    }

    private void addMarkersToMap(LatLng start, LatLng end) {

        //start marker
        mGoogleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(start)
                .title("start")
                );

        //start marker
        mGoogleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(end)
                .title("end"));
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

    public class MultiChooserRecyclerAdapter extends RecyclerView.Adapter<MultiChooserRecyclerAdapter.CustomViewHolder> {

        private List<User> mUserList;
        private List<ActivityType> mActivityTypeList;


        public MultiChooserRecyclerAdapter(List<User> userList, List<ActivityType> activityTypeList) {

            mUserList = userList;
            mActivityTypeList = activityTypeList;
        }

        @Override
        public int getItemCount() {
            return (null != mUserList ? mUserList.size() : 0);
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {

            ///protected ImageView avatar;
            protected CheckBox userCheckBox;
            protected TextView username;
            protected ImageButton activityIcon;


            public CustomViewHolder(View view) {

                super(view);
                //this.avatar = (ImageView) view.findViewById(R.id.avatar);
                this.userCheckBox = (CheckBox)view.findViewById(R.id.userCheckBox);
                this.username = (TextView)view.findViewById(R.id.username);
                this.activityIcon = (ImageButton)view.findViewById(R.id.activityIcon);

            }
        }

        @Override
        public MultiChooserRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.multi_activity_chooser_item, null);

            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MultiChooserRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

            User user = mUserList.get(i);

            // Populate data from ActivityType data object
            //customViewHolder.avatar.setImageResource(mContext.getResources().getIdentifier(user.getAvatarFileName(), "drawable", mContext.getPackageName()));
            customViewHolder.username.setText(user.getUserName());

            //TODO: remove this after image listener is implemented
            //set tag on checkbox
            customViewHolder.userCheckBox.setTag(user);

            //set click listener and tag on imageview
            customViewHolder.activityIcon.setTag(user);
            customViewHolder.activityIcon.setLayoutParams(new LinearLayout.LayoutParams(100, 100));

            //default icon
            customViewHolder.activityIcon.setBackgroundResource(getResources().getIdentifier("checkbox_icon_small", "drawable", mFragmentActivity.getPackageName()));

            //if user list is not empty
            if (mActivityDetail.getUserActivityList().size() > 0){

                //user is on the list of participants
                UserActivity userActivity = new UserActivity(user);
                if (mActivityDetail.getUserActivityList().contains(userActivity)){

                    customViewHolder.userCheckBox.setChecked(true);
                    int index = mActivityDetail.getUserActivityList().indexOf(new UserActivity(user));
                    customViewHolder.activityIcon.setVisibility(View.VISIBLE);
                    customViewHolder.activityIcon.setBackgroundResource(getResources().getIdentifier(mActivityDetail.getUserActivityList().get(index).getActivityType().getActivityName() + "_icon_small", "drawable", mFragmentActivity.getPackageName()));
                }
            }

            //set onclicklistener for image
            customViewHolder.activityIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    User user = (User) v.getTag();
                    //save user in Bundle
                    mBundle.putParcelable("currentUser", user);

                    //display Activity Chooser popup

                    ActivityChoiceDialogFragment activityChoiceDialogFragment = new ActivityChoiceDialogFragment();
                    activityChoiceDialogFragment.show(getFragmentManager(), "Activity Chooser");

                }
            });

            //TODO: remove this after implementing onclicklistener on image
            /*
            customViewHolder.userCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    User user = (User) buttonView.getTag();
                    //save user in Bundle
                    mBundle.putParcelable("currentUser", user);

                    if (isChecked) {

                        ActivityChoiceDialogFragment activityChoiceDialogFragment = new ActivityChoiceDialogFragment();
                        activityChoiceDialogFragment.show(getFragmentManager(), "Activity Chooser");

                    } else {

                        UserActivity userActivity = new UserActivity(user);

                        if (mActivityDetail.getUserActivityList().contains(userActivity)) {

                            mActivityDetail.getUserActivityList().remove((mActivityDetail.getUserActivityList().indexOf(userActivity)));

                            refreshDisplay();
                        }
                    }
                }
            });
            */
        }
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
        mRecyclerViewAdapter.notifyDataSetChanged();

    }


    public static class ActivityChoiceDialogFragment extends DialogFragment
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(getActivity());

            dialog.setTitle(R.string.pick_activity);

            List<String> activityTypeStringList = new ArrayList<>();

            //create a string array
            for (ActivityType activityType : mActivityTypeList)
                activityTypeStringList.add(activityType.getActivityName());

            //add the non-participant option at the end
            activityTypeStringList.add(getResources().getString(R.string.not_participating));

            dialog.setSingleChoiceItems(activityTypeStringList.toArray(new String[activityTypeStringList.size()]), -1,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mSelectedActivityTypeIndex = which;

                        }
                    })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            User user = (User) mBundle.getParcelable("currentUser");
                            UserActivity userActivity = new UserActivity(user);

                            //get the index if user exists on the list
                            int index = mActivityDetail.getUserActivityList().indexOf(userActivity);

                            //if the user selected an activity type (not including non-participating)
                            if (mSelectedActivityTypeIndex < mActivityTypeList.size())
                                //save the activity type in the UserActivity
                                userActivity.setActivityType(mActivityTypeList.get(mSelectedActivityTypeIndex));

                            //if the user list is empty
                            if (mActivityDetail.getUserActivityList().size() == 0) {
                                //if activity type is a valid activity type - not non-participating, add the user
                                if (mSelectedActivityTypeIndex != mActivityTypeList.size())
                                    mActivityDetail.getUserActivityList().add(userActivity);

                            }
                            //the user list is not empty
                            else {

                                //if the user exists on the list
                                if (index != -1) {

                                    //the user selected non-participating
                                    if (mSelectedActivityTypeIndex == mActivityTypeList.size()) {

                                        //remove the user on the list
                                        mActivityDetail.getUserActivityList().remove(index);
                                    }
                                    //otherwise, update the user's activity
                                    else
                                        mActivityDetail.getUserActivityList().set(index, userActivity);
                                }
                                //the user is not on the list and if user did not select non-participating, add the user
                                else if (mSelectedActivityTypeIndex != mActivityTypeList.size())
                                    mActivityDetail.getUserActivityList().add(userActivity);
                            }

                            refreshDisplay();

                        }
                    })

                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //don't do anything
                        }
                    });

            return dialog.create();
        }

    }


    private class SaveToDB implements Runnable {

        public void run() {

            saveToDB(mDatabaseHelper, mUnitSplitList, mActivityDetail, mTotalPoints, getDistance(1));
        }
    }

}
