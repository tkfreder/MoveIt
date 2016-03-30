package com.tinakit.moveit.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.ActivityTracker;
import com.tinakit.moveit.activity.PickAvatar;
import com.tinakit.moveit.api.GoogleApi;
import com.tinakit.moveit.api.LocationApi;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.ActivityType;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.model.UserActivity;
import com.tinakit.moveit.model.UserListObservable;
import com.tinakit.moveit.module.CustomApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by Tina on 12/13/2015.
 */
public class ActivityChooser  extends Fragment implements Observer {

    // CONSTANTS
    private static final boolean DEBUG = true;
    public static final String ACTIVITY_CHOOSER_TAG= "ACTIVITY_CHOOSER_TAG";
    public static final String ACTIVITY_CHOOSER_BACKSTACK_TAG= "Start";
    public static final String USER_ACTIVITY_LIST_KEY = "USER_ACTIVITY_LIST_KEY";
    public static final int PICK_AVATAR_REQUEST = 2;
    public static final int ENABLE_GPS = 3;
    private final static int PERMISSIONS_REQUEST_LOCATION = 123;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Inject
    GoogleApi mGoogleApi;

    @Inject
    FitnessDBHelper mDatabaseHelper;

    // local cache
    protected FragmentActivity mFragmentActivity;
    private View rootView;
    protected static List<ActivityType> mActivityTypeList;
    public static ActivityDetail mActivityDetail = new ActivityDetail();
    protected ArrayList<UserActivity> mUserActivityList = new ArrayList<>();
    protected List<UserActivity> mUserActivityList_previous;
    private static ActivityChooser mActivityChooser;

    // API
    private MapFragment mMapFragment;
    private LocationApi mLocationApi;

