package com.tinakit.moveit.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 1/8/2016.
 */
public class EditUser extends Fragment {

    public static final String EDIT_USER_TAG = "EDIT_USER_TAG";
    public static final String EDIT_USER_USER = "EDIT_USER_USER";
    public static final int PICK_AVATAR_REQUEST = 1;
    private static final String YOUR_REWARD = "Your Reward TBD";
    private static final int DEFAULT_REWARD_POINTS = 200;

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
    protected EditText mPassword;
    protected TextView mEmail;
    protected Spinner mAdminLoginPrefs;

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
        mPassword = (EditText)mRootView.findViewById(R.id.password);
        mEmail = (TextView)mRootView.findViewById(R.id.email);
        mAdminLoginPrefs = (Spinner)mRootView.findViewById(R.id.admin_preference_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.admin_login_preferences));
        mAdminLoginPrefs.setAdapter(adapter);

    }

    private void fetchData(){

        // get UserName list, for validating new UserName
        mUserList = mDatabaseHelper.getUsers();

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

            mPassword.setVisibility(View.VISIBLE);
            mPassword.setText(mUser.getPassword());

            mEmail.setVisibility(View.VISIBLE);
            mEmail.setText(mUser.getEmail());

            mAdminLoginPrefs.setVisibility(View.VISIBLE);
            // check SharedPreferences for auto-populate fields
            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            int pref = sharedPreferences.getInt(AdminLoginDialogFragment.ADMIN_LOGIN_PREFS, 0);

            mAdminLoginPrefs.setSelection(pref);
        }



    }

    private void setActionListeners(){

        mUserName.addTextChangedListener(new TextWatcher() {
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
                    mUserName.setError(getString(R.string.message_username_empty));
                    mSaveButton.setEnabled(false);
                }

                else if (isChanged()){

                    if(validateForm()){

                        mSaveButton.setEnabled(true);
                    }
                    else {
                        mSaveButton.setEnabled(false);
                        mUserName.setError(getString(R.string.message_username_exists));
                    }
                }
                else{
                    mSaveButton.setEnabled(false);
                    Snackbar.make(mRootView.findViewById(R.id.main_layout), getString(R.string.message_same_user_settings), Snackbar.LENGTH_LONG)
                            .show();
                }


                /*
                //only check if name exists if new username is same as the previous one
                if (!TextUtils.isEmpty(mUser_previous.getUserName())){

                    boolean isSameUserName = s.toString().equals(mUser_previous.getUserName());
                    if (!isSameUserName){
                        if (existsUserName()){

                            mUserName.setError(getString(R.string.message_username_exists));
                            mSaveButton.setEnabled(false);
                        }

                        else
                            mSaveButton.setEnabled(true);
                    }
                }
                */
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

                else if (isChanged()){

                    if (validateForm())
                        mSaveButton.setEnabled(true);
                    else{

                        mSaveButton.setEnabled(false);
                        mWeight.setError(getString(R.string.message_weight_empty));
                    }

                }
                else{
                    mSaveButton.setEnabled(false);
                    Snackbar.make(mRootView.findViewById(R.id.main_layout), getString(R.string.message_same_user_settings), Snackbar.LENGTH_LONG)
                            .show();
                }

                /*
                if (!TextUtils.isEmpty(mWeight.getText())){
                    if (!isValidWeight()){
                        mWeight.setError(getString(R.string.message_weight_empty));
                        mSaveButton.setEnabled(false);
                    }
                    else
                        mSaveButton.setEnabled(true);
                }
                */
            }
        });

        //set tag on mAvatar to save filepath to avatar image
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if this is admin, save username and password in SharedPreferences
                if (mUser.isAdmin()){

                    SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(AdminLoginDialogFragment.ADMIN_USERNAME, mUserName.getText().toString());
                    editor.putString(AdminLoginDialogFragment.ADMIN_PASSWORD, mPassword.getText().toString());
                    editor.commit();
                }

                saveUser();

                if (mIsNewUser) {

                    long rowId = mDatabaseHelper.addUser(mUser);

                    if (rowId != -1) {

                        mIsNewUser = false;

                        // add a Reward placeholderfor the new user
                        mDatabaseHelper.insertReward(YOUR_REWARD, DEFAULT_REWARD_POINTS, rowId);

                        UserProfile userProfile = new UserProfile();
                        //replace current fragment
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, userProfile).commit();

                    } else {

                        Snackbar.make(mRootView.findViewById(R.id.main_layout), getString(R.string.error_message_add_user), Snackbar.LENGTH_LONG)
                                .show();
                    }


                } else {

                    long rowsAffected = mDatabaseHelper.updateUser(mUser);

                    if (rowsAffected == 1) {

                        Snackbar.make(mRootView.findViewById(R.id.main_layout), getString(R.string.message_saved_changes), Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
            }

        });

        if (mUser.isAdmin()){

            mAdminLoginPrefs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(AdminLoginDialogFragment.ADMIN_LOGIN_PREFS, position);

                    // if position = 1, want auto-populate fields, cache login details
                    editor.putString(AdminLoginDialogFragment.ADMIN_USERNAME, mUser.getUserName());
                    editor.putString(AdminLoginDialogFragment.ADMIN_PASSWORD, mUser.getPassword());

                    editor.commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                mSaveButton.setEnabled(true);
            }
        });

        mAdminLoginPrefs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSaveButton.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        if(mUser.isAdmin())
            mUser.setPassword(mPassword.getText().toString());

        //any change to avatar should already be saved in OnActivityResult

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

    private boolean isChanged(){

        String username = new String(mUserName.getText().toString());

        if(Integer.parseInt(mWeight.getText().toString()) == (mUser_previous.getWeight()) &&
                username.equals(mUser_previous.getUserName())){
            return false;
        }

        return true;

    }

    private boolean validateForm(){

        if(isValidWeight() && isValidUserName()) {

            if(mAdmin.isSelected()){

                if(mPassword.getText().toString().equals("")){
                    mPassword.setError(getString(R.string.message_enter_password));
                    return false;
                }
                else
                    return true;
            }
            else
                return true;
        }
        else
            return false;
    }

    public boolean isValidWeight(){

        String weight = new String(mWeight.getText().toString().trim());


        if(weight.equals(""))
            return false;

        if( Integer.parseInt(mWeight.getText().toString()) == 0){

            return false;
        }

        return true;
    }

    public boolean isValidUserName(){

        if (!TextUtils.isEmpty(mUser_previous.getUserName())){

            boolean isSameUserName = new String(mUserName.getText().toString()).equals(mUser_previous.getUserName().toString());

            if (!isSameUserName){
                if (existsUserName()){

                    return false;
                 }

                else
                    return true;
            }
            else
                return true;
        }
        else if (existsUserName()){

            return false;

        }
        else
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

}
