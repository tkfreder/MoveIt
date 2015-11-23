package com.tinakit.moveit.fragment;

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
import com.tinakit.moveit.adapter.RewardRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.User;

import java.util.List;

/**
 * Created by Tina on 9/23/2015.
 */
public class RewardViewFragment extends Fragment {

    FragmentActivity mFragmentActivity;

    //UI Widgets
    private TextView mUserName;
    private RecyclerView mRecyclerView;
    private ImageView mAvatar;
    private TextView mMessage;
    private TextView mTotalCoins_textview;
    private RewardRecyclerAdapter mRewardRecyclerAdapter;

    User mUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentActivity  = (FragmentActivity)    super.getActivity();
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View rootView = inflater.inflate(R.layout.reward_view, container, false);

        //wire up UI components
        mUserName = (TextView)rootView.findViewById(R.id.username);
        mAvatar = (ImageView)rootView.findViewById(R.id.avatar);
        mTotalCoins_textview = (TextView)rootView.findViewById(R.id.coinTotal);
        mMessage = (TextView)rootView.findViewById(R.id.message);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);

        //if this is a refresh of the screen, get the userId
        if (getArguments().containsKey("user")){

            mUser = getArguments().getParcelable("user");

            displayRewards();
        }

        return rootView;
    }

    private void displayRewards(){

        mUserName.setText(mUser.getUserName());
        mAvatar.setImageResource(getResources().getIdentifier(mUser.getAvatarFileName(), "drawable", mFragmentActivity.getPackageName()));

        //TODO: check points are rounding in a consistent way throughout code, including updating DB
        mTotalCoins_textview.setText(String.format("%d", mUser.getPoints()));

        List<Reward> rewardList = FitnessDBHelper.getInstance(mFragmentActivity).getAllRewards();

        if (rewardList.size() != 0){

            //display message if not enough coins to redeem reward
            if (rewardList.get(0).getPoints() > mUser.getPoints()){

                mMessage.setVisibility(View.VISIBLE);
                mMessage.setText("You need " + String.valueOf(rewardList.get(0).getPoints() - mUser.getPoints()) + " more coins to get a reward.");
            }
        }

        //RecyclerView
        // Initialize recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentActivity));
        mRewardRecyclerAdapter = new RewardRecyclerAdapter(mFragmentActivity, mUser);
        mRecyclerView.setAdapter(mRewardRecyclerAdapter);

    }

}