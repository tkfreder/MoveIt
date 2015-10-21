package com.tinakit.moveit.utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

/**
 * Created by Tina on 10/16/2015.
 */
public class Map {

    private static float SHORT_DISTANCE_FEET = 100f;
    private static float MEDIUM_DISTANCE_FEET = 1000f;
    private static float LONG_DISTANCE_FEET = 2000f;


    public static float getZoomByDistance(float distance){

        if (distance < SHORT_DISTANCE_FEET){
            return 21.0f;
        }
        else if (distance >= SHORT_DISTANCE_FEET && distance < MEDIUM_DISTANCE_FEET ){
            return 15.0f;
        }
        else if (distance >= MEDIUM_DISTANCE_FEET && distance < LONG_DISTANCE_FEET ){
            return 12.0f;

        }else
            return 6.0f;
    }

    public static String getStreetName(Context context, LatLng location){

        String streetName = "";

        try{

            //get street name at location
            Geocoder geocoder;
            List<Address> addresses;

            geocoder = new Geocoder(context, Locale.getDefault());
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            streetName = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            //remove numbers
            streetName = streetName.replaceAll("\\d","");

        } catch (Exception e){
            e.printStackTrace();
        }

        return streetName;
    }
}
