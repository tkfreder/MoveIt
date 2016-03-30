package com.tinakit.moveit.module;

import com.tinakit.moveit.activity.ActivityTracker;
import com.tinakit.moveit.activity.MainActivity;
import com.tinakit.moveit.activity.PickAvatar;
import com.tinakit.moveit.adapter.EditRewardRecyclerAdapter;
import com.tinakit.moveit.adapter.UserProfileRecyclerAdapter;
import com.tinakit.moveit.fragment.ActivityChooser;
import com.tinakit.moveit.fragment.ActivityHistory;
import com.tinakit.moveit.fragment.AdminInBox;
import com.tinakit.moveit.fragment.AdminLoginDialogFragment;
import com.tinakit.moveit.fragment.Charts;
import com.tinakit.moveit.fragment.EditReward;
import com.tinakit.moveit.fragment.EditUser;
import com.tinakit.moveit.fragment.MapFragment;
import com.tinakit.moveit.fragment.SecretQuestionDialogFragment;
import com.tinakit.moveit.fragment.UserProfile;
import com.tinakit.moveit.fragment.UserStats;
import com.tinakit.moveit.fragment.UserStatsMain;
import com.tinakit.moveit.model.RegisterDialogFragment;
import com.tinakit.moveit.model.UserListObservable;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Tina on 1/7/2016.
 */

@Singleton
@Component(

        // define the module that will handle creating the objects to be injected
        modules = {
                // these classes correspond to modules referenced in CustomApplicaiton.java
                StorageModule.class, ApiModule.class
        }
)

// interface between AppComponent and classes which will be using the injected objects
public interface AppComponent {

    void inject(MainActivity mainActivity);
    void inject(ActivityTracker activityTracker);
    void inject(PickAvatar pickAvatar);
    void inject(EditRewardRecyclerAdapter editRewardRecyclerAdapter);
    void inject(UserProfileRecyclerAdapter userProfileRecyclerAdapter);
    void inject(ActivityChooser activityChooser);
    void inject(ActivityHistory activityHistory);
    void inject(UserProfile userProfile);
    void inject(UserStatsMain userStatsMain);
    void inject(MapFragment mapFragment);
    void inject(EditReward editReward);
    void inject(EditUser editUser);
    void inject(UserStats userStats);
    void inject(AdminInBox adminInBox);
    void inject(RegisterDialogFragment registerDialogFragment);
    void inject(AdminLoginDialogFragment adminLoginDialogFragment);
    void inject(SecretQuestionDialogFragment secretQuestionDialogFragment);
    void inject(Charts charts);
    void inject(UserListObservable userListObservable);
}
