package com.tinakit.moveit.fragment;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.model.UserActivity;
import com.tinakit.moveit.utility.DateUtility;
import com.tinakit.moveit.utility.Map;
import com.tinakit.moveit.utility.UnitConverter;

import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by Tina on 12/15/2015.
 */
public class ActivityHistory extends Fragment {

    // CONSTANTS
    public static final String ACTIVITY_HISTORY = "ACTIVIY_HISTORY";

    //INSTANCE FIELDS
    FragmentActivity mFragmentActivity;
    FitnessDBHelper mDatabaseHelper;
    ActivityHistoryRecyclerAdapter mActivityHistoryRecyclerAdapter;
    private List<ActivityDetail> mActivityDetailList;


    // UI COMPONENTS
    View rootView;
    RecyclerView mRecyclerView;
    TextView mNoActivities;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.activity_history, container, false);

        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //get databaseHelper instance
        mDatabaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);

        initializeUI();

        fetchData();

        return rootView;
    }

    private void fetchData(){

        // get UserActivityList from intent

        Bundle bundle = this.getArguments();
        if (bundle.containsKey(ACTIVITY_HISTORY)){

            mActivityDetailList = bundle.getParcelableArrayList(ACTIVITY_HISTORY);

            // if this is the first time, there will be data in the bundle
            if (mActivityDetailList == null){

                // fetch directly from the database
                mActivityDetailList = mDatabaseHelper.getActivityDetailList();

                if (mActivityDetailList.size() == 0)
                    mNoActivities.setVisibility(View.VISIBLE);
                else
                    mNoActivities.setVisibility(View.GONE);

            }
        }
        // display no-activities message
        else{
            mNoActivities.setVisibility(View.VISIBLE);
        }


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
        mActivityHistoryRecyclerAdapter = new ActivityHistoryRecyclerAdapter(mFragmentActivity, mActivityDetailList);
        mRecyclerView.setAdapter(mActivityHistoryRecyclerAdapter);

    }

    public class ActivityHistoryRecyclerAdapter extends RecyclerView.Adapter<ActivityHistoryRecyclerAdapter.CustomViewHolder> {

        private Context mContext;


        public ActivityHistoryRecyclerAdapter(Context context, List<ActivityDetail> activityDetailList) {

            mContext = context;
            mActivityDetailList = activityDetailList;
        }

        @Override
        public int getItemCount() {
            return (null != mActivityDetailList ? mActivityDetailList.size() : 0);
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {

            TextView date;
            TextView minutesElapsed;
            TextView place;
            LinearLayout userLinearLayout;

            public CustomViewHolder(View view) {

                super(view);
                this.date = (TextView)view.findViewById(R.id.date);
                this.minutesElapsed = (TextView)view.findViewById(R.id.minutesElapsed);
                this.place = (TextView)view.findViewById(R.id.place);
                this.userLinearLayout = (LinearLayout)view.findViewById(R.id.userLinearLayout);
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

            ActivityDetail activityDetail = mActivityDetailList.get(i);

            customViewHolder.date.setText(DateUtility.getDateFormattedRecent(activityDetail.getStartDate(), 7));
            float minutes = UnitConverter.convertMillisecondsToUnits(activityDetail.getEndDate().getTime() - activityDetail.getStartDate().getTime(), UnitConverter.TimeUnits.MINUTES);
            String minutesElapsed = String.valueOf(minutes);
            customViewHolder.minutesElapsed.setText(minutesElapsed);

            customViewHolder.place.setText(Map.getLocationDetailByParams(mContext, activityDetail.getStartLocation(), 0));

            for (UserActivity userActivity : activityDetail.getUserActivityList()){

                ImageView avatar = new ImageView(mContext);
                avatar.setImageResource(getResources().getIdentifier(userActivity.getUser().getAvatarFileName(), "drawable", mFragmentActivity.getPackageName()));
                customViewHolder.userLinearLayout.addView(avatar);

            }

        }
    }
}
