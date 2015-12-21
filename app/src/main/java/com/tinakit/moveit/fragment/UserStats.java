package com.tinakit.moveit.fragment;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.RewardView;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.RewardStatusType;
import com.tinakit.moveit.model.User;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Tina on 12/19/2015.
 */
public class UserStats extends Fragment {

    // CONSTANTS
    public static final String USER_STATS_LIST = "USER_STATS_LIST";

    // local cache
    protected static List<User> mUserList;
    protected FragmentActivity mFragmentActivity;
    private View rootView;

    // UI COMPONENTS
    protected RecyclerView mRecyclerView;
    public static UserStatsExpandableAdapter mUserStatsExpandableAdapter;

    //database
    FitnessDBHelper mDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.user_stats_list, container, false);

        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //get databaseHelper instance
        mDatabaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);

        initializeUI();

        fetchData();

        return rootView;
    }

    private void fetchData(){

        // get UserActivityList from intent

        Bundle bundle = this.getArguments();
        if (bundle.containsKey(USER_STATS_LIST)){

            mUserList = bundle.getParcelableArrayList(USER_STATS_LIST);

            // if this is the first time, there will be data in the bundle
            if (mUserList == null){

                // fetch directly from the database
                mUserList = mDatabaseHelper.getUsers();
            }
        }
    }

    private void initializeUI(){

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mFragmentActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mUserStatsExpandableAdapter = new UserStatsExpandableAdapter(mFragmentActivity, mUserList);
        mRecyclerView.setAdapter(mUserStatsExpandableAdapter);

    }

    public class UserStatsExpandableAdapter extends ExpandableRecyclerAdapter<UserStatsExpandableAdapter.RewardParentViewHolder, UserStatsExpandableAdapter.RewardChildViewHolder> {

        private Context mContext;
        private List<User> mUserList;
        private LayoutInflater mInflater;
        private User mCurrentUser;


        public UserStatsExpandableAdapter(Context context, List<User> userList) {
            super(userList);
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

            rewardParentViewHolder.avatar.setImageResource(getResources().getIdentifier(user.getAvatarFileName(), "drawable", mFragmentActivity.getPackageName()));
            rewardParentViewHolder.points.setText(String.valueOf(user.getPoints()));

        }

        @Override
        public void onBindChildViewHolder(RewardChildViewHolder rewardChildViewHolder, int i, Object object) {

            Reward reward = (Reward)object;

            rewardChildViewHolder.rewardPoints.setText(String.valueOf(reward.getPoints()));
            rewardChildViewHolder.name.setText(reward.getName());

            //if user has enough points, enable this button
            if (reward.getPoints() <= mCurrentUser.getPoints() && reward.getRewardStatusType() == RewardStatusType.AVAILABLE) {
                rewardChildViewHolder.statusButton.setText("Get It");
                rewardChildViewHolder.statusButton.setTag(reward);
                rewardChildViewHolder.statusButton.setVisibility(View.VISIBLE);


            } else if (reward.getRewardStatusType() == RewardStatusType.PENDING) {

                rewardChildViewHolder.statusButton.setText("Cancel");
                rewardChildViewHolder.statusButton.setTag(reward);
                rewardChildViewHolder.statusButton.setVisibility(View.VISIBLE);
                rewardChildViewHolder.status.setText("in progress");

            } else if (reward.getRewardStatusType() == RewardStatusType.DENIED) {

                rewardChildViewHolder.statusButton.setEnabled(false);
                rewardChildViewHolder.statusButton.setTag(reward);
                rewardChildViewHolder.status.setText("Mommy said no to this.");
            }
        }



        @Override
        public int getItemCount() {
            return (null != mUserList ? mUserList.size() : 0);
        }

        public class RewardParentViewHolder extends ParentViewHolder {

            public ImageView avatar;
            public TextView points;
            public ImageButton expandArrow;

            public RewardParentViewHolder(View view) {

                super(view);
                this.avatar = (ImageView)view.findViewById(R.id.avatar);
                this.points = (TextView)view.findViewById(R.id.points);
                this.expandArrow = (ImageButton)view.findViewById(R.id.expandArrow);
            }
        }

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
        }
    }
}
