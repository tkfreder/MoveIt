package com.tinakit.moveit.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.RewardView;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.activity.PickAvatar;
import com.tinakit.moveit.fragment.EditReward;
import com.tinakit.moveit.fragment.EditUser;
import com.tinakit.moveit.fragment.UserProfile;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 12/31/2015.
 */
public class UserProfileRecyclerAdapter extends RecyclerView.Adapter<UserProfileRecyclerAdapter.CustomViewHolder>  {

    // CONSTANTS
    public static final int AVATAR_FILENAME = 1;

    @Inject
    FitnessDBHelper mDatabaseHelper;

    private Context mContext;
    private FragmentActivity mActivity;
    private List<User> mUserList;
    private User mUser;
    private View mItemView;


    public UserProfileRecyclerAdapter(Context context, FragmentActivity activity, List<User> userList) {

        mContext = context;
        mActivity = activity;
        mUserList = userList;

        // Dagger 2 injection
        ((CustomApplication)activity.getApplication()).getAppComponent().inject(this);

    }

    public void setList(List<User> userList){

        mUserList = userList;
    }

    public List<User> getList(){

       return mUserList;
    }


    @Override
    public UserProfileRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_user_list_item, viewGroup, false);

            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
    }
    @Override
    public void onBindViewHolder(final UserProfileRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        User user = mUserList.get(i);

        customViewHolder.userName.setText(user.getUserName());

        customViewHolder.isAdmin.setVisibility(user.isAdmin() ? View.VISIBLE : View.GONE);

        customViewHolder.weight.setText(String.valueOf(Math.round(user.getWeight())));

        customViewHolder.avatar.setImageResource(mContext.getResources().getIdentifier(user.getAvatarFileName(), "drawable", mActivity.getPackageName()));

        //save current User data in deleteButton
        customViewHolder.deleteButton.setTag(user);
        customViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mUser = (User)v.getTag();

                AlertDialog alertDialog = new AlertDialog.Builder(
                        mActivity,
                        R.style.AlertDialogCustom_Destructive)
                        .setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener()
                {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // delete user
                                long rowsAffected = mDatabaseHelper.disableUser(mUser);
                                if (rowsAffected == 1){

                                    //refresh by redirecting to UserProfile
                                    UserProfile userProfile = new UserProfile();
                                    mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, userProfile).commit();
                                }
                            }
                        })
                        .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Cancel Action
                                //don't do anything

                            }
                        })
                        .setTitle(R.string.title_delete_user)
                        .setMessage(R.string.message_delete_user)
                        .show();


            }
        });

        //save current User data in editButton
        customViewHolder.editButton.setTag(user);
        customViewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditUser editUser= new EditUser();

                //save User changes
                Bundle bundle = new Bundle();
                bundle.putParcelable(EditUser.EDIT_USER_USER, (User)v.getTag());
                editUser.setArguments(bundle);

                mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, editUser, EditUser.EDIT_USER_TAG)
                .commit();

            }
        });

        customViewHolder.rewardButton.setTag(user);
        customViewHolder.rewardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditReward editReward = new EditReward();

                //save User changes
                Bundle bundle = new Bundle();
                bundle.putParcelable(EditReward.EDIT_REWARD_USER, (User)v.getTag());
                editReward.setArguments(bundle);

                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, editReward, EditReward.EDIT_REWARD_TAG)
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
            return (null != mUserList ? mUserList.size() : 0);
            }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView userName;
        protected TextView isAdmin;
        protected TextView weight;
        protected ImageView avatar;
        protected ImageView deleteButton;
        protected ImageView editButton;
        protected ImageView rewardButton;

        public CustomViewHolder(View view) {
            super(view);

            this.userName = (TextView)view.findViewById(R.id.userName);
            this.isAdmin = (TextView)view.findViewById(R.id.isAdmin);
            this.weight = (TextView)view.findViewById(R.id.weight);
            this.avatar = (ImageView) view.findViewById(R.id.avatar);
            this.deleteButton = (ImageView) view.findViewById(R.id.deleteButton);
            this.editButton = (ImageView) view.findViewById(R.id.editButton);
            this.rewardButton = (ImageView) view.findViewById(R.id.rewardButton);



        }
    }




}

