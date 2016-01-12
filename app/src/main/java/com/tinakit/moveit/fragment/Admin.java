package com.tinakit.moveit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinakit.moveit.R;
import com.tinakit.moveit.adapter.UserProfileRecyclerAdapter;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.module.CustomApplication;
import com.tinakit.moveit.tab.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

    // source reference: http://blog.grafixartist.com/material-design-tabs-with-android-design-support-library/
/**
 * Created by Tina on 1/12/2016.
 */
public class Admin extends Fragment {

    // CONSTANTS
    public static final String ADMIN_TAG = "ADMIN_TAG";

    //@Inject
    //FitnessDBHelper mDatabaseHelper;

    // UI
    protected FragmentActivity mFragmentActivity;
    private View rootView;

    // SlidingTabLayout
    private ViewPager mViewPager;
    protected ViewPagerAdapter mViewPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    private TabLayout mTabLayout;

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

        mTabLayout = (TabLayout)rootView.findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

        //TabLayout
        //mSlidingTabLayout = (SlidingTabLayout)rootView.findViewById(R.id.tabLayout);
        //mSlidingTabLayout.setViewPager(mViewPager);

        //set tab index if this is redirected
        //if(getIntent().hasExtra("tab_index")){

        //    mViewPager.setCurrentItem((int)getIntent().getExtras().get("tab_index"));
        //}
    }

    private void setActionListeners(){

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                mViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    //**********************************************************************************************
    //  ViewPagerAdapter
    //**********************************************************************************************

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = mFragmentList.get(position);

            return fragment;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }

    }

    //**********************************************************************************************
    //  setUpViewPager()
    //**********************************************************************************************
    private void setupViewPager(ViewPager viewPager){

        mViewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

        // create Tabs
        mViewPagerAdapter.addFrag(new AdminInBox(), getString(R.string.admin_tab_inbox));
        mViewPagerAdapter.addFrag(new UserProfile(), getString(R.string.admin_tab_users));
        mViewPagerAdapter.addFrag(new ActivityHistory(), getString(R.string.admin_tab_history));
        mViewPagerAdapter.addFrag(new AdminSettings(), getString(R.string.admin_tab_settings));
        viewPager.setAdapter(mViewPagerAdapter);
    }


}
