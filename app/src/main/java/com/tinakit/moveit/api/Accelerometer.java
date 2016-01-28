package com.tinakit.moveit.api;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;

import com.tinakit.moveit.activity.ActivityTracker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tina on 12/12/2015.
 */
public class Accelerometer implements SensorEventListener{

    // CONSTANTS
    public static final int SENSITIVITY_LIGHT = 11;
    public static final int SENSITIVITY_MEDIUM = 13;
    public static final int SENSITIVITY_HARD = 15;
    public static final String ACCELEROMETER_INTENT = "ACCELEROMETER_INTENT";
    private static final int ACCELEROMETER_DELAY = 3; //in seconds
    //private static final float SHAKE_THRESHOLD = 0.3f;
    private static final int ACCELERATION_THRESHOLD = SENSITIVITY_LIGHT;

    // INSTANCE FIELDS
    private FragmentActivity mFragmentActivity;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ScheduledExecutorService executor;
    private float last_x, last_y, last_z;
    private long lastUpdate = 0;

    public Accelerometer(FragmentActivity fragmentActivity){

        mFragmentActivity = fragmentActivity;

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                //float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
                final double magnitudeSquared = x * x + y * y + z * z;


                // Instead of comparing magnitude to ACCELERATION_THRESHOLD,
                // compare their squares. This is equivalent and doesn't need the
                // actual magnitude, which would be computed using (expesive) Math.sqrt().
                //check for inactivity, below shake threshold
                if(magnitudeSquared < ACCELERATION_THRESHOLD * ACCELERATION_THRESHOLD){
                //if (speed < SHAKE_THRESHOLD){

                    // send message to indicate there is new location data
                    Intent intent = new Intent(ACCELEROMETER_INTENT);
                    intent.putExtra(ActivityTracker.ACTIVITY_TRACKER_BROADCAST_RECEIVER, ACCELEROMETER_INTENT);
                    LocalBroadcastManager.getInstance(mFragmentActivity).sendBroadcast(intent);

                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        // do nothing
    }

    public boolean hasAccelerometer(){

        mSensorManager = (SensorManager) mFragmentActivity.getSystemService(Context.SENSOR_SERVICE);
        return mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
    }

    public void start(SensorManager sensorManager){

        if (mAccelerometer == null) {
            mSensorManager = sensorManager;
            mAccelerometer = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER);

            // If this phone has an accelerometer, listen to it.
            //if (mAccelerometer != null) {
            //    mSensorManager.registerListener(Accelerometer.this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            //}

            executor = Executors.newSingleThreadScheduledExecutor();

            //if (hasAccelerometer()) {
            if (mAccelerometer != null){
                // success! we have an accelerometer

                executor.schedule(new Runnable(){

                    @Override
                    public void run(){

                        //mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                        //mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                        mSensorManager.registerListener(Accelerometer.this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                    }
                }, ACCELEROMETER_DELAY, TimeUnit.SECONDS);

            }
        }

    }

    public void stop(){

        //unregister accelerometer
        if(mAccelerometer != null){
            mSensorManager.unregisterListener(this);
            mSensorManager = null;
            mAccelerometer = null;

            if (!executor.isTerminated())
                executor.shutdownNow();
        }

    }
}
