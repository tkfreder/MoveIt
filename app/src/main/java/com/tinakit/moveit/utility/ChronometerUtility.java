package com.tinakit.moveit.utility;

import android.os.SystemClock;
import android.widget.Chronometer;

/**
 * Created by Tina on 12/13/2015.
 */
public class ChronometerUtility {

    // INSTANCE FIELDS
    private Chronometer mChronometer;


    public ChronometerUtility (Chronometer chronometer){

        mChronometer = chronometer;
    }

    public void start(){

        mChronometer.start();

    }
    public void stop(){

        mChronometer.stop();
    }

    public long elapsedTime(){

        return mChronometer.getBase() - SystemClock.elapsedRealtime();
    }

    public void resume(){

        mChronometer.setBase(SystemClock.elapsedRealtime() + elapsedTime());
        mChronometer.start();

    }

    public void resetTime(){

        mChronometer.setBase(SystemClock.elapsedRealtime());

    }

    public static int getTimeByUnits(String timeString, int units){

        String string = timeString;

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

        switch (units) {

            //seconds
            case 0:
                return Math.round(seconds + (minutes*60f) + (hours*3600f));
            //minutes
            case 1:
                return Math.round(seconds/60f + minutes + (hours * 60));

            //hours
            case 2:
                return Math.round(seconds/3600f + minutes/60f + hours);

            //seconds
            default:
                return Math.round(seconds + (minutes*60f) + (hours*3600f));
        }
    }
}
