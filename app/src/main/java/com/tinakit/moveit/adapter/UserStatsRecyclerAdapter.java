package com.tinakit.moveit.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.RewardView;
import com.tinakit.moveit.model.User;

import java.util.List;

/**
 * Created by Tina on 12/28/2015.
 */
public class UserStatsRecyclerAdapter extends RecyclerView.Adapter<UserStatsRecyclerAdapter.CustomViewHolder>  {

    private Context mContext;
    private FragmentActivity mActivity;
    private List<User> mUserList;


    public UserStatsRecyclerAdapter(Context context, FragmentActivity activity, List<User> userList) {

        mContext = context;
        mActivity = activity;
        mUserList = userList;

        }


    @Override
    public UserStatsRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stat_list_item_parent, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
        }
    @Override
    public void onBindViewHolder(final UserStatsRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        User user = mUserList.get(i);

        customViewHolder.avatar.setImageResource(mContext.getResources().getIdentifier(user.getAvatarFileName(), "drawable", mActivity.getPackageName()));
        customViewHolder.points.setText(String.valueOf(user.getPoints()));
        customViewHolder.itemView.setTag(user);

        customViewHolder.rewardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                User user = (User) customViewHolder.itemView.getTag();

                Intent intent = new Intent(mContext, RewardView.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return (null != mUserList ? mUserList.size() : 0);
        }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView avatar;
        protected TextView points;
        protected Button rewardButton;

        public CustomViewHolder(View view) {
            super(view);

            this.avatar = (ImageView) view.findViewById(R.id.avatar);
            this.points = (TextView) view.findViewById(R.id.points);
            this.rewardButton = (Button) view.findViewById(R.id.rewardButton);
        }
    }

}
