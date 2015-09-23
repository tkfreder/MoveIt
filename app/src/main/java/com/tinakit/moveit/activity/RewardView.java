package com.tinakit.moveit.activity;

import android.support.v4.app.Fragment;

import com.tinakit.moveit.fragment.ActivityHistoryFragment;
import com.tinakit.moveit.fragment.RewardViewFragment;

/**
 * Created by Tina on 9/23/2015.
 */
public class RewardView extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new RewardViewFragment();
    }
}