package com.tinakit.moveit.component;

import com.tinakit.moveit.activity.ActivityTracker;
import com.tinakit.moveit.activity.MainActivity;
import com.tinakit.moveit.activity.PickAvatar;
import com.tinakit.moveit.activity.RewardView;
import com.tinakit.moveit.adapter.EditRewardRecyclerAdapter;
import com.tinakit.moveit.adapter.EditUserRecyclerAdapter;
import com.tinakit.moveit.adapter.RewardRecyclerAdapter;
import com.tinakit.moveit.fragment.ActivityChooser;
import com.tinakit.moveit.fragment.UserProfile;
import com.tinakit.moveit.fragment.UserStats;
import com.tinakit.moveit.module.StorageModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Tina on 1/7/2016.
 */

@Singleton
@Component(

        // define the module that will handle creating the objects to be injected
        modules = {
                StorageModule.class
        }
)
public interface StorageComponent {

    void inject(MainActivity mainActivity);
    void inject(ActivityTracker activityTracker);
    void inject(PickAvatar pickAvatar);
    void inject(RewardView rewardView);
    void inject(RewardRecyclerAdapter rewardRecyclerAdapter);
    void inject(EditRewardRecyclerAdapter editRewardRecyclerAdapter);
    void inject(EditUserRecyclerAdapter editUserRecyclerAdapter);
    void inject(ActivityChooser activityChooser);
    void inject(UserProfile userProfile);
    void inject(UserStats userStats);
}