    // UI COMPONENTS
    protected RecyclerView mRecyclerView;
    public static MultiChooserRecyclerAdapter mRecyclerViewAdapter;
    protected Button mNextButton;
    private ViewGroup mContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.activity_chooser, container, false);
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // inject FitnessDBHelper
        ((CustomApplication)getActivity().getApplication()).getAppComponent().inject(this);

        // check Location Services is turned on for this device
        if (!mGoogleApi.servicesAvailable(mFragmentActivity))
            mFragmentActivity.finish();
        else
            mGoogleApi.buildGoogleApiClient(mFragmentActivity);

        mLocationApi = new LocationApi(mFragmentActivity, mGoogleApi.client());

        if(mLocationApi.hasLocationService()){
            initializeUI();
            setActionListeners();
        }
        return rootView;
    }

    private void setActionListeners(){

        mNextButton = (Button)rootView.findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                //sort the UserActivityList before saving it
                Intent intent = new Intent(mFragmentActivity, ActivityTracker.class);
                Collections.sort(mUserActivityList);
                intent.putParcelableArrayListExtra(USER_ACTIVITY_LIST_KEY, mUserActivityList);
                startActivity(intent);
*/
                // sort activity list first
                Collections.sort(mUserActivityList);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(USER_ACTIVITY_LIST_KEY, mUserActivityList);
                ActivityTracker activityTracker = new ActivityTracker();
                activityTracker.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, activityTracker,ActivityTracker.ACTIVITY_TRACKER_TAG)
                        .commit();
            }
        });
    }

    private void initializeUI(){

        // Get userlist
        //List<User> userList = mDatabaseHelper.getUsers();
        CustomApplication app = ((CustomApplication)getActivity().getApplication());
        UserListObservable mUserListObservable = app.getUserListObservable();
        List<User> userList = mUserListObservable.getValue();
        mActivityTypeList = mDatabaseHelper.getActivityTypes();

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mFragmentActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerViewAdapter = new MultiChooserRecyclerAdapter(mFragmentActivity, userList, mUserActivityList_previous);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        //mMapFragment = new MapFragment(getActivity().getSupportFragmentManager(), getActivity());
        //mMapFragment.addMap(R.id.map_container, mContainer);

        //LocalBroadcastManager.getInstance(mFragmentActivity).registerReceiver(mMessageReceiver, new IntentFilter(GoogleApi.GOOGLE_API_INTENT));

        //display map of starting point
        //mMapFragment.displayStartMap();
    }

    @Override
    public void update(Observable observable, Object data) {
        List<User> userList = (List<User>)data;
        mRecyclerViewAdapter.setList(userList);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(mFragmentActivity).registerReceiver(mMessageReceiver, new IntentFilter(GoogleApi.GOOGLE_API_INTENT));
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(mFragmentActivity).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    // Handler for received Intents.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(ActivityTracker.ACTIVITY_TRACKER_BROADCAST_RECEIVER);
            if (DEBUG) Log.d(ACTIVITY_CHOOSER_TAG, "BroadcastReceiver - onReceive(): message: " + message);
            // message to indicate Google API Client connection
            if(message.equals(GoogleApi.GOOGLE_API_INTENT)){
                // check app permissions, API 23 or higher
                if (Build.VERSION.SDK_INT >= 23) {
                    verifyPermissions(getActivity());
                } else {
                    mMapFragment = new MapFragment(getActivity().getSupportFragmentManager(), getActivity());
                    mMapFragment.addMap(R.id.map_container, mContainer);
                    LocalBroadcastManager.getInstance(mFragmentActivity).unregisterReceiver(mMessageReceiver);
                }
            }
        }
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public void verifyPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    PERMISSIONS_REQUEST_LOCATION
            );
        } else {
            mMapFragment = new MapFragment(getActivity().getSupportFragmentManager(), getActivity());
            mMapFragment.addMap(R.id.map_container, mContainer);
            LocalBroadcastManager.getInstance(mFragmentActivity).unregisterReceiver(mMessageReceiver);
        }
    }

    //**********************************************************************************************
    //  onRequestPermissionsResult
    //**********************************************************************************************
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMapFragment = new MapFragment(getActivity().getSupportFragmentManager(), getActivity());
                    mMapFragment.addMap(R.id.map_container, mContainer);
                    LocalBroadcastManager.getInstance(mFragmentActivity).unregisterReceiver(mMessageReceiver);
                } else {
                    Snackbar.make(rootView.findViewById(R.id.main_content), getString(R.string.message_no_location_permission), Snackbar.LENGTH_LONG)
                            .show();                }
                return;
            }
        }
    }

    public class MultiChooserRecyclerAdapter extends RecyclerView.Adapter<MultiChooserRecyclerAdapter.CustomViewHolder> {
        private Context mContext;
        private List<User> mUserList;
        private String[] activityList;
        private List<UserActivity> mUserActivityList_previous;

        public MultiChooserRecyclerAdapter(Context context, List<User> userList, List<UserActivity> userActivityList_previous) {
            mContext = context;
            mUserList = userList;
            mUserActivityList_previous = userActivityList_previous;

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

        private void setList(List<User> userList){
            mUserList = userList;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(MultiChooserRecyclerAdapter.CustomViewHolder customViewHolder, int i) {

            User user = mUserList.get(i);
            String activityTypeName = "";

            // Populate data from ActivityType data object
            customViewHolder.avatar.setImageResource(getResources().getIdentifier(user.getAvatarFileName(), "drawable", mFragmentActivity.getPackageName()));
            customViewHolder.avatar.setTag(user);
            customViewHolder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //save the user in bundle
                    Bundle args = new Bundle();
                    args.putParcelable(PickAvatar.PICK_AVATAR_KEY_USER, (User)v.getTag());
                    Intent intent = new Intent(getActivity(), PickAvatar.class);
                    intent.putExtras(args);
                    mFragmentActivity.startActivityForResult(intent, ActivityChooser.PICK_AVATAR_REQUEST);
                }
            });

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
                    if (position == 0) {

                        // if user exists on participant list, remove the user
                        if (mUserActivityList.size() > 0 && mUserActivityList.contains(userActivity)) {

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
                    else {
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
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityChooser.PICK_AVATAR_REQUEST){
            if (resultCode == Activity.RESULT_OK) {
                User user = data.getParcelableExtra(PickAvatar.PICK_AVATAR_KEY_USER);
                mDatabaseHelper.updateUser(user);
                mRecyclerViewAdapter.setList(mDatabaseHelper.getUsers());
                mRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
