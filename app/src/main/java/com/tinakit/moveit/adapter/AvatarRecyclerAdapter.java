package com.tinakit.moveit.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.ActivityHistory;
import com.tinakit.moveit.activity.ActivityTracker;
import com.tinakit.moveit.model.ActivityType;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.utility.Collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tina on 10/24/2015.
 */
public class AvatarRecyclerAdapter extends RecyclerView.Adapter<AvatarRecyclerAdapter.CustomViewHolder> {

    private List<User> mUserList = new ArrayList<>();
    private Context mContext;

    public AvatarRecyclerAdapter(Context context, List<User> userList) {

        mContext = context;
        mUserList = userList;
    }

    @Override
    public AvatarRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_users_list_item, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AvatarRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        User user = mUserList.get(i);

        // Populate data from ActivityType data object
        customViewHolder.avatar.setImageResource(mContext.getResources().getIdentifier(user.getAvatarFileName(), "drawable", mContext.getPackageName()));

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomViewHolder holder = (CustomViewHolder) view.getTag();
                int position = holder.getAdapterPosition();

                User user = mUserList.get(position);

                Intent intent = new Intent(mContext, ActivityHistory.class);
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

        public CustomViewHolder(View view) {
            super(view);
            this.avatar = (ImageView) view.findViewById(R.id.avatar);
        }
    }

}
