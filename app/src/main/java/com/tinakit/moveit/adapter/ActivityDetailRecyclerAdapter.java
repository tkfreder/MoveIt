package com.tinakit.moveit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.utility.Collections;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

