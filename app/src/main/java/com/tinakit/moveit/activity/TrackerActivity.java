package com.tinakit.moveit.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.tinakit.moveit.fragment.LoginFragment;
import com.tinakit.moveit.fragment.RegisterUserFragment;
import com.tinakit.moveit.model.ActivityType;
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
import com.tinakit.moveit.utility.UnitConverter;

public class TrackerActivity extends AppCompatActivity {

    //DEBUG
    private static final String LOG = "MAIN_ACTIVITY";
    private static final boolean DEBUG = true;
    private static final ActivityType mActivityType = ActivityType.RUNNING;

    //CONSTANTS
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final float METER_MILE_CONVERSION = 0.00062137f;
    private static final float METER_FEET_CONVERSION = 3.28084f;
    private static final float FEET_COIN_CONVERSION = 0.5f;
     //2 feet = 1 coin
    private static final float CALORIE_COIN_CONVERSION = 10f; //#coins equal to 1 calorie
    protected static final String SHARED_PREFERENCES_COINS = "SHARED_PREFERENCES_COINS";

    //UNITS
    private static final int MILES = 0;
    private static final int FEET = 1;
    private static final int METERS = 2;

    LocationService mBoundService;

    //save all location points during location updates
    ArrayList<Location> mLocationList;
    ArrayList<UnitSplitCalorie> mUnitSplitCalorieList;


    protected static boolean mRequestedService = false;
    long mTimeElapsed = 0; //in seconds

    //state flags
    private boolean mIsStatView = false;

    //UI widgets
    private TextView mResults;
    private Button mStartButton;
    private Button mStopButton;
    private TextView mActivityDetails;
    private static Chronometer mChronometer;
    private ImageView mActivityIcon;
    private TextView mDistance;
    private TextView mCoins;
    private TextView mFeetPerMinute;
    private ImageView mMapImage;

    //TODO:debug
    private TextView mFirstSplitTime;
    private Date mStartTime;


    //local cache
    private float mTotalCoins = 0f;
    private int mActivityId = -1;
    private int mUserId;
    float mTotalCalories = 0;

    //TODO: replace test data with intent bundle from login screen
    //Session variables
    private User mUser = new User("Lucy","password",false,40,"bunny");

    //SharedPreferences
    //private static final String SHARED_PREFERENCES_LOGIN = "SHARED_PREFERENCES_LOGIN";
    //SharedPreferences mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES_LOGIN, 0);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(LOG, "onCreate()");
        initialize();
        super.onCreate(savedInstanceState);

        //fix the orientation to portrait
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        //TODO: this came from Login screen, removing Login for now
        //get userId out of the intent
        /*
        if(getIntent().getExtras().containsKey("userId"))
            mUserId = getIntent().getExtras().getInt("userId");
        */
        //TODO: how to end this elegantly?
        //if(!checkPlayServices()){
        //    Toast.makeText(this, "You need to install Google Play Services for this app to work.", Toast.LENGTH_LONG);
        //    finish();
        //}




        //wire up UI widgets
        mStartButton = (Button)findViewById(R.id.startButton);
        mResults = (TextView)findViewById(R.id.results);
        mActivityDetails = (TextView)findViewById(R.id.activityDetails);
        mChronometer = (Chronometer)findViewById(R.id.chronometer);
        mActivityIcon = (ImageView)findViewById(R.id.activityType_icon);
        mDistance = (TextView)findViewById(R.id.distance);
        mCoins = (TextView)findViewById(R.id.coins);
        mFeetPerMinute = (TextView)findViewById(R.id.feetPerMinute);
        mMapImage = (ImageView)findViewById(R.id.map);

        //TODO: debug
        mFirstSplitTime = (TextView)findViewById(R.id.firstSplitTime);

