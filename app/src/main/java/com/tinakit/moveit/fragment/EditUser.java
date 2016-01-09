package com.tinakit.moveit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import javax.inject.Inject;

/**
 * Created by Tina on 1/8/2016.
 */
public class EditUser extends Fragment {

    public static final String EDIT_USER_TAG = "EDIT_USER_TAG";
    public static final String EDIT_USER_USER = "EDIT_USER_USER";

    @Inject
    FitnessDBHelper mDatabaseHelper;

    // local cache
    protected FragmentActivity mFragmentActivity;
    private View rootView;
    private User mUser;

    // UI Widgets
    ImageView mAvatar;
    EditText mUserName;
    EditText mWeight;
    CheckBox mAdmin;
    Button mSaveButton;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.edit_user, container, false);
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // inject FitnessDBHelper
        ((CustomApplication)getActivity().getApplication()).getAppComponent().inject(this);

        initializeUI();

        fetchData();

        setActionListeners();

        return rootView;
    }

    private void initializeUI(){

        mAvatar = (ImageView)rootView.findViewById(R.id.avatar);
        mUserName = (EditText)rootView.findViewById(R.id.userName);
        mWeight = (EditText)rootView.findViewById(R.id.weight);
        mAdmin = (CheckBox)rootView.findViewById(R.id.isAdmin);
        mSaveButton = (Button)rootView.findViewById(R.id.saveButton);
    }

    private void fetchData(){

        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(EDIT_USER_USER)) {

            mUser = bundle.getParcelable(EDIT_USER_USER);

            // if this is the first time, there will be no data in the bundle
            if (mUser == null) {

                // redirect to UserStats screen
                //Intent intent = new Intent(this, UserStats.class);

                // check if UserStats is already displayed
                UserProfile userProfile = (UserProfile) getActivity().getSupportFragmentManager().findFragmentByTag(UserProfile.USER_PROFILE_TAG);
                if (userProfile == null) {

                    userProfile = new UserProfile();
                    //replace current fragment
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, userProfile).commit();

                }
            } else {

                populateForm();
            }
        }
    }

    private void populateForm(){

        mUserName.setText(mUser.getUserName());
        mAvatar.setImageResource(getResources().getIdentifier(mUser.getAvatarFileName(), "drawable", getActivity().getPackageName()));
        mWeight.setText(String.valueOf(mUser.getWeight()));
        mAdmin.setChecked(mUser.isAdmin());
    }

    private void setActionListeners(){


        //TODO:  set onClickListener on Avatar to browse images and/or go to camera
        //set tag on mAvatar to save filepath to avatar image
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = new User();
                user.setUserName(mUserName.getText().toString());
                user.setAvatarFileName((String)mAvatar.getTag());
                user.setWeight(Integer.parseInt(mWeight.getText().toString()));

                mDatabaseHelper.updateUser(user);
            }
        });
    }
}
