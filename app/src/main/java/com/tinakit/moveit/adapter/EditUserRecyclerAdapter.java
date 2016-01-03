package com.tinakit.moveit.adapter;

import android.content.Context;
import android.content.Intent;
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

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.activity.PickAvatar;
import com.tinakit.moveit.fragment.UserProfile;
import com.tinakit.moveit.model.User;

import java.util.List;

/**
 * Created by Tina on 12/31/2015.
 */
public class EditUserRecyclerAdapter extends RecyclerView.Adapter<EditUserRecyclerAdapter.CustomViewHolder>  {

    private Context mContext;
    private FragmentActivity mActivity;
    private List<User> mUserList;
    private FitnessDBHelper mDatabaseHelper;
    public static final int AVATAR_FILENAME = 1;


    public EditUserRecyclerAdapter(Context context, FragmentActivity activity, List<User> userList) {

        mContext = context;
        mActivity = activity;
        mUserList = userList;

        mDatabaseHelper = FitnessDBHelper.getInstance(mContext);

    }

    public void setList(List<User> userList){

        mUserList = userList;
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
        customViewHolder.isAdmin.setChecked(user.isAdmin());
        customViewHolder.weight.setText(String.valueOf(Math.round(user.getWeight())));
        customViewHolder.avatar.setImageResource(mContext.getResources().getIdentifier(user.getAvatarFileName(), "drawable", mActivity.getPackageName()));

        customViewHolder.avatarButton.setTag(user);

        // populate list with filenames
        //List<String> avatarFileNames = Arrays.asList(mContext.getResources().getStringArray(R.array.avatar_images));


        customViewHolder.avatarButton.setOnClickListener(new View.OnClickListener() {
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

        //save current User data in updateButton
        customViewHolder.updateButton.setTag(user);
        customViewHolder.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user = (User) view.getTag();

                //get remaining values for User from form in case it was changed
                user.setUserName(customViewHolder.userName.getText().toString());
                user.setIsAdmin(customViewHolder.isAdmin.isChecked());
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

    }

    @Override
    public int getItemCount() {
            return (null != mUserList ? mUserList.size() : 0);
            }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected EditText userName;
        protected CheckBox isAdmin;
        protected EditText weight;
        protected ImageView avatar;
        protected Button avatarButton;
        protected Button updateButton;

        public CustomViewHolder(View view) {
            super(view);

            this.userName = (EditText)view.findViewById(R.id.userName);
            this.isAdmin = (CheckBox)view.findViewById(R.id.isAdmin);
            this.weight = (EditText)view.findViewById(R.id.weight);
            this.avatar = (ImageView) view.findViewById(R.id.avatar);
            this.avatarButton = (Button) view.findViewById(R.id.avatarButton);
            this.updateButton = (Button) view.findViewById(R.id.updateButton);
        }
    }

}

