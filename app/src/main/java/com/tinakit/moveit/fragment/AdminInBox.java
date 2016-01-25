package com.tinakit.moveit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;
import com.tinakit.moveit.utility.DateUtility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 1/12/2016.
 */
public class AdminInBox extends Fragment {

    @Inject
    FitnessDBHelper mDatabaseHelper;

    protected FragmentActivity mFragmentActivity;
    protected View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentActivity = (FragmentActivity)getActivity();
        mRootView = inflater.inflate(R.layout.recycler_view, container, false);

        // Dagger 2 injection
        ((CustomApplication)getActivity().getApplication()).getAppComponent().inject(this);

        RecyclerView recyclerView = (RecyclerView)mRootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mFragmentActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        List<Reward> rewardList = mDatabaseHelper.getUnFulfilledRewards();
        List<User> userList = mDatabaseHelper.getUsers();

        AdminInboxRecyclerAdapter recyclerAdapter = new AdminInboxRecyclerAdapter(rewardList, userList, mDatabaseHelper);
        recyclerView.setAdapter(recyclerAdapter);

        return mRootView;

    }

    public static class AdminInboxRecyclerAdapter extends RecyclerView.Adapter<AdminInboxRecyclerAdapter.CustomViewHolder>{

        private List<Reward> mRewardList;
        private List<User> mUserList;
        private FitnessDBHelper mDBHelper;

        public AdminInboxRecyclerAdapter(List<Reward> rewardList, List<User> userList, FitnessDBHelper dbHelper){

            mRewardList = rewardList;
            mUserList = userList;
            mDBHelper = dbHelper;
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder{

            protected TextView date;
            protected TextView userName;
            protected TextView rewardName;
            protected Button fulfill;

            public CustomViewHolder(View itemView) {

                super(itemView);

                date = (TextView)itemView.findViewById(R.id.date);
                userName = (TextView)itemView.findViewById(R.id.userName);
                rewardName = (TextView)itemView.findViewById(R.id.rewardName);
                fulfill = (Button)itemView.findViewById(R.id.fulfill);

            }
        }

        @Override
        public int getItemCount() {
            return (null != mRewardList ? mRewardList.size() : 0);
        }

        @Override
        public AdminInboxRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_queue_list_item, parent, false);
            CustomViewHolder customViewHolder = new CustomViewHolder(view);

            return customViewHolder;
        }

        @Override
        public void onBindViewHolder(AdminInboxRecyclerAdapter.CustomViewHolder holder, int position) {

            Reward reward = mRewardList.get(position);

            holder.date.setText(DateUtility.getDateFormattedRecent(reward.getDateEarned(), 7));

            String userName = "";
            for (User user : mUserList){

                if(user.getUserId() == reward.getUserId())
                    userName = user.getUserName();
            }
            holder.userName.setText(userName);

            holder.rewardName.setText(reward.getName());

            holder.fulfill.setTag(reward);
            holder.fulfill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Reward reward = (Reward)v.getTag();
                    reward.setDateFulfilled(new Date());
                    mDBHelper.updateRewardEarned((Reward)v.getTag());
                }
            });
        }


    }
}
