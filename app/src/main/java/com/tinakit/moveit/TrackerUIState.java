package com.tinakit.moveit;

import com.tinakit.moveit.api.LocationApi;

/**
 * Created by Tina on 12/15/2015.
 */
public interface TrackerUIState {

    public void onStart();
    public void onPause();
    public void onResume();
    public void onStop();

}