        //TODO: get activity details from Preference Activity, to be displayed at the top of the screen
        if(getIntent() != null){

            if(getIntent().getExtras().containsKey("username") && getIntent().getExtras().containsKey("activity_type")){
                mActivityDetails.setText(getIntent().getExtras().getString("username") +
                        " " + getIntent().getExtras().getString("activity_type") +
                        " " + new SimpleDateFormat("EEEE h:mm a").format(new Date()));

                mActivityIcon.setImageResource(getResources().getIdentifier(getIntent().getExtras().getString("activity_type") + "_icon_small", "drawable", getPackageName()));

            }

            if(getIntent().getExtras().containsKey("activityId")){
                mActivityId = getIntent().getExtras().getInt("activityId");
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

                    startRun();

                } else if (mStartButton.getText().equals(getResources().getString(R.string.stop))) {

                    stopRun();
                    //TODO:  disable this until main functionality is done
                    //save the total number of coins
                    //saveCoins(mUserId, Integer.parseInt(mCoins.getText().toString()));

                    //display number of coins earned
                    displayResults();

                } else if (mStartButton.getText().equals(getResources().getString(R.string.done))) {

                    finish();

                }
            }
        });
    }

    private void initialize(){
        if (DEBUG) Log.d (LOG, "initialize(): STARTING");

        //there are three main bind/unbind groupings: onCreate() and onDestroy(), onStart() and onStop(), and onResume() and onPause()
        //http://stackoverflow.com/questions/1992676/i-cant-get-rid-of-this-error-message-activity-app-name-has-leaked-servicecon
        //TODO: not sure if we need this, let startrun and stoprun handle starting and stopping service
        //doBindService();

        if (DEBUG) Log.d (LOG, "initialize(): COMPLETE");

    }


    @Override
    protected void onDestroy() {
        if (DEBUG) Log.d(LOG, "onDestroy");

        //TODO: not sure if we need this, let startrun and stoprun handle starting and starting service
        //doUnbindService();

        super.onDestroy();
    }

    private void startRun(){
        mRequestedService = true;

        mStartButton.setText(getResources().getString(R.string.stop));

        //bind to service
        doBindService();

        //chronometer settings, set base time right before starting the chronometer
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

        //TODO: debug
        mStartTime = new Date();

    }

    private void displayResults(){

        mResults.setText("You earned " + mCoins.getText() + " coins!");
        mMapImage.setVisibility(View.VISIBLE);
        playSound();

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

    private void stopRun(){

        //unbind service
        doUnbindService();

        //get latest data
        getData();

        mStartButton.setText(getString(R.string.done));

        //stop chronometer
        mChronometer.stop();

    }

    private void saveCoins(int userId, int numberOfCoins){

        //TODO: either cache coin count or save to DB
        //get data from SharedPreferences
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences sharedPreferences = getSharedPreferences(LoginFragment.SHARED_PREFERENCES_MOVEIT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String commaDelimitedCoins = sharedPreferences.getString(RegisterUserFragment.SHARED_PREFERENCES_COINS, "");
        List<String> coinList = new ArrayList<String>(Arrays.asList(commaDelimitedCoins.split(",")));

        int currentCoins = 0;

        //if userId is within range of the indices of the list
        if(userId <= coinList.size() && userId >= 0){

            //add coins earned to the current total
            currentCoins = Integer.parseInt(coinList.get(userId));
            coinList.set(userId, String.valueOf(numberOfCoins + currentCoins));

            //convert back to comma-delimited string
            commaDelimitedCoins = TextUtils.join(",", coinList);

            //save in SharedPreferences
            editor.putString(SHARED_PREFERENCES_COINS, commaDelimitedCoins);
            editor.commit();
        }
    }

    private void stopLocationService(){
        mRequestedService = false;
        stopService(new Intent(TrackerActivity.this, LocationService.class));

    }



    //**********************************************************************************************
    //  onStart()
    //**********************************************************************************************

    @Override
    protected void onStart() {
        if (DEBUG) Log.d(LOG, "onStart");
        super.onStart();

        // Register to receive Intents with actions named DATA_SERVICE_INTENT.
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter(LocationService.LOCATION_SERVICE_INTENT));
    }

    private void getData(){

        //TODO: remove after mLocationTimeList is fully implemented
        //mLocationList = (ArrayList)mBoundService.getLocationList();

        mUnitSplitCalorieList = (ArrayList)mBoundService.getUnitSplitCalorieList();

        //mTimeElapsed = mBoundService.getTimeElapsed();

        mTimeElapsed = getSecondsFromChronometer();

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

        mLocationList = new ArrayList<>();

        //copy Location data into mLocationList
        for (UnitSplitCalorie unitSplitCalorie : mUnitSplitCalorieList){
            mLocationList.add(unitSplitCalorie.getLocation());

        }

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
    //  doBindService()
    //**********************************************************************************************

    void doBindService() {
        if (DEBUG) Log.d(LOG, "doBindService()");

        if(!isBound()) {
            if (DEBUG) Log.d(LOG, "Binding Service");

            //explicitly start service
            Bundle extras = new Bundle();
            extras.putBoolean("RequestingServiceUpdate", true);
            startService(new Intent(TrackerActivity.this, LocationService.class));

            bindService(new Intent(TrackerActivity.this, LocationService.class), mConnection, Context.BIND_AUTO_CREATE);

        }
    }

    //**********************************************************************************************
    //  doUnBindService()
    //**********************************************************************************************
    void doUnbindService() {
        if (DEBUG) Log.d(LOG, "doUnbindService()");

        if (isBound()) {
            if (DEBUG) Log.d(LOG, "Unbinding Service");

            //always stop service then unbind from it
            //http://stackoverflow.com/questions/3385554/do-i-need-to-call-both-unbindservice-and-stopservice-for-android-services
            //TODO: not sure if necessary to call stopservice before unbind
            stopLocationService();

            //detach our existing connection.
            unbindService(mConnection);
        }
    }

    //**********************************************************************************************
    //  ServiceConnection
    //**********************************************************************************************

    private ServiceConnection mConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (DEBUG) Log.d(LOG, "onServiceConnected - Bound");
            mBoundService = ((LocationService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (DEBUG) Log.d(LOG, "onServiceDisconnected - Unbound");
            mBoundService = null;


            //TODO: not sure if this is correct
            //is there another way that the system will unbind besides the user clicking Stop button
            //displayAlertDialog(getString(R.string.lost_connection), getString(R.string.connection_lost_restart));
            stopLocationService();
        }
    };

    //**********************************************************************************************
    //  isBound()  returns true if service is connected, otherwise return false
    //**********************************************************************************************

    private boolean isBound(){
        return(mBoundService != null);
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


            //let Service drive the update in the MainActivity UI
            //Service will send a message periodically until service is stopped
            if(mRequestedService && message.equals(LocationService.SERVICE_HAS_DATA)){
                if(isBound()){

                    //get data from Location service
                    getData();

                    //remove outliers from LocationTimeList, save location data in mLocationList
                    removeOutliers();

                    displayCurrent();
                }
            }
            else if(message.contains(LocationService.GOOGLEAPI_CONNECTION_FAILURE)){
                String connectionResult = message.substring(LocationService.GOOGLEAPI_CONNECTION_FAILURE.length() - 1, (message.length() - 1));
                String errorMessage = "Google Play Services connection failure: " + connectionResult + ". Try again.";
                        Toast.makeText(TrackerActivity.this, errorMessage, Toast.LENGTH_LONG);

            }
            else if(message.equals(LocationService.SERVICE_TIME_LIMIT)){

                displayAlertDialog(getString(R.string.time_limit), getString(R.string.reached_time_limit_30_minutes));
                stopRun();
                //TODO:  disable this until main functionality is done
                //save the total number of coins
                //saveCoins(mUserId, Integer.parseInt(mCoins.getText().toString()));

                //display number of coins earned
                displayResults();
            }

        }
    };

    private void displayAlertDialog(String title, String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                TrackerActivity.this);

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
        if (DEBUG) Log.d(LOG, "displayCurrent: intervalCount" + mLocationList.size());
        if (DEBUG) Log.d(LOG, "displayCurrent: intervalCount" + mUnitSplitCalorieList.size());

        if (mLocationList.size() > 1){

            //update distance textview
            float distanceFeet = getDistance(1);
            mDistance.setText(String.format("%d", (int)distanceFeet));

            //update speed feet/minute
            float elapsedMinutes = (float)(SystemClock.elapsedRealtime() - mChronometer.getBase())/(1000 * 60);
            mFeetPerMinute.setText(String.format("%.1f", (float)distanceFeet/elapsedMinutes));

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
            mCoins.setText(String.format("%.1f", totalCoins));

            //save latest total number of coins
            mTotalCoins = totalCoins;

        }
    }


    private void refreshUnitSplitCalorie(){

        //TODO: debug
        mFirstSplitTime.setText(String.valueOf(mUnitSplitCalorieList.get(0).getTimeStamp().getTime() - mStartTime.getTime() / (1000f * 60f)));

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


    private float getDistance(int units){

        float[] intervalDistance = new float[3];
        float totalDistance = 0.0f;

        //DEBUG
        //StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0 ; i < mLocationList.size() - 1; i++){
            Location.distanceBetween(mLocationList.get(i).getLatitude(),mLocationList.get(i).getLongitude(),mLocationList.get(i+1).getLatitude(),mLocationList.get(i + 1).getLongitude(), intervalDistance);
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

        switch (mActivityId){

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
    private boolean checkPlayServices() {
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
                displayRewards();
                return true;
            case R.id.action_settings:
                //TODO:  openSettings();
                return true;

            default:
        }

        return super.onOptionsItemSelected(item);

    }

    private void displayRewards(){
        Intent intent = new Intent(this, ViewRewards.class);
        startActivity(intent);
    }
}
