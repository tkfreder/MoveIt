package com.tinakit.moveit.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.TrackerActivity;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tina on 9/24/2015.
 */
public class ChooserRecyclerAdapter extends RecyclerView.Adapter<ChooserRecyclerAdapter.CustomViewHolder> {

    //private List<ActivityType> mActivityTypeList = new ArrayList<>(Arrays.asList(ActivityType.values()));
    private List<ActivityType> mActivityTypeList = new ArrayList<>();
    private Context mContext;

    public ChooserRecyclerAdapter(Context context) {

        mContext = context;

        // Get singleton instance of database
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(mContext);

        // Get Activity Types
        List<ActivityType> activityTypeList = databaseHelper.getActivityTypes();

        //get data from string resource
        Resources res = mContext.getResources();
        String[] activityTypeNames = res.getStringArray(R.array.string_array_activity_types);

        //save this in a HashMap for easy lookkup
        Map<Integer,String> activityTypeMap = new HashMap<>();

        for (String s : activityTypeNames){

            String strArray[] = s.split(",");
            activityTypeMap.put(Integer.parseInt(strArray[0]), strArray[1]);
        }

        //if results are returned from getActivityTypes(), build the ActivityType array for the dropdown
        if (activityTypeList.size() > 0){

            for (ActivityType activityType : activityTypeList){

                ActivityType at = activityType;

                //set the name from the resource string array, based on the ActivityTypeId
                at.setActivityName(activityTypeMap.get(at.getActivityTypeId()));

                //add this activityType to the list
                mActivityTypeList.add(at);
            }

        }
    }

    @Override
    public ChooserRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_type_list_item, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChooserRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

       ActivityType activityType = mActivityTypeList.get(i);

        // Populate data from ActivityType data object
        customViewHolder.activityName.setText(activityType.getActivityName());
        customViewHolder.activityName.setTag(activityType);
        customViewHolder.activityIcon.setImageResource(mContext.getResources().getIdentifier(activityType.getActivityName() + "_icon_small", "drawable", mContext.getPackageName()));

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomViewHolder holder = (CustomViewHolder) view.getTag();
                int position = holder.getAdapterPosition();

                ActivityType activityType = mActivityTypeList.get(position);

                Intent intent = new Intent(mContext, TrackerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("activity_type",activityType.getActivityName());
                bundle.putInt("activityId",activityType.getActivityTypeId());
                bundle.putString("username", "Lucy");
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        };

        //Handle click event on both title and image click
        customViewHolder.activityName.setOnClickListener(clickListener);
        customViewHolder.activityIcon.setOnClickListener(clickListener);

        customViewHolder.activityName.setTag(customViewHolder);
        customViewHolder.activityIcon.setTag(customViewHolder);

    }

    @Override
    public int getItemCount() {
        return (null != mActivityTypeList ? mActivityTypeList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView activityName;
        protected ImageView activityIcon;

        public CustomViewHolder(View view) {
            super(view);
            this.activityName = (TextView) view.findViewById(R.id.activityName);
            this.activityIcon = (ImageView) view.findViewById(R.id.activityIcon);
        }
    }

}
