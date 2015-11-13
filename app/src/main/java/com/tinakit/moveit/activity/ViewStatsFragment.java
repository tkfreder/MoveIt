package com.tinakit.moveit.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.ViewStatsRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;

/**
 * Created by Tina on 10/26/2015.
 */
public class ViewStatsFragment extends Fragment {

    //UI Widgets
    private RecyclerView mRecyclerView;
    private ViewStatsRecyclerAdapter mViewStatsRecyclerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fix the orientation to portrait
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.view_stats);

        // Get singleton instance of database
        FitnessDBHelper databaseHelper = FitnessDBHelper.getInstance(this);


        //RecyclerView
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); //child items have fixed dimensions, allows the RecyclerView to optimize better by figuring out the exact height and width of the entire list based on the adapter.


        // The number of Columns
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mViewStatsRecyclerAdapter = new ViewStatsRecyclerAdapter(this, databaseHelper.getUsers());
        mRecyclerView.setAdapter(mViewStatsRecyclerAdapter);


    }
}
