package com.tinakit.moveit.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.model.UserActivity;
import com.tinakit.moveit.module.CustomApplication;
import com.tinakit.moveit.utility.DateUtility;
import com.tinakit.moveit.utility.Map;
import com.tinakit.moveit.utility.UnitConverter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 12/15/2015.
 */
public class ActivityHistory extends Fragment {

    // CONSTANTS
    public static final String ACTIVITY_HISTORY_TAG = "ACTIVIY_HISTORY_TAG";
    public static final String ACTIVITY_HISTORY_KEY = "ACTIVIY_HISTORY_KEY";
    private static final int APPROX_SIZE_AVATAR_IMAGES = 250;
    public static final int ACTIVITY_LIMIT_COUNT = 5;
    private static final int PLACE_NUM_CHARS = 10;

    @Inject
    FitnessDBHelper mDatabaseHelper;

    //INSTANCE FIELDS
    private FragmentActivity mFragmentActivity;

    private ActivityHistoryRecyclerAdapter mActivityHistoryRecyclerAdapter;
    private List<User> mUserList;
    private List<ActivityDetail> mActivityDetailList;


    // UI COMPONENTS
    protected View rootView;
    protected RecyclerView mRecyclerView;
    protected TextView mNoActivities;
    protected Spinner mLimitCountSpinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.activity_history, container, false);

        //mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Dagger 2 injection
        ((CustomApplication)getActivity().getApplication()).getAppComponent().inject(this);

        //get databaseHelper instance
        //mDatabaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);

        initializeUI();

        fetchData();

        return rootView;
    }

    private void fetchData(){


        // get UserActivityList from intent

        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(ACTIVITY_HISTORY_KEY)){

            mActivityDetailList = bundle.getParcelableArrayList(ACTIVITY_HISTORY_KEY);
        }
        else{

            // if this is the first time, fetch directly from the database
            mActivityDetailList = mDatabaseHelper.getActivityDetailList(ACTIVITY_LIMIT_COUNT);

            if (mActivityDetailList.size() == 0){
                //mNoActivities.setVisibility(View.VISIBLE);
                mNoActivities.setVisibility(View.GONE);
            }
            else{
                mNoActivities.setVisibility(View.GONE);
            }

        }

        // get number of users
        mUserList = mDatabaseHelper.getUsers();

        // display search results for current month
        //List<ActivityDetail> activityDetailList = mDatabaseHelper.getActivityDetailList(startDate, endDate);

        mActivityHistoryRecyclerAdapter = new ActivityHistoryRecyclerAdapter(mFragmentActivity, mActivityDetailList);
        mRecyclerView.setAdapter(mActivityHistoryRecyclerAdapter);
        mActivityHistoryRecyclerAdapter.notifyDataSetChanged();
    }

    private void initializeUI(){


        //List<ActivityDetail> activityDetailList = mDatabaseHelper.getActivityDetailList();

        //noActivities TextView
        mNoActivities = (TextView)rootView.findViewById(R.id.noActivities);

        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mFragmentActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        mLimitCountSpinner = (Spinner)rootView.findViewById(R.id.numActivities);
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.activity_count));
        mLimitCountSpinner.setAdapter(adapterMonth);

        mLimitCountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                doSearch();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public class ActivityHistoryRecyclerAdapter extends RecyclerView.Adapter<ActivityHistoryRecyclerAdapter.CustomViewHolder> {

        private Context mContext;
        private List<ActivityDetail> activityDetailList;


        public ActivityHistoryRecyclerAdapter(Context context, List<ActivityDetail> activityDetailList) {

            mContext = context;
            this.activityDetailList = activityDetailList;
        }

        @Override
        public int getItemCount() {
            return (null != activityDetailList ? activityDetailList.size() : 0);
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {

            TextView date;
            TextView minutesElapsed;
            TextView place;
            LinearLayout userLinearLayout;
            ImageView deleteButton;

            public CustomViewHolder(View view) {

                super(view);
                this.date = (TextView)view.findViewById(R.id.date);
                this.minutesElapsed = (TextView)view.findViewById(R.id.minutesElapsed);
                this.place = (TextView)view.findViewById(R.id.place);
                this.userLinearLayout = (LinearLayout)view.findViewById(R.id.userLinearLayout);
                this.deleteButton = (ImageView)view.findViewById(R.id.deleteButton);
            }
        }


        @Override
        public ActivityHistoryRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_history_list_item, viewGroup, false);
            CustomViewHolder viewHolder = new CustomViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ActivityHistoryRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

            ActivityDetail activityDetail = activityDetailList.get(i);

            customViewHolder.date.setText(DateUtility.getDateFormattedRecent(activityDetail.getStartDate(), 7));
            float minutes = UnitConverter.convertMillisecondsToUnits(activityDetail.getEndDate().getTime() - activityDetail.getStartDate().getTime(), UnitConverter.TimeUnits.MINUTES);

            float hour = 0;
            if (minutes > 60){

                // hour
                hour = minutes / 60;
            }
            String minutesElapsed = String.format("%02d", Math.round(hour)) + ":" + String.format("%02d", Math.round(minutes));
            customViewHolder.minutesElapsed.setText(minutesElapsed);

            String street = Map.getLocationDetailByParams(mContext, activityDetail.getStartLocation(), 0);

            customViewHolder.place.setText(street.substring(0, street.length() > PLACE_NUM_CHARS ? PLACE_NUM_CHARS : street.length()) + "...");

            int imageSize = APPROX_SIZE_AVATAR_IMAGES / mUserList.size();

            for (UserActivity userActivity : activityDetail.getUserActivityList()){

                ImageView avatar = new ImageView(mContext);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize,imageSize);
                avatar.setLayoutParams(params);
                avatar.setImageResource(getResources().getIdentifier(userActivity.getUser().getAvatarFileName(), "drawable", mFragmentActivity.getPackageName()));
                customViewHolder.userLinearLayout.addView(avatar);

            }

            customViewHolder.deleteButton.setTag(activityDetail.getActivityId());
            customViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int activityId = (int) v.getTag();

                    AlertDialog alertDialog = new AlertDialog.Builder(
                            mFragmentActivity,
                            R.style.AlertDialogCustom_Destructive)
                            .setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    java.util.Map<Integer, Integer> userIdList = mDatabaseHelper.getActivityUsers(activityId);

                                    for (java.util.Map.Entry<Integer, Integer> entry : userIdList.entrySet()) {

                                        // check if user has a reward earned but not fulfilled, if so, remove that reward
                                        boolean isFulfilled = false;
                                        int rewardPoints = mDatabaseHelper.getRewardPoints(entry.getKey(), isFulfilled);

                                        // update user total points
                                        if (rewardPoints != -1) {

                                            User user = mDatabaseHelper.getUser(entry.getKey());

                                            // if the point value of the reward is greater than the points earned from the activity
                                            if (rewardPoints > entry.getValue()) {

                                                mDatabaseHelper.updateUserPoints(user, rewardPoints - entry.getValue());
                                            } else {

                                                mDatabaseHelper.updateUserPoints(user, -(entry.getKey() - rewardPoints));
                                            }
                                        }

                                        // check if user has enough points to earn their reward
                                        // get latest user data
                                        User user = mDatabaseHelper.getUser(entry.getKey());

                                        if (user.getPoints() >= user.getChildItemList().get(0).getPoints()) {

                                            user.setPoints(user.getPoints() - user.getChildItemList().get(0).getPoints());

                                            // insert Reward Earned
                                            mDatabaseHelper.insertRewardEarned(user.getChildItemList().get(0).getName(), user.getChildItemList().get(0).getPoints(), user.getUserId(), activityId);
                                        }

                                        mDatabaseHelper.updateUser(user);

                                        //TODO: delete references to this activity in Activities, ActivityUsers
                                        if (mDatabaseHelper.deleteActivity(activityId)) {

                                            Snackbar.make(rootView.findViewById(R.id.main_layout), getString(R.string.message_activity_deleted), Snackbar.LENGTH_LONG)
                                                    .show();
                                        }

                                    }

                                }
                            })
                            .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Cancel Action
                                    //don't do anything

                                }
                            })
                            .setTitle(R.string.title_delete_activity)
                            .setMessage(R.string.message_delete_activity)
                            .show();


                }
            });

        }
    }

    private void doSearch(){

        //List<ActivityDetail> activityDetailList = mDatabaseHelper.getActivityDetailList(startDate, endDate);
        List<ActivityDetail> activityDetailList = mDatabaseHelper.getActivityDetailList(Integer.parseInt(mLimitCountSpinner.getSelectedItem().toString()));

        mActivityHistoryRecyclerAdapter = new ActivityHistoryRecyclerAdapter(mFragmentActivity, activityDetailList);
        mRecyclerView.setAdapter(mActivityHistoryRecyclerAdapter);
        mActivityHistoryRecyclerAdapter.notifyDataSetChanged();
    }
}
