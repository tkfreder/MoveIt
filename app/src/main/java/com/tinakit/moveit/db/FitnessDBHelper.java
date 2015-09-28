package com.tinakit.moveit.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tina on 9/28/2015.
 */

public class FitnessDBHelper extends SQLiteOpenHelper implements Cloneable {

    private static final String LOGTAG = FitnessDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME    = "fitness.db";
    private static final int    DATABASE_VERSION = 1;

    private static volatile FitnessDBHelper sInstance;
    private static Context mContext;



    // ...

    //reference:  Singleton pattern https://www.youtube.com/watch?v=GH5_lhFShfU
    public static FitnessDBHelper getInstance(Context context) {

        mContext = context;
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

        return Holder.INSTANCE;
    }

    //Singleton holder
    static class Holder{

        static final FitnessDBHelper INSTANCE = new FitnessDBHelper(mContext);
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private FitnessDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
