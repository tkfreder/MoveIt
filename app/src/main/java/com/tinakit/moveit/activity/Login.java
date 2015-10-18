package com.tinakit.moveit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tinakit.moveit.R;
import com.tinakit.moveit.utility.DialogUtility;

/**
 * Created by Tina on 10/4/2015.
 */
public class Login extends AppCompatActivity{

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.fragment_login);

        //wire up UI widgets
        mUserNameEditText = (EditText)findViewById(R.id.username);
        mPasswordEditText = (EditText)findViewById(R.id.password);
        mSignInButton = (Button)findViewById(R.id.signInButton);
        mSignUpButton = (Button)findViewById(R.id.signUpButton);



        //OnClickListeners

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validateLogin returns user index
                //TODO: for now use user index as UserId
                int userId = -1;
                //userId = validateLogin(mUserNameEditText.getText().toString(), mPasswordEditText.getText().toString());

                if (userId != -1) {

                    //save userId in Preferences
                    //saveUserIdInPreferences(getActivity(), userId);

                    //display new screen based on whether user is parent or child
                    //displayNewScreen(getActivity(), userId);
                } else {
                    DialogUtility.displayAlertDialog(getApplicationContext(), "Alert", "Your username/password is incorrect. Try again.", "OK");
                }
            }
        });

    }
}
