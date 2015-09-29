package com.tinakit.moveit.db;

//reference: https://github.com/codepath/android_guides/wiki/Local-Databases-with-SQLiteOpenHelper

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;

/**
 * Created by Tina on 9/28/2015.
 */

public class FitnessDBHelper extends SQLiteOpenHelper {

    //DEBUG
    private static final String LOGTAG = FitnessDBHelper.class.getSimpleName();

    //DATABASE
    private static final String DATABASE_NAME    = "fitnessDatabase";
    private static final int    DATABASE_VERSION = 1;

    //LOCATIONS TABLE
    private static final String TABLE_LOCATIONS = "Locations";
    private static final String KEY_LOCATION_ID = "_id";
    private static final String KEY_LOCATION_USER_ID_FK = "userId";
    private static final String KEY_LOCATION_ACTIVITY_ID_FK = "activityId";
    private static final String KEY_LOCATION_LATITUDE = "latitude";
    private static final String KEY_LOCATION_LONGITUDE = "longitude";
    public static final String  KEY_LOCATION_ALTITUDE = "altitude";
    private static final String KEY_LOCATION_ACCURACY = "accuracy";
    public static final String  KEY_LOCATION_CREATED_DATE = "createdDate";

    //USERS TABLE
    private static final String TABLE_USERS = "Users";
    private static final String KEY_USER_ID = "_id";
    private static final String KEY_USER_IS_ADMIN = "isAdmin";
    private static final String KEY_USER_WEIGHT = "weight";

    //ACTIVITIES TABLE
    private static final String TABLE_ACTIVITIES = "Activities";
    private static final String KEY_ACTIVITY_ID = "_id";
    private static final String KEY_ACTIVITY_ACTIVITY_TYPE_ID_FK = "activityTypeId";
    private static final String KEY_ACTIVITY_START_DATE = "start";
    private static final String KEY_ACTIVITY_END_DATE =  "end";
    private static final String KEY_ACTIVITY_POINTS_EARNED = "pointsEarned";

    //ACTIVITY TYPE TABLE
    private static final String TABLE_ACTIVITY_TYPE = "ActivityType";
    private static final String KEY_ACTIVITY_TYPE_ID = "_id";
    private static final String KEY_ACTIVITY_TYPE_NAME = "activityName";
    private static final String KEY_ACTIVITY_TYPE_MAXSPEED = "maxSpeed";
    private static final String KEY_ACTIVITY_TYPE_PRIORITY =  "priority";

    //
    private static volatile FitnessDBHelper sInstance;
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


        /*
        if (sInstance == null) {
            synchronized(FitnessDBHelper.class){
                if (sInstance == null){
                    sInstance = new FitnessDBHelper(context.getApplicationContext());
                }
            }
        }
        return sInstance;
        */
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

        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATIONS +
                "(" +
                KEY_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                "FOREIGN KEY (" + KEY_LOCATION_USER_ID_FK + " ) REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + "), " +
                "FOREIGN KEY (" + KEY_LOCATION_ACTIVITY_ID_FK + " ) REFERENCES " + TABLE_ACTIVITIES + "(" + KEY_ACTIVITY_ID + "), " +
                KEY_LOCATION_LATITUDE  + " REAL, " +
                KEY_LOCATION_LONGITUDE  + " REAL, " +
                KEY_LOCATION_ALTITUDE  + " REAL, " +
                KEY_LOCATION_ACCURACY  + " REAL, " +
                KEY_LOCATION_CREATED_DATE + " NUMERIC" +
                ")";

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_USER_IS_ADMIN  + " NUMERIC, " +
                KEY_USER_WEIGHT  + " REAL" +
                ")";

        String CREATE_ACTIVITIES_TABLE = "CREATE TABLE " + TABLE_ACTIVITIES +
                "(" +
                KEY_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                "FOREIGN KEY (" + KEY_ACTIVITY_ACTIVITY_TYPE_ID_FK + " ) REFERENCES " + TABLE_ACTIVITY_TYPE + "(" + KEY_ACTIVITY_TYPE_ID + "), " +
                KEY_ACTIVITY_START_DATE  + " NUMERIC, " +
                KEY_ACTIVITY_END_DATE  + " NUMERIC, " +
                KEY_ACTIVITY_POINTS_EARNED  + " REAL" +
                ")";

        String CREATE_ACTIVITY_TYPE_TABLE = "CREATE TABLE " + TABLE_ACTIVITY_TYPE +
                "(" +
                KEY_ACTIVITY_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_ACTIVITY_TYPE_NAME  + " TEXT, " +
                KEY_ACTIVITY_TYPE_MAXSPEED  + " REAL, " +
                KEY_ACTIVITY_TYPE_PRIORITY  + " INTEGER" +
                ")";

        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ACTIVITIES_TABLE);
        db.execSQL(CREATE_ACTIVITY_TYPE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOGTAG, "***onUpgrade***");

        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY_TYPE);
            onCreate(db);
        }
    }





}
