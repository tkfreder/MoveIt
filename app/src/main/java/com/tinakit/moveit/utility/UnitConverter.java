package com.tinakit.moveit.utility;

/**
 * Created by Tina on 9/19/2015.
 */
public class UnitConverter {

    public static float convertMetersToMiles(float meters) {

        return meters * 0.00062137f;
    }

    public static float convertMetersToFeet(float meters){
        return meters * 3.28084f;
    }

}
