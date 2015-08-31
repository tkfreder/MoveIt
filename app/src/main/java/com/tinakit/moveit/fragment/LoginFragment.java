package com.tinakit.moveit.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.RegisterUserActivity;
import com.tinakit.moveit.activity.TrackerActivity;
import com.tinakit.moveit.activity.UserSummaryActivity;
import com.tinakit.moveit.utility.Dialog;
import com.tinakit.moveit.activity.LoginActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Tina on 7/2/2015.
 */
public class LoginFragment extends Fragment {

    //UI widgets
    //private ListView mUserListView;
    private EditText mUserNameEditText;
    private EditText mPasswordEditText;
    private Button mSignInButton;
    private Button mSignUpButton;

    //TODO:  why can't this be accessed if set to protected?
    public static final String SHARED_PREFERENCES_MOVEIT = "SHARED_PREFERENCES_MOVEIT";
    public static final String SHARED_PREFERENCES_USERNAMES = "SHARED_PREFERENCES_USERNAMES";

    private int mUserId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set visiblility of UI widgets for signing in based on whether there are existing user accounts
        //if(getNumberOfUsers() == 0) {
            Intent intent = new Intent(getActivity(), RegisterUserActivity.class);
            startActivity(intent);
        //}

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, parent, false);

        //wire up UI widgets
        mUserNameEditText = (EditText)view.findViewById(R.id.username);
        mPasswordEditText = (EditText)view.findViewById(R.id.password);
        mSignInButton = (Button)view.findViewById(R.id.signInButton);
        mSignUpButton = (Button)view.findViewById(R.id.signUpButton);



        //OnClickListeners

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validateLogin returns user index
                //TODO: for now use user index as UserId
                int userId = -1;
                userId = validateLogin(mUserNameEditText.getText().toString(), mPasswordEditText.getText().toString());

                if(userId != -1){

                    //save userId in Preferences
                    saveUserIdInPreferences(getActivity(), userId);

                    //display new screen based on whether user is parent or child
                    displayNewScreen(getActivity(), userId);
                }else{
                    Dialog.displayAlertDialog(getActivity(), "Alert", "Your username/password is incorrect. Try again.", "OK");
                }
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterUserActivity.class);
                startActivity(intent);
            }
        });

        /*
        mUserListView = (ListView)view.findViewById(R.id.userListView);
        mUserListView.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.string_array_username)));

        mUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //save the userId to global
                mUserId = position;
                displayPasswordDialog("Type your password \n (hint: it's your username)");
            }
        });
*/
        return view;
    }

/*

    private void displayPasswordDialog(String message){
        //DISPLAY PASSWORD DIALOG

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        //TODO:  change this eventually, for now, the usernames and passwords are the same.
        String[] username = getResources().getStringArray(R.array.string_array_password);

        alert.setTitle("Hello, " + username[mUserId]);
        alert.setMessage(message);

        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        alert.setView(input);

        alert.setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();

                if(validatePassword(value)){
                    //if correct password, display the appropriate screen
                    displayNewScreen();
                }else{
                    //TODO: display alert that password was incorrect
                    // redisplay password dialog
                    displayPasswordDialog("That password is incorrect.  Try again.");
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private boolean validatePassword(String input){

        String[] password = getResources().getStringArray(R.array.string_array_password);
        if(password[mUserId].equals(input))
            return true;
        else
            return false;
    }
*/

    @Override
    public void onStart() {
        super.onStart();


    }

    public int getNumberOfUsers(){
        int i = 0;
        //SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_MOVEIT, Context.MODE_PRIVATE);
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_MOVEIT, Context.MODE_PRIVATE);
        String commaDelimitedUsernames = sharedPreferences.getString(SHARED_PREFERENCES_USERNAMES, "");
        List<String> usernameList = new ArrayList<String>(Arrays.asList(commaDelimitedUsernames.split(",")));

        //Set<String> usernames = sharedPreferences.getStringSet(RegisterUserFragment.SHARED_PREFERENCES_USERNAMES, new HashSet<String>());

        if(commaDelimitedUsernames.equals(""))
            return 0;
        else
            return usernameList.size();
    }

    //this method must correspond to RegisterUserFragment.saveUsernamePassword()
    protected int validateLogin(String username, String password){

        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_MOVEIT, Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPreferences.edit();

        //check if username already exists
        //Set<String> usernames = sharedPreferences.getStringSet(RegisterUserFragment.SHARED_PREFERENCES_USERNAMES, new HashSet<String>());
        String commaDelimitedUsernames = sharedPreferences.getString(RegisterUserFragment.SHARED_PREFERENCES_USERNAMES, "");
        List<String> usernameList = new ArrayList<String>(Arrays.asList(commaDelimitedUsernames.split(",")));


        int userIndex = usernameList.indexOf(username);
        //this username exists already
        if( userIndex != -1){

            //check if password is correct for this username
            //Set<String> passwords = sharedPreferences.getStringSet(RegisterUserFragment.SHARED_PREFERENCES_PASSWORDS, new HashSet<String>());
            //List<String> passwordList = new ArrayList<String>(passwords);
            String commaDelimitedPasswords = sharedPreferences.getString(RegisterUserFragment.SHARED_PREFERENCES_PASSWORDS, "");
            List<String> passwordList = new ArrayList<String>(Arrays.asList(commaDelimitedPasswords.split(",")));

            if(!passwordList.get(userIndex).equals(password))
                userIndex = -1;// incorrect password for the username
        }else
            userIndex = -1;

        return userIndex;

        //List<String> usernameList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.string_array_username)));
        //List<String> passwordList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.string_array_password)));

        //check if there is a key/value that matches username/password in SharedPreferences

        /*
        //get data from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(LoginFragment.SHARED_PREFERENCES_MOVEIT, 0);



        if(sharedPreferences.contains(username)){
            if(sharedPreferences.getString(username,"").equals(password)){
                //if username/password is valid, get their userid and save it to the global
                if(sharedPreferences.contains(username+UserSummaryFragment.USER_ID)){
                    //mUserId = sharedPreferences.getInt(username+UserSummaryFragment.USER_ID, -1);
                    return sharedPreferences.getInt(username+UserSummaryFragment.USER_ID, -1);

                }
            }
        }
        */

/*

        userIndex = usernameList.indexOf(username);

        if(userIndex != -1){
            if(!passwordList.get(userIndex).equals(password)){
                userIndex = -1;
            }
        }
        */

    }

    protected static void displayNewScreen(Context context, int userId){

        //if user is a parent, go to User Summary screen
        if(userId == 0){
            Intent intent = new Intent(context, UserSummaryActivity.class);
            context.startActivity(intent);
        }//user is a child, go to Activity Chooser screen
        else{
            Intent intent = new Intent(context, TrackerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("userId",userId);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    protected void saveUserIdInPreferences(Context context, int userId) {

        //save the userid in SharedPreferences
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_MOVEIT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("UserId", userId);
    }
}
