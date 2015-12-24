package com.tinakit.moveit.adapter.view_holder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.tinakit.moveit.R;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.RewardStatusType;
import com.tinakit.moveit.model.User;

/**
 * Created by Tina on 12/23/2015.
 */
public class RewardChildViewHolder extends ChildViewHolder {

    public TextView rewardPoints;
    public TextView name;
    public Button statusButton;
    public TextView status;

    public RewardChildViewHolder (View view) {

        super(view);
        this.rewardPoints = (TextView)view.findViewById(R.id.rewardPoints);
        this.name = (TextView)view.findViewById(R.id.name);
        this.statusButton = (Button)view.findViewById(R.id.statusButton);
        this.status = (TextView)view.findViewById(R.id.status);
    }

    public void bind(User user, Reward reward){

        rewardPoints.setText(String.valueOf(reward.getPoints()));
        name.setText(reward.getName());

        //if user has enough points, enable this button
        if (reward.getPoints() <= user.getPoints() && reward.getRewardStatusType() == RewardStatusType.AVAILABLE) {
            statusButton.setText("Get It");
            statusButton.setTag(reward);
            statusButton.setVisibility(View.VISIBLE);


        } else if (reward.getRewardStatusType() == RewardStatusType.PENDING) {

            statusButton.setText("Cancel");
            statusButton.setTag(reward);
            statusButton.setVisibility(View.VISIBLE);
            status.setText("in progress");

        } else if (reward.getRewardStatusType() == RewardStatusType.DENIED) {

            statusButton.setEnabled(false);
            statusButton.setTag(reward);
            status.setText("Mommy said no to this.");
        }
    }
}
