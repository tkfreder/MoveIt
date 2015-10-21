package com.tinakit.moveit.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.tinakit.moveit.adapter.EditActivityRecyclerAdapter;
import com.tinakit.moveit.adapter.UserListCheckBoxRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.ActivityType2;
import com.tinakit.moveit.model.UnitSplitCalorie;
import com.tinakit.moveit.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tinakit.moveit.R;
import com.tinakit.moveit.utility.CalorieCalculator;
import com.tinakit.moveit.utility.DialogUtility;
import com.tinakit.moveit.utility.Map;
import com.tinakit.moveit.utility.UnitConverter;

public class ActivityTracker extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback,
        SensorEventListener {

    //DEBUG
    private static final String LOG = "MAIN_ACTIVITY";
    private static final boolean DEBUG = true;
    private static final ActivityType2 mActivityType = ActivityType2.RUNNING;

    //CONSTANTS
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final float METER_MILE_CONVERSION = 0.00062137f;
    private static final float METER_FEET_CONVERSION = 3.28084f;
    private static final float FEET_COIN_CONVERSION = 0.5f;
     //2 feet = 1 coin
    private static final float CALORIE_COIN_CONVERSION = 10f; //#coins equal to 1 calorie

    //UNITS
    private static final int MILES = 0;
    private static final int FEET = 1;
    private static final int METERS = 2;

    //save all location points during location updates
    private List<Location> mLocationList;
    private List<UnitSplitCalorie> mUnitSplitCalorieList = new ArrayList<>();


    protected static boolean mRequestedService = false;
    long mTimeElapsed = 0; //in seconds

    //state flags
    private boolean mIsStatView = false;

    //LocationRequest settings
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mBestReading;
    private static long POLLING_FREQUENCY = 10 * 1000; //10 seconds
    private static long FASTEST_POLLING_FREQUENCY = 10 * 1000; //5 second
    private static long DISPLACEMENT = 1; //meters //displacement takes precedent over interval/fastestInterval
    private static long STOP_SERVICE_TIME_LIMIT = 30 * 60 * 1000 * 60; // 30 minutes in seconds
    private static final long LOCATION_ACCURACY = 20; //within # meter accuracy
    private boolean mIsTimeLimit = false;

    public static final String GOOGLEAPI_CONNECTION_FAILURE = "x40241.tina.fredericks.a5.app.GOOGLEAPI_CONNECTION_FAILURE";

    //UI widgets
    private TextView mResults;
    private Button mStartButton;
    private Button mStopButton;
    private Button mPauseButton;
    private Button mSaveButton;
    private Button mResumeButton;
    private TextView mActivityHeader;
    private static Chronometer mChronometer;
    private ImageView mActivityIcon;
    private TextView mDistance;
    private TextView mCoins;
    private TextView mFeetPerMinute;
    private RecyclerView mRecyclerView;

    //local cache
    private ActivityDetail mActivityDetail = new ActivityDetail();
    private User mUser;
    private long mTimeWhenPaused;
    private boolean mSaveLocationData = false;

    private static final float ZOOM_STREET_ROUTE = 15.0f;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;

    //TODO: DEBUG
    private static final LatLng HOME1 = new LatLng(34.143000, -118.077089);
    private static final LatLng HOME2 = new LatLng(34.143274, -118.077125);
    private static final LatLng HOME3 = new LatLng(34.143273, -118.076434);
    private static final LatLng HOME4 = new LatLng(34.142364, -118.076501);
    private static final LatLng HOME5 = new LatLng(34.142342, -118.078899);
    private static final LatLng HOME6 = new LatLng(34.143240, -118.078939);
    private static final LatLng HOME7 = new LatLng(34.143255, -118.077145);

    //ACCELEROMETER
    private SensorManager mSensorManager;
    private Sensor sensorAccelerometer;
    private int ACCELEROMETER_DELAY = 60 * 5; //in seconds
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();


    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final float SHAKE_THRESHOLD = 0.5f;


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
                    unregisterAccelerometer();

                    Dialog confirmDialog = DialogUtility.displayConfirmDialog(ActivityTracker.this, getResources().getString(R.string.no_movement),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    resumeTracking();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    stopRun();
                                }
                            });

                    confirmDialog.show();
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

        //disable Accelerometer listener
        unregisterAccelerometer();

        //stop timer
        mChronometer.stop();

        //save current time
        mTimeWhenPaused = mChronometer.getBase() - SystemClock.elapsedRealtime();
    }

    private void resumeTracking(){

        //set flag to save location data
        mSaveLocationData = true;

        //start accelerometer listener, after a delay of 5 minutes
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

        /*
        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Google Map with polylines.");

        // A simple polyline with the default options from Melbourne-Adelaide-Perth.
        map.addPolyline((new PolylineOptions())
                //.add(MELBOURNE, ADELAIDE, PERTH));
                .add(HOME1, HOME2, HOME3, HOME4, HOME5, HOME6, HOME7)
                .color(Color.RED));

        // Move the map so that it is centered on the mutable polyline.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(HOME1, ZOOM_STREET_ROUTE));
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(LOG, "onCreate()");
        super.onCreate(savedInstanceState);

        //end the activity if Google Play Services is not present
        //redirect user to Google Play Services
        if (!servicesAvailable()) {
            finish();

            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }

        }

        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        //TODO:  replace this:  check UI multiselection of users, save the userid's in an array

        //TODO: replace this with DB call or Application Preferences
        mUser = new User();
        mUser.setUserId(1);
        mUser.setUserName("Lucy");
        mUser.setIsAdmin(false);
        mUser.setWeight(40);
        mUser.setAvatarFileName("tiger");

        //display admin menu, if user is admin
        if (mUser.isAdmin()) {
            findViewById(R.id.action_rewards).setVisibility(View.VISIBLE);
        }

        //wire up UI widgets
        mStartButton = (Button) findViewById(R.id.startButton);
        mStopButton = (Button) findViewById(R.id.stopButton);
        mPauseButton = (Button) findViewById(R.id.pauseButton);
        mSaveButton = (Button) findViewById(R.id.saveButton);
        mResumeButton = (Button) findViewById(R.id.resumeButton);
        mResults = (TextView) findViewById(R.id.results);
        mActivityHeader = (TextView) findViewById(R.id.activityHeader);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mActivityIcon = (ImageView) findViewById(R.id.activityType_icon);
        mDistance = (TextView) findViewById(R.id.distance);
        mCoins = (TextView) findViewById(R.id.coins);
        mFeetPerMinute = (TextView) findViewById(R.id.feetPerMinute);

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //get user list
        List<User> userList = new ArrayList<>();
        FitnessDBHelper mDatabaseHelper = FitnessDBHelper.getInstance(this);
        userList = mDatabaseHelper.getUsers();

        //store user list in ActivityDetail
        mActivityDetail.setUserList(userList);

        //user list checkboxes
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        UserListCheckBoxRecyclerAdapter mUserListCheckBoxRecyclerAdapter = new UserListCheckBoxRecyclerAdapter(ActivityTracker.this, mActivityDetail);
        mRecyclerView.setAdapter(mUserListCheckBoxRecyclerAdapter);

        mMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        mMapFragment.getView().setVisibility(View.INVISIBLE);


        //TODO: get activity details from Preference Activity, to be displayed at the top of the screen
        if (getIntent() != null) {

            if (getIntent().getExtras().containsKey("username") && getIntent().getExtras().containsKey("activity_type")) {
                mActivityHeader.setText(getIntent().getExtras().getString("username") +
                        " " + getIntent().getExtras().getString("activity_type") +
                        " " + new SimpleDateFormat("EEEE h:mm a").format(new Date()));

                mActivityIcon.setImageResource(getResources().getIdentifier(getIntent().getExtras().getString("activity_type") + "_icon_small", "drawable", getPackageName()));

            }

            if (getIntent().getExtras().containsKey("activityTypeId")) {
                mActivityDetail.setActivityTypeId(getIntent().getExtras().getInt("activityTypeId"));
            }
            //if(getIntent().getExtras().containsKey("avatar_id"))
            //if(getIntent().getExtras().containsKey("username"))
        }

        //**********************************************************************************************
        //  onClickListeners
        //**********************************************************************************************

        mStartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //set flag to save location data
                mSaveLocationData = true;

                if (mStartButton.getText().equals(getResources().getString(R.string.restart))) {
                    mResults.setText("");
                }

                //get timestamp of start
                mActivityDetail.setStartDate(new Date());

                //set button visibility
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

                stopRun();

                //save Activity Detail data
                if (mUnitSplitCalorieList.size() > 1) {

                    mSaveButton.setVisibility(View.VISIBLE);

                    //display number of coins
                    displayResults();

                } else {
                    mResults.setText("Not enough route information. Restart your activity.");
                    mStartButton.setText(getResources().getString(R.string.restart));

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

            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //save location data to database
                saveToDB();

                //destroy current activity
                finish();

                //display Activity history screen
                Intent intent = new Intent(getApplicationContext(), ActivityHistory.class);
                startActivity(intent);

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


    }

    private void resetFields(){

        mCoins.setText("0");
        mDistance.setText("0");
        mFeetPerMinute.setText("0");
        mChronometer.setBase(SystemClock.elapsedRealtime());

    }

    //this method should be called once at start of run
    //while stopServices should be called once at end of run
    //do not call buildLocationRequest() and stopServices multiple times, difficult to trach asynchronous process
    //use flag mSaveLocationData to determine whether to save data.  during Pause, set mSaveLocationData to false
    private void buildLocationRequest() {

        //create instance of LocationRequest
        createLocationRequest();

        //if connection doesn't exist
        if (mGoogleApiClient == null) {
            //create instance of Google Play Services API client
            buildGoogleApiClient();
        }

    }

    //**********************************************************************************************
    //  Location API overridden methods
    //**********************************************************************************************

    @Override
    public void onConnected(Bundle connectionHint) {
        if (DEBUG) Log.d(LOG, "GoogleAPIClient Connection successful.");

        //TODO:  this isn't always accurate so not sure if it should be used
        //get the starting point
        //updateCache(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));

        //start getting location data after there is a connection
        startServices();

    }

    //TODO:  may be able to utilize this to get a more accurage first data point
    //reference:  http://www.adavis.info/2014/09/android-location-updates-with.html?m=1
    private Location bestLastKnownLocation(float minAccuracy, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Get the best most recent location currently available
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

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
    public void onConnectionSuspended(int i) {
        //TODO: do something if connection suspended
        //let chronometer continue, user should get credit for activity
    }

    @Override
    public void onConnectionFailed(com.google.android.gms.common.ConnectionResult connectionResult) {
        //TODO: do something if connection failed
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
        //mLocationRequest.setSmallestDisplacement(DISPLACEMENT);// to avoid unnecessary updates, but we want to know if runner has not moved so no need to set a minimum distance displacement
        // TODO:  build a warning system or tracker autoshutoff
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

    }

    //PERIODIC LOCATION UPDATES
    protected void startServices() {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        registerAccelerometer();

    }

    private boolean hasAccelerometer(){

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        return mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
    }

    //TODO: rename this method to reflect both operations
    protected void stopServices() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()){

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        }

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

        buildLocationRequest();

        //chronometer settings, set base time right before starting the chronometer
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

    }

    private void stopRun(){

        stopServices();

        //stop chronometer
        mChronometer.stop();

        //save elapsed time
        mTimeElapsed = getSecondsFromChronometer();

    }

    private void playSound(){

        MediaPlayer mp;
        mp = MediaPlayer.create(getApplicationContext(), R.raw.cat_meow);
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

    private void saveToDB(){

        // Get singleton instance of database
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(getApplicationContext());

        //save Activity Detail (overall stats)
        long activityId = databaseHelper.insertActivity(mActivityDetail.getActivityTypeId()
                , (float)mUnitSplitCalorieList.get(0).getLocation().getLatitude()
                , (float)mUnitSplitCalorieList.get(0).getLocation().getLongitude()
                , mActivityDetail.getStartDate()
                , mActivityDetail.getEndDate()
                , getDistance(1)
                , mActivityDetail.getCalories()
                , mActivityDetail.getPointsEarned());

        if (activityId != -1){

            //create arraylist of userIds
            List<Integer> userIdList = new ArrayList<>();
            for (User user : mActivityDetail.getUserList())
                    userIdList.add(user.getUserId());

            //track participants for this activity: save userIds for this activityId
            int rowsAffected = databaseHelper.insertActivityUsers(activityId, userIdList);

            for ( int i = 0; i < mUnitSplitCalorieList.size(); i++) {

                //calculate bearing for all data points except for last one, which will have the same bearing as the previous data point.
                float bearing = 0f;

                if(i < mUnitSplitCalorieList.size() - 1){

                    Location current = mUnitSplitCalorieList.get(i).getLocation();
                    Location next = mUnitSplitCalorieList.get(i+1).getLocation();
                    bearing = current.bearingTo(next);
                }

                databaseHelper.insertActivityLocationData(activityId
                        , mActivityDetail.getStartDate()
                        , mUnitSplitCalorieList.get(i).getLocation().getLatitude()
                        , mUnitSplitCalorieList.get(i).getLocation().getLongitude()
                        , mUnitSplitCalorieList.get(i).getLocation().getAltitude()
                        , mUnitSplitCalorieList.get(i).getLocation().getAccuracy()
                        , bearing
                        , mUnitSplitCalorieList.get(i).getCalories()
                        ,mUnitSplitCalorieList.get(i).getSpeed());
            }
        }
    }

    //**********************************************************************************************
    //  updateCache()   /* saves data to cache*/
    //**********************************************************************************************

    private void  updateCache(Location location) {
        if (DEBUG) Log.d(LOG, "updateCache()");

        //TODO:to be replaced by mLocationTimeList
        //save current location
        //mLocationList.add(location);

        //save current location and timestamp
        Date date = new Date();
        float timeStamp = date.getTime();
        mUnitSplitCalorieList.add(new UnitSplitCalorie(date, location));

        //save time elapsed
        //get time from Chronometer
        mTimeElapsed = getSecondsFromChronometer();
    }


    private void displayResults(){

        mResults.setText("You earned " + mCoins.getText() + " coins!");

        displayMap(mUnitSplitCalorieList);

        //TODO:  why does sound get truncated?
        playSound();

    }

    //**********************************************************************************************
    //  onStart()
    //**********************************************************************************************

    @Override
    protected void onStart() {
        if (DEBUG) Log.d(LOG, "onStart");
        super.onStart();

    }

    private void removeOutliers(){

        //assume first and last data points are accurate
        if (mUnitSplitCalorieList.size() >= 4)

        //check for outliers for all other data
        for (int i = 1; i < mUnitSplitCalorieList.size() - 2; i++){

            float distance = mUnitSplitCalorieList.get(i).getLocation().distanceTo(mUnitSplitCalorieList.get(i+1).getLocation());
            float time = (mUnitSplitCalorieList.get(i+1).getTimeStamp().getTime() - mUnitSplitCalorieList.get(i).getTimeStamp().getTime())/1000;
            float speed = distance/time;

            //compare against world records for this activity
            //if it exceeds the world records, the data must be inaccurate
            if ( speed > mActivityType.getMaxSpeed()){

                Log.i(LOG, "Removing location data: Distance: " + distance + ", Time: " + time + "\n");
                //remove this datapoint
                mUnitSplitCalorieList.remove(i+1);

                //if there are more than 3 datapoints left, re-evaluate the next speed based on the previous and following data points from the one that was removed,
                //by decrementing.  For example if there are 5 datapoints and the 3rd one was removed, need to reevaluate the speed between datapoint 2 and datapoint 4.
                if(mUnitSplitCalorieList.size() >= 4){
                    i--;
                }
            }
        }

        /*
        mLocationList = new ArrayList<>();

        //copy Location data into mLocationList
        for (UnitSplitCalorie unitSplitCalorie : mUnitSplitCalorieList){
            mLocationList.add(unitSplitCalorie.getLocation());

        }
        */

    }


    //**********************************************************************************************
    //  onStop()
    //**********************************************************************************************

    @Override
    protected void onStop() {
        if (DEBUG) Log.d(LOG, "onStop");
        super.onStop();
    }

    //**********************************************************************************************
    //  onPause() - Activity is partially obscured by another app but still partially visible and not the activity in focus
    //**********************************************************************************************

    @Override
    protected void onPause() {
        if (DEBUG) Log.d(LOG, "onPause");

        //do nothing, we want to continue to collect location data until user clicks Stop button
        //other apps may run concurrently, such as music player

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

    private void refreshData(){


        mTimeElapsed = getSecondsFromChronometer();


        //remove outliers from LocationTimeList, save location data in mLocationList
        removeOutliers();

        displayCurrent();
    }

    //**********************************************************************************************
    //  onDestroy()
    //**********************************************************************************************

    @Override
    protected void onDestroy() {
        if (DEBUG) Log.d(LOG, "onDestroy");
        super.onDestroy();

        if(!executor.isTerminated()){
            executor.shutdownNow();
        }
    }

    //**********************************************************************************************
    //  Message methods
    //**********************************************************************************************

    private void reachedTimeLimit(){

        displayAlertDialog(getString(R.string.time_limit), getString(R.string.reached_time_limit_30_minutes));
        stopRun();
        //TODO:  disable this until main functionality is done
        //save the total number of coins
        //saveCoins(mUserId, Integer.parseInt(mCoins.getText().toString()));

        //display number of coins earned
        displayResults();
    }

    private void displayAlertDialog(String title, String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ActivityTracker.this);

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
        if (DEBUG) Log.d(LOG, "displayCurrent: intervalCount" + mUnitSplitCalorieList.size());

        if (mUnitSplitCalorieList.size() > 1){

            //update distance textview
            float distanceFeet = getDistance(1);
            mDistance.setText(String.format("%d", (int)distanceFeet));

            //update speed feet/minute
            float elapsedMinutes = (float)(SystemClock.elapsedRealtime() - mChronometer.getBase())/(1000 * 60);
            mFeetPerMinute.setText(String.format("%.0f", (float)distanceFeet/elapsedMinutes));

            //update the UnitSplitCalorie list with calorie and speed values
            refreshUnitSplitCalorie();

            //number of coins earned
            float totalCoins =  mActivityDetail.getCalories() * CALORIE_COIN_CONVERSION;

            //compare previous totalCoins to current one
            float delta = totalCoins - mActivityDetail.getPointsEarned();

            if(delta > 0 ){
                playSound();
            }

            //update coins
            mCoins.setText(String.format("%d", Math.round(totalCoins)));

            //save latest total number of coins
            mActivityDetail.setPointsEarned(totalCoins);

        }
    }


    private void refreshUnitSplitCalorie(){

        //TODO: how to handle the first split, first data point is captured up to 4 seconds after the run starts.

        mActivityDetail.setCalories(0.0f);

            for ( int i = 0 ; i < mUnitSplitCalorieList.size() - 1; i++ ){

                float minutesElapsed = (mUnitSplitCalorieList.get(i+1).getTimeStamp().getTime() - mUnitSplitCalorieList.get(i).getTimeStamp().getTime()) / (1000f * 60f) ;
                float miles = UnitConverter.convertMetersToMiles(mUnitSplitCalorieList.get(i + 1).getLocation().distanceTo(mUnitSplitCalorieList.get(i).getLocation()));
                float hoursElapsed = minutesElapsed/60f;
                float speed = miles / hoursElapsed;
                float calorie = getCalorieByActivity(mUser.getWeight(), minutesElapsed , speed);

                //save calorie and speed in list
                mUnitSplitCalorieList.get(i).setCalories(calorie);
                mUnitSplitCalorieList.get(i).setSpeed(speed);

                //add to total calories
                mActivityDetail.setCalories(mActivityDetail.getCalories() + calorie);
            }
    }

    private void displayMap(List<UnitSplitCalorie> unitSplitCalorieList){

        if (mGoogleMap != null){

            mMapFragment.getView().setVisibility(View.VISIBLE);

            // Override the default content description on the view, for accessibility mode.
            // Ideally this string would be localised.
            mGoogleMap.setContentDescription("Google Map with polylines.");

            ArrayList<LatLng> locationList = new ArrayList<>();
            for ( UnitSplitCalorie unitSplitCalorie : unitSplitCalorieList) {
                locationList.add(new LatLng(unitSplitCalorie.getLocation().getLatitude(), unitSplitCalorie.getLocation().getLongitude()));
            }

            mGoogleMap.addPolyline((new PolylineOptions().addAll(locationList).color(Color.BLUE)));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HOME1, Map.getZoomByDistance(getDistance(1))));

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

        for (int i = 0 ; i < mUnitSplitCalorieList.size() - 1 ; i++){
            Location.distanceBetween(mUnitSplitCalorieList.get(i).getLocation().getLatitude(),mUnitSplitCalorieList.get(i).getLocation().getLongitude(),mUnitSplitCalorieList.get(i+1).getLocation().getLatitude(),mUnitSplitCalorieList.get(i + 1).getLocation().getLongitude(), intervalDistance);
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

    private float getCalorieByActivity(float weight, float minutes, float speed){

        float calorie = 0f;

        switch (mActivityDetail.getActivityId()){

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

    /**
     * Method to verify google play services on the device
     * */
    private boolean servicesAvailable() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                if (DEBUG) Log.d(LOG, "This device does not support Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
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
        Intent intent = new Intent(this, EditReward.class);
        startActivity(intent);
    }
}
