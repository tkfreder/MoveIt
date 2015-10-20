package com.tinakit.moveit.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tinakit.moveit.R;
import com.tinakit.moveit.model.UnitSplitCalorie;

import java.util.List;
import java.util.Locale;

/**
 * Created by Tina on 10/19/2015.
 */
public class ActivityHistoryRecyclerAdapter  extends RecyclerView.Adapter<ActivityHistoryRecyclerAdapter.CustomViewHolder> {

    private Context mContext;
    private List<UnitSplitCalorie> mUnitList;

    public ActivityHistoryRecyclerAdapter(Context context, List<UnitSplitCalorie> unitList) {

        mContext = context;
        mUnitList = unitList;
    }

    @Override
    public ActivityHistoryRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_history_list_item, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ActivityHistoryRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        UnitSplitCalorie unitSplit = mUnitList.get(i);
        String address = "";

        try{

            //get street name at location
            Geocoder geocoder;
            List<Address> addresses;

            geocoder = new Geocoder(mContext, Locale.getDefault());
            addresses = geocoder.getFromLocation(mUnitList.get(i).getLocation().getLatitude(), mUnitList.get(i).getLocation().getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            //remove numbers
            address = address.replaceAll("\\d","");

        } catch (Exception e){
            e.printStackTrace();
        }

        customViewHolder.streetName.setText(address);
        Picasso.with(mContext)
                .load("https://maps.googleapis.com/maps/api/streetview?size=400x400&location=" + unitSplit.getLocation().getLatitude() + "," + unitSplit.getLocation().getLongitude() +
                        "&fov=90&heading=" + unitSplit.getBearing() + "&pitch=10" +
                        "&key=AIzaSyC5IJ88NXWXdHNazquwM6O5EDaCZ3Daf5Y") //TODO: save API key in some config file
                        //.resize(50, 50)
                        //.centerCrop()
                .into(customViewHolder.streetView);

    }

    @Override
    public int getItemCount() {
        return (null != mUnitList ? mUnitList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView streetView;
        protected TextView streetName;

        public CustomViewHolder(View view) {
            super(view);

            this.streetView = (ImageView)view.findViewById(R.id.streetView);
            this.streetName = (TextView)view.findViewById(R.id.streetName);
        }
    }
}
