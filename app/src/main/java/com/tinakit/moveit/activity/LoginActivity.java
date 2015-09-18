package com.tinakit.moveit.activity;

import android.support.v4.app.Fragment;
import com.tinakit.moveit.fragment.LoginFragment;

/**
 * Created by Tina on 7/2/2015.
 */
public class LoginActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new LoginFragment();
    }
}
