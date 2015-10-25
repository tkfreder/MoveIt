package com.tinakit.moveit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.ChooserRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;


public class ActivityChooser extends AppCompatActivity {

    //UI Widgets
    private RecyclerView mRecyclerView;
    private ChooserRecyclerAdapter mChooserRecyclerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_chooser);

        // Get singleton instance of database
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(this);

        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.


        // The number of Columns
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mChooserRecyclerAdapter = new ChooserRecyclerAdapter(this);
        mRecyclerView.setAdapter(mChooserRecyclerAdapter);

    }
}
