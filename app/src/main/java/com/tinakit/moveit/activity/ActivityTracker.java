package com.tinakit.moveit.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.fragment.RegisterUserFragment;
import com.tinakit.moveit.model.ActivityType2;
import com.tinakit.moveit.model.UnitSplitCalorie;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.service.LocationService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.tinakit.moveit.R;
import com.tinakit.moveit.utility.CalorieCalculator;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.utility.DialogUtility;
import com.tinakit.moveit.utility.Map;
import com.tinakit.moveit.utility.UnitConverter;

public class ActivityTracker extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

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
    private TextView mActivityHeader;
    private static Chronometer mChronometer;
    private ImageView mActivityIcon;
    private TextView mDistance;
    private TextView mCoins;
    private TextView mFeetPerMinute;

    //local cache
    private float mTotalCoins = 0f;
    private int mActivityTypeId = -1;
    private int mUserId;
    private float mTotalCalories = 0;
    private Date mStartDate;
    private Date mEndDate;
    public static ArrayList<ActivityDetail> mActivityDetailList = new ArrayList<>();
    private User mUser;

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
        if(!servicesAvailable()){
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

        //TODO:  do something with this check
        //display admin menu, if user is admin
        if(mUser.isAdmin()){
            findViewById(R.id.action_rewards).setVisibility(View.VISIBLE);
        }

        //wire up UI widgets
        mStartButton = (Button)findViewById(R.id.startButton);
        mResults = (TextView)findViewById(R.id.results);
        mActivityHeader = (TextView)findViewById(R.id.activityHeader);
        mChronometer = (Chronometer)findViewById(R.id.chronometer);
        mActivityIcon = (ImageView)findViewById(R.id.activityType_icon);
        mDistance = (TextView)findViewById(R.id.distance);
        mCoins = (TextView)findViewById(R.id.coins);
        mFeetPerMinute = (TextView)findViewById(R.id.feetPerMinute);

        mMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        mMapFragment.getView().setVisibility(View.INVISIBLE);



        //TODO: get activity details from Preference Activity, to be displayed at the top of the screen
        if(getIntent() != null){

            if(getIntent().getExtras().containsKey("username") && getIntent().getExtras().containsKey("activity_type")){
                mActivityHeader.setText(getIntent().getExtras().getString("username") +
                        " " + getIntent().getExtras().getString("activity_type") +
                        " " + new SimpleDateFormat("EEEE h:mm a").format(new Date()));

                mActivityIcon.setImageResource(getResources().getIdentifier(getIntent().getExtras().getString("activity_type") + "_icon_small", "drawable", getPackageName()));

            }

            if(getIntent().getExtras().containsKey("activityTypeId")){
                mActivityTypeId = getIntent().getExtras().getInt("activityTypeId");
            }
            //if(getIntent().getExtras().containsKey("avatar_id"))
            //if(getIntent().getExtras().containsKey("username"))
        }


    //**********************************************************************************************
    //  onClickListeners
    //**********************************************************************************************


        mStartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                if (mStartButton.getText().equals(getResources().getString(R.string.go))) {
                    // Perform action on click

                    //get timestamp of start
                    mStartDate = new Date();

                    startRun();

                }
                else if (mStartButton.getText().equals(getResources().getString(R.string.restart))){

                    //get timestamp of start
                    mStartDate = new Date();

                    startRun();

                    mResults.setText("");

                } else if (mStartButton.getText().equals(getResources().getString(R.string.stop))) {

                    //get timestamp of end
                    mEndDate = new Date();

                    stopRun();

                    //save Activity Detail data
                    if (mUnitSplitCalorieList.size() > 1){

                        //display number of coins
                        displayResults();

                        DialogUtility.displayConfirmDialog(getApplicationContext(), getString(R.string.save_this_activity),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        saveToDB();
                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do nothing
                                    }
                                });




                    }
                    else{
                        mResults.setText("Not enough route information. Restart your activity.");
                        mStartButton.setText(getResources().getString(R.string.restart));

                    }

                } else if (mStartButton.getText().equals(getResources().getString(R.string.done))) {

                    //destroy current activity
                    finish();

                    //display Activity history screen
                    Intent intent = new Intent(getApplicationContext(), ActivityHistory.class);
                    startActivity(intent);

                }
            }
        });
    }

    private void resetFields(){

        mCoins.setText("0");
        mDistance.setText("0");
        mFeetPerMinute.setText("0");
        mChronometer.setBase(SystemClock.elapsedRealtime());

    }

    private void getRunData() {

        //create instance of LocationRequest
        createLocationRequest();

        //create instance of Google Play Services API client
        buildGoogleApiClient();

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
        startLocationUpdates();

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

        //only track data when it has high level of accuracy
        if (isAccurate(location)){
            //update cache
            updateCache(location);

            refreshData();
        }

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
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    //**********************************************************************************************
    //  Control methods
    //**********************************************************************************************

    private void startRun(){
        mRequestedService = true;

        getRunData();

        mStartButton.setText(getResources().getString(R.string.stop));

        //chronometer settings, set base time right before starting the chronometer
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

    }

    private void stopRun(){

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            stopLocationUpdates();

        mStartButton.setText(getString(R.string.done));

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
        long activityId = databaseHelper.insertActivity(mUser.getUserId(), mActivityTypeId, mStartDate, mEndDate, getDistance(1), mTotalCalories, mTotalCoins);

        if (activityId != -1){

            for ( UnitSplitCalorie unitSplitCalorie : mUnitSplitCalorieList ) {
                databaseHelper.insertActivityLocationData(activityId, mStartDate, unitSplitCalorie.getLocation().getLatitude(), unitSplitCalorie.getLocation().getLongitude(), unitSplitCalorie.getLocation().getAltitude(), unitSplitCalorie.getLocation().getAccuracy());
            }
        }

        //TODO: debug - display the map based on location data just saved
        List<UnitSplitCalorie> unitSplitList = databaseHelper.getActivityLocationData(activityId);

        for (UnitSplitCalorie unitSplitCalorie : unitSplitList){
            System.out.println("latitude = " + String.valueOf(unitSplitCalorie.getLocation().getLatitude()));
            System.out.println("longitude = " + String.valueOf(unitSplitCalorie.getLocation().getLongitude()));
            System.out.println("altitude = " + String.valueOf(unitSplitCalorie.getLocation().getAltitude()));
            System.out.println("accuracy = " + String.valueOf(unitSplitCalorie.getAccuracy()));
            System.out.println("activity id = " + String.valueOf(unitSplitCalorie.getActivityId()));
            System.out.println("timeStamp = " + unitSplitCalorie.getTimeStamp());
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
            //int totalCoins = (int) (distanceFeet * FEET_COIN_CONVERSION);
            //int totalCoins = Math.round(mTotalCalories * CALORIE_COIN_CONVERSION);
            float totalCoins =  mTotalCalories * CALORIE_COIN_CONVERSION;

            //compare previous totalCoins to current one
            float delta = totalCoins - mTotalCoins;

            if(delta > 0 ){
                playSound();
            }

            //update coins
            mCoins.setText(String.format("%d", Math.round(totalCoins)));

            //save latest total number of coins
            mTotalCoins = totalCoins;

        }
    }


    private void refreshUnitSplitCalorie(){

        //TODO: how to handle the first split, first data point is captured up to 4 seconds after the run starts.

        mTotalCalories = 0f;

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
                mTotalCalories += calorie;
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

        switch (mActivityTypeId){

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
