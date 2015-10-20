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
import com.tinakit.moveit.model.RewardStatusType;
import com.tinakit.moveit.model.UnitSplitCalorie;
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

    //ACTIVITY_LOCATION_DATA TABLE
    private static final String TABLE_ACTIVITY_LOCATION_DATA = "ActivityLocationData";
    private static final String KEY_ACTIVITY_LOCATION_DATA_ID = "_id";
    private static final String KEY_ACTIVITY_ID_FK = "activityId";
    private static final String KEY_ACTIVITY_LOCATION_DATA_TIMESTAMP = "timeStamp";
    private static final String KEY_ACTIVITY_LOCATION_DATA_LATITUDE = "latitude";
    private static final String KEY_ACTIVITY_LOCATION_DATA_LONGITUDE = "longitude";
    private static final String KEY_ACTIVITY_LOCATION_DATA_ALTITUDE = "altitude";
    private static final String KEY_ACTIVITY_LOCATION_DATA_ACCURACY = "accuracy";
    private static final String KEY_ACTIVITY_LOCATION_DATA_BEARING = "bearing";
    private static final String LOCATION_PLACEHOLDER_PROVIDER = "location_placeholder_provider";

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
    private static final String KEY_REWARD_NAME = "rewardName";
    private static final String KEY_REWARD_POINTS = "points";
    private static final String KEY_REWARD_IS_ENABLED = "isEnabled";

    //REWARDUSER TABLE
    private static final String TABLE_REWARDUSER = "RewardUser";
    private static final String KEY_REWARDUSER_ID = "_id";
    private static final String KEY_REWARDUSER_REWARD_ID_FK = "rewardId";
    private static final String KEY_REWARDUSER_USER_ID_FK = "userId";
    private static final String KEY_REWARDUSER_REWARD_STATUS_ID = "rewardStatusId";


    //VIEWS
    private static final String VIEW_REWARDSTATUS_USER = "RewardStatusUser";
    private static final String VIEW_FIRST_LOCATION_POINTS = "FirstLocationPoints";

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

        String CREATE_ACTIVITY_LOCATION_DATA_TABLE = "CREATE TABLE " + TABLE_ACTIVITY_LOCATION_DATA +
                "(" +
                KEY_ACTIVITY_LOCATION_DATA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_ACTIVITY_ID_FK + " INTEGER REFERENCES " + TABLE_ACTIVITIES + "," + // Define a foreign key
                KEY_ACTIVITY_LOCATION_DATA_TIMESTAMP  + " TEXT, " +
                KEY_ACTIVITY_LOCATION_DATA_LATITUDE  + " REAL, " +
                KEY_ACTIVITY_LOCATION_DATA_LONGITUDE  + " REAL, " +
                KEY_ACTIVITY_LOCATION_DATA_ALTITUDE  + " REAL, " +
                KEY_ACTIVITY_LOCATION_DATA_ACCURACY  + " REAL, " +
                KEY_ACTIVITY_LOCATION_DATA_BEARING  + " REAL " +
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
                KEY_REWARD_NAME  + " TEXT, " +
                KEY_REWARD_POINTS  + " INTEGER, " +
                KEY_REWARD_IS_ENABLED  + " NUMERIC " +
                ")";

        String CREATE_REWARDUSER_TABLE = "CREATE TABLE " + TABLE_REWARDUSER +
                "(" +
                KEY_REWARDUSER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_REWARDUSER_REWARD_ID_FK + " INTEGER REFERENCES " + TABLE_REWARDS + "," + // Define a foreign key
                KEY_REWARDUSER_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," + // Define a foreign key
                KEY_REWARDUSER_REWARD_STATUS_ID + " INTEGER " +
                ")";

        String CREATE_VIEW_REWARDSTATUSUSER = "CREATE VIEW " + VIEW_REWARDSTATUS_USER + " AS" +
                " SELECT r._id AS " + KEY_REWARDUSER_REWARD_ID_FK + " , r." + KEY_REWARD_POINTS + " AS " + KEY_REWARD_POINTS + " , ru." + KEY_REWARDUSER_REWARD_STATUS_ID + " AS " + KEY_REWARDUSER_REWARD_STATUS_ID + " , u._id as " + KEY_REWARDUSER_USER_ID_FK + " , r." + KEY_REWARD_NAME + " AS " + KEY_REWARD_NAME +
                " FROM " + TABLE_REWARDUSER + " ru" +
                " INNER JOIN " + TABLE_REWARDS + " r on ru._id = r._id" +
                " INNER JOIN " + TABLE_USERS + " u on u._id = ru.userId" +
                " WHERE r." + KEY_REWARD_IS_ENABLED + " = 1";

        String CREATE_VIEW_FIRST_LOCATION_POINTS = "CREATE VIEW " + VIEW_FIRST_LOCATION_POINTS + " AS" +
                " SELECT " + KEY_ACTIVITY_LOCATION_DATA_TIMESTAMP +
                "," + KEY_ACTIVITY_LOCATION_DATA_LATITUDE +
                "," + KEY_ACTIVITY_LOCATION_DATA_LONGITUDE +
                "," + KEY_ACTIVITY_LOCATION_DATA_ALTITUDE +
                "," + KEY_ACTIVITY_LOCATION_DATA_ACCURACY +
                "," + KEY_ACTIVITY_LOCATION_DATA_BEARING +
                ", MIN(" + KEY_ACTIVITY_ID_FK + ") AS " + KEY_ACTIVITY_ID_FK +
                " FROM " + TABLE_ACTIVITY_LOCATION_DATA +
                " GROUP BY " + KEY_ACTIVITY_ID_FK;

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ACTIVITY_TYPE_TABLE);
        db.execSQL(CREATE_ACTIVITIES_TABLE);
        db.execSQL(CREATE_ACTIVITY_LOCATION_DATA_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_REWARDS_TABLE);
        db.execSQL(CREATE_REWARDUSER_TABLE);
        db.execSQL(CREATE_VIEW_REWARDSTATUSUSER);
        db.execSQL(CREATE_VIEW_FIRST_LOCATION_POINTS);

        //populate ActivityType table
        db.execSQL("INSERT INTO " + TABLE_ACTIVITY_TYPE + " VALUES (null, 4.6, '1995 world record, walking speed meters/second', 1,'walk',1);");
        db.execSQL("INSERT INTO " + TABLE_ACTIVITY_TYPE + " VALUES (null, 12.4, '2009 world record, running speed meters/second', 2,'run', 1);");
        db.execSQL("INSERT INTO " + TABLE_ACTIVITY_TYPE + " VALUES (null, 5.0, 'estimate based on 20 km/h, world record does not exist', 3,'scooter', 1);");
        db.execSQL("INSERT INTO " + TABLE_ACTIVITY_TYPE + " VALUES (null, 74.7,'1985 world record, cycling speed meters/second',4,'bike', 1);");
        db.execSQL("INSERT INTO " + TABLE_ACTIVITY_TYPE + " VALUES (null, 4.6,'1995 world record, walking speed meters/second', 5,'hike', 1);");
        db.execSQL("INSERT INTO " + TABLE_ACTIVITY_TYPE + " VALUES (null, 2.3,'1990 world record, swimming speed meters/second', 6,'swim', 1);");

        //TODO: DUMMY DATA
        //populate Rewards table
        db.execSQL("INSERT INTO " + TABLE_REWARDS + " VALUES (1, 'cool treat', 25, 1);");
        db.execSQL("INSERT INTO " + TABLE_REWARDS + " VALUES (2, '$5 gift certificate', 50, 1);");
        db.execSQL("INSERT INTO " + TABLE_REWARDS + " VALUES (3, 'movie with friend', 100, 1);");
        db.execSQL("INSERT INTO " + TABLE_REWARDS + " VALUES (4, 'beach picnic', 150, 1);");
        db.execSQL("INSERT INTO " + TABLE_REWARDS + " VALUES (5, 'family roadtrip', 250, 1);");

        //TODO: DUMMY DATA
        db.execSQL("INSERT INTO " + TABLE_USERS + " VALUES (null, 'Lucy', 0, 40, 'tiger', 0);");

        //TODO: DUMMY DATA
        //TODO:  when adding User or Reward, ensure that RewardStatus gets populated with available rewards for that user
        //populate RewardStatus table
        db.execSQL("INSERT INTO " + TABLE_REWARDUSER + " VALUES (1, 1, 1, 0);");
        db.execSQL("INSERT INTO " + TABLE_REWARDUSER + " VALUES (2, 2, 1, 0);");
        db.execSQL("INSERT INTO " + TABLE_REWARDUSER + " VALUES (3, 3, 1, 0);");
        db.execSQL("INSERT INTO " + TABLE_REWARDUSER + " VALUES (4, 4, 1, 0);");
        db.execSQL("INSERT INTO " + TABLE_REWARDUSER + " VALUES (5, 5, 1, 0);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOGTAG, "***onUpgrade***");

        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY_TYPE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY_LOCATION_DATA);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REWARDS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REWARDUSER);
            db.execSQL("DROP VIEW IF EXISTS " + VIEW_REWARDSTATUS_USER);
            db.execSQL("DROP VIEF IF EXISTS " + VIEW_FIRST_LOCATION_POINTS);
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

    public long insertActivity(int userId, int activityTypeId, Date startDate, Date endDate, float distanceInFeet, float calories, float points){

        long activityId = -1;

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_ACTIVITY_USER_ID_FK, userId);
            values.put(KEY_ACTIVITY_ACTIVITY_TYPE_ID_FK, activityTypeId);
            values.put(KEY_ACTIVITY_START_DATE, new SimpleDateFormat(DATE_FORMAT).format(startDate));
            values.put(KEY_ACTIVITY_END_DATE, new SimpleDateFormat(DATE_FORMAT).format(endDate));
            values.put(KEY_ACTIVITY_DISTANCE_FEET, distanceInFeet);
            values.put(KEY_ACTIVITY_CALORIES, calories);
            values.put(KEY_ACTIVITY_POINTS_EARNED, points);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            activityId = db.insertOrThrow(TABLE_ACTIVITIES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOGTAG, "Error during insertActivity()");
        } finally {
            db.endTransaction();
        }

        return activityId;
    }

    public ActivityDetail getActivityDetail(int activityId){

        // Create and/or open the database for writing
        SQLiteDatabase db = getReadableDatabase();

        //initialize ActivityDetail
        ActivityDetail activityDetail = null;

        try {

            Cursor cursor = db.query(TABLE_ACTIVITIES,
                    new String[]{KEY_ACTIVITY_ID, KEY_ACTIVITY_ACTIVITY_TYPE_ID_FK,KEY_ACTIVITY_START_DATE,KEY_ACTIVITY_END_DATE,KEY_ACTIVITY_POINTS_EARNED},
                    KEY_ACTIVITY_ID + " = ?",
                    new String[]{String.valueOf(activityId)}, null, null, KEY_ACTIVITY_START_DATE);

            try{

                if (cursor.moveToFirst())
                {
                    activityDetail = new ActivityDetail();
                    activityDetail.setActivityId(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVITY_ID)));
                    activityDetail.setActivityTypeId(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVITY_TYPE_ID)));
                    activityDetail.setStartDate(new SimpleDateFormat(DATE_FORMAT).parse(cursor.getString(cursor.getColumnIndex(KEY_ACTIVITY_START_DATE))));
                    activityDetail.setEndDate(new SimpleDateFormat(DATE_FORMAT).parse(cursor.getString(cursor.getColumnIndex(KEY_ACTIVITY_END_DATE))));
                    activityDetail.setPointsEarned(cursor.getFloat(cursor.getColumnIndex(KEY_ACTIVITY_POINTS_EARNED)));

                }

            }catch(Exception exception) {

                exception.printStackTrace();

            } finally{

                if (cursor != null && !cursor.isClosed())
                {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            Log.d(LOGTAG, "Error during getActivityDetailList()");
        }

        return activityDetail;
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
            Log.d(LOGTAG, "Error during getActivityDetailList()");
        }

        return activityDetailList;
    }

    /***********************************************************************************************
     ACTIVITY_LOCATION_DATA Operations
     ***********************************************************************************************
     */

    public void insertActivityLocationData(long activityId, Date timeStamp, double latitude, double longitude, double altitude, float accuracy, float bearing){

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_ACTIVITY_ID_FK, activityId);
            values.put(KEY_ACTIVITY_LOCATION_DATA_TIMESTAMP, new SimpleDateFormat(DATE_FORMAT).format(timeStamp));
            values.put(KEY_ACTIVITY_LOCATION_DATA_LATITUDE, latitude);
            values.put(KEY_ACTIVITY_LOCATION_DATA_LONGITUDE, longitude);
            values.put(KEY_ACTIVITY_LOCATION_DATA_ALTITUDE, altitude);
            values.put(KEY_ACTIVITY_LOCATION_DATA_ACCURACY, accuracy);
            values.put(KEY_ACTIVITY_LOCATION_DATA_BEARING, bearing);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_ACTIVITY_LOCATION_DATA, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOGTAG, "Error during insertActivityLocationData()");
        } finally {
            db.endTransaction();
        }
    }

    public List<UnitSplitCalorie> getActivityLocationData(long activityId){

        // Create and/or open the database for writing
        SQLiteDatabase db = getReadableDatabase();

        //initialize UnitSplitCalorie array
        List<UnitSplitCalorie> locationList = new ArrayList<>();

        try {

            Cursor cursor = db.query(TABLE_ACTIVITY_LOCATION_DATA,
                    new String[]{KEY_ACTIVITY_ID_FK, KEY_ACTIVITY_LOCATION_DATA_TIMESTAMP,KEY_ACTIVITY_LOCATION_DATA_LATITUDE,KEY_ACTIVITY_LOCATION_DATA_LONGITUDE,KEY_ACTIVITY_LOCATION_DATA_ALTITUDE,KEY_ACTIVITY_LOCATION_DATA_ACCURACY,KEY_ACTIVITY_LOCATION_DATA_BEARING},
                    KEY_ACTIVITY_ID_FK + " = ?",
                    new String[]{String.valueOf(activityId)}, null, null, null);

            try{

                if (cursor.moveToFirst()) {

                    do{
                        Location location = new Location(LOCATION_PLACEHOLDER_PROVIDER);
                        location.setLatitude(cursor.getFloat(cursor.getColumnIndex(KEY_ACTIVITY_LOCATION_DATA_LATITUDE)));
                        location.setLongitude(cursor.getFloat(cursor.getColumnIndex(KEY_ACTIVITY_LOCATION_DATA_LONGITUDE)));
                        location.setAltitude(cursor.getFloat(cursor.getColumnIndex(KEY_ACTIVITY_LOCATION_DATA_ALTITUDE)));

                        UnitSplitCalorie unitSplitCalorie = new UnitSplitCalorie(new SimpleDateFormat(DATE_FORMAT).parse(cursor.getString(cursor.getColumnIndex(KEY_ACTIVITY_LOCATION_DATA_TIMESTAMP))), location);
                        unitSplitCalorie.setAccuracy(cursor.getFloat(cursor.getColumnIndex(KEY_ACTIVITY_LOCATION_DATA_ACCURACY)));
                        unitSplitCalorie.setActivityId(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVITY_ID_FK)));
                        unitSplitCalorie.setBearing(cursor.getFloat(cursor.getColumnIndex(KEY_ACTIVITY_LOCATION_DATA_BEARING)));
                        locationList.add(unitSplitCalorie);
                    }while (cursor.moveToNext());

                }

            }catch(Exception exception) {

                exception.printStackTrace();

            } finally{

                if (cursor != null && !cursor.isClosed())
                {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            Log.d(LOGTAG, "Error during getActivityLocationData()");
        }

        return locationList;
    }

    public List<UnitSplitCalorie> getFirstLocationPoints(){

        // Create and/or open the database for writing
        SQLiteDatabase db = getReadableDatabase();

        //initialize UnitSplitCalorie array
        List<UnitSplitCalorie> locationList = new ArrayList<>();

        try {

            Cursor cursor = db.query(VIEW_FIRST_LOCATION_POINTS,
                    new String[]{KEY_ACTIVITY_ID_FK,KEY_ACTIVITY_LOCATION_DATA_LATITUDE
                            ,KEY_ACTIVITY_LOCATION_DATA_LONGITUDE
                            ,KEY_ACTIVITY_LOCATION_DATA_ALTITUDE
                            ,KEY_ACTIVITY_LOCATION_DATA_TIMESTAMP
                            ,KEY_ACTIVITY_LOCATION_DATA_ACCURACY
                            ,KEY_ACTIVITY_LOCATION_DATA_BEARING},
                    null, null, null, null, KEY_ACTIVITY_LOCATION_DATA_TIMESTAMP);

            try{

                if (cursor.moveToFirst()) {

                    do{
                        Location location = new Location(LOCATION_PLACEHOLDER_PROVIDER);
                        location.setLatitude(cursor.getFloat(cursor.getColumnIndex(KEY_ACTIVITY_LOCATION_DATA_LATITUDE)));
                        location.setLongitude(cursor.getFloat(cursor.getColumnIndex(KEY_ACTIVITY_LOCATION_DATA_LONGITUDE)));
                        location.setAltitude(cursor.getFloat(cursor.getColumnIndex(KEY_ACTIVITY_LOCATION_DATA_ALTITUDE)));

                        UnitSplitCalorie unitSplitCalorie = new UnitSplitCalorie(new SimpleDateFormat(DATE_FORMAT).parse(cursor.getString(cursor.getColumnIndex(KEY_ACTIVITY_LOCATION_DATA_TIMESTAMP))), location);
                        unitSplitCalorie.setAccuracy(cursor.getFloat(cursor.getColumnIndex(KEY_ACTIVITY_LOCATION_DATA_ACCURACY)));
                        unitSplitCalorie.setActivityId(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVITY_ID_FK)));
                        unitSplitCalorie.setBearing(cursor.getFloat(cursor.getColumnIndex(KEY_ACTIVITY_LOCATION_DATA_BEARING)));
                        locationList.add(unitSplitCalorie);
                    }while (cursor.moveToNext());

                }

            }catch(Exception exception) {

                exception.printStackTrace();

            } finally{

                if (cursor != null && !cursor.isClosed())
                {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            Log.d(LOGTAG, "Error during getAllActivities()");
        }

        return locationList;
    }


    /***********************************************************************************************
     REWARD Operations
     ***********************************************************************************************
     */

    public void insertReward(String rewardName, int points){

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_REWARD_NAME, rewardName);
            values.put(KEY_REWARD_POINTS, points);
            values.put(KEY_REWARD_IS_ENABLED, 1);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_REWARDS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOGTAG, "Error during insertReward()");
        } finally {
            db.endTransaction();
        }
    }

    public Reward getReward(int rewardId){

        // Create and/or open the database for writing
        SQLiteDatabase db = getReadableDatabase();

        //initialize Reward object
        Reward reward = new Reward();

        try {

            Cursor cursor = db.query(TABLE_REWARDS,
                    new String[]{KEY_REWARD_ID,KEY_REWARD_NAME,KEY_REWARD_POINTS},
                    KEY_REWARD_ID + " = ?",
                    new String[]{String.valueOf(rewardId)}, null, null, null);

            try{

                if (cursor.moveToFirst()) {
                    reward.setRewardId(cursor.getInt(cursor.getColumnIndex(KEY_REWARD_ID)));
                    reward.setName(cursor.getString(cursor.getColumnIndex(KEY_REWARD_NAME)));
                    reward.setPoints(cursor.getInt(cursor.getColumnIndex(KEY_REWARD_POINTS)));
                }

            }catch(Exception exception) {

                exception.printStackTrace();

            } finally{

                if (cursor != null && !cursor.isClosed())
                {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            Log.d(LOGTAG, "Error during getReward()");
        }

        return reward;
    }

    public List<Reward> getAllRewards(){

        // Create and/or open the database for writing
        SQLiteDatabase db = getReadableDatabase();

        //initialize Reward array
        List<Reward> rewardList = new ArrayList<>();

        try {

            Cursor cursor = db.query(TABLE_REWARDS,
                    new String[]{KEY_REWARD_ID,KEY_REWARD_NAME,KEY_REWARD_POINTS},
                    KEY_REWARD_IS_ENABLED + " = ?",
                    new String[]{String.valueOf(1)}, null, null, null);

            try{

                if (cursor.moveToFirst()) {

                    do{
                        Reward reward = new Reward();
                        reward.setRewardId(cursor.getInt(cursor.getColumnIndex(KEY_REWARD_ID)));
                        reward.setName(cursor.getString(cursor.getColumnIndex(KEY_REWARD_NAME)));
                        reward.setPoints(cursor.getInt(cursor.getColumnIndex(KEY_REWARD_POINTS)));
                        rewardList.add(reward);
                    }while (cursor.moveToNext());

                }

            }catch(Exception exception) {

                exception.printStackTrace();

            } finally{

                if (cursor != null && !cursor.isClosed())
                {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            Log.d(LOGTAG, "Error during getAllRewards()");
        }

        return rewardList;
    }

    public int updateReward(int rewardId, String rewardName, int points, int isEnabled){

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        int rowsAffected = 0;

        // It's a good idea to wrap the update in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_REWARD_NAME, rewardName);
            values.put(KEY_REWARD_POINTS, points);
            values.put(KEY_REWARD_IS_ENABLED, 1);

            rowsAffected = db.update(TABLE_REWARDS, values, KEY_REWARD_ID + "= ? ", new String[]{String.valueOf(rewardId)});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOGTAG, "Error during updateReward()");
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

    public boolean deleteReward(int rewardId){

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap the delete in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            return db.delete(TABLE_REWARDS, KEY_REWARD_ID + "= ? ", new String[]{String.valueOf(rewardId)}) > 0;

        } catch (Exception e) {
            Log.d(LOGTAG, "Error during deleteReward()");
        } finally {
            db.endTransaction();
        }

        return false;
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
                    new String[]{KEY_REWARDUSER_REWARD_ID_FK, KEY_REWARD_POINTS, KEY_REWARDUSER_REWARD_STATUS_ID,KEY_REWARD_NAME},
                    KEY_REWARDUSER_USER_ID_FK + " = ? ",
                    new String[]{String.valueOf(userId)}, null, null, KEY_REWARD_POINTS);

            try{

                if (cursor.moveToFirst())
                {
                    do{
                        Reward reward = new Reward();
                        reward.setPoints(cursor.getInt(cursor.getColumnIndex(KEY_REWARD_POINTS)));
                        reward.setRewardStatusType(RewardStatusType.values()[cursor.getInt(cursor.getColumnIndex(KEY_REWARDUSER_REWARD_STATUS_ID))]);
                        reward.setRewardId(cursor.getInt(cursor.getColumnIndex(KEY_REWARDUSER_REWARD_ID_FK)));
                        reward.setName(cursor.getString(cursor.getColumnIndex(KEY_REWARD_NAME)));
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

    public long setRewardStatus(int userId, int rewardId, RewardStatusType rewardStatusType){

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        long rowsAffected = 0;

        // It's a good idea to wrap the update in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_REWARDUSER_REWARD_STATUS_ID, rewardStatusType.ordinal());

            rowsAffected = db.update(TABLE_REWARDUSER, values, KEY_REWARDUSER_USER_ID_FK + "= ? AND " + KEY_REWARDUSER_REWARD_ID_FK + " = ?", new String[]{String.valueOf(userId), String.valueOf(rewardId)});

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
            db.delete(TABLE_ACTIVITY_LOCATION_DATA, null, null);
            db.delete(TABLE_REWARDS, null, null);
            db.delete(TABLE_REWARDUSER, null, null);
            db.delete(VIEW_REWARDSTATUS_USER, null, null);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOGTAG, "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }


}
