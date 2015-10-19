package com.tinakit.moveit.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.tinakit.moveit.R;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.UnitSplitCalorie;
import com.tinakit.moveit.utility.Collections;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Tina on 9/24/2015.
 */
public class ActivityDetailRecyclerAdapter extends RecyclerView.Adapter<ActivityDetailRecyclerAdapter.CustomViewHolder> {

    private List<ActivityDetail> mActivityDetailList;
    private Context mContext;

    //TODO: test data
    private static final LatLng HOME1 = new LatLng(34.143274, -118.077125);
    private static final LatLng HOME2 = new LatLng(34.143273, -118.076434);

    private static final LatLng GETTY1 = new LatLng(34.078061, -118.474366);
    private static final LatLng GETTY2 = new LatLng(34.077620, -118.473992);

    private static final LatLng ANNENBERG1 = new LatLng(34.023916, -118.513201);
    private static final LatLng ANNENBERG2 = new LatLng(34.023624, -118.513458);

    private static final LatLng TARPITS1 = new LatLng(34.063305, -118.355559);
    private static final LatLng TARPITS2 = new LatLng(34.063104, -118.355524);



    private Location location1 = new Location("home1");
    private Location location2 = new Location("home2");
    private Location location3 = new Location("getty1");
    private Location location4 = new Location("getty2");
    private Location location5 = new Location("annenberg1");
    private Location location6 = new Location("annenberg2");
    private Location location7 = new Location("tarpits1");
    private Location location8 = new Location("tarpits2");
    private float heading;
    ArrayList<UnitSplitCalorie> locationList;
    String address;



