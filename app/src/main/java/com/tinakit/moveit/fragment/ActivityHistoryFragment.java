package com.tinakit.moveit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tinakit.moveit.R;
import com.tinakit.moveit.activity.TrackerActivity;
import com.tinakit.moveit.adapter.ActivityDetailListAdapter;

/**
 * Created by Tina on 9/22/2015.
 */
public class ActivityHistoryFragment extends Fragment {

    private ListView mActivityDetailListView;
    private ActivityDetailListAdapter mActivityDetailListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_history, container, false);

        mActivityDetailListView = (ListView)view.findViewById(R.id.activityDetailListView);
        mActivityDetailListAdapter = new ActivityDetailListAdapter(getActivity());
        mActivityDetailListView.setAdapter(mActivityDetailListAdapter);
        mActivityDetailListAdapter.setList(TrackerActivity.mActivityDetailList);
        //mActivityDetailListAdapter.notifyDataSetChanged();


        return view;
    }
}
