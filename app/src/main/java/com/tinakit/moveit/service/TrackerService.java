package com.tinakit.moveit.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.UnitSplit;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.model.UserActivity;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Tina on 11/23/2015.
 */
public class TrackerService  extends Service {

    private static final String LOGTAG = "TRACKERSERVICE";
    private static final boolean DEBUG = true;

    //messaging tags
    public static final String TRACKER_SERVICE_UPDATE = "TRACKER_SERVICE_UPDATE";
    public static final String TRACKER_SERVICE_INTENT = "com.tinakit.moveit.TRACKER_SERVICE";

    // Track if a client Activity is bound to us.
    private boolean isBound = false;

    // Track if we've been started at least once.
    private boolean initialized   = false;

    // cache
    private ActivityDetail mActivityDetail;
    private List<UnitSplit> mUnitSplitList;
    private List<User> mUserList;
    private int mPoints;
    private float mDistance;

    //ScheduledThreadPoolExecutor
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> sScheduledFuture;

    //database objects
    FitnessDBHelper mDatabaseHelper;


    //**********************************************************************************************
    //  onCreate()
    //**********************************************************************************************

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(LOGTAG, "*** onCreate(): STARTING");

    }

    //**********************************************************************************************
    //  onStartCommand()
    //**********************************************************************************************

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (DEBUG) {
            Log.d(LOGTAG, "*** onStartCommand(): STARTING; initialized=" + initialized);
            Log.d(LOGTAG, "*** onStartCommand(): flags=" + flags);
            Log.d(LOGTAG, "*** onStartCommand(): intent=" + intent);
        }

        if (initialized)
            return START_STICKY;
        initialize();

        if (DEBUG) Log.d(LOGTAG, "*** onStart(): ENDING");
        // We want this service to continue running until it is explicitly stopped.
        return START_STICKY;
    }

    //**********************************************************************************************
    //  initialize() - occurs once by using initialized flag
    //**********************************************************************************************

    private void initialize() {
        if (DEBUG) Log.d(LOGTAG, "*** initialize()");

        //instantiate DBHelper, to be closed in onDestroy()
        mDatabaseHelper = FitnessDBHelper.getInstance(this);

        initialized = true;

    }

    //**********************************************************************************************
    //  LocalBinder
    //**********************************************************************************************

    private final IBinder mBinder = new LocalBinder();

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder
    {
        public TrackerService getService() {
            return TrackerService.this;
        }
    }

    //**********************************************************************************************
    //  onBind()
    //**********************************************************************************************

    @Override
    public IBinder onBind(Intent intent) {
        if (DEBUG) Log.d(LOGTAG, "*** onBind()");
        if (DEBUG) {
            Log.d (LOGTAG, "*** onBind(): action="+intent.getAction());
            Log.d (LOGTAG, "*** onBind(): toString="+intent.toString());
        }
        isBound = true;

        return mBinder;
    }

    //**********************************************************************************************
    //  onUnbind()
    //**********************************************************************************************


    @Override
    public boolean onUnbind(Intent intent) {
        if (DEBUG) Log.d (LOGTAG, "*** onUnbind()");
        if (DEBUG) {
            Log.d (LOGTAG, "*** onUnbind(): action="+intent.getAction());
            Log.d (LOGTAG, "*** onUnbind(): toString="+intent.toString());
        }


        //cancel Stock download task
        //sScheduledFuture.cancel(false);
        isBound = false;
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        if (DEBUG) Log.d(LOGTAG, "*** onRebind()");
        if (DEBUG) {
            Log.d (LOGTAG, "*** onUnbind(): action="+intent.getAction());
            Log.d (LOGTAG, "*** onUnbind(): toString="+intent.toString());
        }    }

    //**********************************************************************************************
    //  SaveToDBTask
    //**********************************************************************************************

    private class SaveToDBTask implements Runnable {

        public void run() {
            if(DEBUG) Log.d(LOGTAG, "SaveToDBTask");

            long activityId = insertActivity();

            if(activityId != -1){

                insertActivityUsers((int)activityId);

                updateUserPoints();
            }

            //notify client new data is ready to be fetched
            sendMessage(TRACKER_SERVICE_UPDATE);
        }
    }

    //**********************************************************************************************
    //  insertActivity
    //**********************************************************************************************

    protected long insertActivity(){

        //save Activity Detail (overall stats)
        long activityId = mDatabaseHelper.insertActivity((float) mUnitSplitList.get(0).getLocation().getLatitude()
                , (float) mUnitSplitList.get(0).getLocation().getLongitude()
                , mActivityDetail.getStartDate()
                , mActivityDetail.getEndDate()
                , mDistance
                , mUnitSplitList.size() > 1 ? mUnitSplitList.get(0).getBearing() : 0);

        return activityId;
    }

    //**********************************************************************************************
    //  insertActivityUsers
    //**********************************************************************************************

    protected int insertActivityUsers(int activityId){

        //track participants for this activity: save userIds for this activityId
        int rowsAffected = mDatabaseHelper.insertActivityUsers(activityId, mActivityDetail.getUserActivityList());

        for ( int i = 0; i < mUnitSplitList.size(); i++) {

            mDatabaseHelper.insertActivityLocationData(activityId
                    , mActivityDetail.getStartDate()
                    , mUnitSplitList.get(i).getLocation().getLatitude()
                    , mUnitSplitList.get(i).getLocation().getLongitude()
                    , mUnitSplitList.get(i).getLocation().getAltitude()
                    , mUnitSplitList.get(i).getLocation().getAccuracy()
                    , mUnitSplitList.get(i).getBearing()
                    , mUnitSplitList.get(i).getSpeed());
        }

        return rowsAffected;
    }


    //**********************************************************************************************
    //  updateUserPoints
    //**********************************************************************************************

    protected void updateUserPoints(){

        //update points for each user
        for (UserActivity userActivity : mActivityDetail.getUserActivityList()){

            User user = userActivity.getUser();
            user.setPoints(mPoints + user.getPoints());
            mDatabaseHelper.updateUser(user);
        }
    }

    //**********************************************************************************************
    //  sendMessage()
    // Send an Intent with an action name stored in "message". The Intent
    // sent should be received by the ReceiverActivity.
    //**********************************************************************************************

    private void sendMessage(String message) {
        if(DEBUG) Log.d("sender", "Broadcasting message");

        Intent intent = new Intent(TRACKER_SERVICE_INTENT);
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }


    @Override
    public void onDestroy() {
        if(DEBUG) Log.d(LOGTAG, "*** onDestroy()");

        if(mDatabaseHelper != null){
            mDatabaseHelper.close();
            mDatabaseHelper = null;
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        if(DEBUG) Log.d(LOGTAG, "*** onLowMemory()");
    }

    @Override
    public void onTrimMemory(int level) {
        if(DEBUG) Log.d(LOGTAG, "*** onTrimMemory()");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(LOGTAG, "*** onTaskRemoved()");
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(fd, writer, args);
    }

    //**********************************************************************************************
    //  getActivityDetail()
    //
    //**********************************************************************************************

    public ActivityDetail getActivityDetail(){
        return mActivityDetail;
    }

    //**********************************************************************************************
    //  getUserList
    //
    //**********************************************************************************************

    public List<User> getUserList(){
        return mUserList;
    }

    //**********************************************************************************************
    //  setUser
    //
    //**********************************************************************************************

    public void setUser(User user){

        mDatabaseHelper.updateUser(user);

    }

    //**********************************************************************************************
    //  setActivityDetail
    //
    //**********************************************************************************************

    public void setActivityDetail(ActivityDetail activityDetail){

        mActivityDetail = activityDetail;

    }

    //**********************************************************************************************
    //  setUnitSplitList
    //
    //**********************************************************************************************

    public void setUnitSplitList(List<UnitSplit> unitSplitList){

        mUnitSplitList = unitSplitList;

    }

    //**********************************************************************************************
    //  setPoints
    //
    //**********************************************************************************************

    public void setPoints(int points){

        mPoints = points;

    }

    //**********************************************************************************************
    //  setDistance
    //
    //**********************************************************************************************

    public void setDistance(float distance){

        mDistance = distance;

    }
}

