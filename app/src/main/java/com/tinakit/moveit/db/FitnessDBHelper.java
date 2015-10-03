package com.tinakit.moveit.db;

//reference: https://github.com/codepath/android_guides/wiki/Local-Databases-with-SQLiteOpenHelper

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.ActivityType;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.User;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tina on 9/28/2015.
 */

public class FitnessDBHelper extends SQLiteOpenHelper {

    //DEBUG
    private static final String LOGTAG = FitnessDBHelper.class.getSimpleName();

    //DATABASE
    private static final String DATABASE_NAME    = "fitnessDatabase";
    private static final int    DATABASE_VERSION = 1;

    //FORMATTING
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";

    //LOCATIONS TABLE
    private static final String TABLE_LOCATIONS = "Locations";
    private static final String KEY_LOCATION_ID = "_id";
    private static final String KEY_LOCATION_ACTIVITY_ID_FK = "activityId";
    private static final String KEY_LOCATION_LATITUDE = "latitude";
    private static final String KEY_LOCATION_LONGITUDE = "longitude";
    private static final String  KEY_LOCATION_ALTITUDE = "altitude";
    private static final String KEY_LOCATION_ACCURACY = "accuracy";
    private static final String  KEY_LOCATION_CREATED_DATE = "createdDate";

    //USERS TABLE
    private static final String TABLE_USERS = "Users";
    private static final String KEY_USER_ID = "_id";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_IS_ADMIN = "isAdmin";
    private static final String KEY_USER_WEIGHT = "weight";
    private static final String KEY_USER_AVATAR_FILENAME = "avatarFileName";
    private static final String KEY_USER_POINTS = "points";

    //ACTIVITIES TABLE
    private static final String TABLE_ACTIVITIES = "Activities";
    private static final String KEY_ACTIVITY_ID = "_id";
    private static final String KEY_ACTIVITY_USER_ID_FK = "userId";
    private static final String KEY_ACTIVITY_ACTIVITY_TYPE_ID_FK = "activityTypeId";
    private static final String KEY_ACTIVITY_START_DATE = "startDate"; //milliseconds have passed since January 1, 1970, 00:00:00 GMT
    private static final String KEY_ACTIVITY_END_DATE =  "endDate";  //milliseconds have passed since January 1, 1970, 00:00:00 GMT
    private static final String KEY_ACTIVITY_DISTANCE_FEET = "distanceInFeet";
    private static final String KEY_ACTIVITY_CALORIES = "calories";
    private static final String KEY_ACTIVITY_POINTS_EARNED = "pointsEarned";


    //ACTIVITY TYPE TABLE
    private static final String TABLE_ACTIVITY_TYPE = "ActivityType";
    private static final String KEY_ACTIVITY_TYPE_ID = "_id";
    private static final String KEY_ACTIVITY_TYPE_MAXSPEED = "maxSpeed";
    private static final String KEY_ACTIVITY_TYPE_MAXSPEED_NOTES = "maxSpeedNotes";
    private static final String KEY_ACTIVITY_TYPE_PRIORITY =  "priority";
    private static final String KEY_ACTIVITY_TYPE_ICON_FILENAME =  "iconFileName";
    private static final String KEY_ACTIVITY_TYPE_IS_ENABLED =  "isEnabled";

    //REWARDS TABLE
    private static final String TABLE_REWARDS = "Rewards";
    private static final String KEY_REWARD_ID = "_id";
    private static final String KEY_REWARD_POINTS = "points";
    private static final String KEY_REWARD_IS_ENABLED = "isEnabled";

    //REWARDUSER TABLE
    private static final String TABLE_REWARDUSER = "RewardUser";
    private static final String KEY_REWARDUSER_ID = "_id";
    private static final String KEY_REWARDUSER_REWARD_ID_FK = "rewardId";
    private static final String KEY_REWARDUSER_USER_ID_FK = "userId";
    private static final String KEY_REWARDUSER_REWARDSTATUS_ID_FK = "rewardStatusId";
    private static final String  KEY_REWARDUSER_CREATED_DATE = "createdDate";


    //REWARD STATUS TABLE
    private static final String TABLE_REWARDSTATUS = "RewardStatus";
    private static final String KEY_REWARDSTATUS_ID = "_id";
    private static final String KEY_REWARDSTATUS_NAME = "rewardStatusName";
    private static final String KEY_REWARDSTATUS_DESCRIPTION = "description";

