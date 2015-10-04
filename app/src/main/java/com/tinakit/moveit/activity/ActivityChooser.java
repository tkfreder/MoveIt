package com.tinakit.moveit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.ChooserRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.User;


public class ActivityChooser extends AppCompatActivity {

    //UI Widgets
    private TextView mUserName;
    private ImageView mAvatar;
    private RecyclerView mRecyclerView;
    private ChooserRecyclerAdapter mChooserRecyclerAdapter;
    //TODO: replace test data with intent bundle from login screen
    //Session variables
    //private User mUser = new User("Lucy","password",false,40,"tiger");
    private User mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_chooser);

        //View view = inflater.inflate(R.layout.fragment_activity_chooser, container, false);

        //TODO: get user details from Intent bundle or SharedPreferences

        //wire up UI widgets
        mUserName = (TextView)findViewById(R.id.username);

        //TODO: to delete
        //delete database
        deleteDatabase("fitnessDatabase");

        //TODO: to delete
        //insert User in DB
        // Create sample data
        User sampleUser = new User();
        sampleUser.setUserName("Lucy");
        sampleUser.setIsAdmin(false);
        sampleUser.setWeight(40);
        sampleUser.setAvatarFileName("tiger");

        // Get singleton instance of database
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(this);

        // Add sample user to the database if the user doesn't exist already
        if (!databaseHelper.hasUser(sampleUser))
            databaseHelper.addUser(sampleUser);

        //get user from DB
        mUser = databaseHelper.getUser(sampleUser.getUserName());

        //display username
        mUserName.setText(mUser.getUserName());

        mAvatar = (ImageView)findViewById(R.id.avatar);
        mAvatar.setImageResource(getResources().getIdentifier(sampleUser.getAvatarFileName() , "drawable", getPackageName()));

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChooserRecyclerAdapter = new ChooserRecyclerAdapter(this);
        mRecyclerView.setAdapter(mChooserRecyclerAdapter);

    }
}
