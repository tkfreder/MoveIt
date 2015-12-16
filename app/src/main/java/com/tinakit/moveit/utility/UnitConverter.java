package com.tinakit.moveit.utility;

/**
 * Created by Tina on 9/19/2015.
 */
public class UnitConverter{

    //constants
    private static final float METER_MILE_CONVERSION = 0.00062137f;
    private static final float METER_FEET_CONVERSION = 3.28084f;
    private static final float FEET_COIN_CONVERSION = 0.5f;  //2 feet = 1 coin //TODO: DEBUG
    private static final float CALORIE_COIN_CONVERSION = 10f; //#coins equal to 1 calorie

    public static float convertMetersToMiles(float meters) {

        return meters * 0.00062137f;
    }

    public static float convertMetersToFeet(float meters){
        return meters * 3.28084f;
    }

    public static float convertMillisecondsToUnits(long milliseconds, TimeUnits toUnits){

        float toValue = 0.0f;

        switch (toUnits){

            case SECONDS:
                toValue = milliseconds / 1000;
                break;

            case MINUTES:
                toValue = milliseconds / (1000 * 60);
                break;

            case HOURS:
                toValue = milliseconds / (1000 * 60 * 60);
                break;
        }

        return toValue;

    }

    public enum TimeUnits{

        SECONDS,
        MINUTES,
        HOURS
    }

}
