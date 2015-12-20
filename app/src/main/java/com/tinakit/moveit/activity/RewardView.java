package com.tinakit.moveit.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.RewardStatusType;
import com.tinakit.moveit.model.User;

import java.util.List;

/**
 * Created by Tina on 9/23/2015.
 */
public class RewardView  extends AppCompatActivity {

    // CACHE
    private User mUser;

    //UI Widgets
    private TextView mUserName;
    private RecyclerView mRecyclerView;
    private ImageView mAvatar;
    private TextView mMessage;
    private TextView mTotalCoins_textview;
    private RewardRecyclerAdapter mRewardRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward_view);
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initializeUI();

        fetchData();
    }


    private void initializeUI(){

        //wire up UI components
        mUserName = (TextView)findViewById(R.id.username);
        mAvatar = (ImageView)findViewById(R.id.avatar);
        mTotalCoins_textview = (TextView)findViewById(R.id.coinTotal);
        mMessage = (TextView)findViewById(R.id.message);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
    }

    private void fetchData(){


        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("user")){

            mUser = bundle.getParcelable("user");

            // if this is the first time, there will be data in the bundle
            if (mUser == null){

                // TODO: redirect to UserStats screen
            }
            else {

                displayRewards();
            }
        }


    }

    private void displayRewards(){

        mUserName.setText(mUser.getUserName());
        mAvatar.setImageResource(getResources().getIdentifier(mUser.getAvatarFileName(), "drawable", getPackageName()));

        //TODO: check points are rounding in a consistent way throughout code, including updating DB
        mTotalCoins_textview.setText(String.format("%d", mUser.getPoints()));

        List<Reward> rewardList = FitnessDBHelper.getInstance(this).getAllRewards();

        if (rewardList.size() != 0){

            //display message if not enough coins to redeem reward
            if (rewardList.get(0).getPoints() > mUser.getPoints()){

                mMessage.setVisibility(View.VISIBLE);
                mMessage.setText("You need " + String.valueOf(rewardList.get(0).getPoints() - mUser.getPoints()) + " more coins to get a reward.");
            }
        }

        //RecyclerView
        // Initialize recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRewardRecyclerAdapter = new RewardRecyclerAdapter(this, mUser);
        mRecyclerView.setAdapter(mRewardRecyclerAdapter);

    }

    public class RewardRecyclerAdapter extends RecyclerView.Adapter<RewardRecyclerAdapter.CustomViewHolder> {

        private Context mContext;
        //TODO: make private after building DB
        public List<Reward> mRewardList;

        private User mUser;

        public RewardRecyclerAdapter(Context context, User user) {
            mContext = context;
            mUser = user;

            // Get singleton instance of database
            FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(context);

            // Get Reward list
            mRewardList = databaseHelper.getUserRewards(mUser.getUserId());
        }

        @Override
        public RewardRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reward_list_item, viewGroup, false);

            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RewardRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

            Reward reward = mRewardList.get(i);

            // Populate data from Reward data object
            int numPoints = reward.getPoints();

            customViewHolder.rewardPoints.setText(String.valueOf(numPoints));
            customViewHolder.name.setText(reward.getName());
            customViewHolder.description.setText(" " + reward.getDescription());
            customViewHolder.itemView.setTag(reward);

            customViewHolder.statusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(mContext);

                    Button button = (Button)v;
                    if (button.getText().equals("Get It")){
                        Reward reward = (Reward)v.getTag();
                        mUser.setPoints(mUser.getPoints() - reward.getPoints());

                        //update user's points
                        databaseHelper.updateUser(mUser);

                        //update the reward status
                        databaseHelper.setRewardStatus(mUser.getUserId(), reward.getRewardId(), RewardStatusType.PENDING);

                    }
                    else if (button.getText().equals("Cancel")){

                        Reward reward = (Reward)v.getTag();
                        mUser.setPoints(mUser.getPoints() + reward.getPoints());

                        //update user's points
                        databaseHelper.updateUser(mUser);

                        //update the reward status
                        databaseHelper.setRewardStatus(mUser.getUserId(), reward.getRewardId(), RewardStatusType.AVAILABLE);

                    }

                    //TODO: create Service class and BroadcastReceiver to manage updates
                    //Service class has method which sends message to BroadcastReceiver that calls MainActivity.setupViewPager

                    /*
                    //TODO: is there another way to refresh the screen, ie notifydatasethaschanged?
                    ((Activity)mContext).finish();
                    Intent intent = new Intent(mContext, RewardViewFragment.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", mUser);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
*/

                    //notifyDataSetChanged();

                    //TODO
                    // put this reward item in the queue
                    // decrement points of the reward from TotalCoins
                    // set Reward.userStatus = 1
                    // change status for this reward
                    // refresh list to, enable/disable buttons to reflect new TotalCoins or display Pending status
                }
            });

            //if user has enough points, enable this button
            if (reward.getPoints() <= mUser.getPoints() && reward.getRewardStatusType() == RewardStatusType.AVAILABLE) {
                customViewHolder.statusButton.setText("Get It");
                customViewHolder.statusButton.setTag(reward);
                customViewHolder.statusButton.setVisibility(View.VISIBLE);


            } else if (reward.getRewardStatusType() == RewardStatusType.PENDING) {

                customViewHolder.statusButton.setText("Cancel");
                customViewHolder.statusButton.setTag(reward);
                customViewHolder.statusButton.setVisibility(View.VISIBLE);
                customViewHolder.status.setText("in progress");

            } else if (reward.getRewardStatusType() == RewardStatusType.DENIED) {

                customViewHolder.statusButton.setEnabled(false);
                customViewHolder.statusButton.setTag(reward);
                customViewHolder.status.setText("Mommy said no to this.");
            }
        }

        @Override
        public int getItemCount() {
            return (null != mRewardList ? mRewardList.size() : 0);
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {

            TextView rewardPoints;
            TextView name;
            TextView description;
            Button statusButton;
            TextView status;

            public CustomViewHolder(View view) {
                super(view);

                this.rewardPoints = (TextView) view.findViewById(R.id.rewardPoints);
                this.name = (TextView) view.findViewById(R.id.name);
                this.description = (TextView) view.findViewById(R.id.description);
                this.statusButton = (Button) view.findViewById(R.id.statusButton);
                this.status = (TextView) view.findViewById(R.id.status);


            }
        }

    }

}