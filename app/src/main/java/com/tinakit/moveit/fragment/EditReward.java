package com.tinakit.moveit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.EditRewardRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.RewardListObservable;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.model.UserListObservable;
import com.tinakit.moveit.module.CustomApplication;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

/**
 * Created by Tina on 10/4/2015.
 */
public class EditReward extends Fragment implements Observer {

    public static final String EDIT_REWARD_TAG = "EDIT_REWARD_TAG";
    public static final String EDIT_REWARD_USER = "EDIT_REWARD_USER";

    @Inject
    FitnessDBHelper mDatabaseHelper;

    private FragmentActivity mFragmentActivity;
    private List<User> mUserList;
    private List<Reward> mRewardList;
    private UserListObservable mUserListObservable;

    // UI widgets
    private View mRootView;
    private EditRewardRecyclerAdapter mEditRewardRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private Button mSaveButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentActivity  = (FragmentActivity)super.getActivity();
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mRootView = inflater.inflate(R.layout.edit_reward, container, false);

        // Dagger 2 injection
        ((CustomApplication)getActivity().getApplication()).getAppComponent().inject(this);

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mFragmentActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // get list of Users
        //mUserList = mDatabaseHelper.getUsers();
        CustomApplication app = ((CustomApplication)mFragmentActivity.getApplication());
        mUserListObservable = app.getUserListObservable();
        mUserListObservable.addObserver(this);
        mUserList = mUserListObservable.getValue();

        //mRewardList = mDatabaseHelper.getAllRewards();
        mRewardList = app.getRewardListObservable().getValue();

        mEditRewardRecyclerAdapter = new EditRewardRecyclerAdapter(inflater.getContext(), mFragmentActivity, mRewardList, mUserList);
        mRecyclerView.setAdapter(mEditRewardRecyclerAdapter);
        mSaveButton = (Button)mRootView.findViewById(R.id.saveButton);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // changes tracked by adapter, get latest data
                List<Reward> rewardList = mEditRewardRecyclerAdapter.getRewardList();
                int rowsAffected = mDatabaseHelper.updateAllRewards(rewardList);
                if(rowsAffected > 0){
                    Snackbar.make(mRootView.findViewById(R.id.main_layout), getString(R.string.message_update_reward), Snackbar.LENGTH_LONG)
                            .show();
                }
                else{
                    Snackbar.make(mRootView.findViewById(R.id.main_layout), getString(R.string.error_message_update_reward), Snackbar.LENGTH_LONG)
                            .show();
                }
                //check whether user does not already have a reward.  if so, check whether user has enough points to earn their reward
                //List<User> userList = mDatabaseHelper.getUsers();
                for (User user : mUserList){
                    boolean isFulfilled = false;
                    Reward reward = mDatabaseHelper.getRewardEarned(user.getUserId(), isFulfilled);
                    // update user total points, if we were able to get a valid point value for the reward
                    if (reward != null) {
                        EditUser.checkRewardEarned(user, mDatabaseHelper);
                    }
                }
            }
        });
        return mRootView;
    }

    @Override
    public void update(Observable observable, Object data) {
        List<User> userList = (List<User>)data;
        List<Reward> rewardList = mDatabaseHelper.getAllRewards();
        //CustomApplication app = ((CustomApplication)mFragmentActivity.getApplication());
        //app.getRewardListObservable().setValue(rewardList);
        mEditRewardRecyclerAdapter.setLists(userList, rewardList);
        mEditRewardRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        mUserListObservable.deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
