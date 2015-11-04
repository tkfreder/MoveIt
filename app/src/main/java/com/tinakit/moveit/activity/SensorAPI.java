package com.tinakit.moveit.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
//import com.google.android.gms.fit.samples.common.logger.Log;
//import com.google.android.gms.fit.samples.common.logger.LogView;
//import com.google.android.gms.fit.samples.common.logger.LogWrapper;
//import com.google.android.gms.fit.samples.common.logger.MessageOnlyLogFilter;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.tinakit.moveit.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * This sample demonstrates how to use the Sensors API of the Google Fit platform to find
 * available data sources and to register/unregister listeners to those sources. It also
 * demonstrates how to authenticate a user with Google Play Services.
 */
public class SensorAPI extends AppCompatActivity {

    //TODO: DEBUG
    private TextView message;
    private Button startRecordButton;
    private Button stopRecordButton;
    private Button historyButton;
    private int mStepCount = 0;
    private boolean mIsConnected = false;
    private static final String DATE_FORMAT = "EEE MMM dd HH:mm";

    public static final String TAG = "BasicSensorsApi";
    // [START auth_variable_references]
    private static final int REQUEST_OAUTH = 1;

    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    private GoogleApiClient mClient = null;
    // [END auth_variable_references]

    // [START mListener_variable_reference]
    // Need to hold a reference to this listener, as it's passed into the "unregister"
    // method in order to stop all sensors from sending data to this listener.
    private OnDataPointListener mListener;
    private OnDataPointListener listener;
    // [END mListener_variable_reference]


    @Override
    public boolean supportRequestWindowFeature(int featureId) {
        return super.supportRequestWindowFeature(featureId);
    }

    // [START auth_oncreate_setup_beginning]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Put application specific code here.
        // [END auth_oncreate_setup_beginning]
        setContentView(R.layout.activity_sensor_api);

        //TODO: DEBUG
        message = (TextView)findViewById(R.id.stepCount);
        startRecordButton = (Button)findViewById(R.id.startRecording);
        stopRecordButton = (Button)findViewById(R.id.stopRecording);
        historyButton = (Button)findViewById(R.id.historyButton);


        // This method sets up our custom logger, which will print all log messages to the device
        // screen, as well as to adb logcat.
        initializeLogging();

