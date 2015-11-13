package com.tinakit.moveit.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.ActivityType;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.model.UserActivity;

import java.util.ArrayList;
import java.util.List;


public class ActivityChooserFragment extends Fragment {

    private FragmentActivity mFragmentActivity;

    //cache
    private ActivityDetail mActivityDetail = new ActivityDetail();

    //UI Widgets
    private RecyclerView mRecyclerView;
    private MultiChooserRecyclerAdapter mMultiChooserRecyclerAdapter;
    private Button mNextButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentActivity  = (FragmentActivity)    super.getActivity();
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View rootView = inflater.inflate(R.layout.multi_activity_chooser, container, false);

        // Get singleton instance of database
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);
        List<User> userList = databaseHelper.getUsers();
        List<ActivityType> activityTypeList = databaseHelper.getActivityTypes();

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.

        // The number of Columns
        //mRecyclerView.setLayoutManager(new GridLayoutManager(mFragmentActivity, 2));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentActivity));

        mMultiChooserRecyclerAdapter = new MultiChooserRecyclerAdapter(mFragmentActivity, userList, activityTypeList);
        mRecyclerView.setAdapter(mMultiChooserRecyclerAdapter);

        //next button
        mNextButton = (Button)rootView.findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mActivityDetail.getUserActivityList().size() > 0){
                    //get users and activity type selected
                    Intent intent = new Intent(mFragmentActivity, ActivityTracker.class);
                    intent.putParcelableArrayListExtra("userActivityList", new ArrayList(mActivityDetail.getUserActivityList()));
                    startActivity(intent);
                }
                else {

                    Toast.makeText(getActivity(), "Please select an activity for at least one user.", Toast.LENGTH_LONG);
                }
            }
        });

        return rootView;
    }

    private class MultiChooserRecyclerAdapter extends RecyclerView.Adapter<MultiChooserRecyclerAdapter.CustomViewHolder> {

        private List<User> mUserList;
        private List<ActivityType> mActivityTypeList;
        private Context mContext;


        public MultiChooserRecyclerAdapter(Context context, List<User> userList, List<ActivityType> activityTypeList) {

            mContext = context;
            mUserList = userList;
            mActivityTypeList = activityTypeList;
        }

        @Override
        public MultiChooserRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.multi_activity_chooser_item, null);

            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MultiChooserRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

            User user = mUserList.get(i);

            // Populate data from ActivityType data object
            //customViewHolder.avatar.setImageResource(mContext.getResources().getIdentifier(user.getAvatarFileName(), "drawable", mContext.getPackageName()));
            customViewHolder.username.setText(user.getUserName());
            customViewHolder.activityTypes.setTag(user);

            //get string array of activity types
            List<String> activityTypeStringList = new ArrayList<>();

            //add an empty item called no participant
            activityTypeStringList.add(mContext.getResources().getString(R.string.not_participant));

            for ( ActivityType activityType : mActivityTypeList){
                activityTypeStringList.add(activityType.getActivityName());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_spinner_item, activityTypeStringList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            customViewHolder.activityTypes.setAdapter(dataAdapter);

            //Handle click event on spinner
            customViewHolder.activityTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    User user = (User) parent.getTag();
                    //TODO: due to adding the first item in spinner as empty selection, must increment the index to get the correct activitytype
                    UserActivity userActivity = new UserActivity(user);

                    //check if "not participant" was selected
                    if (position == 0) {

                        //remove this user, if exists
                        mActivityDetail.getUserActivityList().remove(userActivity);

                    } else {

                        //set the activity type, only if position is not 0
                        userActivity.setActivityType(mActivityTypeList.get(position - 1));

                        //check if user already exists in the ActivityDetail.userList
                        //first remove that activity before adding a new activity for that user
                        if (mActivityDetail.getUserActivityList().contains(userActivity)){
                            mActivityDetail.getUserActivityList().remove(userActivity);
                        }

                        //add user and corresponding activity
                        mActivityDetail.getUserActivityList().add(userActivity);

                    }

                    //enable Next button if there is at least one user participating
                    if (mActivityDetail.getUserActivityList().size() > 0)
                        mNextButton.setEnabled(true);
                    else
                        mNextButton.setEnabled(false);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });



        }

        @Override
        public int getItemCount() {
            return (null != mUserList ? mUserList.size() : 0);
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {

            ///protected ImageView avatar;
            protected TextView username;
            protected Spinner activityTypes;

            public CustomViewHolder(View view) {
                super(view);
                //this.avatar = (ImageView) view.findViewById(R.id.avatar);
                this.username = (TextView)view.findViewById(R.id.username);
                this.activityTypes = (Spinner)view.findViewById(R.id.activityTypeSpinner);
            }
        }

    }
}
