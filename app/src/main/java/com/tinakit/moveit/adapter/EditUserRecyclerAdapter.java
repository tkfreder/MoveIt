package com.tinakit.moveit.adapter;

import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.activity.PickAvatar;
import com.tinakit.moveit.fragment.EditUser;
import com.tinakit.moveit.fragment.UserProfile;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 12/31/2015.
 */
public class EditUserRecyclerAdapter extends RecyclerView.Adapter<EditUserRecyclerAdapter.CustomViewHolder>  {

    // CONSTANTS
    public static final int AVATAR_FILENAME = 1;

    @Inject
    FitnessDBHelper mDatabaseHelper;
    //private FitnessDBHelper mDatabaseHelper;

    private Context mContext;
    private FragmentActivity mActivity;
    private List<User> mUserList;


    public EditUserRecyclerAdapter(Context context, FragmentActivity activity, List<User> userList) {

        mContext = context;
        mActivity = activity;
        mUserList = userList;

        //mDatabaseHelper = FitnessDBHelper.getInstance(mContext);

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
    public EditUserRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_user_list_item, viewGroup, false);

            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
    }
    @Override
    public void onBindViewHolder(final EditUserRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        User user = mUserList.get(i);

        customViewHolder.userName.setText(user.getUserName());
        customViewHolder.isAdmin.setVisibility(user.isAdmin() ? View.VISIBLE : View.GONE);
        customViewHolder.weight.setText(String.valueOf(Math.round(user.getWeight())));
        customViewHolder.avatar.setImageResource(mContext.getResources().getIdentifier(user.getAvatarFileName(), "drawable", mActivity.getPackageName()));

        customViewHolder.avatar.setTag(user);
        customViewHolder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //save the user in bundle
            Bundle args = new Bundle();
            args.putParcelable(PickAvatar.PICK_AVATAR_KEY_USER, (User)v.getTag());

            Intent intent = new Intent(mContext, PickAvatar.class);
            intent.putExtras(args);
            mActivity.startActivityForResult(intent,AVATAR_FILENAME);
            }
        });

        //save current User data in deleteButton
        customViewHolder.deleteButton.setTag(user);
        customViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = (User)v.getTag();

                //TODO: display dialog, asking if you're sure you want to delete this user
                String message = mActivity.getResources().getString(R.string.confirm_delete_message)  + user.getUserName();


            }
        });

        //save current User data in editButton
        customViewHolder.editButton.setTag(user);
        customViewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check whether UserProfile is already visible
                EditUser editUser = (EditUser)mActivity.getSupportFragmentManager().findFragmentByTag(EditUser.EDIT_USER_TAG);
                if (editUser == null){

                    editUser= new EditUser();
                    FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.fragmentContainer, editUser, EditUser.EDIT_USER_TAG);
                    transaction.commit();

                    //mActivity.getActionBar().setTitle(mContext.getResources().getString(R.string.user_profiles));
                }

            }
        });


        /*
        customViewHolder.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            User user = (User) view.getTag();

            //get remaining values for User from form in case it was changed
            user.setUserName(customViewHolder.userName.getText().toString());
            user.setIsAdmin(customViewHolder.isAdmin.getVisibility() == View.VISIBLE ? true : false );
            user.setWeight(Float.parseFloat(customViewHolder.weight.getText().toString()));
            // avatar filename is saved in onclicklistener of avatar listview

            mDatabaseHelper.updateUser(user);

            // redirect back to User Profile

            // check whether UserProfile is already visible
            UserProfile userProfile = (UserProfile)mActivity.getSupportFragmentManager().findFragmentByTag(UserProfile.USER_PROFILE_TAG);
            if (userProfile == null){

                userProfile= new UserProfile ();
                FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.fragmentContainer, userProfile, UserProfile.USER_PROFILE_TAG);
                transaction.commit();

                //mActivity.getActionBar().setTitle(mContext.getResources().getString(R.string.user_profiles));
            }
            }
        });
        */

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

        public CustomViewHolder(View view) {
            super(view);

            this.userName = (TextView)view.findViewById(R.id.userName);
            this.isAdmin = (TextView)view.findViewById(R.id.isAdmin);
            this.weight = (TextView)view.findViewById(R.id.weight);
            this.avatar = (ImageView) view.findViewById(R.id.avatar);
            this.deleteButton = (ImageView) view.findViewById(R.id.deleteButton);
            this.editButton = (ImageView) view.findViewById(R.id.editButton);

        }
    }

}

