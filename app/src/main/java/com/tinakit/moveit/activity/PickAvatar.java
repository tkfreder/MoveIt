package com.tinakit.moveit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.PickAvatarRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 1/1/2016.
 */
public class PickAvatar extends Fragment {

    // CONSTANTS
    public static final String PICK_AVATAR_TAG = "PICK_AVATAR";
    public static final String PICK_AVATAR_KEY_USER = "user";
    public static final int PICK_AVATAR_REQUEST_CODE = 1;

    // local cache
    protected FragmentActivity mFragmentActivity;
    private View rootView;
    protected static List<Reward> mRewardList;
    protected User mUser;

    // UI COMPONENTS
    protected RecyclerView mRecyclerView;
    protected PickAvatarRecyclerAdapter mPickAvatarRecyclerAdapter;

    @Inject
    FitnessDBHelper mDatabaseHelper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.recycler_view, container, false);
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // fetch data before initializing UI
        fetchData();

        initializeUI();

        return rootView;
    }

    private void fetchData(){

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null && bundle.containsKey(PICK_AVATAR_KEY_USER)){

            mUser = bundle.getParcelable(PICK_AVATAR_KEY_USER);

        }

    }

    private void initializeUI(){

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mFragmentActivity);
        //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));




        // get array list of avatar filenames from string array resource
        List<String> avatarFileList = Arrays.asList(getResources().getStringArray(R.array.avatar_images));

        mPickAvatarRecyclerAdapter = new PickAvatarRecyclerAdapter(getActivity(), mFragmentActivity, avatarFileList, mUser);
        mRecyclerView.setAdapter(mPickAvatarRecyclerAdapter);
    }
}
