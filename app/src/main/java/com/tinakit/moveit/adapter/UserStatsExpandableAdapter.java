package com.tinakit.moveit.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinakit.moveit.adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.view_holder.RewardChildViewHolder;
import com.tinakit.moveit.adapter.view_holder.RewardParentViewHolder;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.User;

import java.util.List;

/**
 * Created by Tina on 12/24/2015.
 */
public class UserStatsExpandableAdapter extends ExpandableRecyclerAdapter<RewardParentViewHolder, RewardChildViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private List<User> mUserList;
    private LayoutInflater mInflater;
    private User mCurrentUser;


    public UserStatsExpandableAdapter(Context context, Activity activity, List<User> userList) {
        super(userList);
        mContext = context;
        mActivity = activity;
        mInflater = LayoutInflater.from(context);
        mUserList = userList;


    }


    @Override
    public RewardParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {

        View view = mInflater.inflate(R.layout.stat_list_item_parent, viewGroup, false);
        return new RewardParentViewHolder(view);
    }

    @Override
    public RewardChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {

        View view = mInflater.inflate(R.layout.stat_list_item_child, viewGroup, false);
        return new RewardChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(RewardParentViewHolder rewardParentViewHolder, int i, ParentListItem parentListItem) {

        User user = (User) parentListItem;
        mCurrentUser = user;
        rewardParentViewHolder.bind(mContext.getResources().getIdentifier(user.getAvatarFileName(), "drawable", mActivity.getPackageName()), user);

    }

    @Override
    public void onBindChildViewHolder(RewardChildViewHolder rewardChildViewHolder, int i, Object object) {

        Reward reward = (Reward)object;
        rewardChildViewHolder.bind(mCurrentUser, reward);
    }

    @Override
    public int getItemCount() {
        return (null != mUserList ? mUserList.size() : 0);
    }

}
