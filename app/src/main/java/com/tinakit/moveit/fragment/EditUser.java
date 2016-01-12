package com.tinakit.moveit.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

    @Inject
    FitnessDBHelper mDatabaseHelper;

    // local cache
    protected FragmentActivity mFragmentActivity;
    private View mRootView;
    private User mUser;
    private boolean mIsNewUser = false;

    // UI Widgets
    ImageView mAvatar;
    ImageView mEditAvatar;
    EditText mUserName;
    EditText mWeight;
    CheckBox mAdmin;
    Button mSaveButton;

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
        mAdmin = (CheckBox)mRootView.findViewById(R.id.isAdmin);
        mSaveButton = (Button)mRootView.findViewById(R.id.saveButton);
    }

    private void fetchData(){

        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(EDIT_USER_USER)) {

            mUser = bundle.getParcelable(EDIT_USER_USER);

            // if this is the first time, there will be no data in the bundle
            if (mUser == null) {

                // redirect to UserProfile
                UserProfile userProfile = new UserProfile();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, userProfile).commit();
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
        mSaveButton.setText(getResources().getString(R.string.button_add));

    }

    private void populateForm(User user){

        mUserName.setText(user.getUserName());
        mAvatar.setImageResource(getResources().getIdentifier(user.getAvatarFileName(), "drawable", getActivity().getPackageName()));
        mWeight.setText(String.valueOf(user.getWeight()));
        mAdmin.setChecked(user.isAdmin());
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

                    validateForm();
            }
        });

        mEditAvatar.setOnClickListener(new View.OnClickListener() {
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
        });

        mWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                validateForm();
            }
        });

        mAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSaveButton.setEnabled(true);
            }
        });

        //TODO:  set onClickListener on Avatar to browse images and/or go to camera
        //set tag on mAvatar to save filepath to avatar image
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUser();

                if (mIsNewUser){

                    long rowId = mDatabaseHelper.addUser(mUser);

                    if (rowId != -1){

                        mIsNewUser = false;

                        UserProfile userProfile = new UserProfile();
                        //replace current fragment
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, userProfile).commit();

                    }else{

                        Snackbar.make(mRootView.findViewById(R.id.main_layout), getResources().getString(R.string.error_message_add_user), Snackbar.LENGTH_LONG)
                                .show();
                    }


                }
                else{

                    long rowsAffected = mDatabaseHelper.updateUser(mUser);

                    if (rowsAffected == 1){

                        Snackbar.make(mRootView.findViewById(R.id.main_layout), getResources().getString(R.string.message_saved_changes), Snackbar.LENGTH_LONG)
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
        mUser.setIsAdmin(mAdmin.isChecked());

        //any change to avatar should already be saved in OnActivityResult

    }

    private void validateForm(){

        if(mUserName.getText().toString().trim().equals("") || mWeight.getText().toString().trim().equals("")) {

            mSaveButton.setEnabled(false);

            if (mUserName.getText().toString().trim().equals(""))
                mUserName.setError(getResources().getString(R.string.message_username_empty));

            if (mWeight.getText().toString().trim().equals("0"))
                mWeight.setError(getResources().getString(R.string.message_weight_empty));

        }
        else
            mSaveButton.setEnabled(true);

    }

}
