package com.tinakit.moveit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.ViewPagerAdapter;
import com.tinakit.moveit.model.ActivityDetail;
import com.tinakit.moveit.model.IAdminFragmentObserver;
import com.tinakit.moveit.tab.SlidingTabLayout;

import java.util.List;

// source reference: http://blog.grafixartist.com/material-design-tabs-with-android-design-support-library/
/**
 * Created by Tina on 1/12/2016.
 */
public class Admin extends Fragment {

    // CONSTANTS
    public static final String ADMIN_TAG = "ADMIN_TAG";
    public static final String ADMIN_BACKSTACK_NAME = "Admin";

    //@Inject
    //FitnessDBHelper mDatabaseHelper;

    // UI
    protected FragmentActivity mFragmentActivity;
    private View rootView;

    // SlidingTabLayout
    protected ViewPager mViewPager;
    protected ViewPagerAdapter mViewPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentActivity  = (FragmentActivity)super.getActivity();
        rootView = inflater.inflate(R.layout.admin_layout, container, false);
        mFragmentActivity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Dagger 2 injection
        //((CustomApplication)getActivity().getApplication()).getAppComponent().inject(this);

        initializeUI();

        setActionListeners();

        return rootView;
    }

    private void initializeUI(){

        //ViewPager
        mViewPager = (ViewPager)rootView.findViewById(R.id.tab_viewpager_admin);
        if (mViewPager != null){
            setupViewPager(mViewPager);
        }

        //TabLayout
        mSlidingTabLayout = (SlidingTabLayout)rootView.findViewById(R.id.tabLayout);
        mSlidingTabLayout.setViewPager(mViewPager);


    }

    private void setActionListeners(){

        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //**********************************************************************************************
    //  setUpViewPager()
    //**********************************************************************************************
    private void setupViewPager(ViewPager viewPager){

        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        // create Tabs
        mViewPagerAdapter.addFrag(new AdminInBox(), getString(R.string.admin_tab_requests));
        mViewPagerAdapter.addFrag(new EditReward(), getString(R.string.admin_tab_rewards));
        mViewPagerAdapter.addFrag(new UserProfile(), getString(R.string.admin_tab_users));
        mViewPagerAdapter.addFrag(new ActivityHistory(), getString(R.string.admin_tab_history));
        //mViewPagerAdapter.addFrag(new AdminSettings(), getString(R.string.admin_tab_settings));
        viewPager.setAdapter(mViewPagerAdapter);

    }


}
