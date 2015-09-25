package com.tinakit.moveit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tinakit.moveit.R;
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
    private List<Reward> mRewardList;
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

        //if user has enough points, enable this button
        if (reward.getPoints() <= mTotalPoints && reward.getUserStatus() == 0) {
            customViewHolder.statusButton.setText("Get It");
            customViewHolder.statusButton.setVisibility(View.VISIBLE);

            customViewHolder.statusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TODO
                    // put this reward item in the queue
                    // decrement points of the reward from TotalCoins
                    // change status for this reward
                    // refresh list to, enable/disable buttons to reflect new TotalCoins or display Pending status
                }
            });
        } else if (reward.getUserStatus() == 1) {

            customViewHolder.statusButton.setText("Cancel");
            customViewHolder.status.setText("waiting for Mommy to say yes");
        } else if (reward.getUserStatus() == 2) {

            customViewHolder.statusButton.setEnabled(false);
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


