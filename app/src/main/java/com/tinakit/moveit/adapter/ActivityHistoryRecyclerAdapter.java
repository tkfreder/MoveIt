package com.tinakit.moveit.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.EditActivity;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.utility.DateUtility;
import com.tinakit.moveit.utility.Map;

import java.util.List;

/**
 * Created by Tina on 10/19/2015.
 */
public class ActivityHistoryRecyclerAdapter  extends RecyclerView.Adapter<ActivityHistoryRecyclerAdapter.CustomViewHolder> {

    private Context mContext;
    private List<ActivityDetail> mActivityList;

    public ActivityHistoryRecyclerAdapter(Context context, List<ActivityDetail> activityList) {

        mContext = context;
        mActivityList = activityList;
    }

    @Override
    public ActivityHistoryRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_history_list_item, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ActivityHistoryRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        final ActivityDetail activityDetail = mActivityList.get(i);

        customViewHolder.streetName.setText(DateUtility.getDateFormattedRecent(activityDetail.getStartDate(), 7) + " " +  Map.getStreetName(mContext, new LatLng(activityDetail.getStartLocation().latitude, activityDetail.getStartLocation().longitude)));

        Picasso.with(mContext)
                .load("https://maps.googleapis.com/maps/api/streetview?size=400x400&location=" + activityDetail.getStartLocation().latitude + "," + activityDetail.getStartLocation().longitude +
                        "&fov=90&heading=" + activityDetail.getBearing() + "&pitch=10" +
                        "&key=AIzaSyC5IJ88NXWXdHNazquwM6O5EDaCZ3Daf5Y") //TODO: save API key in some config file
                        //.resize(50, 50)
                        //.centerCrop()
                .into(customViewHolder.streetView);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomViewHolder holder = (CustomViewHolder) view.getTag();
                int position = holder.getAdapterPosition();

                ActivityDetail activityDetail = mActivityList.get(position);

                Intent intent = new Intent(mContext, EditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("activityId", activityDetail.getActivityId());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        };

        //Handle click event on both streetName and image
        customViewHolder.streetView.setOnClickListener(clickListener);
        customViewHolder.streetName.setOnClickListener(clickListener);

        customViewHolder.streetView.setTag(customViewHolder);
        customViewHolder.streetName.setTag(customViewHolder);

    }

    @Override
    public int getItemCount() {
        return (null != mActivityList ? mActivityList.size() : 0);
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
