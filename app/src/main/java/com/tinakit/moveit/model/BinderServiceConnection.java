package com.tinakit.moveit.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.tinakit.moveit.service.TrackerService;

/**
 * Created by Tina on 11/23/2015.
 */
public class BinderServiceConnection implements ServiceConnection {

    private static final String LOG = "SERVICE_CONNECTION";
    private static final boolean DEBUG = true;

    public TrackerService mBoundService;

    public static BinderServiceConnection getInstance() {

        return Holder.INSTANCE;
    }

    //Singleton holder
    static class Holder{
        static final BinderServiceConnection INSTANCE = new BinderServiceConnection();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        mBoundService = ((TrackerService.LocalBinder)service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBoundService = null;
    }

    //**********************************************************************************************
    //  doBindService()
    //**********************************************************************************************

    public void doBindService(Context context, Intent intent) {
        if (DEBUG) Log.d(LOG, "doBindService()");

        if(!isBound()) {
            if (DEBUG) Log.d(LOG, "Binding Service");
            context.bindService(intent /*new Intent(MainActivity.this, DataService.class)*/, this, Context.BIND_AUTO_CREATE);
        }
    }

    //**********************************************************************************************
    //  doUnBindService()
    //**********************************************************************************************
    public void doUnbindService(Context context) {
        if (DEBUG) Log.d(LOG, "doUnbindService()");

        if (isBound()) {
            if (DEBUG) Log.d(LOG, "Unbinding Service");

            //detach our existing connection.
            context.unbindService(this);
        }
    }

    //**********************************************************************************************
    //  isBound()  returns true if service is connected, otherwise return false
    //**********************************************************************************************

    public boolean isBound(){
        return(mBoundService != null);
    }
}
