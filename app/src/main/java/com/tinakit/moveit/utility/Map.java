package com.tinakit.moveit.utility;

/**
 * Created by Tina on 10/16/2015.
 */
public class Map {

    private static float SHORT_DISTANCE_FEET = 100f;
    private static float MEDIUM_DISTANCE_FEET = 1000f;
    private static float LONG_DISTANCE_FEET = 2000f;


    public static float getZoomByDistance(float distance){

        if (distance < SHORT_DISTANCE_FEET){
            return 2.0f;
        }
        else if (distance >= SHORT_DISTANCE_FEET && distance < MEDIUM_DISTANCE_FEET ){
            return 15.0f;
        }
        else if (distance >= MEDIUM_DISTANCE_FEET && distance < LONG_DISTANCE_FEET ){
            return 18.0f;

        }else
            return 21.0f;
    }
}
