package com.tinakit.moveit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.PickAvatarRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.User;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tina on 1/1/2016.
 */
public class PickAvatar extends AppCompatActivity {

    // CONSTANTS
    public static final String PICK_AVATAR_TAG = "PICK_AVATAR";
    public static final String PICK_AVATAR_KEY_USER = "user";
    public static final int PICK_AVATAR_REQUEST_CODE = 1;

    // local cache
    protected static List<Reward> mRewardList;
    protected FragmentActivity mFragmentActivity;
    protected User mUser;

    // UI COMPONENTS
    protected RecyclerView mRecyclerView;
    protected PickAvatarRecyclerAdapter mPickAvatarRecyclerAdapter;

    //database
    FitnessDBHelper mDatabaseHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //get databaseHelper instance
        mDatabaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);

        // fetch data before initializing UI
        fetchData();

        initializeUI();


    }

    private void fetchData(){

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(PICK_AVATAR_KEY_USER)){

            mUser = bundle.getParcelable(PICK_AVATAR_KEY_USER);

        }

    }

    private void initializeUI(){

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mFragmentActivity);
        //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));




        // get array list of avatar filenames from string array resource
        List<String> avatarFileList = Arrays.asList(getResources().getStringArray(R.array.avatar_images));

        mPickAvatarRecyclerAdapter = new PickAvatarRecyclerAdapter(getApplicationContext(), this, avatarFileList, mUser);
        mRecyclerView.setAdapter(mPickAvatarRecyclerAdapter);
    }
}