    //VIEWS
    private static final String VIEW_REWARDSTATUS_USER = "RewardStatusUser";

    private static Context mContext;

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private FitnessDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //reference:  Singleton pattern https://www.youtube.com/watch?v=GH5_lhFShfU
    public static FitnessDBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx

        mContext = context;
        return Holder.INSTANCE;
    }

    //Singleton holder
    static class Holder{
        static final FitnessDBHelper INSTANCE = new FitnessDBHelper(mContext);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOGTAG, "***onCreate***");

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_USER_NAME + " TEXT, " +
                KEY_USER_IS_ADMIN  + " NUMERIC, " +
                KEY_USER_WEIGHT  + " REAL, " +
                KEY_USER_AVATAR_FILENAME + " TEXT, " +
                KEY_USER_POINTS + " INTEGER" +
                ")";

        String CREATE_ACTIVITY_TYPE_TABLE = "CREATE TABLE " + TABLE_ACTIVITY_TYPE +
                "(" +
                KEY_ACTIVITY_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_ACTIVITY_TYPE_MAXSPEED  + " REAL, " +
                KEY_ACTIVITY_TYPE_MAXSPEED_NOTES  + " TEXT, " +
                KEY_ACTIVITY_TYPE_PRIORITY  + " INTEGER," +
                KEY_ACTIVITY_TYPE_ICON_FILENAME  + " TEXT," +
                KEY_ACTIVITY_TYPE_IS_ENABLED  + " NUMERIC" +
                ")";

        String CREATE_ACTIVITIES_TABLE = "CREATE TABLE " + TABLE_ACTIVITIES +
                "(" +
                KEY_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_ACTIVITY_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," + // Define a foreign key
                KEY_ACTIVITY_ACTIVITY_TYPE_ID_FK + " INTEGER REFERENCES " + TABLE_ACTIVITY_TYPE + "," + // Define a foreign key
                KEY_ACTIVITY_START_DATE  + " TEXT, " +
                KEY_ACTIVITY_END_DATE  + " TEXT, " +
                KEY_ACTIVITY_DISTANCE_FEET  + " REAL, " +
                KEY_ACTIVITY_CALORIES  + " REAL, " +
                KEY_ACTIVITY_POINTS_EARNED  + " REAL" +
                ")";

        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATIONS +
                "(" +
                KEY_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_LOCATION_ACTIVITY_ID_FK + " INTEGER REFERENCES " + TABLE_ACTIVITIES + "," + // Define a foreign key
                KEY_LOCATION_LATITUDE  + " REAL, " +
                KEY_LOCATION_LONGITUDE  + " REAL, " +
                KEY_LOCATION_ALTITUDE  + " REAL, " +
                KEY_LOCATION_ACCURACY  + " REAL, " +
                KEY_LOCATION_CREATED_DATE + " INTEGER" +
                ")";

        String CREATE_REWARDS_TABLE = "CREATE TABLE " + TABLE_REWARDS +
                "(" +
                KEY_REWARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_REWARD_POINTS  + " INTEGER, " +
                KEY_REWARD_IS_ENABLED  + " NUMERIC " +
                ")";

        String CREATE_REWARDUSER_TABLE = "CREATE TABLE " + TABLE_REWARDUSER +
                "(" +
                KEY_REWARDUSER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_REWARDUSER_REWARD_ID_FK + " INTEGER REFERENCES " + TABLE_REWARDS + "," + // Define a foreign key
                KEY_REWARDUSER_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," + // Define a foreign key
                KEY_REWARDUSER_REWARDSTATUS_ID_FK + " INTEGER REFERENCES " + TABLE_REWARDSTATUS + "," + // Define a foreign key
                KEY_REWARDUSER_CREATED_DATE + " INTEGER" +
                ")";

        String CREATE_REWARDSTATUS_TABLE = "CREATE TABLE " + TABLE_REWARDSTATUS +
                "(" +
                KEY_REWARDSTATUS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_REWARDSTATUS_NAME  + " TEXT, " +
                KEY_REWARDSTATUS_DESCRIPTION  + " TEXT " +
                ")";

        String CREATE_VIEW_REWARDSTATUSUSER = "CREATE VIEW " + VIEW_REWARDSTATUS_USER + " AS" +
                "SELECT r._id AS " + KEY_REWARDUSER_REWARD_ID_FK + " , r." + KEY_REWARD_POINTS + " AS " + KEY_REWARD_POINTS + " , rs._id AS " + KEY_REWARDUSER_REWARDSTATUS_ID_FK + " , rs." + KEY_REWARDSTATUS_NAME + " AS " + KEY_REWARDSTATUS_NAME + " , u._id as " + KEY_REWARDUSER_USER_ID_FK +
                "FROM " + TABLE_REWARDUSER + " ru" +
                "INNER JOIN " + TABLE_REWARDS + " r on ru._id = r._id" +
                "INNER JOIN " + TABLE_REWARDSTATUS + " rs on ru._id = rs._id" +
                "INNER JOIN " + TABLE_USERS + " u on u._id = ru.userId" +
                "WHERE r." + KEY_REWARD_IS_ENABLED + " = 1";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ACTIVITY_TYPE_TABLE);
        db.execSQL(CREATE_ACTIVITIES_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_REWARDS_TABLE);
        db.execSQL(CREATE_REWARDUSER_TABLE);
        db.execSQL(CREATE_REWARDSTATUS_TABLE);
        db.execSQL(CREATE_VIEW_REWARDSTATUSUSER);

        //populate ActivityType table
        db.execSQL("INSERT INTO " + TABLE_ACTIVITY_TYPE + " VALUES (null, 4.6, '1995 world record, walking speed meters/second', 1,'walk',1);");
        db.execSQL("INSERT INTO " + TABLE_ACTIVITY_TYPE + " VALUES (null, 12.4, '2009 world record, running speed meters/second', 2,'run', 1);");
        db.execSQL("INSERT INTO " + TABLE_ACTIVITY_TYPE + " VALUES (null, 5.0, 'estimate based on 20 km/h, world record does not exist', 3,'scooter', 1);");
        db.execSQL("INSERT INTO " + TABLE_ACTIVITY_TYPE + " VALUES (null, 74.7,'1985 world record, cycling speed meters/second',4,'bike', 1);");
        db.execSQL("INSERT INTO " + TABLE_ACTIVITY_TYPE + " VALUES (null, 4.6,'1995 world record, walking speed meters/second', 5,'hike', 1);");

        //populate RewardStatus table
        db.execSQL("INSERT INTO " + TABLE_REWARDSTATUS + " VALUES (0, 'not requested', 'user has not requested the reward');");
        db.execSQL("INSERT INTO " + TABLE_REWARDSTATUS + " VALUES (1, 'requested', 'user has requested the reward, pending fulfillment');");
        db.execSQL("INSERT INTO " + TABLE_REWARDSTATUS + " VALUES (2, 'denied', 'request for reward has been denied');");

        //populate Rewards table
        db.execSQL("INSERT INTO " + TABLE_REWARDS + " VALUES (null, 'popsicle', 1, 1);");
        db.execSQL("INSERT INTO " + TABLE_REWARDS + " VALUES (null, 'park playdate', 2, 1);");
        db.execSQL("INSERT INTO " + TABLE_REWARDS + " VALUES (null, 'movie buddy', 3, 1);");
        db.execSQL("INSERT INTO " + TABLE_REWARDS + " VALUES (null, 'pizza party', 10, 1);");
        db.execSQL("INSERT INTO " + TABLE_REWARDS + " VALUES (null, 'family roadtrip', 15, 1);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOGTAG, "***onUpgrade***");

        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY_TYPE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
            onCreate(db);
        }
    }

    /***********************************************************************************************
        USERS Operations
     ***********************************************************************************************
     */

    // Insert a User into the database
    public void addUser(User user) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_USER_NAME, user.getUserName());
            values.put(KEY_USER_IS_ADMIN, user.isAdmin());
            values.put(KEY_USER_WEIGHT, user.getWeight());
            values.put(KEY_USER_AVATAR_FILENAME, user.getAvatarFileName());
            values.put(KEY_USER_POINTS, 0);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_USERS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOGTAG, "Error during addUser()");
        } finally {
            db.endTransaction();
        }
    }

    public User getUser(String userName)
    {
        // Create and/or open the database for writing
        SQLiteDatabase db = getReadableDatabase();

        User user = new User();

        try {

            Cursor cursor = db.query(TABLE_USERS,
                    new String[]{KEY_USER_ID, KEY_USER_NAME, KEY_USER_IS_ADMIN, KEY_USER_WEIGHT, KEY_USER_AVATAR_FILENAME, KEY_USER_POINTS},
                    KEY_USER_NAME + " = ?", new String[]{userName}, null, null, null);

            try{

                if (cursor.moveToFirst())
                {
                    user.setUserId(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)));
                    user.setUserName(cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)));
                    user.setIsAdmin(cursor.getInt(cursor.getColumnIndex(KEY_USER_IS_ADMIN)) > 0 ? true : false);
                    user.setWeight(cursor.getFloat(cursor.getColumnIndex(KEY_USER_WEIGHT)));
                    user.setAvatarFileName(cursor.getString(cursor.getColumnIndex(KEY_USER_AVATAR_FILENAME)));
                    user.setPoints(cursor.getInt(cursor.getColumnIndex(KEY_USER_POINTS)));
                }

            } finally{

                if (cursor != null && !cursor.isClosed())
                {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            Log.d(LOGTAG, "Error while trying to get user to database");
        }

        return user;
    }

    public boolean hasUser(User user) {

        SQLiteDatabase db = getReadableDatabase();

        long userId = -1;

        try{
            Cursor cursor = db.query(TABLE_USERS,
                    new String[]{KEY_USER_ID},
                    KEY_USER_NAME + " = ?", new String[]{user.getUserName()}, null, null, null);

            try {
                if (cursor.moveToFirst()) {
                    userId = cursor.getLong(cursor.getColumnIndex(KEY_USER_ID));
                }
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

        }catch (Exception exception){
            exception.printStackTrace();
        }

        if (userId != -1)
            return true;
        else
            return false;
    }

    /***********************************************************************************************
     ACTIVITY Operations
     ***********************************************************************************************
     */

    public void insertActivity(int userId, int activityId, Date startDate, Date endDate, float distanceInFeet, float calories, float points){

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_ACTIVITY_USER_ID_FK, userId);
            values.put(KEY_ACTIVITY_ACTIVITY_TYPE_ID_FK, activityId);
            values.put(KEY_ACTIVITY_START_DATE, new SimpleDateFormat(DATE_FORMAT).format(startDate));
            values.put(KEY_ACTIVITY_END_DATE, new SimpleDateFormat(DATE_FORMAT).format(endDate));
            values.put(KEY_ACTIVITY_DISTANCE_FEET, distanceInFeet);
            values.put(KEY_ACTIVITY_CALORIES, calories);
            values.put(KEY_ACTIVITY_POINTS_EARNED, points);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_ACTIVITIES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOGTAG, "Error during insertActivity()");
        } finally {
            db.endTransaction();
        }
    }

    public List<ActivityDetail> getActivityDetailList(int userId){

        // Create and/or open the database for writing
        SQLiteDatabase db = getReadableDatabase();

        //initialize ActivityType list
        List<ActivityDetail> activityDetailList = new ArrayList<>();

        try {

            Cursor cursor = db.query(TABLE_ACTIVITIES,
                    new String[]{KEY_ACTIVITY_ID, KEY_ACTIVITY_ACTIVITY_TYPE_ID_FK,KEY_ACTIVITY_START_DATE,KEY_ACTIVITY_END_DATE,KEY_ACTIVITY_POINTS_EARNED},
                    KEY_ACTIVITY_USER_ID_FK + " = ?",
                    new String[]{String.valueOf(userId)}, null, null, KEY_ACTIVITY_ID);

            try{

                if (cursor.moveToFirst())
                {
                    do{
                        ActivityDetail activityDetail = new ActivityDetail();
                        activityDetail.setActivityId(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVITY_ID)));
                        activityDetail.setActivityTypeId(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVITY_TYPE_ID)));
                        activityDetail.setStartDate(new SimpleDateFormat(DATE_FORMAT).parse(cursor.getString(cursor.getColumnIndex(KEY_ACTIVITY_START_DATE))));
                        activityDetail.setEndDate(new SimpleDateFormat(DATE_FORMAT).parse(cursor.getString(cursor.getColumnIndex(KEY_ACTIVITY_END_DATE))));
                        activityDetail.setPointsEarned(cursor.getFloat(cursor.getColumnIndex(KEY_ACTIVITY_POINTS_EARNED)));
                        activityDetailList.add(activityDetail);

                    } while(cursor.moveToNext());}

            }catch(Exception exception) {

                exception.printStackTrace();

            } finally{

                if (cursor != null && !cursor.isClosed())
                {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            Log.d(LOGTAG, "Error during getActivityTypes()");
        }

        return activityDetailList;
    }

    /***********************************************************************************************
     REWARDSTATUS Operations
     ***********************************************************************************************
     */

    public List<Reward> getUserRewards(int userId){

        // Create and/or open the database for writing
        SQLiteDatabase db = getReadableDatabase();

        //initialize ActivityType list
        List<Reward> rewardList = new ArrayList<>();

        try {

            Cursor cursor = db.query(VIEW_REWARDSTATUS_USER,
                    new String[]{KEY_REWARDUSER_REWARD_ID_FK, KEY_REWARD_POINTS, KEY_REWARDUSER_REWARDSTATUS_ID_FK, KEY_REWARDSTATUS_NAME},
                    KEY_REWARDUSER_USER_ID_FK + " = ? AND " + KEY_REWARD_IS_ENABLED + " = ? ",
                    new String[]{String.valueOf(userId), "1"}, null, null, KEY_REWARD_POINTS);

            try{

                if (cursor.moveToFirst())
                {
                    do{
                        Reward reward = new Reward();
                        reward.setName(cursor.getString(cursor.getColumnIndex(KEY_REWARDSTATUS_NAME)));
                        reward.setPoints(cursor.getInt(cursor.getColumnIndex(KEY_REWARD_POINTS)));
                        reward.setUserStatus(cursor.getInt(cursor.getColumnIndex(KEY_REWARDUSER_REWARDSTATUS_ID_FK)));
                        rewardList.add(reward);

                    } while(cursor.moveToNext());}

            }catch(Exception exception) {

                exception.printStackTrace();

            } finally{

                if (cursor != null && !cursor.isClosed())
                {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            Log.d(LOGTAG, "Error during getUserRewards()");
        }

        return rewardList;
    }

    public long setRewardStatus(int userId, int rewardId, int rewardStatusId){

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        long rowsAffected = 0;

        // It's a good idea to wrap the update in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_REWARDUSER_REWARDSTATUS_ID_FK, rewardStatusId);

            rowsAffected = db.update(TABLE_REWARDSTATUS, values, KEY_REWARDUSER_USER_ID_FK + "= ? AND " + KEY_REWARDUSER_REWARD_ID_FK + " = ?", new String[]{String.valueOf(userId), String.valueOf(rewardId)});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOGTAG, "Error during setRewardStatus()");
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

    public long setUserPoints(int userId, int points){

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        long rowsAffected = 0;

        // It's a good idea to wrap the update in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_USER_POINTS, points);

            rowsAffected = db.update(TABLE_USERS, values, KEY_USER_ID + "= ?", new String[]{String.valueOf(userId)});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOGTAG, "Error during setUserPoints()");
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

     /***********************************************************************************************
     ACTIVITY TYPE Operations
     ***********************************************************************************************
     */

    public List<ActivityType> getActivityTypes(){

        // Create and/or open the database for writing
        SQLiteDatabase db = getReadableDatabase();

        //initialize ActivityType list
        List<ActivityType> activityTypeList = new ArrayList<>();

        try {

            Cursor cursor = db.query(TABLE_ACTIVITY_TYPE,
                    new String[]{KEY_ACTIVITY_TYPE_ID, KEY_ACTIVITY_TYPE_MAXSPEED,KEY_ACTIVITY_TYPE_ICON_FILENAME},
                    KEY_ACTIVITY_TYPE_IS_ENABLED + " = ?",
                    new String[]{"1"}, null, null, KEY_ACTIVITY_TYPE_PRIORITY);

            try{

                if (cursor.moveToFirst())
                {
                    do{
                        ActivityType activityType = new ActivityType();
                        activityType.setActivityTypeId(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVITY_TYPE_ID)));
                        activityType.setMaxSpeed(cursor.getFloat(cursor.getColumnIndex(KEY_ACTIVITY_TYPE_MAXSPEED)));
                        activityType.setIconFileName(cursor.getString(cursor.getColumnIndex(KEY_ACTIVITY_TYPE_ICON_FILENAME)));
                        activityTypeList.add(activityType);

                    } while(cursor.moveToNext());}

                }catch(Exception exception) {

                    exception.printStackTrace();

                } finally{

                if (cursor != null && !cursor.isClosed())
                {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            Log.d(LOGTAG, "Error during getActivityTypes()");
        }

        return activityTypeList;
    }



    public void deleteAll() {
        Log.d(LOGTAG, "***deleteAll***");

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_LOCATIONS, null, null);
            db.delete(TABLE_USERS, null, null);
            db.delete(TABLE_ACTIVITIES, null, null);
            db.delete(TABLE_ACTIVITY_TYPE, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOGTAG, "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }


}
