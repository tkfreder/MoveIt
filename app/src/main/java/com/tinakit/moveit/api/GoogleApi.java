package com.tinakit.moveit.api;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.tinakit.moveit.activity.ActivityTracker;

/**
 * Created by Tina on 12/12/2015.
 */
public class GoogleApi implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // DEBUG
    private static final boolean DEBUG = true;
    private static final String LOG = "GoogleApi";

    // CONSTANTS
    protected static GoogleApiClient mGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static final String GOOGLE_API_INTENT = "GOOGLE_API_INTENT";

    // instance fields
    private static FragmentActivity mFragmentActivity;

    //GOOGLE PLAY SERVICES
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private static boolean mResolvingError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    public GoogleApi(FragmentActivity fragmentActivity){

        mFragmentActivity = fragmentActivity;
    }

    public GoogleApiClient client(){

        return mGoogleApiClient;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (DEBUG) Log.d(LOG, "GoogleAPIClient Connection successful.");

        //TODO:  this isn't always accurate so not sure if it should be used
        //get the starting point
        //updateCache(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));

        Intent intent = new Intent(GOOGLE_API_INTENT);
        intent.putExtra(ActivityTracker.ACTIVITY_TRACKER_BROADCAST_RECEIVER, GOOGLE_API_INTENT);
        LocalBroadcastManager.getInstance(mFragmentActivity).sendBroadcast(intent);

        //TODO:  send broadcast message, register receiver on activitytracker, then call displayStartMap() and startServices() from ActivityTracker from the broadcastreceiver
        //display map of starting point
        //displayStartMap();

        //start getting location data after there is a connection
        //startServices(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(mFragmentActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    public boolean isConnectedToGoogle(){

        return(mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    @Override
    public void onConnectionFailed(com.google.android.gms.common.ConnectionResult result) {

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(mFragmentActivity/*mFragmentActivity*/, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    /**
     * Method to verify google play services on the device, will direct user to Google Play Store if not installed
     * */
    public static boolean servicesAvailable() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(mFragmentActivity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, mFragmentActivity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                if (DEBUG) Log.d(LOG, "This device does not support Google Play Services.");
                mFragmentActivity.finish();
            }
            return false;
        }
        return true;
    }

     /* Called from ErrorDialogFragment when the dialog is dismissed. */
        public static void onDialogDismissed() {
        mResolvingError = false;
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(mFragmentActivity.getSupportFragmentManager(), "errordialog");
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            onDialogDismissed();
        }
    }

}
