package com.tinakit.moveit.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.model.Reward;

import java.util.List;

/**
 * Created by Tina on 9/23/2015.
 */
public class RewardListAdapter  extends BaseAdapter {

    private Context mContext;
    private List<Reward> mRewardList;
    private int mTotalPoints;
    private int mUserId;

    public RewardListAdapter (Context context, int totalPoints, int userId){

        mContext = context;
        mTotalPoints = totalPoints;
        mUserId = userId;
    }

    public void setList(List<Reward> rewardList) {
        mRewardList = rewardList;
        //TODO: do we need this call?
        //notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ((mRewardList == null) ? 0 : mRewardList.size());
    }

    @Override
    public Object getItem(int position) {
        return mRewardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder{

        TextView points;
        TextView name;
        TextView description;
        Button statusButton;
        TextView status;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null || convertView.getTag() == null) {
            convertView = mInflater.inflate(R.layout.reward_list_item, null);
            holder = new ViewHolder();

            holder.points = (TextView) convertView.findViewById(R.id.points);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.statusButton = (Button) convertView.findViewById(R.id.statusButton);
            holder.status = (TextView) convertView.findViewById(R.id.status);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //get a reference to the data
        Reward reward = mRewardList.get(position);

        // Populate data from Reward data object
        int numPoints = reward.getPoints();

        holder.points.setText(String.valueOf(numPoints));
        holder.name.setText(reward.getName());
        holder.description.setText(" " + reward.getDescription());

        //if user has enough points, enable this button
        if (reward.getPoints() <= mTotalPoints && reward.getUserStatus() == 0) {
            holder.statusButton.setText("Get It");
            holder.statusButton.setVisibility(View.VISIBLE);

            holder.statusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TODO
                    // put this reward item in the queue
                    // decrement points of the reward from TotalCoins
                    // change status for this reward
                    // refresh list to, enable/disable buttons to reflect new TotalCoins or display Pending status
                }
            });
        }
        else if (reward.getUserStatus() == 1){

            holder.statusButton.setText("Cancel");
            holder.status.setText("waiting for Mommy to say yes");
        }
        else if (reward.getUserStatus() == 2){

            holder.statusButton.setEnabled(false);
            holder.status.setText("Mommy said no to this.  See her for more info.");
        }


        // Return the completed view to render on screen
        return convertView;
    }
}


