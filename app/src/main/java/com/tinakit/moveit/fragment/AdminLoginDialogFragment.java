package com.tinakit.moveit.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 2/7/2016.
 */
public class AdminLoginDialogFragment extends DialogFragment {

    public static final String ADMIN_LOGIN_DIALOG_TAG = "ADMIN_LOGIN_DIALOG_TAG";
    public static final String ADMIN_LOGIN_PREFS = "ADMIN_LOGIN_PREFS";
    public static final String ADMIN_USERNAME = "ADMIN_USERNAME";
    public static final String ADMIN_PASSWORD = "ADMIN_PASSWORD";

    protected SharedPreferences mSharedPreferences;

    protected Spinner mSpinner;
    protected View mView;


    @Inject
    FitnessDBHelper mDatabaseHelper;

    /* The activity that creates an instance of this dialog fragment must
        * implement this interface in order to receive event callbacks.
        * Each method passes the DialogFragment in case the host needs to query it. */
    public interface AdminLoginDialogListener {
        public void onAdminLoginDialogPositiveClick(DialogFragment dialog);
        public void onAdminLoginDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    AdminLoginDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // DI
        ((CustomApplication)activity.getApplication()).getAppComponent().inject(this);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AdminLoginDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onStart() {
        //super.onStart() is where dialog.show() is actually called on the underlying dialog
        super.onStart();

        AlertDialog alertDialog = (AlertDialog)getDialog();

        final Button button = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
        button.setEnabled(false);

        final TextView username = (TextView)mView.findViewById(R.id.username);
        final TextView password = (TextView)mView.findViewById(R.id.password);

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!username.getText().toString().equals("") && !password.getText().toString().equals(""))
                    button.setEnabled(true);
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!username.getText().toString().equals("") && !password.getText().toString().equals(""))
                    button.setEnabled(true);
            }
        });

        int pref = mSharedPreferences.getInt(ADMIN_LOGIN_PREFS, 0);

        // auto-populate fields
        if (pref == 1){

            if(mSharedPreferences.contains(ADMIN_USERNAME)){
                username.setText(mSharedPreferences.getString(ADMIN_USERNAME, ""));
            }

            if(mSharedPreferences.contains(ADMIN_PASSWORD)){
                password.setText(mSharedPreferences.getString(ADMIN_PASSWORD, ""));
            }

            button.setEnabled(true);

        }

        mSpinner = (Spinner) mView.findViewById(R.id.admin_preference_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.admin_login_preferences));
        mSpinner.setAdapter(adapter);

        mSpinner.setSelection(pref);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_admin_login, null);

        final TextView username = (TextView)mView.findViewById(R.id.username);
        final TextView password = (TextView)mView.findViewById(R.id.password);

        mSpinner = (Spinner) mView.findViewById(R.id.admin_preference_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.admin_login_preferences));
        mSpinner.setAdapter(adapter);

        // check SharedPreferences for auto-populate fields
        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        int pref = mSharedPreferences.getInt(ADMIN_LOGIN_PREFS, 0);

        mSpinner.setSelection(pref);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putInt(ADMIN_LOGIN_PREFS, position);

                // if position = 1, want auto-populate fields, cache login details
                editor.putString(ADMIN_USERNAME, username.getText().toString());
                editor.putString(ADMIN_PASSWORD, password.getText().toString());

                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(mView)
                // Add action buttons
                .setPositiveButton(R.string.button_signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // register user as admin

                        // build user for admin
                        // admin is hard-coded at userid = 4, as defined in FitnessDBHelper.onCreate()

                        boolean isValidAdmin =  mDatabaseHelper.validateAdmin(username.getText().toString(), password.getText().toString());

                        if (isValidAdmin){

                            // treating this as a flag indicating validation of sign in succeeded
                            mListener.onAdminLoginDialogPositiveClick(AdminLoginDialogFragment.this);
                        }
                        else {

                            // treating this as a flag indicating validation of sign in failed
                            mListener.onAdminLoginDialogNegativeClick(AdminLoginDialogFragment.this);
                        }
                    }
                });

        return builder.create();
    }
}

