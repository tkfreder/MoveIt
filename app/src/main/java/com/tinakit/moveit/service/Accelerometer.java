package com.tinakit.moveit.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;

import com.tinakit.moveit.R;
import com.tinakit.moveit.utility.DialogUtility;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tina on 12/12/2015.
 */
public class Accelerometer implements SensorEventListener{

    // CONSTANTS
    public static final String ACCELEROMETER_INTENT = "ACCELEROMETER_INTENT";
    private static final int ACCELEROMETER_DELAY = 60 * 30; //in seconds
    private static final float SHAKE_THRESHOLD = 0.5f;

    // INSTANCE FIELDS
    private FragmentActivity mFragmentActivity;
    private SensorManager mSensorManager;
    private Sensor sensorAccelerometer;
    private ScheduledExecutorService executor;
    private float last_x, last_y, last_z;
    private long lastUpdate = 0;

    public Accelerometer(FragmentActivity fragmentActivity){

        mFragmentActivity = fragmentActivity;
        executor = Executors.newSingleThreadScheduledExecutor();
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

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;


                //check for inactivity, below shake threshold
                if (speed < SHAKE_THRESHOLD) {

                    // send message to indicate there is new location data
                    Intent intent = new Intent(ACCELEROMETER_INTENT);
                    intent.putExtra(ACCELEROMETER_INTENT, ACCELEROMETER_INTENT);
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

    public void registerAccelerometer(){

        if (hasAccelerometer()) {
            // success! we have an accelerometer

            executor.schedule(new Runnable(){

                @Override
                public void run(){

                    //mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    sensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    mSensorManager.registerListener(Accelerometer.this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }, ACCELEROMETER_DELAY, TimeUnit.SECONDS);

        }

    }

    public void unregisterAccelerometer(){

        //unregister accelerometer
        if(sensorAccelerometer != null){
            mSensorManager.unregisterListener(this);

            if (!executor.isTerminated())
                executor.shutdownNow();

        }

    }
}
