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

import com.tinakit.moveit.R;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.ActivityType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Tina on 9/24/2015.
 */
public class ActivityDetailRecyclerAdapter extends RecyclerView.Adapter<ActivityDetailRecyclerAdapter.CustomViewHolder> {

    private List<ActivityDetail> mActivityDetailList;
    private Context mContext;

    public ActivityDetailRecyclerAdapter(Context context, List<ActivityDetail> activityDetailList) {
        mContext = context;
        mActivityDetailList = activityDetailList;
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

        // Populate data from ActivityDetail data object
        customViewHolder.activityId.setText(ActivityType.values()[ActivityType.getIndexById(activityDetail.getActivityId())].getName());

        //display day of the week for activities occurred in the last 7 days

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date weekAgo = calendar.getTime();
        if (activityDetail.getStartDate().after(weekAgo)){
            customViewHolder.day.setText(new SimpleDateFormat("EEEE").format(activityDetail.getStartDate()));
        }
        //otherwise, display the date of the activity
        else {
            customViewHolder.day.setText(new SimpleDateFormat("MM.dd.yy").format(activityDetail.getStartDate()));
        }

        customViewHolder.time.setText(new SimpleDateFormat("h:mm a").format(activityDetail.getStartDate().getTime()));
        customViewHolder.minutes.setText(String.format("%d", (long)activityDetail.getMinutesElapsed()) +
                ":" + String.format("%.0f", (activityDetail.getMinutesElapsed() % 1) * 60) + " min");
        customViewHolder.coins.setText(String.format("%.0f", activityDetail.getCoinsEarned()) + " coins");
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

        public CustomViewHolder(View view) {
            super(view);

            this.activityId = (TextView) view.findViewById(R.id.activityId);
            this.day = (TextView) view.findViewById(R.id.day);
            this.time = (TextView) view.findViewById(R.id.time);
            this.minutes = (TextView) view.findViewById(R.id.minutes);
            this.coins = (TextView) view.findViewById(R.id.coins);
        }
    }

}

