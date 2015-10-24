package com.tinakit.moveit.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.RewardRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tina on 9/23/2015.
 */
public class RewardView extends AppCompatActivity {

    //UI Widgets
    private RecyclerView mRecyclerView;
    private TextView mMessage;
    private RadioGroup mUserList_RadioGroup;
    private TextView mTotalCoins_textview;
    private RewardRecyclerAdapter mRewardRecyclerAdapter;

    FitnessDBHelper mDatabaseHelper;
    List<User> mUserList;
    User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.reward_view);

        //wire up UI components
        initialize();

        //get Users to populate radio group
        mUserList = mDatabaseHelper.getUsers();

        if (mUserList != null) {
            //if this is a refresh of the screen, get the userId
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().containsKey("user")) {
                    mUser = getIntent().getExtras().getParcelable("user");
                    getIntent().getExtras().clear();
                }
            }

            //add radio buttons to userList_RadioGroup
            addUserListRadioButtons();

            if (mUser != null)
                displayRewards();
        }

    }

    private void initialize(){

        mTotalCoins_textview = (TextView)findViewById(R.id.coinTotal);
        mMessage = (TextView)findViewById(R.id.message);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mUserList_RadioGroup = (RadioGroup)findViewById(R.id.userListRadioGroup);

        mDatabaseHelper = FitnessDBHelper.getInstance(getApplicationContext());
    }

    private void addUserListRadioButtons(){

        for ( User user : mUserList){

            ToggleableRadioButton radioButton = new ToggleableRadioButton(this);
            radioButton.setText(user.getUserName());
            radioButton.setTag(user);
            radioButton.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            /*
            if (mUser != null && user.equals(mUser)){

                    radioButton.setChecked(true);
            }
*/

            mUserList_RadioGroup.addView(radioButton);
        }



        //set checked listener
        mUserList_RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton radioButton = (RadioButton)group.findViewById(group.getCheckedRadioButtonId());
                mUser = (User)radioButton.getTag();
                displayRewards();

            }
        });
    }

    private void displayRewards(){

        //TODO: check points are rounding in a consistent way throughout code, including updating DB
        mTotalCoins_textview.setText(String.format("%d", mUser.getPoints()));

        List<Reward> rewardList = mDatabaseHelper.getAllRewards();

        if (rewardList.size() != 0){

            //display message if not enough coins to redeem reward
            if (rewardList.get(0).getPoints() > mUser.getPoints()){

                mMessage.setText("You need " + String.valueOf(rewardList.get(0).getPoints() - mUser.getPoints()) + " more coins to get a reward.");
            }
        }

        //RecyclerView
        // Initialize recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRewardRecyclerAdapter = new RewardRecyclerAdapter(this, mUser);
        mRecyclerView.setAdapter(mRewardRecyclerAdapter);

    }

    public class ToggleableRadioButton extends RadioButton {

        public ToggleableRadioButton(Context context) {
            super(context);
        }

        @Override
        public void toggle() {
            if(isChecked()) {
                if(getParent() instanceof RadioGroup) {
                    ((RadioGroup)getParent()).clearCheck();
                }
            } else {
                setChecked(true);
            }
        }
    }
}