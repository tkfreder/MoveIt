package com.tinakit.moveit.model;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.fragment.AdminLoginDialogFragment;
import com.tinakit.moveit.module.CustomApplication;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by Tina on 2/6/2016.
 */
public class RegisterDialogFragment extends DialogFragment {

    public static final String REGISTER_DIALOG_TAG = "REGISTER_DIALOG_TAG";

    // UI components
    View mView;
    TextView mUserName;
    TextView mPassword;
    TextView mPasswordConfirm;
    TextView mEmail;
    TextView mEmailConfirm;

    @Inject
    FitnessDBHelper mDatabaseHelper;

    /* The activity that creates an instance of this dialog fragment must
        * implement this interface in order to receive event callbacks.
        * Each method passes the DialogFragment in case the host needs to query it. */
    public interface RegisterDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    RegisterDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // DI
        ((CustomApplication)activity.getApplication()).getAppComponent().inject(this);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (RegisterDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_register, null);
        mUserName = (TextView)mView.findViewById(R.id.username);
        mPassword = (TextView)mView.findViewById(R.id.password);
        mPasswordConfirm = (TextView)mView.findViewById(R.id.passwordConfirm);
        mEmail = (TextView)mView.findViewById(R.id.email);
        mEmailConfirm = (TextView)mView.findViewById(R.id.emailConfirm);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(mView)
                // Add action buttons
                .setPositiveButton(R.string.button_signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // register user as admin

                        // build user for admin
                        // admin is hard-coded at userid = 1, as defined in FitnessDBHelper.onCreate()

                        User user = new User();
                        user.setUserId(1);
                        user.setWeight(125);
                        user.setIsEnabled(true);
                        user.setPoints(0);
                        user.setIsAdmin(true);
                        user.setUserName(mUserName.getText().toString());
                        user.setEmail(mEmail.getText().toString());
                        user.setPassword(mPassword.getText().toString());

                        // save username and password in SharedPreferences in case admin wants to auto-populate login in the future
                        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(AdminLoginDialogFragment.ADMIN_LOGIN_PREFS, 0); // 0 = retype login

                        // if position = 1, want auto-populate fields, cache login details
                        editor.putString(AdminLoginDialogFragment.ADMIN_USERNAME, mUserName.getText().toString());
                        editor.putString(AdminLoginDialogFragment.ADMIN_PASSWORD, mPassword.getText().toString());
                        editor.commit();

                        // fourth avatar image is default
                        List<String> avatarFileList = Arrays.asList(getResources().getStringArray(R.array.avatar_images));
                        user.setAvatarFileName(avatarFileList.get(0));

                        long rowsAffected = mDatabaseHelper.updateUser(user);

                        if (rowsAffected == 1){

                            // treating this as a flag indicating validation of sign in succeeded
                            mListener.onDialogPositiveClick(RegisterDialogFragment.this);
                        }
                        else {

                            // treating this as a flag indicating validation of sign in failed
                            mListener.onDialogNegativeClick(RegisterDialogFragment.this);
                        }
                    }
                });

        return builder.create();
    }


    @Override
    public void onStart() {
        super.onStart();

        AlertDialog alertDialog = (AlertDialog)getDialog();

        // by default disable button
        final Button button = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
        button.setEnabled(false);

        // enable Sign In button only if form is complete
        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (isFormComplete())
                    button.setEnabled(true);
                else
                    button.setEnabled(false);
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (isFormComplete())
                    button.setEnabled(true);
                else
                    button.setEnabled(false);
            }
        });

        mPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().equals(mPassword.getText().toString())){

                    mPasswordConfirm.setError(getString(R.string.message_password_mismatch));
                }

                if (isFormComplete())
                    button.setEnabled(true);
                else
                    button.setEnabled(false);
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

                if (isFormComplete())
                    button.setEnabled(true);
                else
                    button.setEnabled(false);
            }
        });

        mEmailConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().equals(mEmail.getText().toString())){

                    mEmailConfirm.setError(getString(R.string.message_email_mismatch));
                }

                if (isFormComplete())
                    button.setEnabled(true);
                else
                    button.setEnabled(false);
            }
        });


    }

    private boolean isFormComplete(){

        if (!mUserName.getText().toString().equals("") && !mPassword.getText().toString().equals("")
                && !mPasswordConfirm.getText().toString().equals("") && !mEmail.getText().toString().equals("") && !mEmailConfirm.getText().toString().equals(""))
            return true;
        else
            return false;

    }
}



