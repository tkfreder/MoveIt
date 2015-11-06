package com.tinakit.moveit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.ActivityTracker;
import com.tinakit.moveit.activity.RewardView;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.ActivityType;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.utility.Collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tina on 11/5/2015.
 */
public class MultiChooserRecyclerAdapter extends RecyclerView.Adapter<MultiChooserRecyclerAdapter.CustomViewHolder> {

    private List<User> mUserList;
    private List<ActivityType> mActivityTypeList;
    private Context mContext;
    private List<User> mParticipantList;
    private List<ActivityType> mParticipantActivityList;
    private ActivityDetail mActivityDetail = new ActivityDetail();

    public MultiChooserRecyclerAdapter(Context context, List<User> userList, List<ActivityType> activityTypeList) {

        mContext = context;
        mUserList = userList;
        mActivityTypeList = activityTypeList;

        mParticipantList = new ArrayList<>();
        mParticipantActivityList = new ArrayList<>();
    }

    @Override
    public MultiChooserRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.multi_activity_chooser_item, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MultiChooserRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        User user = mUserList.get(i);

        // Populate data from ActivityType data object
        customViewHolder.avatar.setImageResource(mContext.getResources().getIdentifier(user.getAvatarFileName(), "drawable", mContext.getPackageName()));
        customViewHolder.username.setText(user.getUserName());
        customViewHolder.activityTypes.setTag(user);

        //Handle click event on spinner
        customViewHolder.activityTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                User user = (User) parent.getTag();

                //check if "not participant" was selected
                if (position == 0) {

                    //remove this user, if exists
                    mActivityDetail.removeUserActivity(user);
                } else {

                    //check if user already exists in the ActivityDetail.userList
                    //first remove that activity before adding a new activity for that user
                    if (mActivityDetail.getUserList().contains(user)){
                        mActivityDetail.removeUserActivity(user);
                    }

                    //add user and corresponding activity
                    //TODO: due to adding the first item in spinner as empty selection, must decrement the index
                    mActivityDetail.addUserActivity(user, mActivityTypeList.get(position - 1));

                }

                //TODO: possibly replace this with EventBus to pass data to ActivityTracker.java
                ActivityTracker.mActivityDetail.setUserList(mActivityDetail.getUserList());
                ActivityTracker.mActivityDetail.setActivityTypeList(mActivityDetail.getActivityTypeList());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //get string array of activity types
        List<String> activityTypeStringList = new ArrayList<>();

        //add an empty item called no participant
        activityTypeStringList.add(mContext.getResources().getString(R.string.not_participant));

        for ( ActivityType activityType : mActivityTypeList){
            activityTypeStringList.add(activityType.getActivityName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, activityTypeStringList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customViewHolder.activityTypes.setAdapter(dataAdapter);

    }

    @Override
    public int getItemCount() {
        return (null != mUserList ? mUserList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView avatar;
        protected TextView username;
        protected Spinner activityTypes;

        public CustomViewHolder(View view) {
            super(view);
            this.avatar = (ImageView) view.findViewById(R.id.avatar);
            this.username = (TextView)view.findViewById(R.id.username);
            this.activityTypes = (Spinner)view.findViewById(R.id.activityTypeSpinner);
        }
    }

}
