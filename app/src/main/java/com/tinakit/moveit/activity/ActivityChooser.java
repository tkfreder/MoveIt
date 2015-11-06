package com.tinakit.moveit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.ChooserRecyclerAdapter;
import com.tinakit.moveit.adapter.MultiChooserRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.model.ActivityType;
import com.tinakit.moveit.model.User;

import java.util.ArrayList;
import java.util.List;


public class ActivityChooser extends AppCompatActivity {

    //UI Widgets
    private RecyclerView mRecyclerView;
    private MultiChooserRecyclerAdapter mMultiChooserRecyclerAdapter;
    private Button mNextButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.multi_activity_chooser);

        // Get singleton instance of database
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(this);
        List<User> userList = databaseHelper.getUsers();
        List<ActivityType> activityTypeList = databaseHelper.getActivityTypes();


        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.


        // The number of Columns
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mMultiChooserRecyclerAdapter = new MultiChooserRecyclerAdapter(this, userList, activityTypeList);
        mRecyclerView.setAdapter(mMultiChooserRecyclerAdapter);

        //next button
        mNextButton = (Button)findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: replace this with BroadcastReceiver to listen for RecyclerView has at least one user activity selected
                if (ActivityTracker.mActivityDetail.getUserList().size() > 0){

                    //get users and activity type selected
                    startActivity(new Intent(getApplicationContext(), ActivityTracker.class));
                }
                else {

                    Toast.makeText(getApplicationContext(), "Please select an activity for at least one user.", Toast.LENGTH_LONG);
                }


            }
        });


    }
}
