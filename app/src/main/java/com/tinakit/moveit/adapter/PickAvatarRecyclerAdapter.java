package com.tinakit.moveit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.MainActivity;
import com.tinakit.moveit.activity.PickAvatar;
import com.tinakit.moveit.model.User;

import java.util.List;

/**
 * Created by Tina on 1/1/2016.
 */
public class PickAvatarRecyclerAdapter extends RecyclerView.Adapter<PickAvatarRecyclerAdapter.CustomViewHolder>  {

    private Context mContext;
    private Activity mActivity;
    private List<String> mAvatarFileList;
    private User mUser;


    public PickAvatarRecyclerAdapter(Context context, Activity activity, List<String> avatarFileList, User user) {

        mContext = context;
        mActivity = activity;
        mAvatarFileList = avatarFileList;
        mUser = user;

    }


    @Override
    public PickAvatarRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.avatar_list_item, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final PickAvatarRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

        String avatarFileName = mAvatarFileList.get(i);

        customViewHolder.avatar.setImageResource(mContext.getResources().getIdentifier(avatarFileName, "drawable", mActivity.getPackageName()));
        //customViewHolder.avatar.setImageResource(getResources().getIdentifier(user.getAvatarFileName(), "drawable", mFragmentActivity.getPackageName()));

        customViewHolder.avatar.setTag(avatarFileName);

        customViewHolder.avatar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mUser.setAvatarFileName((String)v.getTag());

                Intent intent = new Intent(mContext, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(PickAvatar.PICK_AVATAR_KEY_USER, mUser);
                intent.putExtras(bundle);
                mActivity.setResult(mActivity.RESULT_OK, intent);
                mActivity.finish();
            }
        });


    }

    @Override
    public int getItemCount() {
        return (null != mAvatarFileList ? mAvatarFileList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView avatar;


        public CustomViewHolder(View view) {
            super(view);

            this.avatar = (ImageView) view.findViewById(R.id.avatar);

        }
    }

}

