package com.tinakit.moveit.activity;

import android.support.v4.app.Fragment;
import com.tinakit.moveit.fragment.ActivityChooserFragment;


public class ActivityChooser extends SingleFragmentActivity {
        @Override
        protected Fragment createFragment(){
            return new ActivityChooserFragment();
        }
}
