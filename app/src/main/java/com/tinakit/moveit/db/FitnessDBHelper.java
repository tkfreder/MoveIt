package com.tinakit.moveit.db;

//reference: https://github.com/codepath/android_guides/wiki/Local-Databases-with-SQLiteOpenHelper

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;

import com.tinakit.moveit.model.User;

import java.sql.SQLException;

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
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_IS_ADMIN = "isAdmin";
    private static final String KEY_USER_WEIGHT = "weight";
    private static final String KEY_USER_AVATAR_FILENAME = "avatarFileName";

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
                KEY_USER_NAME + " TEXT, " +
                KEY_USER_IS_ADMIN  + " NUMERIC, " +
                KEY_USER_WEIGHT  + " REAL, " +
                KEY_USER_AVATAR_FILENAME + " TEXT " +
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

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_USERS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOGTAG, "Error while trying to add user to database");
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
                    new String[]{KEY_USER_ID, KEY_USER_NAME, KEY_USER_IS_ADMIN, KEY_USER_WEIGHT, KEY_USER_AVATAR_FILENAME},
                    KEY_USER_NAME + " = ?", new String[]{userName}, null, null, null);

            try{

                if (cursor.moveToFirst())
                {
                    user.setUserId(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
                    user.setUserName(cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)));
                    user.setIsAdmin(cursor.getInt(cursor.getColumnIndex(KEY_USER_IS_ADMIN)) > 0 ? true : false);
                    user.setWeight(cursor.getFloat(cursor.getColumnIndex(KEY_USER_WEIGHT)));
                    user.setAvatarFileName(cursor.getString(cursor.getColumnIndex(KEY_USER_AVATAR_FILENAME)));
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
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();

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
