package com.tinakit.moveit.utility;

import android.content.Context;
import android.content.res.Resources;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tina on 9/30/2015.
 */
public class Collections {

    public static List<ActivityType> getActivityTypes(Context context){

        // Get singleton instance of database
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(context);

        // Get Activity Types
        List<ActivityType> activityTypeList = databaseHelper.getActivityTypes();


        //save this in a HashMap for easy lookkup
        Map<Integer,String> activityTypeMap = getActivityTypeMap(context);

        //if results are returned from getActivityTypes(), build the ActivityType array for the dropdown
        if (activityTypeList.size() > 0){

            for (ActivityType activityType : activityTypeList){

                ActivityType at = activityType;

                //set the name from the resource string array, based on the ActivityTypeId
                at.setActivityName(activityTypeMap.get(at.getActivityTypeId()));

            }

        }

        return activityTypeList;
    }

    public static Map<Integer,String> getActivityTypeMap(Context context){

        //get data from string resource
        Resources res = context.getResources();
        String[] activityTypeNames = res.getStringArray(R.array.string_array_activity_types);

        //save this in a HashMap for easy lookkup
        Map<Integer,String> activityTypeMap = new HashMap<>();

        for (String s : activityTypeNames){

            String strArray[] = s.split(",");
            activityTypeMap.put(Integer.parseInt(strArray[0]), strArray[1]);
        }

        return activityTypeMap;
    }
}
