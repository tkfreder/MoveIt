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
    private boolean mHasNewPassword = false;

    // UI Widgets
    protected ImageView mAvatar;
    protected ImageView mEditAvatar;
    protected EditText mUserName;
    protected EditText mWeight;
    protected TextView mAdmin;
    protected Button mSaveButton;
    protected EditText mPassword;
    //protected EditText mSecretAnswer;
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
        //admin password mPassword = (EditText)mRootView.findViewById(R.id.password);
        //mSecretAnswer = (EditText)mRootView.findViewById(R.id.secretAnswer);
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

            //admin password mPassword.setVisibility(View.VISIBLE);
            //mPassword.setText(mUser.getPassword());

            mEmail.setVisibility(View.VISIBLE);
            mEmail.setText(mUser.getEmail());
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

                //displaySecretQuestion();

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

        // admin password
        /*
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mHasNewPassword = true;
            }
        });

        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || event.getKeyCode() == KeyEvent.FLAG_EDITOR_ACTION){
                    // Check if no view has focus:
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        //displaySecretQuestion();
                        // close softkeyboard which obscures the hint on TextInputLayout
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                return true;
            }
        });

        */
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

        /*
        mSecretAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mSecretAnswer.setError(mUser.getSecretQuestion());
                if(mUser.getSecretAnswer().equals(s.toString())){
                    mSaveButton.setEnabled(true);
                }
                else
                    mSaveButton.setEnabled(false);
            }
        });
        */

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

                View view = mFragmentActivity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)mFragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

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

                        //notify UserListObserver
                        CustomApplication app = ((CustomApplication)mFragmentActivity.getApplication());
                        UserListObservable mUserListObservable = app.getUserListObservable();
                        mUserListObservable.addUser(mUser);

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

                        //notify UserListObserver
                        CustomApplication app = ((CustomApplication)mFragmentActivity.getApplication());
                        UserListObservable mUserListObservable = app.getUserListObservable();
                        mUserListObservable.setUser(mUser);

                        Snackbar.make(mRootView.findViewById(R.id.main_layout), getString(R.string.message_saved_changes), Snackbar.LENGTH_LONG)
                                .show();

                        // admin password
                        /*
                        // send email notification that user changed password
                        if(mHasNewPassword){
                            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //emailIntent.setType("plain/text");
                            emailIntent.setType("message/rfc822");
                            emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{mUser.getEmail()});
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.admin_password_changed_subject));
                            emailIntent.putExtra(Intent.EXTRA_TEXT   , getString(R.string.admin_password_changed_body));
                            try {
                                startActivity(emailIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Snackbar.make(mRootView.findViewById(R.id.main_layout), getString(R.string.message_admin_password_changed), Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        }
                        */
                    }
                }
            }

        });

        // admin password
        /*
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (mUser.getSecretAnswer().equals(s.toString()))
                    mSaveButton.setEnabled(true);
            }
        });
        */
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

        /*
        if(mUser.isAdmin()){
            mHasNewPassword = true;
        }

        mUser.setPassword(mPassword.getText().toString());
        */
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

            // admin password
            /*
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
                */
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

    public static User checkRewardEarned(User user, FitnessDBHelper fitnessDBHelper){

        if (user.getPoints() >= user.getReward().getPoints()) {

            user.setPoints(user.getPoints() - user.getReward().getPoints());

            // insert Reward Earned
            fitnessDBHelper.insertRewardEarned(user.getReward().getName(), user.getReward().getPoints(), user.getUserId());
            fitnessDBHelper.updateUser(user);
        }

        return user;
    }

    /*
    private void displaySecretQuestion(){
        // ask for secret answer
        mSecretAnswer.setVisibility(View.VISIBLE);
        mSecretAnswer.setError(mUser.getSecretQuestion());
        mSaveButton.setEnabled(false);
    }
*/
}
