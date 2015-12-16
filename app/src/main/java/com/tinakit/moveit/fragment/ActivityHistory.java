package com.tinakit.moveit.fragment;

import android.content.Context;
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
import com.tinakit.moveit.utility.UnitConverter;

import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by Tina on 12/15/2015.
 */
public class ActivityHistory extends Fragment {

    //INSTANCE FIELDS
    FragmentActivity mFragmentActivity;
    FitnessDBHelper mDatabaseHelper;
    ActivityHistoryRecyclerAdapter mActivityHistoryRecyclerAdapter;


    // UI COMPONENTS
    View rootView;
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.activity_history, container, false);

        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //get databaseHelper instance
        mDatabaseHelper = FitnessDBHelper.getInstance(mFragmentActivity);

        initializeRecyclerView();

        return rootView;
    }

    private void initializeRecyclerView(){

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mFragmentActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mActivityHistoryRecyclerAdapter = new ActivityHistoryRecyclerAdapter(mFragmentActivity);
        mRecyclerView.setAdapter(mActivityHistoryRecyclerAdapter);

    }

    public class ActivityHistoryRecyclerAdapter extends RecyclerView.Adapter<ActivityHistoryRecyclerAdapter.CustomViewHolder> {

        private Context mContext;
        private List<ActivityDetail> mActivityDetailList;

        public ActivityHistoryRecyclerAdapter(Context context) {

            mContext = context;
            mActivityDetailList = mDatabaseHelper.getActivityDetailList();
        }

        @Override
        public int getItemCount() {
            return (null != mActivityDetailList ? mActivityDetailList.size() : 0);
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {

            TextView date;
            TextView minutesElapsed;
            LinearLayout userLinearLayout;
            LinearLayout activityLinearLayout;

            public CustomViewHolder(View view) {

                super(view);
                this.date = (TextView)view.findViewById(R.id.date);
                this.minutesElapsed = (TextView)view.findViewById(R.id.minutesElapsed);
                this.userLinearLayout = (LinearLayout)view.findViewById(R.id.userLinearLayout);
                this.activityLinearLayout = (LinearLayout)view.findViewById(R.id.activityLinearLayout);
            }
        }

        @Override
        public ActivityHistoryRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            //View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.participant_activity_list_item, null);
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.participant_activity_list_item, viewGroup, false);

            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ActivityHistoryRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

            ActivityDetail activityDetail = mActivityDetailList.get(i);

            customViewHolder.date.setText(DateUtility.getDateFormattedRecent(activityDetail.getStartDate(), 7));
            customViewHolder.minutesElapsed.setText(String.valueOf(UnitConverter.convertMillisecondsToUnits(activityDetail.getEndDate().getTime() - activityDetail.getStartDate().getTime(), UnitConverter.TimeUnits.MINUTES)));

            for (UserActivity userActivity : activityDetail.getUserActivityList()){

                ImageView avatar = new ImageView(mContext);
                avatar.setImageResource(getResources().getIdentifier(userActivity.getUser().getAvatarFileName(), "drawable", mFragmentActivity.getPackageName()));
                customViewHolder.userLinearLayout.addView(avatar);

                ImageView activityIcon = new ImageView(mContext);
                activityIcon.setImageResource(getResources().getIdentifier(userActivity.getActivityType().getIconFileName(), "drawable", mFragmentActivity.getPackageName()));
                customViewHolder.activityLinearLayout.addView(activityIcon);
            }
        }
    }
}
