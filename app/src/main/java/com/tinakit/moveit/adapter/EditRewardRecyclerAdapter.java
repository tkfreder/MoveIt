package com.tinakit.moveit.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.model.UserListObservable;
import com.tinakit.moveit.module.CustomApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

/**
 * Created by Tina on 1/7/2016.
 */
public class EditRewardRecyclerAdapter extends RecyclerView.Adapter<EditRewardRecyclerAdapter.CustomViewHolder>  {

    @Inject
    FitnessDBHelper mDatabaseHelper;

    Context mContext;
    private FragmentActivity mFragmentActivity;
    private List<Reward> mRewardList;
    private List<User> mUserList;

    Map<Integer,Reward> mRewardMap;

    public EditRewardRecyclerAdapter(Context context, FragmentActivity fragmentActivity, List<Reward> rewardList, List<User> userList) {
        mContext = context;
        mFragmentActivity = fragmentActivity;
        mRewardList = rewardList;
        mUserList = userList;
        // inject FitnessDBHelper
        ((CustomApplication)mFragmentActivity.getApplication()).getAppComponent().inject(this);
        // Get Activity Types
        //mRewardList = mDatabaseHelper.getAllRewards();
        mRewardMap = new TreeMap<Integer,Reward>();
        for (Reward r : mRewardList){
            mRewardMap.put(r.getRewardId(), r);
        }
    }

    public List<Reward> getRewardList(){
        mRewardList = new ArrayList<Reward>(mRewardMap.values());
        return mRewardList;
    }

    public void setUserList(List<User> userList){
        mUserList = userList;
    }

    @Override
    public EditRewardRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_reward_list_item, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final EditRewardRecyclerAdapter.CustomViewHolder customViewHolder, int i) {
        Reward reward = mRewardList.get(i);
        User theUser = null;

        for (User user : mUserList){
            if (user.getUserId() == reward.getUserId()){
                theUser = user;
                break;
            }
        }

        // Populate data from Reward data object
        int numPoints = reward.getPoints();

        //customViewHolder.avatar.setImageResource(mContext.getResources().getIdentifier(theUser.getAvatarFileName(), "drawable", mFragmentActivity.getPackageName()));
        customViewHolder.userName.setText(theUser.getUserName());

        customViewHolder.points.setTag(reward);
        customViewHolder.points.setText(String.valueOf(numPoints));
        customViewHolder.points.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Reward reward = (Reward)customViewHolder.points.getTag();
                reward.setPoints(!s.toString().equals("") ? Integer.parseInt(s.toString()) : 0 );
                mRewardMap.put(reward.getRewardId(),reward);

            }
        });

        customViewHolder.name.setTag(reward);
        customViewHolder.name.setText(reward.getName());
        customViewHolder.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Reward reward = (Reward)customViewHolder.points.getTag();
                reward.setName(s.toString());
                mRewardMap.put(reward.getRewardId(),reward);

            }
        });

/*
        customViewHolder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Reward reward = (Reward)customViewHolder.itemView.getTag();

                mDatabaseHelper.updateReward(reward.getRewardId(), customViewHolder.name.getText().toString(), Integer.parseInt(customViewHolder.points.getText().toString()));

                //refresh by redirecting to EditReward
                EditReward editReward = new EditReward();
                mFragmentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, editReward).commit();
            }
        });
        */

        /*
        customViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mReward = (Reward)v.getTag();

                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        //set message, title, and icon
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this reward?")
                                //.setIcon(R.drawable.delete)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //your deleting code

                                mDatabaseHelper.deleteReward(mReward.getRewardId());
                                dialog.dismiss();
                            }

                        })

                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        })
                        .create();

                alertDialog.show();

            }
        });
*/

    }

    @Override
    public int getItemCount() {
        return (null != mRewardList ? mRewardList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        //protected ImageView avatar;
        protected TextView userName;
        protected EditText points;
        protected EditText name;
        //protected ImageView saveButton;
        //ImageView deleteButton;

        public CustomViewHolder(View view) {
            super(view);

            //this.avatar = (ImageView)view.findViewById(R.id.avatar);
            this.userName = (TextView)view.findViewById(R.id.userName);
            this.points = (EditText) view.findViewById(R.id.points);
            this.name = (EditText) view.findViewById(R.id.name);
            //this.saveButton = (ImageView) view.findViewById(R.id.saveButton);
            //this.deleteButton = (ImageView) view.findViewById(R.id.delete);
        }
    }

}