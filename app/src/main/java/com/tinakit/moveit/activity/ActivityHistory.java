package com.tinakit.moveit.activity;

import android.support.v4.app.Fragment;

import com.tinakit.moveit.fragment.ActivityChooserFragment;
import com.tinakit.moveit.fragment.ActivityHistoryFragment;

/**
 * Created by Tina on 9/22/2015.
 */
public class ActivityHistory extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new ActivityHistoryFragment();
    }
}
