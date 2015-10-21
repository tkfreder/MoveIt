package com.tinakit.moveit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tina on 10/21/2015.
 */
public class UserListCheckBoxRecyclerAdapter extends RecyclerView.Adapter<UserListCheckBoxRecyclerAdapter.CustomViewHolder> {

    private Context mContext;
    private List<User> mUserList;
    private ActivityDetail mActivityDetail;



    public UserListCheckBoxRecyclerAdapter(Context context, ActivityDetail activityDetail) {

        mContext = context;
        mActivityDetail = activityDetail;
    }

    @Override
    public UserListCheckBoxRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_list_checkbox, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserListCheckBoxRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        FitnessDBHelper mDatabaseHelper = FitnessDBHelper.getInstance(mContext);
        mUserList = mDatabaseHelper.getUsers();

        User user = mUserList.get(i);

        //set checked value based on mActivityDetail
        customViewHolder.userCheckBox.setChecked(mActivityDetail.hasUser(user));
        customViewHolder.userCheckBox.setTag(customViewHolder);

        //set onCheckedListener
        customViewHolder.userCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                CustomViewHolder holder = (CustomViewHolder) buttonView.getTag();
                int position = holder.getAdapterPosition();

                if (isChecked)
                    mActivityDetail.addUser(mUserList.get(position));
                else
                    mActivityDetail.removeUser(mUserList.get(position));
            }
        });

        //set usernames
        customViewHolder.username.setText(user.getUserName());

    }

    @Override
    public int getItemCount() {
        return (null != mActivityDetail.getUserList() ? mActivityDetail.getUserList().size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected CheckBox userCheckBox;
        protected TextView username;

        public CustomViewHolder(View view) {
            super(view);

            this.userCheckBox = (CheckBox)view.findViewById(R.id.userCheckBox);
            this.username = (TextView)view.findViewById(R.id.username);
        }
    }
}