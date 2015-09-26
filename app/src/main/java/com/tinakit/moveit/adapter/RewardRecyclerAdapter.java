package com.tinakit.moveit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.RewardView;
import com.tinakit.moveit.model.ActivityType;
import com.tinakit.moveit.model.Reward;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Tina on 9/25/2015.
 */
public class RewardRecyclerAdapter extends RecyclerView.Adapter<RewardRecyclerAdapter.CustomViewHolder> {

    private Context mContext;
    //TODO: make private after building DB
    public static List<Reward> mRewardList;
    private int mTotalPoints;
    private int mUserId;

    public RewardRecyclerAdapter(Context context, List<Reward> rewardList, int totalPoints, int userId) {
        mContext = context;
        mRewardList = rewardList;
        mTotalPoints = totalPoints;
        mUserId = userId;
    }

    @Override
    public RewardRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reward_list_item, viewGroup, false);

            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
    }

    @Override
    public void onBindViewHolder(RewardRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        Reward reward = mRewardList.get(i);

        // Populate data from Reward data object
        int numPoints = reward.getPoints();

        customViewHolder.points.setText(String.valueOf(numPoints));
        customViewHolder.name.setText(reward.getName());
        customViewHolder.description.setText(" " + reward.getDescription());

        customViewHolder.statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button button = (Button)v;
                if (button.getText().equals("Get It")){
                    Reward reward = (Reward)v.getTag();
                    mTotalPoints -= reward.getPoints();
                    //TODO: and update the points on the DB
                    mRewardList.get(v.getVerticalScrollbarPosition()).setUserStatus(1);
                    //TODO: and update the reward user status on the DB

                }
                else if (button.getText().equals("Cancel")){

                    Reward reward = (Reward)v.getTag();
                    mTotalPoints += reward.getPoints();
                    //TODO: and update the points on the DB
                    mRewardList.get(v.getVerticalScrollbarPosition()).setUserStatus(0);
                    //TODO: and update the reward user status on the DB
                }

                //TODO: is there another way to refresh the screen, ie notifydatasethaschanged?
                ((Activity)mContext).finish();
                Intent intent = new Intent(mContext, RewardView.class);
                Bundle bundle = new Bundle();
                bundle.putInt("total_coins", mTotalPoints);
                bundle.putInt("reward_list", 1);
                intent.putExtras(bundle);
                mContext.startActivity(intent);

                //TODO
                // put this reward item in the queue
                // decrement points of the reward from TotalCoins
                // set Reward.userStatus = 1
                // change status for this reward
                // refresh list to, enable/disable buttons to reflect new TotalCoins or display Pending status
            }
        });

        //if user has enough points, enable this button
        if (reward.getPoints() <= mTotalPoints && reward.getUserStatus() == 0) {
            customViewHolder.statusButton.setText("Get It");
            customViewHolder.statusButton.setTag(reward);
            customViewHolder.statusButton.setVisibility(View.VISIBLE);


        } else if (reward.getUserStatus() == 1) {

            customViewHolder.statusButton.setText("Cancel");
            customViewHolder.statusButton.setTag(reward);
            customViewHolder.statusButton.setVisibility(View.VISIBLE);
            customViewHolder.status.setText("waiting for Mommy to say yes");
        } else if (reward.getUserStatus() == 2) {

            customViewHolder.statusButton.setEnabled(false);
            customViewHolder.statusButton.setTag(reward);
            customViewHolder.status.setText("Mommy said no to this.  See her for more info.");
        }
    }

    @Override
    public int getItemCount() {
            return (null != mRewardList ? mRewardList.size() : 0);
            }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView points;
        TextView name;
        TextView description;
        Button statusButton;
        TextView status;

        public CustomViewHolder(View view) {
            super(view);

            this.points = (TextView) view.findViewById(R.id.points);
            this.name = (TextView) view.findViewById(R.id.name);
            this.description = (TextView) view.findViewById(R.id.description);
            this.statusButton = (Button) view.findViewById(R.id.statusButton);
            this.status = (TextView) view.findViewById(R.id.status);
        }
    }

}


