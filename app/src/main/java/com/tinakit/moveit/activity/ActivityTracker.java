package com.tinakit.moveit.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.api.Accelerometer;
import com.tinakit.moveit.api.GoogleApi;
import com.tinakit.moveit.api.LocationApi;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.fragment.ActivityChooser;
import com.tinakit.moveit.fragment.BackHandledFragment;
import com.tinakit.moveit.fragment.MapFragment;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.UnitSplit;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.model.UserActivity;
import com.tinakit.moveit.model.UserListObservable;
import com.tinakit.moveit.module.CustomApplication;
import com.tinakit.moveit.utility.CalorieCalculator;
import com.tinakit.moveit.utility.ChronometerUtility;
import com.tinakit.moveit.utility.UnitConverter;
import com.tinakit.moveit.utility.DialogUtility;
import com.tinakit.moveit.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class ActivityTracker extends BackHandledFragment {

    //DEBUG
    public static final String ACTIVITY_TRACKER_TAG = "ACTIVITY_TRACKER_TAG";
    private static final boolean DEBUG = true;

    //CONSTANTS
    public static final String ACTIVITY_TRACKER_BROADCAST_RECEIVER = "TRACKER_RECEIVER";
    public static final String ACTIVITY_TRACKER_INTENT = "ACTIVITY_TRACKER_INTENT";
    private static final String AUDIO_ADD_POINTS = "cha_ching";
    private static final String AUDIO_NO_MOVEMENT = "bike_horn";

    @Inject
    GoogleApi mGoogleApi;

    @Inject
    FitnessDBHelper mDatabaseHelper;

    private static final float FEET_COIN_CONVERSION = 0.04f;//  # coins per feet (1 coin/50 ft = 0.02)
    private static long STOP_SERVICE_TIME_LIMIT = 30 * 60 * 1000 * 60; // 30 minutes in seconds
    private static int DATA_COUNT_MINIMUM = 2; // minimum number of data required before enabling tracker
    private static final int SERVICE_TIMEOUT_MILLISECONDS = 60*1000;

    // cache


    //save all location points during location updates
    private List<UnitSplit> mUnitSplitList = new ArrayList<>();
    private int mTotalPoints = 0;
    protected static boolean mRequestedService = false;
    private long mTimeElapsed = 0; //in seconds
    private static boolean mWarningIsVisible = false;

    // APIs
    private LocationApi mLocationApi;
    private Accelerometer mAccelerometer;
    private MapFragment mMapFragment;

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
    private ViewGroup mContainer;
    private TextView mBatteryLevel;
    private android.support.v7.app.AlertDialog alertDialog;
    protected FragmentActivity mFragmentActivity;
    private View rootView;

    // INSTANCE FIELDS
    private ActivityDetail mActivityDetail;
    private long mTimeWhenStopped;
    private boolean mSaveLocationData = false;
    private boolean mHasMapFragment = false;
    private boolean mIsInactive = false;
    private ScheduledExecutorService executorService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_tracker, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCounterLayout = (LinearLayout) rootView.findViewById(R.id.counterLayout);
        mStartButton = (Button) rootView.findViewById(R.id.startButton);
        mStopButton = (Button) rootView.findViewById(R.id.stopButton);
        mPauseButton = (Button) rootView.findViewById(R.id.pauseButton);
        mSaveButton = (Button) rootView.findViewById(R.id.saveButton);
        mResumeButton = (Button) rootView.findViewById(R.id.resumeButton);
        mCancelButton = (Button) rootView.findViewById(R.id.cancelButton);
        mButtonLinearLayout = (LinearLayout) rootView.findViewById(R.id.buttonLayout);
        mChronometer = (Chronometer) rootView.findViewById(R.id.chronometer);
        mDistance = (TextView) rootView.findViewById(R.id.distance);
        mCoins = (TextView) rootView.findViewById(R.id.coins);
        mFeetPerMinute = (TextView) rootView.findViewById(R.id.feetPerMinute);
        mMessage = (TextView) rootView.findViewById(R.id.message);
        mMapFragment = new MapFragment(getActivity().getSupportFragmentManager(), getActivity());
        mMapFragment.addMap(R.id.map_container, mContainer);
        mBatteryLevel = (TextView)rootView.findViewById(R.id.batteryLevel);

        // diable hamburger icon
        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(),
                MainActivity.mDrawerLayout, (Toolbar)getActivity().findViewById(R.id.toolBar), R.string.open, R.string.close);
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        mActionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_launcher_48); //set your own
        mActionBarDrawerToggle.syncState();

        setButtonOnClickListeners();
        initializeData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentActivity = (FragmentActivity) super.getActivity();

        // Dagger 2 injection
        ((CustomApplication) mFragmentActivity.getApplication()).getAppComponent().inject(this);

        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(ActivityChooser.USER_ACTIVITY_LIST_KEY)) {
            // get user list from intent
            mActivityDetail = new ActivityDetail();
            ArrayList<UserActivity> userActivityList = bundle.getParcelableArrayList(ActivityChooser.USER_ACTIVITY_LIST_KEY);
            mActivityDetail.setUserActivityList(userActivityList);
            bindApi(savedInstanceState);
        }
    }

    protected void bindApi(@Nullable Bundle savedInstanceState) {

        //end the activity if Google Play Services is not present
        //redirect user to Google Play Services

        if (!mGoogleApi.servicesAvailable(mFragmentActivity))
            mFragmentActivity.finish();
        else
            mGoogleApi.buildGoogleApiClient(mFragmentActivity);

        // location listener
        mLocationApi = new LocationApi(mFragmentActivity, mGoogleApi.client());
        // accelerometer
        mAccelerometer = new Accelerometer(mFragmentActivity);
        //check savedInstanceState not null
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
    }

    protected void initializeData() {
        //lock Navigation Drawer
        MainActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    //**********************************************************************************************
    //  setButtonOnClickListeners
    //**********************************************************************************************
    protected void setButtonOnClickListeners() {
        mStartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // close MainActivity so ActivityChooser values are not saved
                // once tracking is saved or cancelled, MainActivity will be called
                // send message to indicate there is new location data
                Intent intent = new Intent(ACTIVITY_TRACKER_INTENT);
                intent.putExtra(MainActivity.MAIN_ACTIVITY_BROADCAST_RECEIVER, ACTIVITY_TRACKER_INTENT);
                LocalBroadcastManager.getInstance(mFragmentActivity.getApplicationContext()).sendBroadcast(intent);

                // start run if this is a restart
                if (mStartButton.getText().equals(getString(R.string.restart))) {
                    resetCounters();
                    startRun();
                }
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

            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                stopRun();
            }
        });

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pauseTracking();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                finishTracking(getString(R.string.activity_saved));
                //save activity data to database on separate background thread
                //AsyncTask.execute(new SaveToDB());
                new AsyncTask<Void,Void,Void>() {
                    protected Void doInBackground(Void... params) {
                        new SaveToDB().run();
                        return null;
                    }

                    protected void onPostExecute(Void...params) {
                        CustomApplication app = ((CustomApplication)mFragmentActivity.getApplication());
                        UserListObservable userListObservable = app.getUserListObservable();
                        userListObservable.notifyObservers();
                    }

                }.execute();
           }

        });

        mResumeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resumeTracking();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishTracking(getString(R.string.activity_cancelled));
            }
        });
    }

    private void pauseTracking() {
        // unregister intents with BroadcastReceiver
        LocalBroadcastManager.getInstance(mFragmentActivity).unregisterReceiver(mMessageReceiver);
        //set the flag to not save location data
        mSaveLocationData = false;
        //stop timer
        mChronometerUtility.stop();
        //save current time
        mTimeWhenStopped = mChronometerUtility.getTime() - SystemClock.elapsedRealtime();

        //disable accelerometer listener
        mAccelerometer.stop();

        //set button visibility
        mPauseButton.setVisibility(View.GONE);
        mResumeButton.setVisibility(View.VISIBLE);
        mStopButton.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.GONE);
    }

    private void resumeTracking() {
        //register api intents with BroadcastReceiver
        LocalBroadcastManager.getInstance(mFragmentActivity).registerReceiver(mMessageReceiver, new IntentFilter(LocationApi.LOCATION_API_INTENT));
        LocalBroadcastManager.getInstance(mFragmentActivity).registerReceiver(mMessageReceiver, new IntentFilter(Accelerometer.ACCELEROMETER_INTENT));
        //set flag to save location data
        mSaveLocationData = true;
        //start accelerometer listener, after a delay of ACCELEROMETER_DELAY
        mAccelerometer.start();
        //chronometer settings, set base time to time when paused ChronometerUtility.elapsedTime()
        mChronometerUtility.resume(mTimeWhenStopped);
        //set button visibility
        mResumeButton.setVisibility(View.GONE);
        mStopButton.setVisibility(View.VISIBLE);
        mPauseButton.setVisibility(View.VISIBLE);
    }

    private void finishTracking(String message) {
        Snackbar.make(rootView.findViewById(R.id.main_layout), message, Snackbar.LENGTH_LONG)
                .show();
        mFragmentActivity.finish();
        startActivity(new Intent(mFragmentActivity, MainActivity.class));
    }

    //**********************************************************************************************
    //  Control methods
    //**********************************************************************************************
    private void startRun() {
        mRequestedService = true;
        Intent intent = new Intent(ACTIVITY_TRACKER_INTENT);
        intent.putExtra(MainActivity.MAIN_ACTIVITY_BROADCAST_RECEIVER, ACTIVITY_TRACKER_INTENT);
        LocalBroadcastManager.getInstance(mFragmentActivity.getApplicationContext()).sendBroadcast(intent);
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

        mAccelerometer.start();

        //register api intents with BroadcastReceiver
        //LocalBroadcastManager.getInstance(mFragmentActivity).registerReceiver(mMessageReceiver, new IntentFilter(LocationApi.LOCATION_API_INTENT));
        LocalBroadcastManager.getInstance(mFragmentActivity).registerReceiver(mMessageReceiver, new IntentFilter(Accelerometer.ACCELEROMETER_INTENT));
        //initialize ChronometerUtility, start timer
        mChronometerUtility = new ChronometerUtility(mChronometer);
        mChronometerUtility.start();
        //display counters
        mCounterLayout.setVisibility(View.VISIBLE);
        //display map of starting point
        mMapFragment.displayStartMap();
        // register battery charge listener
        getActivity().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        // register receiver for low battery level
        getActivity().registerReceiver(mBatteryLowReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_LOW));
    }

    private BroadcastReceiver mBatteryLowReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopRun();
            DialogUtility.displayAlertDialog(getActivity(), getString(R.string.title_battery_low), getString(R.string.message_battery_low_auto_shutoff), getString(R.string.ok));
            getActivity().unregisterReceiver(mBatteryLowReceiver);
        }
    };

    private void startLocationApi() {
        mLocationApi.start();
        //register api intents with BroadcastReceiver
        LocalBroadcastManager.getInstance(mFragmentActivity).registerReceiver(mMessageReceiver, new IntentFilter(LocationApi.LOCATION_API_INTENT));
    }

    private void stopRun() {
        //stopServices(mGoogleApi.client());
        mLocationApi.stop();
        mAccelerometer.stop();
        // unregister intents with BroadcastReceiver
        LocalBroadcastManager.getInstance(mFragmentActivity).unregisterReceiver(mMessageReceiver);
        //stop chronometer
        mChronometerUtility.stop();
        //save elapsed time
        mTimeElapsed = Math.round(mChronometerUtility.getTimeByUnits(mChronometer.getText().toString(), 0));
        // cancel HandlerTask if it's running
        stopRepeatingTask();
        //get timestamp of end
        mActivityDetail.setEndDate(new Date());
        //set button visibility
        mStopButton.setVisibility(View.GONE);
        mPauseButton.setVisibility(View.GONE);
        mResumeButton.setVisibility(View.GONE);
        //save Activity Detail data
        if (mUnitSplitList.size() > 1) {
            mCancelButton.setVisibility(View.VISIBLE);
            mSaveButton.setVisibility(View.VISIBLE);

            //display number of coins
            displayResults();
        } else {
            //not enough data
            mStartButton.setVisibility(View.VISIBLE);
            mStartButton.setText(getString(R.string.restart));

            mCancelButton.setVisibility(View.VISIBLE);

            //message:  no data to display
            Snackbar.make(rootView.findViewById(R.id.main_layout), getString(R.string.message_no_location_data_restart), Snackbar.LENGTH_LONG)
                    .show();
            playSound(AUDIO_ADD_POINTS);
        }
    }

    private void playSound(String audioFileName) {
        MediaPlayer mp;
        mp = MediaPlayer.create(mFragmentActivity, getResources().getIdentifier(audioFileName, "raw", mFragmentActivity.getPackageName()));//R.raw.cat_meow);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp = null;
            }

        });
        mp.start();
    }

    //**********************************************************************************************
    //  Data methods
    //**********************************************************************************************
    private void saveToDB(FitnessDBHelper databaseHelper, List<UnitSplit> unitSplitList, ActivityDetail activityDetail, int totalPoints, float distance) {
        //save Activity Detail (overall stats)
        long activityId = databaseHelper.insertActivity((float) unitSplitList.get(0).getLocation().getLatitude()
                , (float) unitSplitList.get(0).getLocation().getLongitude()
                , activityDetail.getStartDate()
                , activityDetail.getEndDate()
                , distance
                , unitSplitList.size() > 1 ? unitSplitList.get(0).getBearing() : 0);

        if (activityId != -1) {
            //track participants for this activity: save userIds for this activityId
            int rowsAffected = databaseHelper.insertActivityUsers(activityId, activityDetail.getUserActivityList());

            for (int i = 0; i < unitSplitList.size(); i++) {
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
        for (UserActivity userActivity : activityDetail.getUserActivityList()) {
            User user = userActivity.getUser();

            user.setPoints(totalPoints + user.getPoints());

            // if don't already have a reward, check if user has enough points to earn a reward
            Reward reward = mDatabaseHelper.getRewardEarned(user.getUserId(), false);
            // if reward points is 0 then user has not earned a reward yet
            if (reward.getPoints() == 0) {
                if (user.getPoints() >= user.getReward().getPoints()) {
                    user.setPoints(user.getPoints() - user.getReward().getPoints());
                    // insert Reward Earned
                    databaseHelper.insertRewardEarned(user.getReward().getName(), user.getReward().getPoints(), user.getUserId());
                }
            }
            databaseHelper.updateUser(user);
        }
    }

    //**********************************************************************************************
    //  updateCache()   /* saves data to cache*/
    //**********************************************************************************************

    private void updateCache(Location location) {
        if (DEBUG) Log.d(ACTIVITY_TRACKER_TAG, "updateCache()");
        UnitSplit unitSplit = new UnitSplit(location);
        mUnitSplitList.add(unitSplit);
        //save time elapsed
        //get time from Chronometer
        mTimeElapsed = Math.round(mChronometerUtility.getTimeByUnits(mChronometer.getText().toString(), 0));
    }

    private void displayResults() {
        mMapFragment.displayMap(mUnitSplitList, getDistance(1));
        playSound(AUDIO_ADD_POINTS);
    }

    //**********************************************************************************************
    //  onStart()
    //**********************************************************************************************
    @Override
    public void onStart() {
        if (DEBUG) Log.d(ACTIVITY_TRACKER_TAG, "onStart");
        super.onStart();
        LocalBroadcastManager.getInstance(mFragmentActivity).registerReceiver(mMessageReceiver, new IntentFilter(GoogleApi.GOOGLE_API_INTENT));
    }

    //**********************************************************************************************
    //  onStop()
    //**********************************************************************************************
    @Override
    public void onStop() {
        if (DEBUG) Log.d(ACTIVITY_TRACKER_TAG, "onStop");
        super.onStop();
    }

    //**********************************************************************************************
    //  BroadcastReceiver mMessageReceiver
    //**********************************************************************************************
    // Handler for received Intents.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

           String message = intent.getStringExtra(ACTIVITY_TRACKER_BROADCAST_RECEIVER);
            if (DEBUG)
                Log.d(ACTIVITY_TRACKER_TAG, "BroadcastReceiver - onReceive(): message: " + message);
            // message to indicate Google API Client connection
            if (message.equals(GoogleApi.GOOGLE_API_INTENT)) {
                startLocationApi();
                // check periodically for connection
                startRepeatingTask();
            } else if (message.equals(LocationApi.LOCATION_API_INTENT)) {
                //only track data when it has high level of accuracy && has not been inactive within the last second
                if (mSaveLocationData && !mIsInactive) {
                    //get location data
                    Location location = mLocationApi.location();
                    //only save data if it meets realistic value based on world record for speed.
                    //update cache
                    updateCache(mLocationApi.location());
                    refreshData();
                }
                /*
                // auto-shutoff after reached time limit
                if(mChronometerUtility.getTimeByUnits(mChronometer.getText().toString(), 0) > STOP_SERVICE_TIME_LIMIT && !mIsTimeLimit){
                    mIsTimeLimit = true;
                    reachedTimeLimit();
                    stopRun();
                }
                */
                // detected inactivity
            } else if (message.equals(Accelerometer.ACCELEROMETER_INTENT) && mWarningIsVisible == false) {
                playSound(AUDIO_NO_MOVEMENT);
                pauseTracking();
                displayNoMovementWarning();
                mIsInactive = false;
            }
        }
    };

    void startAnim() {
        rootView.findViewById(R.id.avloadingIndicatorView).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.map_container).setVisibility(View.GONE);
    }

    void stopAnim() {
        rootView.findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
        rootView.findViewById(R.id.loading_layout).setVisibility(View.GONE);
        rootView.findViewById(R.id.map_container).setVisibility(View.VISIBLE);
    }

    // reference: http://stackoverflow.com/questions/7462098/handlerthread-vs-executor-when-is-one-more-appropriate-over-the-other
    //**********************************************************************************************
    //  HandlerTask - checks whether polling for location has started, indicating there is a good signal
    //**********************************************************************************************
    void runHandlerTask() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(mHandlerTask, 1, 1, TimeUnit.SECONDS);
    }

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            startAnim();
            //Wait for polling data to come in
            Long t = Calendar.getInstance().getTimeInMillis();
            while (!mLocationApi.isPollingData(DATA_COUNT_MINIMUM) &&
                    Calendar.getInstance().getTimeInMillis()-t<SERVICE_TIMEOUT_MILLISECONDS) {
                if(DEBUG) Log.d("Activity Tracker", "Waiting for polling information");
                if(DEBUG) Log.d("Activity Tracker", mLocationApi.isPollingData(DATA_COUNT_MINIMUM) ? "YES" : "NO");
            }
            //If polling data failed to go through
            if (!mLocationApi.isPollingData(DATA_COUNT_MINIMUM)) {
                //display alert dialog, warning there may be weak connection
                // move to a better location
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayContinueTrackerDialog();
                    }
                });
            } else { //Else go to the running page
                if (alertDialog != null)
                    alertDialog.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopAnim();
                        startRun();
                    }
                });
                stopRepeatingTask();
            }
        }
    };

    void displayContinueTrackerDialog() {
        alertDialog = new android.support.v7.app.AlertDialog.Builder(
                mFragmentActivity,
                R.style.AlertDialogCustom_Destructive)
                .setPositiveButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mFragmentActivity.finish();
                        startActivity(new Intent(mFragmentActivity, MainActivity.class));
                    }
                })
                .setNegativeButton(R.string.button_wait, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        runHandlerTask();
                    }
                })
                .setTitle(R.string.message_continue_tracker_title)
                .setMessage(R.string.message_continue_tracker)
                .show();
    }

    void startRepeatingTask() {
        runHandlerTask();
    }

    void stopRepeatingTask() {
        executorService.shutdownNow();
    }

    //**********************************************************************************************
    //  ConnectionTask
    //**********************************************************************************************

    private class ConnectionTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }


    private void displayNoMovementWarning() {
        mWarningIsVisible = true;
        AlertDialog.Builder alert = new AlertDialog.Builder(mFragmentActivity);
        alert.setTitle(getString(R.string.warning));
        alert.setMessage(getString(R.string.msg_activity_tracker_no_movement));
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mWarningIsVisible = false;
                resumeTracking();
            }
        });
        alert.show();
    }

    //**********************************************************************************************
    //  onPause() - Activity is partially obscured by another app but still partially visible and not the activity in focus
    //**********************************************************************************************

    @Override
    public void onPause() {
        if (DEBUG) Log.d(ACTIVITY_TRACKER_TAG, "onPause");
        super.onPause();

        LocalBroadcastManager.getInstance(mFragmentActivity).unregisterReceiver(mMessageReceiver);
    }

    //**********************************************************************************************
    //  onResume()
    //**********************************************************************************************

    @Override
    public void onResume() {
        if (DEBUG) Log.d(ACTIVITY_TRACKER_TAG, "onResume");
        super.onResume();
        // register intents with BroadcastReceiver
        LocalBroadcastManager.getInstance(mFragmentActivity).registerReceiver(mMessageReceiver, new IntentFilter(LocationApi.LOCATION_API_INTENT));
        LocalBroadcastManager.getInstance(mFragmentActivity).registerReceiver(mMessageReceiver, new IntentFilter(Accelerometer.ACCELEROMETER_INTENT));
    }

    private void refreshData() {
        mTimeElapsed = Math.round(mChronometerUtility.getTimeByUnits(mChronometer.getText().toString(), 0));
        //save location data in mLocationList
        displayCurrent();
    }

    //**********************************************************************************************
    //  onDestroy()
    //**********************************************************************************************

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(ACTIVITY_TRACKER_TAG, "onDestroy");
        super.onDestroy();
        mAccelerometer.stop();
    }

    //**********************************************************************************************
    //  BackHandledFragment methods
    //**********************************************************************************************
    @Override
    public boolean onBackPressed() {
        // if tracker is running, pause it
        if (mPauseButton.getVisibility() == View.VISIBLE)
            pauseTracking();
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(
                mFragmentActivity,
                R.style.AlertDialogCustom_Destructive)
                .setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mFragmentActivity.finish();
                        startActivity(new Intent(mFragmentActivity, MainActivity.class));
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!mStartButton.getText().equals(getString(R.string.restart)))
                            resumeTracking();
                    }
                })
                .setTitle(R.string.message_discard_activity_title)
                .setMessage(R.string.message_discard_activity)
                .show();
        return true;
    }

    @Override
    public String getTagText() {
        return ACTIVITY_TRACKER_TAG;
    }

    //**********************************************************************************************
    //  Message methods
    //**********************************************************************************************

    private void reachedTimeLimit() {
        displayAlertDialog(getString(R.string.time_limit), getString(R.string.reached_time_limit_30_minutes));
        stopRun();
        //display number of coins earned
        //displayResults();
    }

    private void displayAlertDialog(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mFragmentActivity);
        // set title
        alertDialogBuilder.setTitle(title);
        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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

    private void displayCurrent() {
        if (DEBUG)
            Log.d(ACTIVITY_TRACKER_TAG, "displayCurrent: intervalCount" + mUnitSplitList.size());

        if (mUnitSplitList.size() > 1) {
            //update distance textview
            float distanceFeet = getDistance(1);
            mDistance.setText(String.format("%d", (int) distanceFeet));
            //update distance in cache
            mActivityDetail.setDistanceInFeet(Math.round(distanceFeet));

            //update speed feet/minute
            //float elapsedMinutes = (float)(SystemClock.elapsedRealtime() - mChronometer.getBase())/(1000 * 60);
            float elapsedMinutes = mChronometerUtility.getTimeByUnits(mChronometer.getText().toString(), 1);
            float speed = distanceFeet / elapsedMinutes;
            if (speed > 0)
                mFeetPerMinute.setText(String.format("%.0f", speed));
            else
                mFeetPerMinute.setText("0.0");

            //TODO: move this somewhere else, where business rules are updated, not UI update
            //update the UnitSplitCalorie list with calorie and speed values
            refreshUnitSplitAndTotalCalorie();

            //number of coins earned based on distance traveled
            int totalPoints = Math.round(mActivityDetail.getDistanceInFeet() * FEET_COIN_CONVERSION);

            //compare previous totalCoins to current one
            // points earned is based on distance, so each user will earn the same amount of coins
            float delta = totalPoints - mTotalPoints;

            if (delta > 0) {
                playSound(AUDIO_ADD_POINTS);
            }

            //update coins
            mCoins.setText(String.format("%d", totalPoints));
            //save latest total number of coins
            mTotalPoints = totalPoints;
        }
    }

    private void refreshUnitSplitAndTotalCalorie() {
        //TODO: how to handle the first split, first data point is captured up to 4 seconds after the run starts.

        for (int i = 0; i < mUnitSplitList.size() - 1; i++) {
            float minutesElapsed = (mUnitSplitList.get(i + 1).getLocation().getTime() - mUnitSplitList.get(i).getLocation().getTime()) / (1000f * 60f);
            float miles = UnitConverter.convertMetersToMiles(mUnitSplitList.get(i + 1).getLocation().distanceTo(mUnitSplitList.get(i).getLocation()));
            float hoursElapsed = minutesElapsed / 60f;
            float milesPerHour = miles / hoursElapsed;

            //calculate calorie for each participant for their specific activity
            //update their total calorie count and points for this activity
            for (int j = 0; j < mActivityDetail.getUserActivityList().size(); j++) {
                User user = mActivityDetail.getUserActivityList().get(j).getUser();
                float currentCalorie = mActivityDetail.getUserActivityList().get(j).getCalories();
                mActivityDetail.getUserActivityList().get(j).setCalories(currentCalorie + getCalorieByActivity(user.getWeight(), minutesElapsed, milesPerHour, mActivityDetail.getUserActivityList().get(j).getActivityType().getActivityTypeId()));
                mActivityDetail.getUserActivityList().get(j).setPoints(mTotalPoints);
            }
            //calculate bearing
            float bearing = mUnitSplitList.get(i).getLocation().bearingTo(mUnitSplitList.get(i + 1).getLocation());

            //save calorie, speed, bearing in list
            //mUnitSplitCalorieList.get(i).setCalories(calorie);
            mUnitSplitList.get(i).setSpeed(milesPerHour);
            mUnitSplitList.get(i).setBearing(bearing);
        }
    }

    private float getDistance(int units) {
        float[] intervalDistance = new float[3];
        float totalDistance = 0.0f;

        for (int i = 0; i < mUnitSplitList.size() - 1; i++) {
            Location.distanceBetween(mUnitSplitList.get(i).getLocation().getLatitude(), mUnitSplitList.get(i).getLocation().getLongitude(), mUnitSplitList.get(i + 1).getLocation().getLatitude(), mUnitSplitList.get(i + 1).getLocation().getLongitude(), intervalDistance);
            totalDistance += Math.abs(intervalDistance[0]);
        }

        switch (units) {
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

    private float getCalorieByActivity(float weight, float minutes, float speed, int activityId) {
        float calorie = 0f;
        switch (activityId) {
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

    private class SaveToDB implements Runnable {
        public void run() {
            saveToDB(mDatabaseHelper, mUnitSplitList, mActivityDetail, mTotalPoints, getDistance(1));
        }
    }

    private void resetCounters() {
        mChronometerUtility.resetTime();
        mFeetPerMinute.setText("0");
        mDistance.setText("0");
        mCoins.setText("0");
    }
}
