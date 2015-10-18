package com.tinakit.moveit.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.Login;
import com.tinakit.moveit.utility.DialogUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tina on 7/3/2015.
 */
public class RegisterUserFragment extends Fragment {

    //UI widgets
    private EditText mUserNameEditText;
    private EditText mPasswordEditText;
    private LinearLayout mWeightLinearLayout;
    private Spinner mWeightSpinner;
    private Button mSubmitButton;
    private TextView mHeadingTextView;

    //globals
    private int mUserId;

    //constants
    public static final String SHARED_PREFERENCES_USERNAMES = "SHARED_PREFERENCES_USERNAMES";
    public static final String SHARED_PREFERENCES_PASSWORDS = "SHARED_PREFERENCES_PASSWORDS";
    public static final String SHARED_PREFERENCES_COINS = "SHARED_PREFERENCES_COINS";
    public static final String PACKAGE_NAME = "com.tinakit.moveit";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_user, parent, false);

        //wire up UI widgets
        mHeadingTextView = (TextView)view.findViewById(R.id.heading);
        mWeightSpinner = (Spinner)view.findViewById(R.id.weight);
        mWeightSpinner.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.string_array_weight)));
        mWeightLinearLayout = (LinearLayout)view.findViewById(R.id.weightLayout);

        if (getNumberOfUsers() == 0){
            mHeadingTextView.setText("SIGN IN (Parents Only)");
            DialogUtility.displayAlertDialog(getActivity(), "Warning: Are you a parent?", "The first registered user must be a parent.", "OK");
            mWeightLinearLayout.setVisibility(View.GONE);
        }

        mUserNameEditText = (EditText)view.findViewById(R.id.username);
        mUserNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mPasswordEditText = (EditText)view.findViewById(R.id.password);
        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSubmitButton = (Button)view.findViewById(R.id.submitButton);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if(validateForm()){
                mUserId = saveUsernamePassword(mUserNameEditText.getText().toString(), mPasswordEditText.getText().toString());
                if(mUserId == -1)
                    Toast.makeText(getActivity(), "This username exists already.  Enter a different username.", Toast.LENGTH_SHORT);
                else{
                    getActivity().finish();
                    //Login.displayNewScreen(getActivity(), mUserId);
                }
            }

        }});

        return view;
    }

    public int getNumberOfUsers(){
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Login.SHARED_PREFERENCES_MOVEIT, Context.MODE_PRIVATE);
        String commaDelimitedUsernames = sharedPreferences.getString(RegisterUserFragment.SHARED_PREFERENCES_USERNAMES, "");
        List<String> usernameList = new ArrayList<String>(Arrays.asList(commaDelimitedUsernames.split(",")));

        //Set<String> usernames = sharedPreferences.getStringSet(RegisterUserFragment.SHARED_PREFERENCES_USERNAMES, new HashSet<String>());

        if(commaDelimitedUsernames.equals(""))
            return 0;
        else
            return usernameList.size();
    }

    private void enableUI(){
        if(!mUserNameEditText.getText().toString().trim().equals("") && !mPasswordEditText.getText().toString().trim().equals(""))
            mSubmitButton.setEnabled(false);
    }

    private boolean validateForm(){
        if(mUserNameEditText.getText().toString().trim().equals("") || mPasswordEditText.getText().toString().trim().equals("")){
            DialogUtility.displayAlertDialog(getActivity(), "Incomplete form", "Please fill out Username and Password.", "OK");
            //mSubmitButton.setEnabled(false);
            return false;
        } else if (mWeightSpinner.getSelectedItemPosition() == 0 && getNumberOfUsers() != 0) {
            DialogUtility.displayAlertDialog(getActivity(), "Incomplete form", "Please choose a Weight.", "OK");
            //mSubmitButton.setEnabled(false);
            return false;
        } else{
            //mSubmitButton.setEnabled(true);
            return true;
        }
    }

    //this method must correspond to LoginFragment.validateLogin()
    //saves username and password
    //returns the userId, which is the index on the username/password list
    private int saveUsernamePassword(String username, String password){

        //get data from SharedPreferences
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Login.SHARED_PREFERENCES_MOVEIT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //check if username already exists
        //Set<String> usernames = sharedPreferences.getStringSet(SHARED_PREFERENCES_USERNAMES, new LinkedHashSet<String>());


        int size = 0;

        String commaDelimitedUsernames = sharedPreferences.getString(SHARED_PREFERENCES_USERNAMES, "");
        //create an arraylist out of comma-delimited string
        List<String> usernameList = new ArrayList<String>(Arrays.asList(commaDelimitedUsernames.split(",")));


        if(usernameList.contains(username))
            return -1;
        //username does not exist yet
        else{
            //add username to the list
            commaDelimitedUsernames = commaDelimitedUsernames.concat(commaDelimitedUsernames.equals("") ? username : "," + username);
            editor.putString(SHARED_PREFERENCES_USERNAMES, commaDelimitedUsernames);
            String[] array = commaDelimitedUsernames.split(",");
            size = array.length;

            //get comma-delimited password
            String commaDelimitedPasswords = sharedPreferences.getString(SHARED_PREFERENCES_PASSWORDS, "");
            commaDelimitedPasswords = commaDelimitedPasswords.concat(commaDelimitedPasswords.equals("") ? password : "," + password);
            editor.putString(SHARED_PREFERENCES_PASSWORDS, commaDelimitedPasswords);

            //add an empty string for coins as a placeholder
            String commaDelimitedCoins = sharedPreferences.getString(SHARED_PREFERENCES_COINS, "");
            commaDelimitedCoins = commaDelimitedCoins.concat(commaDelimitedCoins.equals("") ? "0" : "," + "0");
            editor.putString(SHARED_PREFERENCES_COINS, commaDelimitedCoins);


            editor.commit();

            //userid is the index, which is one less than the size
            return (size - 1);

        }

    }


}

