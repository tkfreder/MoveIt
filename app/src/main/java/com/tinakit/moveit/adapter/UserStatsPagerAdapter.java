package com.tinakit.moveit.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tinakit.moveit.fragment.UserStats;
import com.tinakit.moveit.model.User;

import java.util.List;

/**
 * Created by Tina on 1/24/2016.
 */
public class UserStatsPagerAdapter extends FragmentStatePagerAdapter {

    List<User> mUserList;

    public UserStatsPagerAdapter(FragmentManager fm, List<User> userList) {
        super(fm);
        mUserList = userList;

    }

    @Override
    public Fragment getItem(int i) {

        Fragment fragment = new UserStats();
        Bundle args = new Bundle();
        args.putParcelable(UserStats.USER_STATS_ARG_USER, mUserList.get(i));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return mUserList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mUserList.get(position).getUserName();
    }
}