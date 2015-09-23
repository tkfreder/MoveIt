package com.tinakit.moveit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.TrackerActivity;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.ActivityType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Tina on 9/22/2015.
 */
public class ActivityDetailListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ActivityDetail> mActivityDetailList;
    LayoutInflater mInflater;


    public ActivityDetailListAdapter (Context context){
        mContext = context;
    }

    public void setList(List<ActivityDetail> activityDetailList) {
        mActivityDetailList = activityDetailList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ((mActivityDetailList == null) ? 0 : mActivityDetailList.size());
    }

    @Override
    public Object getItem(int position) {
        return mActivityDetailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder{

        TextView activityId;
        TextView day;
        TextView time;
        TextView minutes;
        TextView coins;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null || convertView.getTag() == null) {
            convertView = mInflater.inflate(R.layout.activity_detail_list_item, null);
            holder = new ViewHolder();

            holder.activityId = (TextView) convertView.findViewById(R.id.activityId);
            holder.day = (TextView) convertView.findViewById(R.id.day);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.minutes = (TextView) convertView.findViewById(R.id.minutes);
            holder.coins = (TextView) convertView.findViewById(R.id.coins);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        /*
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TrackerActivity.class);
                Bundle bundle = new Bundle();
                TextView activityName = (TextView)v.findViewById(R.id.activityName);
                bundle.putString("activity_type",activityName.getText().toString());
                ActivityType activityType = (ActivityType)activityName.getTag();
                bundle.putInt("activityId",activityType.getActivityId());
                bundle.putString("username", "Lucy");
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        */

        //get a reference to the data
        ActivityDetail activityDetail = mActivityDetailList.get(position);

        // Populate data from ActivityDetail data object
        holder.activityId.setText(ActivityType.values()[ActivityType.getIndexById(activityDetail.getActivityId())].getName());

        //display day of the week for activities occurred in the last 7 days

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date weekAgo = calendar.getTime();
        if (activityDetail.getStartDate().after(weekAgo)){
            holder.day.setText(new SimpleDateFormat("EEEE").format(activityDetail.getStartDate()));
        }
        //otherwise, display the date of the activity
        else {
            holder.day.setText(new SimpleDateFormat("MM.dd.yy").format(activityDetail.getStartDate()));
        }

        holder.time.setText(new SimpleDateFormat("h:mm a").format(activityDetail.getStartDate().getTime()));
        holder.minutes.setText(String.format("%d", (long)activityDetail.getMinutesElapsed()) +
        ":" + String.format("%.0f", (activityDetail.getMinutesElapsed() % 1) * 60) + " min");
        holder.coins.setText(String.format("%.1f", activityDetail.getCoinsEarned()) + " coins");

        // Return the completed view to render on screen
        return convertView;
    }
}


