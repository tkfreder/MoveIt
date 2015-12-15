package com.tinakit.moveit.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.ActivityTracker;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.ActivityType;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.model.UserActivity;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by Tina on 12/13/2015.
 */
public class ActivityChooser  extends Fragment {


    // CONSTANTS
    public static final String USER_ACTIVITY_LIST = "USER_ACTIVITY_LIST";
    protected static List<ActivityType> mActivityTypeList;
    public static ActivityDetail mActivityDetail = new ActivityDetail();
    protected FragmentActivity mFragmentActivity;
    private View rootView;
    ArrayList<UserActivity> mUserActivityList = new ArrayList<>();

    // UI COMPONENTS
    protected RecyclerView mRecyclerView;
    public static MultiChooserRecyclerAdapter mRecyclerViewAdapter;
    protected Button mNextButton;

    //database
    FitnessDBHelper mDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.activity_chooser, container, false);

        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //get databaseHelper instance
        mDatabaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);

        initializeRecyclerView();

        setActionListeners();

        return rootView;
    }

    private void setActionListeners(){

        mNextButton = (Button)rootView.findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mFragmentActivity, ActivityTracker.class);
                intent.putParcelableArrayListExtra(USER_ACTIVITY_LIST, mUserActivityList);
                startActivity(intent);
            }
        });
    }

    private void initializeRecyclerView(){

        // Get userlist
        List<User> userList = mDatabaseHelper.getUsers();
        mActivityTypeList = mDatabaseHelper.getActivityTypes();

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mFragmentActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerViewAdapter = new MultiChooserRecyclerAdapter(mFragmentActivity, userList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

    }

    public class MultiChooserRecyclerAdapter extends RecyclerView.Adapter<MultiChooserRecyclerAdapter.CustomViewHolder> {

        private Context mContext;
        private List<User> mUserList;
        String[] activityList;


        public MultiChooserRecyclerAdapter(Context context, List<User> userList) {

            mContext = context;
            mUserList = userList;

            //initialize string array for ActivityType list
            activityList = new String[mActivityTypeList.size() + 1];

            //add non-participating option
            activityList[0] = "Not participating";

            for ( int j = 0; j < mActivityTypeList.size(); j++){

                activityList[j + 1] = mActivityTypeList.get(j).getActivityName();
            }

        }

        @Override
        public int getItemCount() {
            return (null != mUserList ? mUserList.size() : 0);
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {

            ImageView avatar;
            TextView userName;
            MaterialSpinner activitySpinner;

            public CustomViewHolder(View view) {

                super(view);
                this.avatar = (ImageView)view.findViewById(R.id.avatar);
                this.userName = (TextView)view.findViewById(R.id.userName);
                this.activitySpinner = (MaterialSpinner)view.findViewById(R.id.activitySpinner);

            }
        }

        @Override
        public MultiChooserRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            //View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.participant_activity_list_item, null);
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.participant_activity_list_item, viewGroup, false);

            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MultiChooserRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

            User user = mUserList.get(i);

            // Populate data from ActivityType data object
            customViewHolder.avatar.setImageResource(getResources().getIdentifier(user.getAvatarFileName(), "drawable", mFragmentActivity.getPackageName()));
            customViewHolder.userName.setText(user.getUserName());

            // set tag on radio group
            customViewHolder.activitySpinner.setTag(user);


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, activityList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            customViewHolder.activitySpinner.setAdapter(adapter);

            customViewHolder.activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    User user = (User) parent.getTag();
                    UserActivity userActivity = new UserActivity(user);


                    // if non-participant was chosen, remove this user from UserActivityList if user is on the list
                    if (position == 0){

                        // if user exists on participant list, remove the user
                        if (mUserActivityList.size() > 0 && mUserActivityList.contains(userActivity)){

                            mUserActivityList.remove(mUserActivityList.indexOf(userActivity));
                        }
                    }
                    // if the user already exists, remove it, add new activity type for this user
                    else if (mUserActivityList.size() > 0 && mUserActivityList.contains(userActivity)) {

                        //add the activitytype if it's not "non-participating"
                        //decrement index in order to compensate for addition of "Not participating" option at index = 0, see line 169
                        userActivity.setActivityType(mActivityTypeList.get(position - 1));
                        mUserActivityList.remove(mUserActivityList.indexOf(userActivity));
                        mUserActivityList.add(userActivity);
                    }
                    // if user does not already exist, add the user to the list
                    else{

                        //add the activitytype if it's not "non-participating"
                        //decrement index in order to compensate for addition of "Not participating" option at index = 0, see line 169
                        userActivity.setActivityType(mActivityTypeList.get(position - 1));
                        mUserActivityList.add(userActivity);
                    }

                    // enable/disable Next Button
                    setButtonState(mUserActivityList);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                    //no change, don't do anything
                }
            });
        }
    }

    private void setButtonState(List<UserActivity> userActivityList){

        //check if at least one user is participating, show Next button
        if (userActivityList.size() > 0){

            mNextButton.setEnabled(true);
            mNextButton.setBackgroundColor(ContextCompat.getColor(mFragmentActivity, R.color.green));
        }
        else{

            mNextButton.setEnabled(false);
            mNextButton.setBackgroundColor(ContextCompat.getColor(mFragmentActivity, R.color.grey));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
