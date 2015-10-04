package com.tinakit.moveit.activity;

import android.support.v4.app.Fragment;

import com.tinakit.moveit.fragment.RegisterUserFragment;

/**
 * Created by Tina on 7/3/2015.
 */
public class RegisterUserActivity  extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new RegisterUserFragment();
    }
}

