package com.tinakit.moveit.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.EditRewardRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.module.CustomApplication;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 10/4/2015.
 */
public class EditRewardFragment extends Fragment {

    private FragmentActivity mFragmentActivity;
    //RecyclerView
    private RecyclerView mRecyclerView;
    private EditRewardRecyclerAdapter mEditRewardRecyclerAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentActivity  = (FragmentActivity)super.getActivity();
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View rootView = inflater.inflate(R.layout.edit_reward, container, false);

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.reward_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentActivity));
        mEditRewardRecyclerAdapter = new EditRewardRecyclerAdapter(getContext(), mFragmentActivity);
        mRecyclerView.setAdapter(mEditRewardRecyclerAdapter);

        return rootView;
    }


}
