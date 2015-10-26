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
import com.tinakit.moveit.activity.ActivityHistory;
import com.tinakit.moveit.activity.RewardView;
import com.tinakit.moveit.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tina on 10/24/2015.
 */
public class ViewStatsRecyclerAdapter extends RecyclerView.Adapter<ViewStatsRecyclerAdapter.CustomViewHolder> {

    private List<User> mUserList = new ArrayList<>();
    private Context mContext;

    public ViewStatsRecyclerAdapter(Context context, List<User> userList) {

        mContext = context;
        mUserList = userList;
    }

    @Override
    public ViewStatsRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_stats_list_item, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewStatsRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        User user = mUserList.get(i);

        // Populate data from ActivityType data object
        customViewHolder.avatar.setImageResource(mContext.getResources().getIdentifier(user.getAvatarFileName(), "drawable", mContext.getPackageName()));
        customViewHolder.username.setText(user.getUserName());
        customViewHolder.points.setText(String.valueOf(user.getPoints()));

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomViewHolder holder = (CustomViewHolder) view.getTag();
                int position = holder.getAdapterPosition();

                User user = mUserList.get(position);

                Intent intent = new Intent(mContext, RewardView.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        };

        //Handle click event on all UI widgets
        customViewHolder.avatar.setOnClickListener(clickListener);
        customViewHolder.avatar.setTag(customViewHolder);

    }

    @Override
    public int getItemCount() {
        return (null != mUserList ? mUserList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView avatar;
        protected TextView username;
        protected TextView points;

        public CustomViewHolder(View view) {
            super(view);
            this.avatar = (ImageView) view.findViewById(R.id.avatar);
            this.username = (TextView)view.findViewById(R.id.username);
            this.points = (TextView)view.findViewById(R.id.points);
        }
    }

}
