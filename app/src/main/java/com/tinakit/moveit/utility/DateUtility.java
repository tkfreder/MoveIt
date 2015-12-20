package com.tinakit.moveit.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tina on 10/20/2015.
 */
public class DateUtility {

    public static String getDateFormattedRecent(Date date, int daysAgo){

        //display day of the week for activities occurred in the last daysAgo
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0 - daysAgo);

        Date weekAgo = calendar.getTime();
        if (date.after(weekAgo)){
            return new SimpleDateFormat("EEE").format(date);//EEE, short version of day of the week
        }
        //otherwise, display the date of the activity
        else {
            return new SimpleDateFormat("MM.dd.yy").format(date);
        }
    }
}
