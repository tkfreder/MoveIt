package com.tinakit.moveit.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.tinakit.moveit.R;
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
public class ActivityChooser  extends AppCompatActivity {

    protected static RecyclerView mRecyclerView;
    public static MultiChooserRecyclerAdapter mRecyclerViewAdapter;

    protected Bundle mBundle;
    protected static List<ActivityType> mActivityTypeList;
    public static ActivityDetail mActivityDetail = new ActivityDetail();

    //database
    FitnessDBHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        //get databaseHelper instance
        mDatabaseHelper = FitnessDBHelper.getInstance(this);

        initializeRecyclerView();

    }

    private void initializeRecyclerView(){

        mBundle = new Bundle();

        // Get userlist
        List<User> userList = mDatabaseHelper.getUsers();
        mActivityTypeList = mDatabaseHelper.getActivityTypes();

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.

        // The number of Columns
        //mRecyclerView.setLayoutManager(new GridLayoutManager(mFragmentActivity, 2));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerViewAdapter = new MultiChooserRecyclerAdapter(this, userList, mActivityTypeList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

    }

    public class MultiChooserRecyclerAdapter extends RecyclerView.Adapter<MultiChooserRecyclerAdapter.CustomViewHolder> {

        private Context mContext;
        private List<User> mUserList;
        private List<ActivityType> mActivityTypeList;
        private ArrayList<UserActivity> mUserActivityList;


        public MultiChooserRecyclerAdapter(Context context, List<User> userList, List<ActivityType> activityTypeList) {

            mContext = context;
            mUserList = userList;
            mActivityTypeList = activityTypeList;
            mUserActivityList = new ArrayList<>();
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
            customViewHolder.avatar.setImageResource(getResources().getIdentifier(user.getAvatarFileName(), "drawable", getPackageName()));
            customViewHolder.userName.setText(user.getUserName());

            // set tag on radio group
            customViewHolder.activitySpinner.setTag(user);


            /*
            //add non-participant option to ActivityTypeList
            ActivityType activityType = new ActivityType();
            activityType.setActivityName("Not participating");
            activityType.setIconFileName("non_participant");
            mActivityTypeList.add(new ActivityType());
            */


            String[] activityList = new String[mActivityTypeList.size() + 1];

            //add non-participating option
            activityList[0] = "Not participating";

            for ( int j = 0; j < mActivityTypeList.size(); j++){

                activityList[j + 1] = mActivityTypeList.get(j).getActivityName();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, activityList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            customViewHolder.activitySpinner.setAdapter(adapter);

            customViewHolder.activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    User user = (User) parent.getTag();
                    UserActivity userActivity = new UserActivity(user);
                    userActivity.setActivityType(mActivityTypeList.get(position));


                    // if non-participant was chosen, remove this user from UserActivityList if user is on the list
                    if (position == 0){

                        // if user exists on participant list, remove the user
                        if (mUserActivityList.contains(userActivity)){

                            mUserActivityList.remove(mUserActivityList.indexOf(userActivity));

                            //update bundle
                            mBundle.putParcelableArrayList("userActivityList", mUserActivityList);
                        }
                    }
                    // if the user already exists, remove it, add new activity type for this user
                    else if (mUserActivityList.contains(userActivity)) {

                        mUserActivityList.remove(mUserActivityList.indexOf(userActivity));
                        mUserActivityList.add(userActivity);

                        //update bundle
                        mBundle.putParcelableArrayList("userActivityList", mUserActivityList);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                    //no change, don't do anything
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
