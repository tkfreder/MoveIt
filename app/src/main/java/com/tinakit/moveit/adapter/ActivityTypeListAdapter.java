package com.tinakit.moveit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.TrackerActivity;
import com.tinakit.moveit.model.ActivityType;

import java.util.List;
import java.util.Map;

/**
 * Created by Tina on 9/18/2015.
 */
public class ActivityTypeListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ActivityType> mActivityList;

    public ActivityTypeListAdapter (Context context){
        mContext = context;
    }

    public void setList(List<ActivityType> activityTypeList) {
        mActivityList = activityTypeList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ((mActivityList == null) ? 0 : mActivityList.size());
    }

    @Override
    public Object getItem(int position) {
        return mActivityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder{

        TextView activityName;
        ImageView activityIcon;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null || convertView.getTag() == null) {
            convertView = mInflater.inflate(R.layout.activity_type_list_item, null);
            holder = new ViewHolder();

            holder.activityName = (TextView) convertView.findViewById(R.id.activityName);
            holder.activityIcon = (ImageView) convertView.findViewById(R.id.activityIcon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

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
        //get a reference to the data
        ActivityType activityType = mActivityList.get(position);

        // Populate data from ActivityType data object
        holder.activityName.setText(activityType.getName());
        holder.activityName.setTag(activityType);
        holder.activityIcon.setImageResource(mContext.getResources().getIdentifier(activityType.getName() + "_icon_small", "drawable", mContext.getPackageName()));

        // Return the completed view to render on screen
        return convertView;
    }
}