        // [START auth_oncreate_setup_ending]

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        buildFitnessClient();

    }



    // [END auth_oncreate_setup_ending]

    // [START auth_build_googleapiclient_beginning]
    /**
     *  Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     *  to connect to Fitness APIs. The scopes included should match the scopes your app needs
     *  (see documentation for details). Authentication will occasionally fail intentionally,
     *  and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     *  can address. Examples of this include the user never having signed in before, or having
     *  multiple accounts on the device and needing to specify which account to use, etc.
     */
    public void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {


                                //register request
                                //registerSensorRequest(mClient);

                                //register Recording API
                                //registerRecordingApi();
                                mIsConnected = true;
                                startRecordButton.setEnabled(true);


                                //Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.
                                // Put application specific code here.
                                // [END auth_build_googleapiclient_beginning]
                                //  What to do? Find some data sources!
                                //findFitnessDataSources();

                                // [START auth_build_googleapiclient_ending]
                            }

                            @Override
                            public void onConnectionSuspended(int i) {

                                mIsConnected = false;


                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    //    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    //    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                //Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            SensorAPI.this, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                //        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(SensorAPI.this,
                                                REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                //        Log.e(TAG, "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                )
                .build();
    }
    // [END auth_build_googleapiclient_ending]

    // [START auth_connection_flow_in_activity_lifecycle_methods]
    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        //Log.i(TAG, "Connecting...");

        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mClient.isConnected()) {
            mClient.disconnect();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {

                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }

            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    public void getHistory(View view){

        //set time range
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = calendar.getTimeInMillis();


        //build data read request
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.HOURS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.HistoryApi.readData(mClient, readRequest).setResultCallback(new ResultCallback<DataReadResult>() {

            @Override
            public void onResult(DataReadResult dataReadResult) {

                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                //The bucket is just a collection of datasets, but the API gives us choice, if there is only one bucket, we can access datasets without the additional step of iterating through the buckets.
                if (dataReadResult.getBuckets().size() > 0) {
                    Log.i(TAG, "DataSet.size(): "
                            + dataReadResult.getBuckets().size());
                    for (Bucket bucket : dataReadResult.getBuckets()) {
                        List<DataSet> dataSets = bucket.getDataSets();
                        for (DataSet dataSet : dataSets) {
                            Log.i(TAG, "dataSet.dataType: " + dataSet.getDataType().getName());

                            for (DataPoint dp : dataSet.getDataPoints()) {
                                describeDataPoint(dp, dateFormat);
                            }
                        }
                    }
                } else if (dataReadResult.getDataSets().size() > 0) {
                    Log.i(TAG, "dataSet.size(): " + dataReadResult.getDataSets().size());
                    for (DataSet dataSet : dataReadResult.getDataSets()) {
                        Log.i(TAG, "dataType: " + dataSet.getDataType().getName());

                        for (DataPoint dp : dataSet.getDataPoints()) {
                            describeDataPoint(dp, dateFormat);
                        }
                    }
                }

            }
        });

    }

    public void describeDataPoint(DataPoint dp, DateFormat dateFormat) {
        String msg = "dataPoint: "
                + "type: " + dp.getDataType().getName() +"\n"
                + ", range: [" + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + "-" + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + "]\n"
                + ", fields: [";

        for(Field field : dp.getDataType().getFields()) {
            msg += field.getName() + "=" + dp.getValue(field) + " ";
        }

        msg += "]";
        //Log.i(TAG, msg);
        message.setText(msg + message.getText());
    }

    //**********************************************************************************************
    //  isConnected()  returns true if GoogleApiClient is connected, otherwise return false
    //**********************************************************************************************

    private boolean isConnected(){
        return(mClient != null && mIsConnected);
    }

    public void registerRecordingApi(View view){

        if (!isConnected())
            return;

        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA)
            .setResultCallback(new ResultCallback<Status>() {

                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        if (status.getStatusCode() == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                            Log.i(TAG, "Existing subscription for activity detected.");
                        } else {
                            Log.i(TAG, "Successfully subscribed!");
                            startRecordButton.setEnabled(false);
                            stopRecordButton.setEnabled(true);
                        }
                    } else {
                        Log.i(TAG, "There was a problem subscribing.");
                    }
                }
            });



    }

    public void unsubscribeRecordingApi(View view){

        Fitness.RecordingApi.unsubscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Successfully unsubscribed recording api");
                            startRecordButton.setEnabled(true);
                            stopRecordButton.setEnabled(false);
                        } else {
                            // Subscription not removed
                            Log.i(TAG, "Failed to unsubscribe recording api");
                        }
                    }
                });

    }

    private void registerSensorRequest(GoogleApiClient client){

        listener = new OnDataPointListener(){
            @Override
            public void onDataPoint(DataPoint dataPoint) {

                for (Field field : dataPoint.getDataType().getFields()){

                    new AsyncTask<Object,Void,Integer>(){

                        @Override
                        protected Integer doInBackground(Object...params){
                            Field field = (Field)params[1];
                            DataPoint dataPoint = (DataPoint)params[0];
                            Value value = dataPoint.getValue(field);
                            return value.asInt();
                        }

                        @Override
                        protected void onPostExecute(Integer result) {
                            mStepCount += result;
                            message.setText(String.valueOf(mStepCount));
                        }
                    }.execute(dataPoint, field);


                    //use the data
                    //add to step counter
                    //mStepCount += value.asInt();

                    //message.setText("Your number of steps are: " + String.valueOf(value.asInt()));
                }

            }
        };

        SensorRequest sensorRequest = new SensorRequest.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setSamplingRate(10, TimeUnit.SECONDS)
                .build();

        PendingResult<Status> pendingResult = Fitness.SensorsApi.add(client, sensorRequest, listener);
        pendingResult.setResultCallback(new ResultCallback<Status>(){

            @Override
            public void onResult(Status status){

                if(status.isSuccess()){
                    System.out.println("Received result");
                } else {
                    System.out.println("No result received.");
                }
            }
        });



    }

    public void removeSensorListener(View view){

        Fitness.SensorsApi.remove(mClient, listener);

    }

    // [END auth_connection_flow_in_activity_lifecycle_methods]

    /**
     * Find available data sources and attempt to register on a specific {@link DataType}.
     * If the application cares about a data type but doesn't care about the source of the data,
     * this can be skipped entirely, instead calling
     *     {@link com.google.android.gms.fitness.SensorsApi
     *     #register(GoogleApiClient, SensorRequest, DataSourceListener)},
     * where the {@link SensorRequest} contains the desired data type.
     */
    private void findFitnessDataSources() {
        // [START find_data_sources]
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                // At least one datatype must be specified.
                .setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
                        // Can specify whether data type is raw or derived.
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        //Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        StringBuilder textMessage = new StringBuilder();
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            textMessage.append("Data source found: " + dataSource.toString());
                            textMessage.append("Data Source type: " + dataSource.getDataType().getName());
                            //    Log.i(TAG, "Data source found: " + dataSource.toString());
                            //    Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());

                            //Let's register a listener to receive Activity data!
                            if (dataSource.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)
                                    && mListener == null) {
                                textMessage.append("Data source for LOCATION_SAMPLE found!  Registering.");
                        //        Log.i(TAG, "Data source for LOCATION_SAMPLE found!  Registering.");
                                registerFitnessDataListener(dataSource,
                                        DataType.TYPE_LOCATION_SAMPLE);
                            }
                        }
                    }
                });
        // [END find_data_sources]
    }

    /**
     * Register a listener with the Sensors API for the provided {@link DataSource} and
     * {@link DataType} combo.
     */
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        // [START register_data_listener]
        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    message.setText(message.getText() + "Detected DataPoint field: " + field.getName());
                    message.setText(message.getText() + "Detected DataPoint value: " + val);
                //    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                //    Log.i(TAG, "Detected DataPoint value: " + val);
                }
            }
        };

        Fitness.SensorsApi.add(
                mClient,
                new SensorRequest.Builder()
                        .setDataSource(dataSource) // Optional but recommended for custom data sets.
                        .setDataType(dataType) // Can't be omitted.
                        .setSamplingRate(10, TimeUnit.SECONDS)
                        .build(),
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            message.setText(message.getText() + "Listener registered!");
                        //    Log.i(TAG, "Listener registered!");
                        } else {
                            message.setText(message.getText() + "Listener not registered!");
                        //    Log.i(TAG, "Listener not registered.");
                        }
                    }
                });
        // [END register_data_listener]
    }

    /**
     * Unregister the listener with the Sensors API.
     */
    private void unregisterFitnessDataListener() {
        if (mListener == null) {
            // This code only activates one listener at a time.  If there's no listener, there's
            // nothing to unregister.
            return;
        }

        // [START unregister_data_listener]
        // Waiting isn't actually necessary as the unregister call will complete regardless,
        // even if called from within onStop, but a callback can still be added in order to
        // inspect the results.
        Fitness.SensorsApi.remove(
                mClient,
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                        //    Log.i(TAG, "Listener was removed!");
                        } else {
                        //    Log.i(TAG, "Listener was not removed.");
                        }
                    }
                });
        // [END unregister_data_listener]
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensor_api, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_unregister_listener) {
            unregisterFitnessDataListener();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  Initialize a custom log class that outputs both to in-app targets and logcat.
     */
    private void initializeLogging() {
        // Wraps Android's native log framework.
        //LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        //Log.setLogNode(logWrapper);
        // Filter strips out everything except the message text.
        //MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        //logWrapper.setNext(msgFilter);
        // On screen logging via a customized TextView.
        //LogView logView = (LogView) findViewById(R.id.sample_logview);
        //logView.setTextAppearance(this, R.style.Log);
        //logView.setBackgroundColor(Color.WHITE);
        //msgFilter.setNext(logView);
        //Log.i(TAG, "Ready");
    }
}
