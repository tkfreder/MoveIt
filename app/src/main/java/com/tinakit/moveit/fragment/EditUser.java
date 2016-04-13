package com.tinakit.moveit.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.PickAvatar;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.Reward;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.model.UserListObservable;
import com.tinakit.moveit.module.CustomApplication;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

/**
 * Created by Tina on 1/8/2016.
 */
public class EditUser extends Fragment {

    public static final String EDIT_USER_TAG = "EDIT_USER_TAG";
    public static final String EDIT_USER_USER = "EDIT_USER_USER";
    public static final int PICK_AVATAR_REQUEST = 1;
    private static final String YOUR_REWARD = "put your reward here";
    private static final int DEFAULT_REWARD_POINTS = 100;

    @Inject
    FitnessDBHelper mDatabaseHelper;

    // local cache
    protected FragmentActivity mFragmentActivity;
    private View mRootView;
    private User mUser;
    private User mUser_previous;
    private boolean mIsNewUser = false;
    private List<User> mUserList;

    // UI Widgets
    protected ImageView mAvatar;
    protected ImageView mEditAvatar;
    protected EditText mUserName;
    protected EditText mWeight;
    protected TextView mAdmin;
    protected Button mSaveButton;
    protected EditText mEmail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity  = (FragmentActivity)super.getActivity();
        mRootView = inflater.inflate(R.layout.edit_user, container, false);
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // inject FitnessDBHelper
        ((CustomApplication)getActivity().getApplication()).getAppComponent().inject(this);
        initializeUI();
        fetchData();
        setActionListeners();
        return mRootView;
    }

    private void initializeUI(){

        mAvatar = (ImageView)mRootView.findViewById(R.id.avatar);
        mEditAvatar = (ImageView)mRootView.findViewById(R.id.editAvatar);
        mUserName = (EditText)mRootView.findViewById(R.id.userName);
        mWeight = (EditText)mRootView.findViewById(R.id.weight);
        mAdmin = (TextView)mRootView.findViewById(R.id.isAdmin);
        mSaveButton = (Button)mRootView.findViewById(R.id.saveButton);
        mEmail = (EditText)mRootView.findViewById(R.id.email);
    }

    private void fetchData(){

        // get UserName list, for validating new UserName
        //mUserList = mDatabaseHelper.getUsers();
        CustomApplication app = ((CustomApplication)mFragmentActivity.getApplication());
        UserListObservable mUserListObservable = app.getUserListObservable();
        mUserList = mUserListObservable.getValue();
        // initialize mUser_previous in case adding new user
        mUser_previous = new User();
        // get User out of Bundle, if exists
        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(EDIT_USER_USER)) {
            mUser = bundle.getParcelable(EDIT_USER_USER);
            // save current User
            mUser_previous = mUser;
            // if this is the first time, there will be no data in the bundle
            if (mUser == null) {
                // redirect to UserProfile
                UserProfile userProfile = new UserProfile();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, userProfile)
                        .commit();
            } else {
                populateForm(mUser);
            }
        }
        // New User mode
        else{
            mIsNewUser = true;
            setDefaults();
        }
    }

    private void setDefaults(){
        mUser = new User();
        // set default avatar
        List<String> avatarFileList = Arrays.asList(getResources().getStringArray(R.array.avatar_images));
        String avatarFileName = avatarFileList.get(0);
        mUser.setAvatarFileName(avatarFileName);
        mAvatar.setImageResource(getResources().getIdentifier(avatarFileName, "drawable", getActivity().getPackageName()));
        // set weight
        mWeight.setText("0");
        // set button text
        mSaveButton.setText(getString(R.string.button_add));
    }

    private void populateForm(User user){
        if (!TextUtils.isEmpty(mUser.getUserName()))
            mUserName.setText(user.getUserName());
        if (!TextUtils.isEmpty(mUser.getAvatarFileName()))
            mAvatar.setImageResource(getResources().getIdentifier(user.getAvatarFileName(), "drawable", getActivity().getPackageName()));
        mWeight.setText(String.valueOf(user.getWeight()));
        if (user.isAdmin()){
            mAdmin.setVisibility(View.VISIBLE);
            //mEmail.setVisibility(View.VISIBLE);
            //mEmail.setText(mUser.getEmail());
        }
    }

    private void setActionListeners(){
        mUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (isChangedUsername()){
                    validateForm();
                }
                else{
                    //mSaveButton.setEnabled(false);
                    Snackbar.make(mRootView.findViewById(R.id.main_layout), getString(R.string.message_same_user_settings), Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                //displaySecretQuestion();
            }
        });

        mAvatar.setOnClickListener(avatarClickListener);
        mEditAvatar.setOnClickListener(avatarClickListener);
        mWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                // check for empty field
                if (s.toString().trim().equals("")){
                    mWeight.setError(getString(R.string.message_weight_empty));
                    mSaveButton.setEnabled(false);
                }
                else{
                    if (isChangedWeight())
                        validateForm();
                }
            }
        });

        mWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (isChangedWeight())
                    validateForm();
            }
        });

        //set tag on mAvatar to save filepath to avatar image
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = mFragmentActivity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) mFragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                saveUser();


                if (mIsNewUser) {
                    long rowId = mDatabaseHelper.addUser(mUser);
                    mUser.setUserId((int) rowId); //Sets the row id for a new user
                    if (rowId != -1) {
                        mIsNewUser = false;
                        // add a Reward placeholder for the new user
                        int id = (int)mDatabaseHelper.insertReward(YOUR_REWARD, DEFAULT_REWARD_POINTS, rowId);
                        //notify UserListObserver
                        CustomApplication app = ((CustomApplication) mFragmentActivity.getApplication());
                        UserListObservable mUserListObservable = app.getUserListObservable();
                        Reward reward = new Reward();
                        reward.setRewardId(id);
                        reward.setName(YOUR_REWARD);
                        reward.setPoints(DEFAULT_REWARD_POINTS);
                        mUser.setReward(reward);
                        mUserListObservable.addUser(mUser);
                        // display success message
                        Snackbar.make(mRootView.findViewById(R.id.main_layout), getString(R.string.message_added_user), Snackbar.LENGTH_LONG)
                                .show();
                        // display all users
                        //UserProfile userProfile = (UserProfile)getActivity().getSupportFragmentManager().findFragmentByTag(UserStatsMain.USER_STATS_TAG);

                        // display previous screen (displaying all users)
                        getActivity().getSupportFragmentManager().popBackStack();
                                //.beginTransaction()
                                //.addToBackStack(getString(R.string.app_bar_header_edit_user))
                                //.replace(R.id.fragmentContainer, userProfile).commit();
                    } else {
                        Snackbar.make(mRootView.findViewById(R.id.main_layout), getString(R.string.error_message_add_user), Snackbar.LENGTH_LONG)
                                .show();
                    }
                } else {
                    long rowsAffected = mDatabaseHelper.updateUser(mUser);
                    if (rowsAffected == 1) {
                        //notify UserListObserver
                        CustomApplication app = ((CustomApplication) mFragmentActivity.getApplication());
                        UserListObservable mUserListObservable = app.getUserListObservable();
                        mUserListObservable.setUser(mUser);

                        Snackbar.make(mRootView.findViewById(R.id.main_layout), getString(R.string.message_saved_changes), Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EditUser.PICK_AVATAR_REQUEST){
            if (resultCode == Activity.RESULT_OK) {
                mUser = data.getParcelableExtra(PickAvatar.PICK_AVATAR_KEY_USER);
                populateForm(mUser);
                //Avatar has been updated, enable Save button
                mSaveButton.setEnabled(true);
            }
        }
    }

    private void saveUser(){
        // save any changes into User object
        mUser.setUserName(mUserName.getText().toString());
        mUser.setWeight(Integer.parseInt(mWeight.getText().toString()));
    }

    View.OnClickListener avatarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveUser();
            //save the user in bundle
            Bundle args = new Bundle();
            args.putParcelable(PickAvatar.PICK_AVATAR_KEY_USER, mUser);
            Intent intent = new Intent(getActivity(), PickAvatar.class);
            intent.putExtras(args);
            mFragmentActivity.startActivityForResult(intent, PICK_AVATAR_REQUEST);
        }
    };

    private boolean isChangedUsername(){
        String username = new String(mUserName.getText().toString());
        if(username.equals(mUser_previous.getUserName())){
            return false;
        }
        return true;
    }

    private boolean isChangedWeight(){
        int weight = Integer.parseInt(mWeight.getText().toString());
        if(weight == mUser_previous.getWeight())
            return false;
        else
            return true;
    }

    private boolean validateForm(){
        mSaveButton.setEnabled(false);

        if (!isValidUserName() || !isValidWeight()){
            mSaveButton.setEnabled(false);
            return false;
        }
        else{
            mSaveButton.setEnabled(true);
            return true;
        }
    }

    private boolean isValidWeight(){
        if (mWeight.getText().toString().equals("")){
            mWeight.setError(getString(R.string.message_weight_non_zero));
            return false;
        }
        else{
            if (mWeight.getText().toString().trim().equals("") || Integer.parseInt(mWeight.getText().toString()) == 0){
                mWeight.setError(getString(R.string.message_weight_non_zero));
                return false;
            }
            else
                return true;
        }
    }

    public boolean isValidUserName(){
        // check if username is empty
        if (mUserName.getText().toString().trim().equals("")){
            mUserName.setError(getString(R.string.message_username_empty));
            return false;
        }
        else {
            if (!TextUtils.isEmpty(mUser_previous.getUserName())){
                boolean isSameUserName = new String(mUserName.getText().toString()).equals(mUser_previous.getUserName().toString());
                if (!isSameUserName) {
                    if (existsUserName()) {
                        mUserName.setError(getString(R.string.message_username_exists));
                        return false;
                    }
                }
            }
            else if (existsUserName()){
                mUserName.setError(getString(R.string.message_username_exists));
                return false;
            }
        }
        return true;
    }

    private boolean existsUserName(){
        for (User user : mUserList){
            if(user.getUserName().equals(mUserName.getText().toString().trim())){
                return true;
            }
        }
        return false;
    }

    public String getTitle(){
        if (mUser == null)
            return getString(R.string.app_bar_header_new_user);
        else
            return getString(R.string.app_bar_header_edit_user);
    }

    public static User checkRewardEarned(User user, FitnessDBHelper fitnessDBHelper){
        if (user.getPoints() >= user.getReward().getPoints()) {
            user.setPoints(user.getPoints() - user.getReward().getPoints());
            // insert Reward Earned
            fitnessDBHelper.insertRewardEarned(user.getReward().getName(), user.getReward().getPoints(), user.getUserId());
            fitnessDBHelper.updateUser(user);
        }
        return user;
    }
}
