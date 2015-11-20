package com.tinakit.moveit.activity;

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

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.EditRewardRecyclerAdapter;

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
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentActivity));
        mEditRewardRecyclerAdapter = new EditRewardRecyclerAdapter(mFragmentActivity);
        mRecyclerView.setAdapter(mEditRewardRecyclerAdapter);

        return rootView;
    }
}
