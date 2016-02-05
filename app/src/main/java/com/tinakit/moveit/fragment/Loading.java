package com.tinakit.moveit.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinakit.moveit.R;

/**
 * Created by Tina on 2/4/2016.
 */
public class Loading extends Fragment {

    public static final String LOADING_TAG = "LOADING_TAG";

    protected FragmentActivity mFragmentActivity;
    protected View mRootView;
    private Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentActivity = (FragmentActivity)super.getActivity();
        mRootView = inflater.inflate(R.layout.loading_animation, container, false);

        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        startAnim();

        mHandler = new Handler();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                stopAnim();
            }
        }, 1000 * 5);

    }

    void startAnim(){
        mRootView.findViewById(R.id.avloadingIndicatorView).setVisibility(View.VISIBLE);
    }

    void stopAnim(){
        mRootView.findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
    }
}
