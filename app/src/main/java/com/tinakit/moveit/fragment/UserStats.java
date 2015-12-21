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
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.RewardView;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;

import java.util.List;

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
    public static UserStatsRecyclerAdapter mRecyclerViewAdapter;

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
        mRecyclerViewAdapter = new UserStatsRecyclerAdapter(mFragmentActivity, mUserList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

    }

    public class UserStatsRecyclerAdapter extends RecyclerView.Adapter<UserStatsRecyclerAdapter.CustomViewHolder> {

        private Context mContext;
        private List<User> mUserList;

        public UserStatsRecyclerAdapter(Context context, List<User> userList) {

            mContext = context;
            mUserList = userList;
        }

        @Override
        public int getItemCount() {
            return (null != mUserList ? mUserList.size() : 0);
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {

            ImageView avatar;
            TextView points;
            //Button viewReward;

            public CustomViewHolder(View view) {

                super(view);
                this.avatar = (ImageView)view.findViewById(R.id.avatar);
                this.points = (TextView)view.findViewById(R.id.points);
                //this.viewReward = (Button)view.findViewById(R.id.viewReward);

            }
        }

        @Override
        public UserStatsRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stat_list_item_parent, viewGroup, false);

            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(UserStatsRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

            User user = mUserList.get(i);

            // Populate data from ActivityType data object
            customViewHolder.avatar.setImageResource(getResources().getIdentifier(user.getAvatarFileName(), "drawable", mFragmentActivity.getPackageName()));
            customViewHolder.points.setText(String.valueOf(user.getPoints()));

            // set tag on radio group
            //customViewHolder.viewReward.setTag(user);
/*
            customViewHolder.viewReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    User user = (User)v.getTag();
                    bundle.putParcelable("user", user);

                    Intent intent = new Intent(mFragmentActivity, RewardView.class);
                    intent.putExtra(USER_STATS_LIST,  bundle);
                    startActivity(intent);

                }
            });

            */
        }
    }


}