    public ActivityDetailRecyclerAdapter(Context context, List<ActivityDetail> activityDetailList) {
        mContext = context;
        mActivityDetailList = activityDetailList;

        locationList = new ArrayList<>();
        float bearing = 0f;
        UnitSplitCalorie unit;

        location1.setLatitude(HOME1.latitude);
        location1.setLongitude(HOME1.longitude);
        location2.setLatitude(HOME2.latitude);
        location2.setLongitude(HOME2.longitude);
        bearing = location1.bearingTo(location2);
        unit = new UnitSplitCalorie(new Date(), location1);
        unit.setBearing(bearing);
        locationList.add(unit);

        location3.setLatitude(GETTY1.latitude);
        location3.setLongitude(GETTY1.longitude);
        location4.setLatitude(GETTY2.latitude);
        location4.setLongitude(GETTY2.longitude);
        bearing = location3.bearingTo(location4);
        unit = new UnitSplitCalorie(new Date(), location3);
        unit.setBearing(bearing);
        locationList.add(unit);

        location5.setLatitude(ANNENBERG1.latitude);
        location5.setLongitude(ANNENBERG1.longitude);
        location6.setLatitude(ANNENBERG2.latitude);
        location6.setLongitude(ANNENBERG2.longitude);
        bearing = location5.bearingTo(location6);
        unit = new UnitSplitCalorie(new Date(), location5);
        unit.setBearing(bearing);
        locationList.add(unit);

        location7.setLatitude(TARPITS1.latitude);
        location7.setLongitude(TARPITS1.longitude);
        location8.setLatitude(TARPITS2.latitude);
        location8.setLongitude(TARPITS2.longitude);
        bearing = location7.bearingTo(location8);
        unit = new UnitSplitCalorie(new Date(), location7);
        unit.setBearing(bearing);
        locationList.add(unit);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(mContext, Locale.getDefault());

        try{
            addresses = geocoder.getFromLocation(location1.getLatitude(), location1.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            //remove numbers
            address = address.replaceAll("\\d","");

            /*String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            */

        }catch (IOException io){
            io.printStackTrace();
        }


    }

    @Override
    public ActivityDetailRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_detail_list_item, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ActivityDetailRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        ActivityDetail activityDetail = mActivityDetailList.get(i);

        //get hashmap of <ActivityId, ActivityName>
        Map<Integer,String> activityMap = Collections.getActivityTypeMap(mContext);

        // Populate data from ActivityDetail data object
        customViewHolder.activityId.setText(activityMap.get(activityDetail.getActivityId()));

        //display day of the week for activities occurred in the last 7 days
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date weekAgo = calendar.getTime();

        Date startDate = activityDetail.getStartDate();
        Date endDate = activityDetail.getEndDate();

        if (startDate.after(weekAgo)){
            customViewHolder.day.setText(new SimpleDateFormat("EEEE").format(startDate));
        }
        //otherwise, display the date of the activity
        else {
            customViewHolder.day.setText(new SimpleDateFormat("MM.dd.yy").format(startDate));
        }

        customViewHolder.time.setText(new SimpleDateFormat("h:mm a").format(startDate.getTime()));

        int secondsElapsed = (int)(endDate.getTime() - startDate.getTime())/1000;
        int minutesElapsed = (int)secondsElapsed/60;

        //TODO: implement leading zeros
        customViewHolder.minutes.setText(String.valueOf(minutesElapsed) + ":" + String.format("%02d", secondsElapsed % 60) + " min");
        customViewHolder.coins.setText(String.format("%.0f", activityDetail.getPointsEarned()) + " coins");

        //TODO:  placeholder for image, testing Picasso

        customViewHolder.streetName.setText(address);

        Picasso.with(mContext)
                //.load("https://maps.googleapis.com/maps/api/streetview?size=400x400&location=40.720032,-73.988354" +
                //"&fov=90&heading=235&pitch=10" +
                .load("https://maps.googleapis.com/maps/api/streetview?size=400x400&location=" + locationList.get(0).getLocation().getLatitude() + "," + locationList.get(0).getLocation().getLongitude() +
                        "&fov=90&heading=" + locationList.get(0).getBearing() + "&pitch=10" +
                "&key=AIzaSyC5IJ88NXWXdHNazquwM6O5EDaCZ3Daf5Y")
                        //.resize(50, 50)
                        //.centerCrop()
                .into(customViewHolder.streetView);
/*
        Picasso.with(mContext)
                //.load("https://maps.googleapis.com/maps/api/streetview?size=400x400&location=40.720032,-73.988354" +
                //"&fov=90&heading=235&pitch=10" +
                .load("https://maps.googleapis.com/maps/api/streetview?size=400x400&location=" + locationList.get(1).getLocation().getLatitude() + "," + locationList.get(1).getLocation().getLongitude() +
                        "&fov=90&heading=" + locationList.get(1).getBearing() + "&pitch=10" +
                        "&key=AIzaSyC5IJ88NXWXdHNazquwM6O5EDaCZ3Daf5Y")
                        //.resize(50, 50)
                        //.centerCrop()
                .into(customViewHolder.streetView2);

        Picasso.with(mContext)
                //.load("https://maps.googleapis.com/maps/api/streetview?size=400x400&location=40.720032,-73.988354" +
                //"&fov=90&heading=235&pitch=10" +
                .load("https://maps.googleapis.com/maps/api/streetview?size=400x400&location=" + locationList.get(2).getLocation().getLatitude() + "," + locationList.get(2).getLocation().getLongitude() +
                        "&fov=90&heading=" + locationList.get(2).getBearing() + "&pitch=10" +
                        "&key=AIzaSyC5IJ88NXWXdHNazquwM6O5EDaCZ3Daf5Y")
                        //.resize(50, 50)
                        //.centerCrop()
                .into(customViewHolder.streetView3);

        Picasso.with(mContext)
                //.load("https://maps.googleapis.com/maps/api/streetview?size=400x400&location=40.720032,-73.988354" +
                //"&fov=90&heading=235&pitch=10" +
                .load("https://maps.googleapis.com/maps/api/streetview?size=400x400&location=" + locationList.get(3).getLocation().getLatitude() + "," + locationList.get(3).getLocation().getLongitude() +
                        "&fov=90&heading=" + locationList.get(3).getBearing() + "&pitch=10" +
                        "&key=AIzaSyC5IJ88NXWXdHNazquwM6O5EDaCZ3Daf5Y")
                        //.resize(50, 50)
                        //.centerCrop()
                .into(customViewHolder.streetView4);

        */
    }

    @Override
    public int getItemCount() {
        return (null != mActivityDetailList ? mActivityDetailList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView activityId;
        protected TextView day;
        protected TextView time;
        protected TextView minutes;
        protected TextView coins;
        protected ImageView streetView;
        protected ImageView streetView2;
        protected ImageView streetView3;
        protected ImageView streetView4;
        protected TextView streetName;

        public CustomViewHolder(View view) {
            super(view);

            this.activityId = (TextView) view.findViewById(R.id.activityId);
            this.day = (TextView) view.findViewById(R.id.day);
            this.time = (TextView) view.findViewById(R.id.time);
            this.minutes = (TextView) view.findViewById(R.id.minutes);
            this.coins = (TextView) view.findViewById(R.id.coins);
            this.streetView = (ImageView)view.findViewById(R.id.streetImage);
            this.streetView2 = (ImageView)view.findViewById(R.id.streetImage2);
            this.streetView3 = (ImageView)view.findViewById(R.id.streetImage3);
            this.streetView4 = (ImageView)view.findViewById(R.id.streetImage4);
            this.streetName = (TextView)view.findViewById(R.id.streetName);

        }
    }

}

