package com.tinakit.moveit.utility;

/**
 * Created by Tina on 9/18/2015.
 */
public class CalorieCalculator {

    //weight in pounds
    //speed in miles per hour

    public static int getCalorieByRun(float weight, float minutes, float speed){

        return Math.round((0.0018f) * weight * minutes * (10.8f * speed + 1));
    }

    public static int getCalorieByBike(float weight, float minutes, float speed){

        float k = 0f;

        if (speed > 10f){
            k = 0.0073f * weight * minutes * (speed - 10f);
        }

        return Math.round((0.03f * weight * minutes) + k);
    }

    public static int getCalorieByScooter(float weight, float minutes){

        return Math.round(weight * minutes * 0.0226f);
    }
}
